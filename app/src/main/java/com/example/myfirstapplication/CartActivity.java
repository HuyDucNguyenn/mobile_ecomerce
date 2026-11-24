package com.example.myfirstapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_LOGGED_USER = "LOGGED_USER";
    private static final String COLLECTION_ORDERS = "orders";

    private ListView listViewCart;
    private TextView tvTotalPrice;
    private Button btnBackFromCart;
    private Button btnPlaceOrder;

    private CartDatabaseHelper dbHelper;
    private List<CartItem> cartList;
    private CartAdapter adapter;
    private double currentTotal = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        SystemBars.setup(this, R.id.heroContent, false);
        NavigationHelper.setupBottomNav(this);

        initViews();
        setupTopAppBar();
        setupActionButtons();
        loadCartData();
    }

    private void initViews() {
        dbHelper = new CartDatabaseHelper(this);
        listViewCart = findViewById(R.id.listViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnBackFromCart = findViewById(R.id.btnBackFromCart);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        cartList = new ArrayList<>();
        adapter = new CartAdapter(this, cartList, cartId -> {
            dbHelper.deleteCartItem(cartId);
            Toast.makeText(this, getString(R.string.cart_item_removed), Toast.LENGTH_SHORT).show();
            loadCartData();
        });
        listViewCart.setAdapter(adapter);
    }

    private void setupTopAppBar() {
        MaterialToolbar topBar = findViewById(R.id.topAppBarCart);
        if (topBar != null) {
            topBar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void setupActionButtons() {
        btnBackFromCart.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
            finish();
        });
        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void loadCartData() {
        cartList.clear();
        currentTotal = 0.0;

        Cursor cursor = dbHelper.getAllCartItems();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_NAME));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_PRICE));
                int image = cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_IMAGE));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(CartDatabaseHelper.COLUMN_QUANTITY));
                cartList.add(new CartItem(id, name, price, image, qty));
                currentTotal += price * qty;
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();

        tvTotalPrice.setText(getString(R.string.cart_total, currentTotal));
        boolean empty = cartList.isEmpty();
        findViewById(R.id.emptyCart).setVisibility(empty ? View.VISIBLE : View.GONE);
        listViewCart.setVisibility(empty ? View.GONE : View.VISIBLE);
        tvTotalPrice.setVisibility(empty ? View.GONE : View.VISIBLE);
        btnPlaceOrder.setVisibility(empty ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();
    }

    private void placeOrder() {
        if (cartList.isEmpty() || !btnPlaceOrder.isEnabled()) {
            return;
        }

        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String username = prefs.getString(KEY_LOGGED_USER, getString(R.string.cart_guest));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String orderDate = sdf.format(new Date());

        List<CartItem> orderedItems = new ArrayList<>(cartList);

        Order newOrder = new Order(
                null,
                username,
                orderDate,
                currentTotal,
                orderedItems,
                getString(R.string.order_status_success)
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        btnPlaceOrder.setEnabled(false);
        AlertDialog submitting = new MaterialAlertDialogBuilder(this)
                .setMessage(R.string.order_submitting)
                .setCancelable(false)
                .show();

        db.collection(COLLECTION_ORDERS)
                .add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    dbHelper.clearCart();
                    if (isDestroyed() || isFinishing()) return;
                    submitting.dismiss();
                    btnPlaceOrder.setEnabled(true);
                    loadCartData();

                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.order_success)
                            .setMessage(getString(R.string.cart_total, newOrder.getTotalPrice()))
                            .setPositiveButton(R.string.order_view_history, (dialog, which) -> {
                                startActivity(new Intent(this, OrderHistoryActivity.class));
                                finish();
                            })
                            .setNegativeButton(android.R.string.ok, null)
                            .show();
                })
                .addOnFailureListener(error -> {
                    if (isDestroyed() || isFinishing()) return;
                    submitting.dismiss();
                    btnPlaceOrder.setEnabled(true);
                    new MaterialAlertDialogBuilder(this)
                            .setTitle(R.string.order_retry_title)
                            .setMessage(R.string.order_retry_message)
                            .setPositiveButton(R.string.order_retry, (dialog, which) -> placeOrder())
                            .setNegativeButton(android.R.string.cancel, null)
                            .show();
                });
    }
}
