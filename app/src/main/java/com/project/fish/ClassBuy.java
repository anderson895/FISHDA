package com.project.fish;

public class ClassBuy {
    private String buyName;
    private String buyDescription;
    private String buyPrice;
    private String buyQuantity;
    private String buyUserID;
    private String buyID;
    private String buyStatus;

    // Constructor
    public ClassBuy(String buyName, String buyDescription, String buyPrice, String buyQuantity, String buyUserID, String buyID, String buyStatus) {
        this.buyName = buyName;
        this.buyDescription = buyDescription;
        this.buyPrice = buyPrice;
        this.buyQuantity = buyQuantity;
        this.buyUserID = buyUserID;
        this.buyStatus = buyStatus;
        this.buyID = buyID;
    }

    // Getters
    public String getBuyName() {
        return buyName;
    }

    public String getBuyDescription() {
        return buyDescription;
    }

    public String getBuyPrice() {
        return buyPrice;
    }

    public String getBuyQuantity() {
        return buyQuantity;
    }

    public String getBuyUserID() {
        return buyUserID;
    }

    public String getBuyID() {
        return buyID;
    }
    public String getBuyStatus() {
        return buyStatus;
    }
}
