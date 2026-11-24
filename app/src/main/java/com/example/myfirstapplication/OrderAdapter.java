package com.example.myfirstapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class OrderAdapter extends BaseAdapter {
    private Context context;
    private List<Order> orderList;
    private LayoutInflater inflater;

    public OrderAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return orderList != null ? orderList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return orderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView tvOrderDate;
        TextView tvOrderStatus;
        TextView tvOrderProducts;
        TextView tvOrderTotalPrice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_order, parent, false);
            holder = new ViewHolder();
            holder.tvOrderDate = convertView.findViewById(R.id.tvOrderDate);
            holder.tvOrderStatus = convertView.findViewById(R.id.tvOrderStatus);
            holder.tvOrderProducts = convertView.findViewById(R.id.tvOrderProducts);
            holder.tvOrderTotalPrice = convertView.findViewById(R.id.tvOrderTotalPrice);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Order order = orderList.get(position);
        Context ctx = convertView.getContext();

        holder.tvOrderDate.setText(ctx.getString(R.string.order_date, order.getOrderDate() != null ? order.getOrderDate() : ctx.getString(R.string.order_date_unknown)));
        holder.tvOrderStatus.setText(order.getStatus() != null ? order.getStatus() : ctx.getString(R.string.order_status_default));
        holder.tvOrderTotalPrice.setText(ctx.getString(R.string.price_fmt, order.getTotalPrice()));

        StringBuilder productsBuilder = new StringBuilder();
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (CartItem item : order.getItems()) {
                String localizedName = ProductLocalization.getLocalizedName(ctx, item.getName());
                productsBuilder.append(ctx.getString(R.string.order_item_line, localizedName, item.getQuantity(),
                        ctx.getString(R.string.price_fmt, item.getPrice() * item.getQuantity()))).append('\n');
            }
            if (productsBuilder.length() > 0 && productsBuilder.charAt(productsBuilder.length() - 1) == '\n') {
                productsBuilder.deleteCharAt(productsBuilder.length() - 1);
            }
        } else {
            productsBuilder.append(ctx.getString(R.string.order_no_items));
        }
        holder.tvOrderProducts.setText(productsBuilder.toString());

        return convertView;
    }

    public void updateList(List<Order> newList) {
        this.orderList = newList;
        notifyDataSetChanged();
    }
}
