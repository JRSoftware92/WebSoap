package com.jrsoftware.websoap.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.services.HTMLSanitizationService;
import com.jrsoftware.websoap.settings.SettingsActivity;
import com.jrsoftware.websoap.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    private LocalBroadcastManager broadcastManager;

    private ProgressDialog loadingDialog;

    private WebView webView;
    private SearchView searchView;

    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        //fragmentManager = getSupportFragmentManager();
        broadcastManager = LocalBroadcastManager.getInstance(this);

        //Registers the Broadcast Receiver for the Search Service
        IntentFilter filter = new IntentFilter();
        filter.addAction(HTMLSanitizationService.BROADCAST_RESULTS);
        broadcastManager.registerReceiver(broadcastReceiver, filter);

        //Initialize WebView
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(webViewClient);

        //Initialize SearchView
        searchView = (SearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(searchListener);

        //Initialize Loading Dialog
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading Requested Page\u2026");
        loadingDialog.setCancelable(false);

        //Load Homepage
        SharedPreferences preferences = AppUtils.getPreferences(this);
        String homePage = preferences.getString(
                getString(R.string.pref_key_home_page),
                getString(R.string.pref_default_home_page)
        );
        searchView.setQuery(homePage, false);
        sendSearchRequest(homePage);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                sendSearchRequest(currentUrl);
                return true;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                //pass the event to parent
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Submits an html request to the HTML Sanitization Service
     * @param url - String url to be loaded
     */
    protected void sendSearchRequest(String url){
        Log.d("MainActivity", "sendSearchRequest");
        //Validates Search Request
        if(url == null || url.length() < 1)
            return;

        if(!url.startsWith(AppUtils.HTTP)
                && !url.startsWith(AppUtils.HTTPS)
                && !url.startsWith(AppUtils.FTP)){
            SharedPreferences preferences = AppUtils.getPreferences(this);
            boolean forceHTTPS = preferences.getBoolean(getString(R.string.pref_key_force_https), true);
            if(forceHTTPS)
                url = AppUtils.HTTPS + url;
            else
                url = AppUtils.HTTP + url;

            searchView.setQuery(url, false);
        }

        Log.d("MainActivity", String.format("URL: %s", url));
        //Saves the url for refresh
        currentUrl = url;

        //Display Loading Dialog while search results are being sent
        loadingDialog.show();

        //Sends the Search Intent to the HTMLSanitizationService
        Log.d("MainActivity", "Sending Search Intent");
        HTMLSanitizationService.startActionSearch(this, url);
    }

    private void displayHTML(String html) {
        Log.d("MainActivity", "displayHTML");
        if(html == null || html.length() < 1)
            return;

        if(webView == null)
            webView = (WebView) findViewById(R.id.webView);

        SharedPreferences preferences = AppUtils.getPreferences(this);
        boolean enableJavascript = preferences.getBoolean(
                getString(R.string.pref_key_allow_javascript), false
        );
        webView.getSettings().setJavaScriptEnabled(enableJavascript);

        webView.loadDataWithBaseURL("", html, "text/html", "UTF-8", "");
    }

    /**
     * Handles HTML Sanitizer Search Service Responses
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HTMLSanitizationService.BROADCAST_RESULTS)) {
                int resultCode = intent.getIntExtra(HTMLSanitizationService.RESULT_CODE,
                        HTMLSanitizationService.EVENT_RESPONSE_EXCEPTION);

                //Check the result code to determine how to handle the intent
                switch(resultCode){
                    case HTMLSanitizationService.EVENT_REQUEST_SUCCESSFUL:
                        Bundle extras = intent.getExtras();
                        String html = extras.getString(HTMLSanitizationService.RESULT_SANITIZED);

                        //Dismiss loading dialog and display images
                        loadingDialog.dismiss();
                        displayHTML(html);
                        break;
                    case HTMLSanitizationService.EVENT_REQUEST_FAILED:
                        AppUtils.showToastLong(context, "Request Failed.");
                        break;
                    case HTMLSanitizationService.EVENT_NETWORK_UNAVAILABLE:
                        AppUtils.showToastShort(context, "Network is currently unavailable.");
                        break;
                    case HTMLSanitizationService.EVENT_RESPONSE_UNSUCCESSFUL:
                        AppUtils.showToastLong(context, "Response Unsuccessful.");
                        break;
                    case HTMLSanitizationService.EVENT_RESPONSE_EXCEPTION:
                    default:
                        AppUtils.showToastLong(context, "Error has occurred with response.");
                        break;
                }
            }
        }
    };

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            searchView.setQuery(url, false);
            sendSearchRequest(url);
            return true;
        }
    };

    private SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextChange(String newText) {
            return true;
        }

        @Override
        public boolean onQueryTextSubmit(String query) {
            sendSearchRequest(query);
            return true;
        }
    };
}
