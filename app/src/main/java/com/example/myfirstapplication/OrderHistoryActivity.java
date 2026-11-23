package com.example.myfirstapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {
    private static final String PREF_NAME = "UserPrefs";
    private static final String KEY_LOGGED_USER = "LOGGED_USER";
    private static final String COLLECTION_ORDERS = "orders";

    private ListView listViewOrders;
    private View tvEmptyOrders;
    private List<Order> orderList;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);
        SystemBars.setup(this, R.id.heroContent, false);
        NavigationHelper.setupBottomNav(this);

        initViews();
        setupTopAppBar();
        loadOrderHistoryFromFirestore();
    }

    private void initViews() {
        listViewOrders = findViewById(R.id.listViewOrders);
        tvEmptyOrders = findViewById(R.id.tvEmptyOrders);
        findViewById(R.id.btnShopNow).setOnClickListener(v -> AuthHelper.openMain(this));

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList);
        listViewOrders.setAdapter(adapter);
    }

    private void setupTopAppBar() {
        MaterialToolbar topAppBar = findViewById(R.id.topAppBarOrderHistory);
        if (topAppBar != null) {
            topAppBar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadOrderHistoryFromFirestore() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        String currentUser = prefs.getString(KEY_LOGGED_USER, "");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(COLLECTION_ORDERS)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, getString(R.string.history_load_error, error.getMessage()), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        orderList.clear();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Order order = doc.toObject(Order.class);
                            if (order != null) {
                                order.setOrderId(doc.getId());

                                if (currentUser.isEmpty() || currentUser.equals(order.getUsername())) {
                                    orderList.add(order);
                                }
                            }
                        }

                        Collections.reverse(orderList);

                        if (orderList.isEmpty()) {
                            tvEmptyOrders.setVisibility(View.VISIBLE);
                        } else {
                            tvEmptyOrders.setVisibility(View.GONE);
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
