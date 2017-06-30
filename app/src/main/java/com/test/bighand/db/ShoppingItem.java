package com.test.bighand.db;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Represents a ShoppingItem relation (table) in the db.
 *
 * @author Usman
 */
public class ShoppingItem extends RealmObject {

    @Ignore
    public static final String COLUMN_ID = "id";
    @Ignore
    public static final String COLUMN_DATE_TIME = "dateTime";


    @PrimaryKey
    private long id;
    private String itemDescription;
    private String itemBrand;
    private double itemPrice;
    private int itemQuantity;
    private String itemBarcode;
    private long dateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemBrand() {
        return itemBrand;
    }

    public void setItemBrand(String itemBrand) {
        this.itemBrand = itemBrand;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemBarcode() {
        return itemBarcode;
    }

    public void setItemBarcode(String itemBarcode) {
        this.itemBarcode = itemBarcode;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public static long getNextId() {
        Realm realm = Realm.getDefaultInstance();
        try {
            Number n = realm.where(ShoppingItem.class).max(COLUMN_ID);
            if (n == null) {
                return 0;
            }
            return n.longValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
