package com.test.bighand.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.test.bighand.R;
import com.test.bighand.db.DBUtils;
import com.test.bighand.ui.EventBus;
import com.test.bighand.ui.fragment.EditShoppingItemFragment;
import com.test.bighand.ui.fragment.ShoppingListFragment;

import rx.Subscription;

/**
 * Home (Landing) screen of the app. This activity renders two distinct layouts: phones and tablets.
 * A screen size of sw600dp (width) is considered as a tablet. The app can be run on a 7inch or higher
 * screen size in order to see the tablet layout.
 *
 * @author Usman
 */
public class HomeActivity extends CommonActivity {

    /**
     * Contains the list of actions, which are posted to this activity's containing fragments.
     */
    public static enum ShoppingListAction {

        ACTION_DELETE_ITEMS, ACTION_DISMISS_MULTI_SELECTION, ACTION_REFRESH_LIST

    }

    private String TAG = "HomeActivity";
    private ShoppingListFragment mShoppingListFragment;
    private EditShoppingItemFragment mEditShoppingItemFragment;
    private Subscription mListOperationSubscription;
    private boolean mShowDeleteMenuItem;
    private int mNumSelectedItems;
    private boolean mIsTablet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbar(getString(R.string.bighand_test_app), false);
        mHomePressHandledByChildActivity = true;
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);
        DBUtils.mayInsertDummyShoppingItems();
        addFragment();

        mListOperationSubscription = EventBus.get().getObservable().subscribe((object) -> {

            if (object instanceof ShoppingListFragment.ListOperations) {
                ShoppingListFragment.ListOperations lo = (ShoppingListFragment.ListOperations) object;
                if (lo.enableMultiSelection) {
                    mShowDeleteMenuItem = true;
                    invalidateOptionsMenu();
                    mNumSelectedItems = lo.numSelectedRows;
                    updateToolbar(String.format("%d", mNumSelectedItems), true);
                } else {
                    long id = lo.selectedShoppingItemId;
                    if (mIsTablet) {
                        EventBus.get().postEvent(new Long(id));
                    } else {
                        // Make sure valid ids are passed to avoid unnecessary processing
                        if (id >= 0) {
                            launchEditShoppingItemActivity(id);
                        }
                    }
                }
            }

        });
    }

    private void launchEditShoppingItemActivity(long id) {
        Intent i = new Intent(this, EditShoppingItemActivity.class);
        i.putExtra(EditShoppingItemActivity.EXTRAS_SHOPPING_ITEM_ID, id);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.action_delete);
        item.setVisible(mShowDeleteMenuItem);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == android.R.id.home) {
            resetToolbar();
            EventBus.get().postEvent(ShoppingListAction.ACTION_DISMISS_MULTI_SELECTION);
        } else if (item.getItemId() == R.id.action_delete) {
            if (mNumSelectedItems == 0) {
                Toast.makeText(this, R.string.no_item_selected, Toast.LENGTH_SHORT).show();
            } else {
                resetToolbar();
                EventBus.get().postEvent(ShoppingListAction.ACTION_DELETE_ITEMS);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void resetToolbar() {
        mShowDeleteMenuItem = false;
        mNumSelectedItems = 0;
        invalidateOptionsMenu();
        updateToolbar(getString(R.string.bighand_test_app), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void addFragment() {
        mShoppingListFragment = ShoppingListFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.shopping_list_container, mShoppingListFragment);

        if (mIsTablet) {
            mEditShoppingItemFragment = EditShoppingItemFragment.newInstance(-1L);
            transaction.add(R.id.shopping_item_container, mEditShoppingItemFragment);
        }

        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        if (mListOperationSubscription != null) {
            mListOperationSubscription.unsubscribe();
            mListOperationSubscription = null;
        }
        super.onDestroy();
    }
}
