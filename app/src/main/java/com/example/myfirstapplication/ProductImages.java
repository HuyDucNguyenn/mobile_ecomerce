package com.example.myfirstapplication;

final class ProductImages {
    private ProductImages() { }

    private static String n(String name) { return name == null ? "" : name.toLowerCase(); }

    static int forName(String name) {
        String s = n(name);
        if (s.contains("ipad") || s.contains("tablet")) return R.drawable.photo_tablet;
        if (s.contains("macbook") || s.contains("laptop")) return R.drawable.photo_laptop;
        if (s.contains("watch") || s.contains("đồng hồ")) return R.drawable.photo_watch;
        if (s.contains("tai nghe") || s.contains("headphone") || s.contains("airpods")) return R.drawable.photo_headphones;
        if (s.contains("chuột") || s.contains("mouse")) return R.drawable.photo_mouse;
        if (s.contains("bàn phím") || s.contains("keyboard")) return R.drawable.photo_keyboard;
        if (s.contains("s24") && s.contains("ultra")) return R.drawable.photo_s24ultra;
        if (s.contains("s24")) return R.drawable.photo_s24;
        if (s.contains("iphone 15") && s.contains("pro")) return R.drawable.photo_iphone15pro;
        if (s.contains("iphone 15")) return R.drawable.photo_iphone15;
        if (s.contains("iphone") || s.contains("galaxy") || s.contains("samsung") || s.contains("điện thoại")) return R.drawable.photo_phone;
        return R.drawable.ic_product_placeholder;
    }

    static int tintFor(String name) {
        String s = n(name);
        if (s.contains("ipad") || s.contains("macbook") || s.contains("laptop") || s.contains("tablet")) return R.color.tint_sky;
        if (s.contains("watch") || s.contains("đồng hồ")) return R.color.tint_sage;
        if (s.contains("bàn phím") || s.contains("keyboard")) return R.color.tint_amber;
        if (s.contains("iphone") || s.contains("galaxy") || s.contains("samsung")) return R.color.c_primary_container;
        return R.color.tint_teal;
    }
}
