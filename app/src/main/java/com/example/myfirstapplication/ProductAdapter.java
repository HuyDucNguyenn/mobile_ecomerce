package com.example.myfirstapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import com.google.android.material.snackbar.Snackbar;
import android.widget.TextView;
import java.util.List;

public class ProductAdapter extends BaseAdapter {

    private Context context;
    private List<Product> productList;
    private LayoutInflater inflater;
    private final CartDatabaseHelper cartDb;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
        this.inflater = LayoutInflater.from(context);
        this.cartDb = new CartDatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return productList != null ? productList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        ImageView imgProduct;
        TextView tvProductName;
        TextView tvProductPrice;
        TextView tvProductDesc;
        View btnQuickAdd;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_product, parent, false);
            holder = new ViewHolder();
            holder.imgProduct = convertView.findViewById(R.id.imgProduct);
            holder.tvProductName = convertView.findViewById(R.id.tvProductName);
            holder.tvProductPrice = convertView.findViewById(R.id.tvProductPrice);
            holder.tvProductDesc = convertView.findViewById(R.id.tvProductDesc);
            holder.btnQuickAdd = convertView.findViewById(R.id.btnQuickAdd);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Product product = productList.get(position);
        Context ctx = convertView.getContext();
        holder.tvProductName.setText(ProductLocalization.getLocalizedName(ctx, product.getName()));
        holder.tvProductPrice.setText(ctx.getString(R.string.price_fmt, product.getPrice()));
        holder.tvProductDesc.setText(ProductLocalization.getLocalizedDescription(ctx, product.getName(), product.getDescription()));
        holder.imgProduct.setImageResource(product.getImageResId());
        holder.btnQuickAdd.setOnClickListener(v -> {
            int image = product.getImageResId() != 0 ? product.getImageResId() : R.drawable.ic_product_placeholder;
            long rowId = cartDb.insertCartItem(product.getName(), product.getPrice(), image, 1);
            Snackbar bar = Snackbar.make(v, context.getString(rowId != -1 ? R.string.added_to_cart : R.string.add_failed, 1),
                    Snackbar.LENGTH_LONG);
            if (rowId != -1) {
                bar.setAction(R.string.view_cart, x -> context.startActivity(new Intent(context, CartActivity.class)));
                NavigationHelper.refreshCartBadge((Activity) context);
            }
            View anchor = ((Activity) context).findViewById(R.id.bottomNavCard);
            if (anchor != null) bar.setAnchorView(anchor);
            bar.show();
        });

        return convertView;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }
}
