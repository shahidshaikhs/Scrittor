package com.shahid.nid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import com.shahid.nid.Activties.AddNotesActivity;
import com.shahid.nid.Activties.MainActivity;
import com.shahid.nid.Activties.SignInActivity;
import com.shahid.nid.Activties.PasswordReset;
import com.shahid.nid.Activties.AboutActivity;
import com.shahid.nid.Utils.DbHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = {23, 28, 36}, application = NID.class)
public class ActivityRegressionTest {
    private NID app;
    private DbHelper db;
    @Before public void setUp() {
        app = (NID) RuntimeEnvironment.getApplication();
        app.getSharedPreferences(app.getString(R.string.MY_PREFS_USER_NAME), 0).edit().putBoolean("firstTime", true).commit();
        app.getSharedPreferences(app.getString(R.string.MY_PREFS_PASSWORD), 0).edit().clear().commit();
        app.getSharedPreferences(app.getString(R.string.MY_PREFS_FINGERPRINT), 0).edit().clear().commit();
        app.deleteDatabase("FeedReader.db"); app.deleteDatabase("CategoriesHelper.db");
        db = DbHelper.getInstance(app);
    }
    @After public void tearDown() { db.destroy(); }

    private void enablePin() {
        app.getSharedPreferences(app.getString(R.string.MY_PREFS_PASSWORD), 0).edit()
                .putString("password_check", "enable").putString("password", "1234").commit();
    }
    @Test public void backgroundingEditorSavesWithoutFinishingOrDuplicating() {
        ActivityController<AddNotesActivity> controller = Robolectric.buildActivity(AddNotesActivity.class).setup();
        AddNotesActivity activity = controller.get();
        ((EditText) activity.findViewById(R.id.note_title)).setText("My draft");
        controller.pause();
        assertFalse(activity.isFinishing());
        assertEquals(1, db.fetchAllNotesFromDb(null).size());
        int id = db.fetchAllNotesFromDb(null).get(0).getNoteID();
        controller.resume();
        ((EditText) activity.findViewById(R.id.note_description)).setText("Continued writing");
        controller.pause();
        assertEquals(1, db.fetchAllNotesFromDb(null).size());
        assertEquals(id, db.fetchAllNotesFromDb(null).get(0).getNoteID());
        assertEquals("Continued writing", db.fetchAllNotesFromDb(null).get(0).getNoteContent());
        controller.stop().destroy();
    }
    @Test public void forgedCheckExtraDoesNotUnlockLauncher() {
        enablePin();
        Intent intent = new Intent(app, MainActivity.class).putExtra("check", 1);
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class, intent).setup();
        assertFalse(app.isAuthenticated());
        Intent gate = shadowOf(controller.get()).getNextStartedActivity();
        assertNotNull(gate);
        assertEquals(SignInActivity.class.getName(), gate.getComponent().getClassName());
        controller.pause().stop().destroy();
    }
    @Test public void externalShareCannotBypassLockOrAutosaveBeforeUnlock() {
        enablePin();
        Intent share = new Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, "Shared draft");
        ActivityController<AddNotesActivity> controller = Robolectric.buildActivity(AddNotesActivity.class, share).setup();
        assertEquals(SignInActivity.class.getName(), shadowOf(controller.get()).getNextStartedActivity().getComponent().getClassName());
        controller.pause();
        assertTrue(db.fetchAllNotesFromDb(null).isEmpty());
        controller.stop().destroy();
    }
    @Test public void successfulPinUnlocksAndBackgroundRelocks() {
        enablePin();
        ActivityController<SignInActivity> controller = Robolectric.buildActivity(SignInActivity.class).setup();
        EditText input = controller.get().findViewById(R.id.app_password_edit_text);
        input.setText("9999"); assertFalse(app.isAuthenticated());
        input.setText("1234"); assertTrue(app.isAuthenticated());
        controller.pause().stop(); assertFalse(app.isAuthenticated());
        controller.destroy();
    }
    @Test public void cancelPinSetupDoesNotDisableExistingLock() {
        enablePin(); app.unlock();
        ActivityController<PasswordReset> controller = Robolectric.buildActivity(PasswordReset.class).setup();
        controller.get().findViewById(R.id.back_button).performClick();
        assertTrue(app.isLockEnabled());
        controller.pause().stop().destroy();
    }
    @Test public void legacyPinMigratesWithoutKeepingPlaintext() {
        enablePin();
        android.content.SharedPreferences prefs = app.getSharedPreferences(app.getString(R.string.MY_PREFS_PASSWORD), 0);
        assertTrue(com.shahid.nid.Utils.PinStore.verify(prefs, "1234"));
        assertFalse(prefs.contains("password"));
        assertTrue(com.shahid.nid.Utils.PinStore.verify(prefs, "1234"));
        assertFalse(com.shahid.nid.Utils.PinStore.verify(prefs, "9999"));
    }
    @Test public void repeatedWrongPinsAreTemporarilyThrottled() {
        enablePin();
        android.content.SharedPreferences prefs = app.getSharedPreferences(app.getString(R.string.MY_PREFS_PASSWORD), 0);
        for (int i = 0; i < 5; i++) assertFalse(com.shahid.nid.Utils.PinStore.verify(prefs, "9999"));
        assertFalse(com.shahid.nid.Utils.PinStore.verify(prefs, "1234"));
    }

    @Test public void privacyPolicyOpensTheCurrentUrlInTheBrowser() {
        ActivityController<AboutActivity> controller = Robolectric.buildActivity(AboutActivity.class).setup();
        controller.get().findViewById(R.id.privacy_policy).performClick();
        Intent browserIntent = shadowOf(controller.get()).getNextStartedActivity();
        assertEquals(Intent.ACTION_VIEW, browserIntent.getAction());
        assertEquals("https://www.shahidshaikh.com/scrittor-privacy-policy", browserIntent.getDataString());
        controller.pause().stop().destroy();
    }

    @Test public void aboutScreenOnlyShowsCurrentSocialLinks() {
        ActivityController<AboutActivity> controller = Robolectric.buildActivity(AboutActivity.class).setup();
        assertEquals(0, app.getResources().getIdentifier("textViewTelegram", "id", app.getPackageName()));
        assertEquals(0, app.getResources().getIdentifier("click_instagram", "id", app.getPackageName()));
        assertEquals(0, app.getResources().getIdentifier("click_google", "id", app.getPackageName()));
        controller.get().findViewById(R.id.click_twitter).performClick();
        Intent browserIntent = shadowOf(controller.get()).getNextStartedActivity();
        assertEquals("https://x.com/letshahid", browserIntent.getDataString());
        controller.pause().stop().destroy();
    }

}
