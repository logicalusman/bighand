package com.test.bighand.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.test.bighand.R;
import com.test.bighand.ui.fragment.EditShoppingItemFragment;

/**
 * Provides editor view to edit a shopping item
 *
 * @author Usman
 */
public class EditShoppingItemActivity extends CommonActivity {

    /**
     * Calling activities should pass item id as long extra.
     */
    public static final String EXTRAS_SHOPPING_ITEM_ID = "com.test.bighand.extras_shopping_item_id";

    private EditShoppingItemFragment mShoppingItemFragment;
    private long mShoppingItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_item);
        setupToolbar(getString(R.string.edit_item), true);
        mShoppingItemId = getIntent().getLongExtra(EXTRAS_SHOPPING_ITEM_ID, -1);
        addFragment();
    }

    private void addFragment() {
        mShoppingItemFragment = EditShoppingItemFragment.newInstance(mShoppingItemId);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.shopping_item_container, mShoppingItemFragment);
        transaction.commit();
    }
}
