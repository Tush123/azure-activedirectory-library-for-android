
package com.microsoft.adal.test;

import android.app.Instrumentation.ActivityMonitor;
import android.app.Instrumentation.ActivityResult;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.microsoft.adal.AuthenticationActivity;
import com.microsoft.adal.AuthenticationResult.AuthenticationStatus;
import com.microsoft.adal.testapp.MainActivity;
import com.microsoft.adal.testapp.R;

;
/**
 * This requires device to be connected to not deal with Inject_events security exception.
 * UI functional tests that enter credentials to test token processing end to end.
 * 
 * @author omercan
 */
public class AuthenticationActivityInstrumentationTests extends
        ActivityInstrumentationTestCase2<MainActivity> {

    private static final String TAG = "AuthenticationActivityInstrumentationTests";

    private MainActivity activity;

    private static String TEST_KEY_USERNAME = "demo@omercantest.onmicrosoft.com";

    private static String TEST_KEY_PASSWORD = "Jink1234";

    /**
     * until page content has something about login page
     */
    private static int PAGE_LOAD_TIMEOUT_SECONDS = 6;

    public AuthenticationActivityInstrumentationTests() {
        super(MainActivity.class);
        activity = null;
    }

    public AuthenticationActivityInstrumentationTests(Class<MainActivity> activityClass) {
        super(activityClass);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(false);
        activity = getActivity();
    }

    /**
     * clear tokens and then ask for token.
     * 
     * @throws Exception
     */
    public void testAcquireTokenAfterReset() throws Exception {

        // ACtivity runs at main thread. Test runs on different thread
        Log.d(TAG, "testAcquireTokenAfterReset starting...");
        // add monitor to check for the auth activity
        final ActivityMonitor monitor = getInstrumentation().addMonitor(
                AuthenticationActivity.class.getName(), null, false);

        // press clear all button to clear tokens and cookies
        Button btnResetToken = (Button)activity.findViewById(R.id.buttonReset);
        Button btnGetToken = (Button)activity.findViewById(R.id.buttonGetToken);
        final TextView textViewStatus = (TextView)activity.findViewById(R.id.textViewStatus);

        // TouchUtils handles the sync with the main thread internally
        TouchUtils.clickView(this, btnResetToken);

        // get token
        TouchUtils.clickView(this, btnGetToken);

        Thread.sleep(1000);
        Log.d(TAG, "testAcquireTokenAfterReset status text:" + textViewStatus.getText().toString());
        assertEquals("Token action", "Getting token...", textViewStatus.getText().toString());

        // Wait 4 secs to start activity and loading the page
        AuthenticationActivity startedActivity = (AuthenticationActivity)monitor
                .waitForActivityWithTimeout(5000);
        assertNotNull(startedActivity);

        Log.d(TAG, "Sleeping until it gets login page");
        sleepUntilLoginDisplays(startedActivity);

        Log.d(TAG, "Entering credentials to login page");
        enterCredentials(startedActivity);

        // wait for the page to set result
        waitUntil(new ResponseVerifier() {
            @Override
            public boolean hasCondition() throws IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
                ActivityResult result = monitor.getResult();
                return result != null;
            }
        });

        waitUntil(new ResponseVerifier() {
            @Override
            public boolean hasCondition() throws IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
                String tokenMsg = (String)textViewStatus.getText();
                return tokenMsg.contains("Status:");
            }
        });

        String tokenMsg = (String)textViewStatus.getText();
        Log.d(TAG, "Status:" + tokenMsg);
        assertTrue("Token status", tokenMsg.contains("Status:" + AuthenticationStatus.Succeeded));

    }

    private void enterCredentials(AuthenticationActivity startedActivity)
            throws InterruptedException {

        // Get Webview to enter credentials for testing
        WebView webview = (WebView)startedActivity.findViewById(com.microsoft.adal.R.id.webView1);
        assertNotNull("Webview is not null", webview);

        webview.requestFocus();
        // Send username
        Thread.sleep(500);
        getInstrumentation().sendStringSync(TEST_KEY_USERNAME);
        Thread.sleep(1000); // wait for redirect script
        sendKeys(KeyEvent.KEYCODE_TAB);
        getInstrumentation().sendStringSync(TEST_KEY_PASSWORD);
        Thread.sleep(300);
        
        // Enter event sometimes is failing to submit form.
        sendKeys(KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_ENTER);
    }

    private void sleepUntilLoginDisplays(final AuthenticationActivity startedActivity)
            throws InterruptedException, IllegalArgumentException, NoSuchFieldException,
            IllegalAccessException {

        Log.d(TAG, "sleepUntilLoginDisplays");

        waitUntil(new ResponseVerifier() {
            @Override
            public boolean hasCondition() throws IllegalArgumentException, NoSuchFieldException,
                    IllegalAccessException {
                return hasLoginPage(getLoginPage(startedActivity));
            }
        });
    }

    private void waitUntil(ResponseVerifier item) throws InterruptedException,
            IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        int waitcount = 0;
        Log.d(TAG, "wait start...");
        while (waitcount < PAGE_LOAD_TIMEOUT_SECONDS) {

            if (item.hasCondition()) {
                Log.d(TAG, "waitUntil done");
                break;
            }

            Thread.sleep(1000);
            waitcount++;
        }
    }

    interface ResponseVerifier {
        boolean hasCondition() throws IllegalArgumentException, NoSuchFieldException,
                IllegalAccessException;
    }

    /**
     * Login page content is written to the script object with javascript
     * injection
     * 
     * @param startedActivity
     * @return
     * @throws IllegalArgumentException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private String getLoginPage(AuthenticationActivity startedActivity)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object scriptInterface = ReflectionUtils.getFieldValue(startedActivity, "mScriptInterface");

        Object content = ReflectionUtils.getFieldValue(scriptInterface, "mHtml");

        // skip empty page
        if (content != null
                && !content.toString().equalsIgnoreCase("<html><head></head><body></body></html>"))
            return content.toString();

        return null;
    }

    /**
     * this can change based on login page implementation
     * 
     * @param htmlContent
     * @return
     */
    private boolean hasLoginPage(String htmlContent) {
        return htmlContent != null && !htmlContent.isEmpty() && htmlContent.contains("password");
    }
}