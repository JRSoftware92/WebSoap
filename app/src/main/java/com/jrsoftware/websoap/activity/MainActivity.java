package com.jrsoftware.websoap.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.controller.AppDataCenter;
import com.jrsoftware.websoap.controller.HistoryManager;
import com.jrsoftware.websoap.model.SiteEntry;
import com.jrsoftware.websoap.model.SiteTree;
import com.jrsoftware.websoap.services.HTMLSanitizationService;
import com.jrsoftware.websoap.settings.SettingsActivity;
import com.jrsoftware.websoap.util.AppUtils;
import com.jrsoftware.websoap.util.DialogUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Main Entrypoint for the application
 */
public class MainActivity extends AppCompatActivity {

    public static final String ARG_SITE = "com.jrsoftware.websoap.site";
    public static final String ARG_BOOKMARKS = "com.jrsoftware.websoap.bookmarks";
    public static final String ARG_HISTORY = "com.jrsoftware.websoap.history";

    private static final String LOG_TAG = "MAIN-ACTIVITY";

    private LocalBroadcastManager broadcastManager;

    private ProgressDialog loadingDialog;

    private WebView webView;
    private SearchView searchView;

    private AppDataCenter dataCenter;

    private boolean eraseForwardStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        dataCenter = new AppDataCenter(this);

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

        //Load Bookmarks and history if Requested
        Intent intent = getIntent();
        boolean loadBookmarks = true, loadHistory = true;
        SiteEntry site = null;
        if(intent != null){
            Bundle extras = intent.getExtras();
            if(extras != null){
                site = intent.getParcelableExtra(ARG_SITE);
                if(intent.hasExtra(ARG_BOOKMARKS)){
                    SiteTree bookmarks = intent.getParcelableExtra(ARG_BOOKMARKS);
                    HistoryManager history = intent.getParcelableExtra(ARG_HISTORY);
                    dataCenter.setBookmarks(bookmarks);
                    dataCenter.setSiteHistoryManager(history);
                    loadBookmarks = false;

                    trySaveBookmarks();
                }
                if(intent.hasExtra(ARG_HISTORY)){
                    HistoryManager history = intent.getParcelableExtra(ARG_HISTORY);
                    dataCenter.setSiteHistoryManager(history);
                    loadHistory = false;

                    Log.i(LOG_TAG, String.format("Parceled history: %d", history.getHistoryTree().size()));

                    trySaveWebHistory();
                }
            }
        }

        if(loadBookmarks)
            loadBookmarks = dataCenter.fileExists(getString(R.string.file_bookmarks), null);

        if(loadHistory)
            loadHistory = dataCenter.fileExists(getString(R.string.file_history), null);

        //Load Serialized Bookmarks if necessary
        if(loadBookmarks)
            tryLoadBookmarks();

        //Load Web History if necessary
        if(loadHistory) {
            tryLoadWebHistory();
            SiteTree history = dataCenter.getSiteHistory();
            Log.i(LOG_TAG, String.format("Loaded history: %d", history.size()));
        }

        //Load Site if requested
        if(site != null){
            sendSearchRequest(site.url(), false);
            return;
        }

        //Load Homepage
        SharedPreferences preferences = AppUtils.getPreferences(this);
        String homePage = preferences.getString(
                getString(R.string.pref_key_home_page),
                getString(R.string.pref_default_home_page)
        );
        sendSearchRequest(homePage, true);
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
        SiteEntry entry;
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_back:
                entry = dataCenter.previousSite();
                if(entry != null)
                    sendSearchRequest(entry.url(), false);
                return true;
            case R.id.action_forward:
                entry = dataCenter.nextSite();
                if(entry != null)
                    sendSearchRequest(entry.url(), false);
                return true;
            case R.id.action_refresh:
                entry = dataCenter.getLastRequestedSite();
                if(entry != null)
                    sendSearchRequest(entry.url(), false);
                else
                    AppUtils.showToastLong(this, "Unable to send refresh request due to unexpected error.");
                return true;
            case R.id.action_home:
                //Load Homepage
                SharedPreferences preferences = AppUtils.getPreferences(this);
                String homePage = preferences.getString(
                        getString(R.string.pref_key_home_page),
                        getString(R.string.pref_default_home_page)
                );
                sendSearchRequest(homePage, true);
                return true;
            case R.id.action_bookmark:
                entry = dataCenter.getLastRequestedSite();
                if(entry != null)
                    showNewBookmarkDialog(entry);
                else
                    AppUtils.showToastLong(this, "Unable to bookmark site due to unexpected error.");
                return true;
            case R.id.action_bookmarks:
                SiteTree bookmarks = dataCenter.getBookmarks();
                intent = new Intent(this, BookmarkListActivity.class);
                intent.putExtra(BookmarkListActivity.ARG_BOOKMARKS, (Parcelable)bookmarks);
                intent.putExtra(BookmarkListActivity.ARG_HISTORY, dataCenter.getHistoryManager());

                startActivity(intent);
                return true;
            case R.id.action_history:
                intent = new Intent(this, WebHistoryActivity.class);
                intent.putExtra(BookmarkListActivity.ARG_HISTORY, dataCenter.getHistoryManager());

                startActivity(intent);
                return true;
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                //pass the event to parent
                return super.onOptionsItemSelected(item);
        }
    }

    private void showNewBookmarkDialog(final SiteEntry entry) {
        final EditText editText = new EditText(this);
        final Context context = this;
        editText.setHint(R.string.hint_new_bookmark_title);
        AlertDialog dialog = DialogUtils.getTextInputDialog(
                this, R.string.title_new_bookmark, editText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Editable editable = editText.getText();
                        if(editable != null && editable.length() > 0){
                            String title = editable.toString();
                            dataCenter.addBookmark(entry.url(), title);
                            trySaveBookmarks();

                            AppUtils.showToastShort(context, "Bookmark Created!");
                        }
                        else{
                            AppUtils.showToastShort(context, "Please enter a name for your bookmark.");
                        }
                    }
                }
        );

        dialog.show();
    }

    /**
     * Submits an html request to the HTML Sanitization Service
     * @param url - String url to be loaded
     */
    protected void sendSearchRequest(String url, boolean eraseForwardStack){
        //Validates Search Request
        if(url == null || url.length() < 1)
            return;

        this.eraseForwardStack = eraseForwardStack;

        //Display Loading Dialog while search results are being sent
        loadingDialog.show();

        //Sends the Search Intent to the HTMLSanitizationService
        HTMLSanitizationService.startActionSearch(this, url);
    }

    private void displayHTML(String url, String html) {
        if(html == null || html.length() < 1)
            return;

        if(webView == null)
            webView = (WebView) findViewById(R.id.webView);

        SharedPreferences preferences = AppUtils.getPreferences(this);
        boolean enableJavascript = preferences.getBoolean(
                getString(R.string.pref_key_allow_javascript), false
        );
        webView.getSettings().setJavaScriptEnabled(enableJavascript);
        webView.loadDataWithBaseURL(url, html, "text/html", "UTF-8", url);
    }

    private void trySaveBookmarks(){
        try {
            dataCenter.saveBookmarks();
        }
        catch(IOException io){
            Log.e(LOG_TAG, "Unexpected Exception has occurred while saving Bookmarks File.");
        }
    }

    private void trySaveWebHistory(){
        try {
            SharedPreferences pref = AppUtils.getPreferences(this);
            boolean rememberHistory = pref.getBoolean(getString(R.string.pref_key_remember_history), true);
            if(rememberHistory)
                dataCenter.saveHistory();
        }
        catch(IOException io){
            Log.e(LOG_TAG, "Unexpected Exception has occurred while saving History File.");
        }
    }

    private void tryLoadBookmarks(){
        try{
            dataCenter.loadBookmarks();
        }
        catch(FileNotFoundException fnfe){
            Log.w(LOG_TAG, "Bookmarks File not located.");
            Log.e(LOG_TAG, fnfe.getMessage());
            fnfe.printStackTrace();
        }
        catch(ClassNotFoundException cnfe){
            Log.e(LOG_TAG, "Unable to load SiteList");
            Log.e(LOG_TAG, cnfe.getMessage());
            cnfe.printStackTrace();
        }
        catch(IOException io){
            Log.e(LOG_TAG, "Unexpected Exception has occurred while loading Bookmarks File.");
            String msg = io.getMessage();
            if(msg != null)
                Log.e(LOG_TAG, io.getMessage());
            io.printStackTrace();
        }
    }

    private void tryLoadWebHistory(){
        try{
            dataCenter.loadHistory();
        }
        catch(FileNotFoundException fnfe){
            Log.w(LOG_TAG, "History File not located.");
            Log.e(LOG_TAG, fnfe.getMessage());
            fnfe.printStackTrace();
        }
        catch(ClassNotFoundException cnfe){
            Log.e(LOG_TAG, "Unable to load SiteList");
            Log.e(LOG_TAG, cnfe.getMessage());
            cnfe.printStackTrace();
        }
        catch(IOException io){
            Log.e(LOG_TAG, "Unexpected Exception has occurred while loading History File.");
            String msg = io.getMessage();
            if(msg != null)
                Log.e(LOG_TAG, io.getMessage());
            io.printStackTrace();
        }
    }

    /**
     * Handles HTML Sanitizer Search Service Responses
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(HTMLSanitizationService.BROADCAST_RESULTS)) {
                Bundle resultBundle = intent.getBundleExtra(HTMLSanitizationService.RESULT_BUNDLE);
                int resultCode = resultBundle.getInt(HTMLSanitizationService.RESULT_CODE,
                        HTMLSanitizationService.EVENT_RESPONSE_EXCEPTION);
                String html = resultBundle.getString(HTMLSanitizationService.RESULT_SANITIZED);
                String url = resultBundle.getString(HTMLSanitizationService.RESULT_URL);
                String refUrl = url;
                String title = url;

                //Check the result code to determine how to handle the intent
                switch(resultCode){
                    case HTMLSanitizationService.EVENT_REQUEST_SUCCESSFUL:
                        title = resultBundle.getString(HTMLSanitizationService.RESULT_TITLE);
                        dataCenter.updateSiteTitle(url, title);
                        setTitle(AppUtils.concatWithEllipsis(title, 24));
                        break;
                    case HTMLSanitizationService.EVENT_RESPONSE_BAD:
                        AppUtils.showToastLong(context, "Request Failed.");
                        refUrl = null;
                        break;
                    case HTMLSanitizationService.EVENT_NETWORK_UNAVAILABLE:
                        AppUtils.showToastShort(context, "Network is currently unavailable.");
                        refUrl = null;
                        break;
                    case HTMLSanitizationService.EVENT_RESPONSE_UNSUCCESSFUL:
                        AppUtils.showToastLong(context, "Response Unsuccessful.");
                        refUrl = null;
                        break;
                    case HTMLSanitizationService.EVENT_RESPONSE_EXCEPTION:
                    default:
                        AppUtils.showToastLong(context, "Error has occurred with response.");
                        refUrl = null;
                        break;
                }

                searchView.setQuery(url, false);
                //Saves the url in the history manager
                if(!url.equals(dataCenter.getLastRequestedURL()));
                    dataCenter.setCurrentSite(url, title, eraseForwardStack);

                Log.i(LOG_TAG, String.format("Found %d history elements.", dataCenter.getSiteHistory().size()));

                trySaveWebHistory();

                //Display Sanitized HTML
                displayHTML(refUrl, html);
                //Dismiss loading dialog
                loadingDialog.dismiss();
            }
        }
    };

    private WebViewClient webViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            sendSearchRequest(url, true);
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
            sendSearchRequest(query, true);
            return true;
        }
    };
}
