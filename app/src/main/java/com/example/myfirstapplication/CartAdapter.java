package com.example.myfirstapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CartAdapter extends BaseAdapter {
    private Context context;
    private List<CartItem> cartList;
    private OnItemDeletedListener listener;
    private LayoutInflater inflater;

    public interface OnItemDeletedListener {
        void onItemDeleted(int cartId);
    }

    public CartAdapter(Context context, List<CartItem> cartList, OnItemDeletedListener listener) {
        this.context = context;
        this.cartList = cartList;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return cartList.size(); }
    @Override
    public Object getItem(int position) { return cartList.get(position); }
    @Override
    public long getItemId(int position) { return cartList.get(position).getId(); }

    private static class ViewHolder {
        ImageView img;
        TextView tvName;
        TextView tvQuantity;
        TextView tvPrice;
        Button btnDelete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder();
            holder.img = convertView.findViewById(R.id.imgCartItem);
            holder.tvName = convertView.findViewById(R.id.tvCartItemName);
            holder.tvQuantity = convertView.findViewById(R.id.tvCartItemQuantity);
            holder.tvPrice = convertView.findViewById(R.id.tvCartItemPrice);
            holder.btnDelete = convertView.findViewById(R.id.btnDeleteCartItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartItem item = cartList.get(position);

        holder.img.setImageResource(ProductImages.forName(item.getName()));
        holder.tvName.setText(item.getName());
        holder.tvQuantity.setText(convertView.getContext().getString(R.string.cart_item_qty, item.getQuantity()));
        holder.tvPrice.setText(convertView.getContext().getString(R.string.price_fmt, item.getPrice() * item.getQuantity()));

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onItemDeleted(item.getId());
        });

        return convertView;
    }
}
