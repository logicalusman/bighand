package com.test.bighand.ui.fragment;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.test.bighand.R;
import com.test.bighand.db.ShoppingItem;
import com.test.bighand.db.StorageAdapter;
import com.test.bighand.ui.EventBus;
import com.test.bighand.ui.activity.HomeActivity;
import com.test.bighand.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;
import rx.Subscription;

/**
 * This fragment can be used as sub-container into an activity, depending on the UI requirement. The views contained in
 * this fragment uses ConstraintLayout that adjust themselves relative to its root container, they are rendered in -
 * this makes its views adjustable on large screen sizes. The UI of this fragment consists of a recycler view (list)
 * of shopping items - see ShoppingItem.java. When user interacts with the list items, such as deleting an item, it
 * posts them as list operation events - see ListOperations; which is an inner class. Interested parties, such as
 * activities containing this fragment can subscribe for them.
 *
 * @author Usman
 */
public class ShoppingListFragment extends Fragment {

    /**
     * ListOperations is posted by this fragment to notify data/change to interested parties, such as the containing activity.
     */
    public static class ListOperations {
        /**
         * true if user has enabled row deletion.
         */
        public boolean enableMultiSelection;

        /**
         * Number of rows selected by the user for deletion.
         */
        public int numSelectedRows;

        /**
         * ID of the shopping item selected by the user.
         */
        public long selectedShoppingItemId;
    }

    private static String TAG = "ShoppingListFragment";
    private RecyclerView mShoppingListRv;
    private TextView mEmptyListTv;
    private List<ShoppingItem> mShoppingItems;
    private ShoppingListAdapter mShoppingListAdapter;
    private StorageAdapter mStorageAdapter;
    private boolean mLongClickEnabled = false;
    private List<ShoppingItem> mSelectedItems;
    private Subscription mToolbarActionSubscription;
    private Realm mRealm;
    private LinearLayoutManager mLinearLayoutManager;
    private int mLongClickedPosition = -1;
    private boolean mIsTablet;
    private int mSelectedRow;
    private boolean mFirstLaunch;


    public ShoppingListFragment() {
        mRealm = Realm.getDefaultInstance();
        mFirstLaunch = true;
    }

    public static ShoppingListFragment newInstance() {
        ShoppingListFragment fragment = new ShoppingListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        mStorageAdapter = StorageAdapter.get(getActivity());
        mShoppingListRv = (RecyclerView) v.findViewById(R.id.shopping_list_rv);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mShoppingListRv.setLayoutManager(mLinearLayoutManager);
        mEmptyListTv = (TextView) v.findViewById(R.id.empty_list_tv);
        mIsTablet = getResources().getBoolean(R.bool.is_tablet);
        initSubscriptions();

        return v;
    }

    private void fetchShoppingItemsFromDb() {
        mShoppingItems = mRealm.where(ShoppingItem.class).findAll().sort(ShoppingItem.COLUMN_DATE_TIME, Sort.ASCENDING);
    }

    private void deleteShoppingItemsInDB() {

        if (mSelectedItems != null && !mSelectedItems.isEmpty()) {

            mRealm.beginTransaction();
            for (ShoppingItem item : mSelectedItems) {
                mRealm.where(ShoppingItem.class).equalTo(ShoppingItem.COLUMN_ID, item.getId()).findAll().deleteAllFromRealm();
            }
            mRealm.commitTransaction();
        }

    }

    private void hideShoppingList(boolean hide) {

        if (hide) {
            mEmptyListTv.setVisibility(View.VISIBLE);
            mShoppingListRv.setVisibility(View.GONE);
        } else {
            mEmptyListTv.setVisibility(View.GONE);
            mShoppingListRv.setVisibility(View.VISIBLE);
        }

    }

    private void setAdapterToList(boolean refreshOnly) {

        if (mShoppingItems == null || mShoppingItems.isEmpty()) {
            hideShoppingList(true);
            return;
        } else {
            hideShoppingList(false);
        }

        if (refreshOnly) {
            mShoppingListAdapter.notifyDataSetChanged();
        } else {
            mShoppingListAdapter = new ShoppingListAdapter();
            mShoppingListRv.setAdapter(mShoppingListAdapter);
        }


    }

    private void initSubscriptions() {
        //Subscribing for the actions posted by the concerned parties - parent activity, containing this fragment.
        mToolbarActionSubscription = EventBus.get().getObservable().subscribe((object) -> {

            if (object instanceof HomeActivity.ShoppingListAction) {
                HomeActivity.ShoppingListAction action = (HomeActivity.ShoppingListAction) object;
                if (action == HomeActivity.ShoppingListAction.ACTION_REFRESH_LIST) {
                    fetchShoppingItemsFromDb();
                    setAdapterToList(true);
                    return;
                }
                if (action == HomeActivity.ShoppingListAction.ACTION_DELETE_ITEMS) {
                    // Just be super safe - Crashes look ugly
                    if (mSelectedItems == null || mSelectedItems.isEmpty()) {
                        return;
                    } else {
                        // delete the selected items
                        deleteShoppingItemsInDB();
                        // fetch the remaining ones, the new list will show them.
                        fetchShoppingItemsFromDb();
                        if (mShoppingItems == null || mShoppingItems.isEmpty()) {
                            postItemSelectionEvent(-1);
                        }
                    }
                }
                // reset the multi-selection vars
                mLongClickEnabled = false;
                mLongClickedPosition = -1;
                mSelectedItems = null;
                setAdapterToList(true);
            }

        });
    }

    private class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.VH> {

        @Override
        public VH onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.shopping_item_row, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int pos) {

            ShoppingItem item = mShoppingItems.get(pos);
            holder.mDescriptionTv.setText(item.getItemDescription());
            holder.mBrandTv.setText(item.getItemBrand());
            holder.mQuantityTv.setText(String.format("%d", item.getItemQuantity()));
            holder.mPriceTv.setText(String.format("%s %.02f", mStorageAdapter.getAppCurrencySymbol(), item.getItemPrice()));

            if (mIsTablet) {
                if (pos == mSelectedRow) {
                    holder.mRootCv.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorListRowSelected));
                } else {
                    holder.mRootCv.setCardBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorListRow));
                }
            }

            if (mLongClickEnabled) {
                holder.mCheckBox.setVisibility(View.VISIBLE);
                if (pos == mLongClickedPosition) {
                    holder.mCheckBox.setChecked(true);
                }
            } else {
                holder.mCheckBox.setVisibility(View.GONE);
                holder.mCheckBox.setChecked(false);
            }
        }

        @Override
        public int getItemCount() {

            if (mShoppingItems == null) {
                return 0;
            } else {
                return mShoppingItems.size();
            }
        }

        class VH extends RecyclerView.ViewHolder {

            TextView mDescriptionTv, mBrandTv, mQuantityTv, mPriceTv;
            ImageView mBarcodeIv, mShareIv;
            ConstraintLayout mContentRootCl;
            CheckBox mCheckBox;
            CardView mRootCv;


            public VH(View v) {
                super(v);

                mRootCv = (CardView) v.findViewById(R.id.root_layout_cv);
                mContentRootCl = (ConstraintLayout) v.findViewById(R.id.shopping_item_row_cl);
                mCheckBox = (CheckBox) v.findViewById(R.id.checkbox);
                mDescriptionTv = (TextView) v.findViewById(R.id.description_tv);
                mBrandTv = (TextView) v.findViewById(R.id.brand_tv);
                mQuantityTv = (TextView) v.findViewById(R.id.quantity_tv);
                mPriceTv = (TextView) v.findViewById(R.id.price_tv);
                mBarcodeIv = (ImageView) v.findViewById(R.id.barcode_iv);
                mShareIv = (ImageView) v.findViewById(R.id.share_iv);

                mCheckBox.setOnClickListener((view) -> {

                    int pos = getAdapterPosition();
                    boolean isChecked = mCheckBox.isChecked();
                    captureAndPostMultiSelectionEvent(pos, isChecked);
                });


                mBarcodeIv.setOnClickListener((view) -> {
                    int pos = getAdapterPosition();
                    UiUtils.showDialog(getActivity(), getString(R.string.barcode), mShoppingItems.get(pos).getItemBarcode());

                });

                mShareIv.setOnClickListener((view) -> {
                    UiUtils.showDialog(getActivity(), null, getString(R.string.feature_not_implemented));
                });

                mContentRootCl.setOnClickListener((view) -> {
                    if (mLongClickEnabled) {
                        return;
                    }

                    int pos = getAdapterPosition();
                    if (mIsTablet) {
                        notifyItemChanged(mSelectedRow);
                        notifyItemChanged(pos);
                    }
                    mSelectedRow = pos;
                    // Once a shopping item (row) is clicked, post the item's id for subscribers
                    postItemSelectionEvent(pos);

                });

                mContentRootCl.setOnLongClickListener((view) -> {
                    // no need to do anything if long click is pre-enabled
                    if (mLongClickEnabled) {
                        return false;
                    }
                    mLongClickEnabled = true;
                    mLongClickedPosition = getAdapterPosition();
                    captureAndPostMultiSelectionEvent(mLongClickedPosition, true);
                    setAdapterToList(true);
                    return false;
                });

            }
        }


    }

    private void postItemSelectionEvent(int pos) {
        ListOperations lo = new ListOperations();
        if (pos >= 0) {
            lo.selectedShoppingItemId = mShoppingItems.get(pos).getId();
        } else {
            lo.selectedShoppingItemId = pos;
        }
        EventBus.get().postEvent(lo);
    }

    private void captureAndPostMultiSelectionEvent(int pos, boolean isChecked) {
        if (mSelectedItems == null) {
            // Storage conscious - the selection can not grow more than the original shopping items' list.
            mSelectedItems = new ArrayList<>(mShoppingItems.size());
        }
        if (isChecked) {
            mSelectedItems.add(mShoppingItems.get(pos));
        } else {
            mSelectedItems.remove(mShoppingItems.get(pos));
        }
        ListOperations lo = new ListOperations();
        lo.enableMultiSelection = true;
        lo.numSelectedRows = mSelectedItems.size();
        EventBus.get().postEvent(lo);

    }

    @Override
    public void onResume() {
        super.onResume();
        fetchShoppingItemsFromDb();
        setAdapterToList(false);

        if (mFirstLaunch && mIsTablet && mShoppingItems != null && !mShoppingItems.isEmpty()) {
            postItemSelectionEvent(0);
            mFirstLaunch = false;
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mToolbarActionSubscription != null) {
            mToolbarActionSubscription.unsubscribe();
            mToolbarActionSubscription = null;
        }
        mRealm.close();
    }
}

