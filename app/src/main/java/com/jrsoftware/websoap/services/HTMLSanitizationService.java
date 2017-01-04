package com.jrsoftware.websoap.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.util.AppUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread. Searches and sanitizes HTML Pages for safe phone display
 */
public class HTMLSanitizationService extends IntentService {

    public static final int EVENT_REQUEST_SUCCESSFUL = 0;
    public static final int EVENT_REQUEST_FAILED = -1;
    public static final int EVENT_RESPONSE_UNSUCCESSFUL = -2;
    public static final int EVENT_NETWORK_UNAVAILABLE = -3;
    public static final int EVENT_RESPONSE_EXCEPTION = -4;

    public static final String BROADCAST_RESULTS = "com.jrsoftware.websoap.receiver";
    public static final String RESULT_SANITIZED = "com.jrsoftware.websoap.services.result.sanitized";
    public static final String RESULT_CODE = "com.jrsoftware.websoap.servuces.result.resultcode";
    public static final String ARG_URL = "com.jrsoftware.websoap.services.arg.url";

    private static final String SERVICE_TAG = "HTMLSanitizationService";
    private static final String ACTION_SEARCH = "com.jrsoftware.websoap.services.action.SEARCH";

    public HTMLSanitizationService() {
        super(SERVICE_TAG);
    }

    /**
     * Starts this service to perform a web search with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSearch(Context context, String param1) {
        Intent intent = new Intent(context, HTMLSanitizationService.class);
        intent.setAction(ACTION_SEARCH);
        intent.putExtra(ARG_URL, param1);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEARCH.equals(action)) {
                final String url = intent.getStringExtra(ARG_URL);
                handleActionSearch(url);
            }
        }
    }

    /**
     * Handle HTML download and sanitize in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSearch(String url) {
        final Context context = getApplicationContext();
        SharedPreferences preferences = AppUtils.getPreferences(this);
        try {
            if(AppUtils.isNetworkAvailable(context)){
                Log.d(SERVICE_TAG, "Network is Available. Retrieving Document\u2026");
                Document doc = Jsoup.connect(url).get();
                Log.d(SERVICE_TAG, "Connection Successful! Converting to String\u2026");
                String html = doc.html();

                Log.d(SERVICE_TAG, "Conversion Successful! Cleaning via whitelist\u2026");
                String safeHTML;
                String whitelistType = preferences.getString(getString(R.string.pref_key_whitelist_type), "basic");
                Log.d(SERVICE_TAG, String.format("Whitelist Type: %s", whitelistType));
                switch(whitelistType){
                    case "none":
                        safeHTML = Jsoup.clean(html, Whitelist.none());
                        break;
                    case "simple-text":
                        safeHTML = Jsoup.clean(html, Whitelist.simpleText());
                        break;
                    case "basic-with-images":
                        safeHTML = Jsoup.clean(html, Whitelist.basicWithImages());
                        break;
                    case "relaxed":
                        safeHTML = Jsoup.clean(html, Whitelist.relaxed());
                        break;
                    case "basic":
                    default:
                        safeHTML = Jsoup.clean(html, Whitelist.basic());
                }

                Log.d(SERVICE_TAG, "Sanitization Successful! Publishing to Broadcaster");
                publishResults(safeHTML, EVENT_REQUEST_SUCCESSFUL);
            }
            else{       //Network Unavailable
                Log.e(SERVICE_TAG, "Network is Currently Unavailable.");
                publishResults(null, EVENT_NETWORK_UNAVAILABLE);
            }
        }
        catch(IOException io){
            Log.e(SERVICE_TAG, "An IOException has occurred");
            Log.e(SERVICE_TAG, io.getMessage());
            publishResults(io.getMessage(), EVENT_RESPONSE_EXCEPTION);
        }
    }

    private void publishResults(String sanitizedHTML, int resultCode) {
        Intent intent = new Intent(BROADCAST_RESULTS);
        intent.putExtra(RESULT_SANITIZED, sanitizedHTML);
        intent.putExtra(RESULT_CODE, resultCode);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
