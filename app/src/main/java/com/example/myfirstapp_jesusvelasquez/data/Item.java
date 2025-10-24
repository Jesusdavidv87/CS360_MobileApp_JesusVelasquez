package com.example.myfirstapp_jesusvelasquez.data;

public class Item {
    public long id;
    public String name;
    public String sku;
    public int qty;
    public String location;
    public int threshold;

    // Empty constructor (for manual field setting / adapters / Cursor mapping)
    public Item() {}

    // Convenience constructor used by your DBRepository: new Item(id, name, qty)
    public Item(long id, String name, int qty) {
        this.id = id;
        this.name = name;
        this.qty = qty;
    }

    // Full constructor (use anywhere you want to set everything at once)
    public Item(long id, String name, String sku, int qty, String location, int threshold) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.qty = qty;
        this.location = location;   // <-- fix: lowercase variable
        this.threshold = threshold;
    }
}




