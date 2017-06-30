package com.test.bighand.db;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Handy methods to interact with DB
 *
 * @author Usman
 */
public class DBUtils {

    private static String TAG = "DBUtils";
    private static List<ShoppingItem> sShoppingItems;

    /**
     * Inserts the dummy shopping items into the db if the db is empty otherwise
     * it simply returns.
     */
    public static void mayInsertDummyShoppingItems() {

        // check if the db is not empty
        if (getNumShoppingItems() > 0) {
            return;
        }

        // If the list was created before, then use it
        if (sShoppingItems != null) {
            saveToDB();
            return;
        }

        // Creating the list; this would happen once only
        sShoppingItems = new ArrayList<>(6);
        long nextId = ShoppingItem.getNextId();

        // 1st item
        ShoppingItem si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("12345678912");
        si.setItemDescription("Lucozade Sport Lite Orange");
        si.setItemBrand("LUCOZADE");
        si.setItemQuantity(1);
        si.setItemPrice(1.99);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        // 2nd item
        si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("12346778912");
        si.setItemDescription("Corsodyl Mint Mouthwash");
        si.setItemBrand("CORSODYL");
        si.setItemQuantity(2);
        si.setItemPrice(10);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        // 3rd item
        si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("19646078912");
        si.setItemDescription("Tropicana Orange Juice 1.6 Litre");
        si.setItemBrand("TROPICANA");
        si.setItemQuantity(1);
        si.setItemPrice(3.50);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        // 4th item
        si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("34845078912");
        si.setItemDescription("Tesco Still 6X2 Ltr");
        si.setItemBrand("TESCO");
        si.setItemQuantity(2);
        si.setItemPrice(4.20);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        // 5th item
        si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("564453578912");
        si.setItemDescription("Kinder Suprise Egg 3 X 20G");
        si.setItemBrand("KINDER");
        si.setItemQuantity(1);
        si.setItemPrice(2);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        // 6th item
        si = new ShoppingItem();
        si.setId(nextId++);
        si.setItemBarcode("76533578912");
        si.setItemDescription("Walkers Wotsits Cheese Snacks 6 Pack 16.5G");
        si.setItemBrand("WALKERS");
        si.setItemQuantity(1);
        si.setItemPrice(2);
        si.setDateTime(System.currentTimeMillis());
        sShoppingItems.add(si);
        saveToDB();


    }

    private static int getNumShoppingItems() {
        int numItems = 0;

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            numItems = realm.where(ShoppingItem.class).findAll().size();
        } finally {
            realm.close();
        }

        return numItems;
    }

    private static void saveToDB() {

        Realm realm = null;
        try {
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            realm.insertOrUpdate(sShoppingItems);
            realm.commitTransaction();
        } finally {
            realm.close();
        }

    }

}
