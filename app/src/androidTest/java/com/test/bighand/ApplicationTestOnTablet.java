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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Test cases contained in this class are merely to run on the tablet.
 * Note that the application must be installed on the device before running the test cases
 *
 * @author Usman
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTestOnTablet {
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
    public void testSingleItemDeletion() throws UiObjectNotFoundException {

        UiObject2 recyclerView = mDevice.findObject(By.res(APP_PACKAGE, "shopping_list_rv"));
        List<UiObject2> mShoppingItems = recyclerView.getChildren();
        // long click on first item
        UiObject2 firstItem = mShoppingItems.get(0).findObject(By.res(APP_PACKAGE, "shopping_item_row_cl"));
        Rect rect = firstItem.getVisibleBounds();
        mDevice.swipe(rect.centerX(), rect.centerY(), rect.centerX(), rect.centerY(), 100);
        mDevice.waitForIdle(2000);
        // press the delete button
        UiObject2 deleteButton = mDevice.findObject(By.res(APP_PACKAGE, "action_delete"));
        deleteButton.click();
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
