package com.test.bighand;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.Until;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * UI automatic testing. The test cases tests the following <br/>
 * 1- Verify application package. </br>
 * 2- Deletes top two items from the list </br>
 * 3- Edits the top item in the list - by changing its quantity and price. </br>
 * <p>
 * Note that the application must be installed on the device before running the test cases
 *
 * @author Usman
 */

@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    public static final String APP_PACKAGE = "com.test.bighand";
    private UiDevice mDevice;
    private static final int LAUNCH_TIMEOUT = 5000;


    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the BigHand Test app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(APP_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(APP_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void testAppPackage() {
        Assert.assertEquals(APP_PACKAGE, mDevice.getCurrentPackageName());
    }

    @Test
    public void testItemsDeletion() throws UiObjectNotFoundException {

        UiObject2 recyclerView = mDevice.findObject(By.res(APP_PACKAGE, "shopping_list_rv"));
        List<UiObject2> mShoppingItems = recyclerView.getChildren();
        // long click on first item
        UiObject2 firstItem = mShoppingItems.get(0).findObject(By.res(APP_PACKAGE, "shopping_item_row_cl"));
        Rect rect = firstItem.getVisibleBounds();
        mDevice.swipe(rect.centerX(), rect.centerY(), rect.centerX(), rect.centerY(), 100);
        mDevice.waitForIdle(2000);
        // select the second item in the list by checking the checkbox
        UiObject2 secondItem = mShoppingItems.get(1).findObject(By.res(APP_PACKAGE, "shopping_item_row_cl"));
        UiObject2 checkbox = secondItem.findObject(By.res(APP_PACKAGE, "checkbox"));
        checkbox.click();
        mDevice.waitForIdle(2000);
        // press the delete button
        UiObject2 deleteButton = mDevice.findObject(By.res(APP_PACKAGE, "action_delete"));
        deleteButton.click();
        mDevice.waitForIdle(5000);
    }

    @Test
    public void testItemEdition() throws UiObjectNotFoundException {
        UiObject2 recyclerView = mDevice.findObject(By.res(APP_PACKAGE, "shopping_list_rv"));
        List<UiObject2> mShoppingItems = recyclerView.getChildren();
        // click on first item
        UiObject2 firstItem = mShoppingItems.get(0).findObject(By.res(APP_PACKAGE, "shopping_item_row_cl"));
        firstItem.click();
        mDevice.waitForWindowUpdate(APP_PACKAGE, LAUNCH_TIMEOUT);
        UiObject2 increaseQtyBtn = mDevice.findObject(By.res(APP_PACKAGE, "increase_iv"));
        // click to increase quantity
        increaseQtyBtn.click();
        mDevice.waitForIdle(1000);
        // click to increase quantity
        increaseQtyBtn.click();
        // update price
        UiObject2 priceEditText = mDevice.findObject(By.res(APP_PACKAGE, "price_et"));
        priceEditText.setText("9.99");
        mDevice.waitForIdle(2000);
        // press save button
        UiObject2 saveBtn = mDevice.findObject(By.res(APP_PACKAGE, "save_btn"));
        saveBtn.click();
        mDevice.waitForIdle(3000);
        // press back to close current activity
        UiObject2 backButton = mDevice.findObject(By.desc("Navigate up"));
        if (backButton != null) {
            backButton.click();
        }
        mDevice.waitForIdle(5000);

    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }


}
