package com.project.fish;

public class FishItem {
    private String itemName;
    private String price;
    private String description;
    private String quantity;

    public FishItem() {
        // Default constructor required for Firebase
    }

    public FishItem(String itemName, String price, String description, String quantity) {
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }
}

