package com.test.bighand.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.test.bighand.R;
import com.test.bighand.db.ShoppingItem;
import com.test.bighand.db.StorageAdapter;
import com.test.bighand.ui.EventBus;
import com.test.bighand.ui.activity.HomeActivity;
import com.test.bighand.util.UiUtils;

import io.realm.Realm;
import rx.Subscription;

/**
 * Shows the shopping list item details. The UI facilitates editing item details and save them. The fragment subscribes
 * for a Long id object once it is loaded; concerned parties, such as containing Activity, as can post the id, which
 * will cause this fragment to update the item details.
 *
 * @author Usman
 */
public class EditShoppingItemFragment extends Fragment {

    private static String TAG = "EditShoppingItemFragment";
    private long mShoppingItemId = -1;
    private TextView mDescriptionTv, mBrandTv, mCurrencySymbolTv;
    private EditText mPriceEt, mQuantityEt;
    private ImageButton mIncreaseQtyBtn, mDecreaseQtyBtn;
    private Button mSaveBtn;
    private StorageAdapter mStorageAdapter;
    private Realm mRealm;
    private ShoppingItem mShoppingItem;
    private ConstraintLayout mRootCl;
    private Subscription mShoppingItemSubscription;
    private boolean mIsTablet;


    public EditShoppingItemFragment() {
        mRealm = Realm.getDefaultInstance();
    }

    public static EditShoppingItemFragment newInstance(@NonNull Long shoppingItemId) {
        EditShoppingItemFragment fragment = new EditShoppingItemFragment();
        fragment.mShoppingItemId = shoppingItemId;
        fragment.fetchShoppingItemFromDb();
        return fragment;
    }

    public void fetchShoppingItemFromDb() {
        mShoppingItem = mRealm.where(ShoppingItem.class).equalTo(ShoppingItem.COLUMN_ID, mShoppingItemId).findFirst();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edit_shopping_item, container, false);
        mStorageAdapter = StorageAdapter.get(getActivity());
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);
        mRootCl = (ConstraintLayout) v.findViewById(R.id.root_layout);
        mDescriptionTv = (TextView) v.findViewById(R.id.desc_tv);
        mBrandTv = (TextView) v.findViewById(R.id.brand_tv);
        mPriceEt = (EditText) v.findViewById(R.id.price_et);
        mQuantityEt = (EditText) v.findViewById(R.id.quantity_et);
        mIncreaseQtyBtn = (ImageButton) v.findViewById(R.id.increase_iv);
        mDecreaseQtyBtn = (ImageButton) v.findViewById(R.id.decrease_iv);
        mSaveBtn = (Button) v.findViewById(R.id.save_btn);
        mCurrencySymbolTv = (TextView) v.findViewById(R.id.currency_symbol_tv);
        mCurrencySymbolTv.setText(mStorageAdapter.getAppCurrencySymbol());
        populateUI();
        initSubscriptions();

        View.OnClickListener listener = (view) -> {
            int qty = Integer.parseInt(mQuantityEt.getText().toString());
            if (view == mIncreaseQtyBtn) {
                qty += 1;
            } else {

                if (qty > 1) {
                    qty -= 1;
                }
            }
            mQuantityEt.setText(String.format("%d", qty));
        };

        mIncreaseQtyBtn.setOnClickListener(listener);
        mDecreaseQtyBtn.setOnClickListener(listener);

        mSaveBtn.setOnClickListener((view) -> {
            if (mShoppingItem == null) {
                return;
            }
            int quantity = Integer.parseInt(mQuantityEt.getText().toString());
            double price = Double.parseDouble(mPriceEt.getText().toString());
            mRealm.beginTransaction();
            mShoppingItem.setItemQuantity(quantity);
            mShoppingItem.setItemPrice(price);
            mRealm.insertOrUpdate(mShoppingItem);
            mRealm.commitTransaction();
            if (mIsTablet) {
                EventBus.get().postEvent(HomeActivity.ShoppingListAction.ACTION_REFRESH_LIST);
            }
            showSaveMessage();
        });
        UiUtils.hideSoftKeyboard(getActivity());
        return v;
    }

    private void initSubscriptions() {

        mShoppingItemSubscription = EventBus.get().getObservable().subscribe((object) -> {

            if (object instanceof Long) {
                mShoppingItemId = (Long) object;
                fetchShoppingItemFromDb();
                populateUI();
            }

        });
    }

    private void populateUI() {
        if (mShoppingItem == null) {
            mRootCl.setVisibility(View.GONE);
            return;
        }
        mRootCl.setVisibility(View.VISIBLE);
        mDescriptionTv.setText(mShoppingItem.getItemDescription());
        mBrandTv.setText(mShoppingItem.getItemBrand());
        mPriceEt.setText(String.format("%.02f", mShoppingItem.getItemPrice()));
        mQuantityEt.setText(String.format("%d", mShoppingItem.getItemQuantity()));
    }

    private void showSaveMessage() {
        Snackbar.make(mRootCl, R.string.item_updated_successfully, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mShoppingItemSubscription != null) {
            mShoppingItemSubscription.unsubscribe();
            mShoppingItemSubscription = null;
        }
        mRealm.close();
    }
}
