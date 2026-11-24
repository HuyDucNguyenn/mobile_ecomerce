package com.example.myfirstapplication;

import android.content.Context;

public final class ProductLocalization {
    private ProductLocalization() { }

    public static String getLocalizedName(Context context, String name) {
        if (context == null || name == null) return name;
        String s = name.toLowerCase().trim();
        if (s.contains("iphone 15") && s.contains("pro")) return context.getString(R.string.prod_name_iphone15pro);
        if (s.contains("s24") && s.contains("ultra")) return context.getString(R.string.prod_name_s24ultra);
        if (s.contains("s24")) return context.getString(R.string.prod_name_s24);
        if (s.contains("macbook")) return context.getString(R.string.prod_name_macbook);
        if (s.contains("wh-1000xm5") || s.contains("tai nghe")) return context.getString(R.string.prod_name_sony_headphones);
        if (s.contains("ipad")) return context.getString(R.string.prod_name_ipad);
        if (s.contains("mx master") || s.contains("chuột") || s.contains("mouse")) return context.getString(R.string.prod_name_mouse);
        if (s.contains("keychron") || s.contains("bàn phím") || s.contains("keyboard")) return context.getString(R.string.prod_name_keyboard);
        if (s.contains("watch") || s.contains("đồng hồ")) return context.getString(R.string.prod_name_watch);
        return name;
    }

    public static String getLocalizedDescription(Context context, String name, String defaultDesc) {
        if (context == null) return defaultDesc != null ? defaultDesc : "";
        String s = name != null ? name.toLowerCase().trim() : "";
        if (s.contains("iphone 15") && s.contains("pro")) return context.getString(R.string.prod_desc_iphone15pro);
        if (s.contains("s24") && s.contains("ultra")) return context.getString(R.string.prod_desc_s24ultra);
        if (s.contains("s24")) return context.getString(R.string.prod_desc_s24);
        if (s.contains("macbook")) return context.getString(R.string.prod_desc_macbook);
        if (s.contains("wh-1000xm5") || s.contains("tai nghe")) return context.getString(R.string.prod_desc_sony_headphones);
        if (s.contains("ipad")) return context.getString(R.string.prod_desc_ipad);
        if (s.contains("mx master") || s.contains("chuột") || s.contains("mouse")) return context.getString(R.string.prod_desc_mouse);
        if (s.contains("keychron") || s.contains("bàn phím") || s.contains("keyboard")) return context.getString(R.string.prod_desc_keyboard);
        if (s.contains("watch") || s.contains("đồng hồ")) return context.getString(R.string.prod_desc_watch);
        return defaultDesc != null ? defaultDesc : "";
    }
}
