package com.test.bighand.ui.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.test.bighand.R;
import com.test.bighand.db.StorageAdapter;


/**
 * Contains functionality that can be shared across activities, avoid writing boilerplate code.
 *
 * @author Usman
 */
public class CommonActivity extends AppCompatActivity {


    private String TAG = "CommonActivity";
    private ProgressDialog mProcessingDialog;
    protected boolean mHomePressHandledByChildActivity;
    private StorageAdapter mStorageAdatper;


    /**
     * Shows a processing dialog. Child activities can call it when doing a background task.
     * This dialog can be dismissed by calling dismissProcessingDialog()
     *
     * @param message
     */
    public void showProcessingDialog(@NonNull String message) {

        mProcessingDialog = new ProgressDialog(this);
        mProcessingDialog.setMessage(message);
        mProcessingDialog.setCancelable(true);
        mProcessingDialog.show();
    }

    /**
     * Dismisses the showing dialog - shown via  showProcessingDialog(). Calling this method
     * on a non showing dialog will not cause any issue, since the call simply returns in such case.
     */
    public void dismissProcessingDialog() {
        if (mProcessingDialog != null && mProcessingDialog.isShowing()) {
            try {
                mProcessingDialog.dismiss();
                mProcessingDialog = null;
            } catch (Exception ignored) {
            }
        }
    }


    /**
     * Setup the toolbar to show title, back (arrow) button. Activities are advised to call
     * this metho on onCreate().
     *
     * @param title
     * @param showBackButton
     */
    public void setupToolbar(@NonNull String title, boolean showBackButton) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
        getSupportActionBar().setTitle(title);
    }

    /**
     * Updates the title and shows/hides the back (arrow) button.
     *
     * @param title
     * @param showBackButton
     */
    public void updateToolbar(@NonNull String title, boolean showBackButton) {
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(showBackButton);
    }

    /**
     * Default implementation, which finishes the current activity when back (arrow) button is pressed on toolbar.
     * A Child activity intending to handle click on back (arrow) button can set super.mHomePressHandledByChildActivity
     * to true and override this method.
     *
     * @param item
     * @return
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * If child of this activity wants to handle the back (arrow) button press on toolbar,
         * it can override this method and set mHomePressHandledByChildActivity to true.
         */
        if (!mHomePressHandledByChildActivity && item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onContextItemSelected(item);
    }

    /**
     * A handy method to return an instance of storage adapter.
     *
     * @return
     */
    public StorageAdapter getStorageAdapter() {
        if (mStorageAdatper == null) {
            mStorageAdatper = StorageAdapter.get(this);
        }
        return mStorageAdatper;
    }

}