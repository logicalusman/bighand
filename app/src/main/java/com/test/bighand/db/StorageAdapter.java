package com.test.bighand.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Facade for storing application data in key=value pairs into the persistent storage.
 *
 * @author Usman
 */
public class StorageAdapter {

    public static final String KEY_CURRENCY_SYMBOL = "key_currency_symbol";

    private Context mCtx;
    private SharedPreferences mSharePrefs;

    private StorageAdapter(Context ctx) {
        mCtx = ctx;
        mSharePrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    /**
     * Gets the instance of this class.
     *
     * @param ctx
     * @return
     */
    public static StorageAdapter get(@NonNull Context ctx) {
        return new StorageAdapter(ctx);
    }

    /**
     * Saves default currency symbol for app
     *
     * @param currencySymbol
     * @return
     */
    public StorageAdapter saveAppCurrencySymbol(@NonNull String currencySymbol) {

        mSharePrefs.edit().putString(KEY_CURRENCY_SYMBOL, currencySymbol).commit();
        return this;
    }

    /**
     * Gets default currency of this app.
     *
     * @return
     */
    public String getAppCurrencySymbol() {
        return mSharePrefs.getString(KEY_CURRENCY_SYMBOL, "£");
    }

}

