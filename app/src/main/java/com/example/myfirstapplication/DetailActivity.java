package com.example.myfirstapplication;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_PRODUCT = "EXTRA_PRODUCT";

    private ImageView imgDetail;
    private TextView tvName, tvPrice, tvDesc;
    private Button btnBack, btnAddToCart;
    private Button btnDecreaseQty, btnIncreaseQty;
    private TextView tvQuantity;

    private CartDatabaseHelper dbHelper;
    private String currentName;
    private double currentPrice;
    private int currentImage;
    private int selectedQuantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        SystemBars.setup(this, R.id.panelProduct, false);
        NavigationHelper.setupBottomNav(this);

        initViews();
        loadProductData();
        setupQuantityButtons();
        setupAddToCart();
    }

    private void initViews() {
        dbHelper = new CartDatabaseHelper(this);

        imgDetail = findViewById(R.id.imgDetail);
        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvDesc = findViewById(R.id.tvDetailDesc);
        btnBack = findViewById(R.id.btnBack);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        btnDecreaseQty = findViewById(R.id.btnDecreaseQty);
        btnIncreaseQty = findViewById(R.id.btnIncreaseQty);
        tvQuantity = findViewById(R.id.tvQuantity);

        btnBack.setOnClickListener(v -> finish());
    }

    private void loadProductData() {
        Intent intent = getIntent();
        if (intent != null) {
            Product product;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                product = intent.getSerializableExtra(EXTRA_PRODUCT, Product.class);
            } else {
                product = (Product) intent.getSerializableExtra(EXTRA_PRODUCT);
            }

            if (product != null) {
                currentName = product.getName();
                currentPrice = product.getPrice();
                currentImage = ProductImages.forName(product.getName());

                if (currentName != null) tvName.setText(ProductLocalization.getLocalizedName(this, currentName));
                tvPrice.setText(getString(R.string.price_fmt, currentPrice));
                tvDesc.setText(ProductLocalization.getLocalizedDescription(this, currentName, product.getDescription()));
                imgDetail.setImageResource(currentImage);
                findViewById(R.id.panelProduct).setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(this, ProductImages.tintFor(currentName))));
            }
        }
    }

    private void setupQuantityButtons() {
        tvQuantity.setText(String.valueOf(selectedQuantity));

        btnDecreaseQty.setOnClickListener(v -> {
            if (selectedQuantity > 1) {
                selectedQuantity--;
                tvQuantity.setText(String.valueOf(selectedQuantity));
            }
        });

        btnIncreaseQty.setOnClickListener(v -> {
            selectedQuantity++;
            tvQuantity.setText(String.valueOf(selectedQuantity));
        });
    }

    private void setupAddToCart() {
        btnAddToCart.setOnClickListener(v -> {
            long rowId = dbHelper.insertCartItem(currentName, currentPrice, currentImage, selectedQuantity);
            if (rowId != -1) {
                Snackbar.make(btnAddToCart, getString(R.string.added_to_cart, selectedQuantity), Snackbar.LENGTH_LONG)
                        .setAnchorView(btnAddToCart)
                        .setAction(R.string.view_cart, view -> startActivity(new Intent(this, CartActivity.class)))
                        .show();
            } else {
                Toast.makeText(DetailActivity.this, getString(R.string.add_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
