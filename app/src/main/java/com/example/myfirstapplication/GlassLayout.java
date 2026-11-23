package com.example.myfirstapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;

public class GlassLayout extends FrameLayout {
    private static final int DOWNSCALE = 14;

    private View source;
    private Bitmap small;
    private Bitmap mid;
    private Canvas midCanvas;
    private Canvas smallCanvas;
    private final Paint bitmapPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.ANTI_ALIAS_FLAG);
    private final Paint tintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint rimPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path pill = new Path();
    private final RectF rect = new RectF();
    private final int[] a = new int[2];
    private final int[] b = new int[2];

    public GlassLayout(Context c) { this(c, null); }

    public GlassLayout(Context c, AttributeSet attrs) {
        super(c, attrs);
        tintPaint.setColor(ContextCompat.getColor(c, R.color.glass_tint));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1f);
        strokePaint.setColor(ContextCompat.getColor(c, R.color.glass_stroke));
        rimPaint.setStyle(Paint.Style.STROKE);
        rimPaint.setStrokeWidth(getResources().getDisplayMetrics().density * 1.5f);
        setWillNotDraw(false);
    }

    public void setBlurSource(View v) {
        source = v;
        v.addOnLayoutChangeListener((view, l, t, r, bt, ol, ot, or, ob) -> invalidate());
        invalidate();
    }

    public void trackScroll(AbsListView list) {
        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override public void onScrollStateChanged(AbsListView v, int state) { invalidate(); }
            @Override public void onScroll(AbsListView v, int first, int visible, int total) { invalidate(); }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        rimPaint.setShader(new LinearGradient(0, 0, 0, h, ContextCompat.getColor(getContext(), R.color.glass_rim_top),
                ContextCompat.getColor(getContext(), R.color.glass_rim_bottom), Shader.TileMode.CLAMP));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (w > 0 && h > 0) {
            float r = h / 2f;
            rect.set(0, 0, w, h);
            pill.reset();
            pill.addRoundRect(rect, r, r, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(pill);
            if (source != null) drawBlurredBackdrop(canvas, w, h);
            canvas.drawRect(rect, tintPaint);
            canvas.restore();
            rect.inset(0.5f, 0.5f);
            canvas.drawRoundRect(rect, r, r, strokePaint);
            rect.inset(1f, 1f);
            canvas.drawRoundRect(rect, r - 1f, r - 1f, rimPaint);
        }
        super.dispatchDraw(canvas);
    }

    private void drawBlurredBackdrop(Canvas canvas, int w, int h) {
        int sw = Math.max(1, w / DOWNSCALE), sh = Math.max(1, h / DOWNSCALE);
        if (small == null || small.getWidth() != sw || small.getHeight() != sh) {
            small = Bitmap.createBitmap(sw, sh, Bitmap.Config.ARGB_8888);
            smallCanvas = new Canvas(small);
        }
        getLocationInWindow(a);
        source.getLocationInWindow(b);
        small.eraseColor(0);
        smallCanvas.save();
        smallCanvas.scale(sw / (float) w, sh / (float) h);
        smallCanvas.translate(-(a[0] - b[0]), -(a[1] - b[1]));
        source.draw(smallCanvas);
        smallCanvas.restore();

        int mw = Math.max(1, w / 4), mh = Math.max(1, h / 4);
        if (mid == null || mid.getWidth() != mw || mid.getHeight() != mh) {
            mid = Bitmap.createBitmap(mw, mh, Bitmap.Config.ARGB_8888);
            midCanvas = new Canvas(mid);
        }
        midCanvas.drawBitmap(small, null, new RectF(0, 0, mw, mh), bitmapPaint);
        canvas.drawBitmap(mid, null, new RectF(0, 0, w, h), bitmapPaint);
    }
}
