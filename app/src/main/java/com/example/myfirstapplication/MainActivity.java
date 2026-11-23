package com.example.myfirstapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_LOGGED_USER = "LOGGED_USER";
    private static final String COLLECTION_PRODUCTS = "products";

    private ListView listView;
    private List<Product> productList;
    private ProductAdapter adapter;
    private String currentQuery = "";
    private boolean loaded = false;
    private boolean loadFailed;
    private View progressProducts;
    private View productState;
    private TextView tvEmptyProducts;
    private Button btnProductState;
    private Chip chipFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SystemBars.setup(this, R.id.heroContent, true);
        NavigationHelper.setupBottomNav(this);

        checkUserLogin();
        initViews();
        setupTopAppBar();
        checkAndSeedSampleProducts();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadDataFromFirestore();
    }

    private void checkUserLogin() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String username = prefs.getString("DISPLAY_NAME", "");
        if (!username.isEmpty()) {
            Toast.makeText(this, getString(R.string.main_welcome, username), Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        listView = findViewById(R.id.listView);
        progressProducts = findViewById(R.id.progressProducts);
        productState = findViewById(R.id.productState);
        btnProductState = findViewById(R.id.btnProductState);
        btnProductState.setOnClickListener(v -> {
            if (loadFailed) {
                loadDataFromFirestore();
            } else {
                currentQuery = "";
                filterProducts(currentQuery);
            }
        });
        tvEmptyProducts = findViewById(R.id.tvEmptyProducts);
        chipFilter = findViewById(R.id.chipFilter);
        chipFilter.setOnClickListener(v -> showSearchDialog());
        chipFilter.setOnCloseIconClickListener(v -> {
            currentQuery = "";
            filterProducts("");
        });
        productList = new ArrayList<>();
        adapter = new ProductAdapter(this, productList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Product selectedProduct = (Product) adapter.getItem(position);

            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_PRODUCT, selectedProduct);
            startActivity(intent);
        });
    }

    private void setupTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            if (item.getItemId() == R.id.menu_search) {
                showSearchDialog();
                return true;
            }
            return false;
        });
    }

    private void showSearchDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_search, null);
        TextInputEditText edtSearch = view.findViewById(R.id.edtSearch);
        edtSearch.setText(currentQuery);
        edtSearch.setSelection(edtSearch.length());
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                currentQuery = s.toString();
                filterProducts(currentQuery);
            }
        });
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.search_title)
                .setView(view)
                .setPositiveButton(R.string.search_done, null)
                .setNeutralButton(R.string.search_clear, (d, w) -> {
                    currentQuery = "";
                    filterProducts("");
                })
                .create();
        edtSearch.setOnEditorActionListener((v, actionId, event) -> { dialog.dismiss(); return true; });
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
        edtSearch.requestFocus();
    }

    private void filterProducts(String keyword) {
        List<Product> resultList = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            resultList.addAll(productList);
        } else {
            String lowerKeyword = keyword.toLowerCase().trim();
            for (Product product : productList) {
                if (product.getName() != null && product.getName().toLowerCase().contains(lowerKeyword)) {
                    resultList.add(product);
                }
            }
        }

        adapter.updateList(resultList);
        updateStateViews();
    }

    private void updateStateViews() {
        boolean filtering = !currentQuery.trim().isEmpty();
        chipFilter.setVisibility(filtering ? View.VISIBLE : View.GONE);
        if (filtering) chipFilter.setText(getString(R.string.filter_chip, currentQuery.trim()));
        boolean empty = adapter.getCount() == 0;
        progressProducts.setVisibility(loaded ? View.GONE : View.VISIBLE);
        productState.setVisibility(loadFailed || (loaded && empty) ? View.VISIBLE : View.GONE);
        listView.setVisibility(empty || loadFailed ? View.INVISIBLE : View.VISIBLE);
        int message = R.string.catalog_empty;
        if (loadFailed) {
            message = R.string.catalog_load_failed;
        } else if (filtering) {
            message = R.string.products_empty;
        }
        tvEmptyProducts.setText(message);
        btnProductState.setText(loadFailed ? R.string.catalog_retry : R.string.search_clear);
        btnProductState.setVisibility(loadFailed || filtering ? View.VISIBLE : View.GONE);
    }

    private void loadDataFromFirestore() {
        loaded = false;
        loadFailed = false;
        updateStateViews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_PRODUCTS)
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        loaded = true;
                        loadFailed = true;
                        updateStateViews();
                        return;
                    }

                    if (value != null) {
                        productList.clear();
                        Set<String> seen = new HashSet<>();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Product product = doc.toObject(Product.class);
                            if (product != null && product.getName() != null && !product.getName().trim().isEmpty()
                                    && seen.add(product.getName().trim().toLowerCase())) {
                                product.setImageResId(ProductImages.forName(product.getName()));
                                productList.add(product);
                            }
                        }
                        loaded = true;
                        filterProducts(currentQuery);
                    }
                });
    }

    private void checkAndSeedSampleProducts() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        boolean isSeeded = prefs.getBoolean("SEEDED_PRODUCTS_LIST_V2", false);
        if (!isSeeded) {
            seedSampleProductsToFirestore();
            prefs.edit().putBoolean("SEEDED_PRODUCTS_LIST_V2", true).apply();
        }
    }

    private void seedSampleProductsToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Product> sampleList = new ArrayList<>();
        sampleList.add(new Product("Apple iPhone 15 Pro", 27990000, "128GB, Titan tự nhiên, Màn hình 120Hz ProMotion", 0));
        sampleList.add(new Product("Samsung Galaxy S24 Ultra", 26490000, "256GB, Bút S-Pen thông minh, Camera 200MP", 0));
        sampleList.add(new Product("MacBook Air M2", 24990000, "8GB RAM, 256GB SSD, Màn Liquid Retina sắc nét", 0));
        sampleList.add(new Product("Tai nghe Sony WH-1000XM5", 7990000, "Chống ồn chủ động đỉnh cao, Thời lượng pin 30h", 0));
        sampleList.add(new Product("iPad Air 5 M1", 14500000, "WiFi 64GB, Màn hình 10.9 inch, Chip Apple M1", 0));
        sampleList.add(new Product("Chuột Logitech MX Master 3S", 2150000, "Cảm biến 8K DPI, Cuộn siêu tốc Magspeed", 0));
        sampleList.add(new Product("Bàn phím cơ Keychron K2", 1850000, "Gateron Brown Switch, Kết nối Bluetooth 5.1", 0));
        sampleList.add(new Product("Đồng hồ Apple Watch Series 9", 9890000, "Nhôm 41mm, Màn hình Always-On sáng rực rỡ", 0));

        for (Product product : sampleList) {
            String id = product.getName().toLowerCase().replaceAll("[^a-z0-9]+", "-");
            db.collection(COLLECTION_PRODUCTS).document(id).set(product);
        }
    }
}
