package com.jrsoftware.websoap.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.jrsoftware.websoap.R;
import com.jrsoftware.websoap.util.HTTPUtils;
import com.jrsoftware.websoap.util.HTTPUtils.HTTPResponseType;
import com.jrsoftware.websoap.util.AppUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.IOException;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread. Searches and sanitizes HTML Pages for safe phone display
 */
public class HTMLSanitizationService extends IntentService {

    public static final int EVENT_REQUEST_SUCCESSFUL = 0;
    public static final int EVENT_RESPONSE_BAD = -1;
    public static final int EVENT_RESPONSE_UNSUCCESSFUL = -2;
    public static final int EVENT_NETWORK_UNAVAILABLE = -3;
    public static final int EVENT_RESPONSE_EXCEPTION = -4;

    public static final String BROADCAST_RESULTS = "com.jrsoftware.websoap.receiver";
    public static final String RESULT_BUNDLE = "com.jrsoftware.websoap.services.result.resultBundle";
    public static final String RESULT_SANITIZED = "com.jrsoftware.websoap.services.result.sanitized";
    public static final String RESULT_URL = "com.jrsoftware.websoap.services.result.url";
    public static final String RESULT_TITLE = "com.jrsoftware.websoap.services.result.title";
    public static final String RESULT_CODE = "com.jrsoftware.websoap.servuces.result.resultcode";
    public static final String RESULT_RESPONSE_TYPE = "com.jrsoftware.websoap.services.result.responseType";
    public static final String RESULT_RESPONSE_MESSAGE = "com.jrsoftware.websoap.services.result.responseMsg";
    public static final String RESULT_RESPONSE_CODE = "com.jrsoftware.websoap.services.result.responseCode";
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
    public static void startActionSearch(Context context, String url) {
        Intent intent = new Intent(context, HTMLSanitizationService.class);
        intent.setAction(ACTION_SEARCH);
        intent.putExtra(ARG_URL, url);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEARCH.equals(action)) {
                final String url = intent.getStringExtra(ARG_URL);
                handleActionSearch(url, true);
            }
        }
    }

    /**
     * Handle HTML download and sanitize in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSearch(String url, boolean useSearchPreference) {
        Log.v(SERVICE_TAG, "handleActionSearch");
        final Context context = getApplicationContext();
        SharedPreferences preferences = AppUtils.getPreferences(this);
        Bundle responseBundle = new Bundle();
        String originalRequest = url;
        String searchEnginePref = preferences.getString(
                getString(R.string.pref_key_search_engine),
                "google"
        );

        boolean wasResponseGood = false;
        try {
            if(AppUtils.isNetworkAvailable(context)){
                //Force HTTPS if requested
                if(!url.startsWith(AppUtils.HTTP)
                        && !url.startsWith(AppUtils.HTTPS)
                        && !url.startsWith(AppUtils.FTP)){
                    boolean forceHTTPS = preferences.getBoolean(getString(R.string.pref_key_force_https), true);
                    if(forceHTTPS)
                        url = AppUtils.HTTPS + url;
                    else
                        url = AppUtils.HTTP + url;
                }
                Log.v(SERVICE_TAG, "Network is Available. Establishing Connection\u2026");
                Log.v(SERVICE_TAG, String.format("URL: %s", url));
                Connection conn = Jsoup.connect(url);
                Connection.Response response = conn.ignoreHttpErrors(true).execute();

                int responseCode = response.statusCode();
                String responseMsg = response.statusMessage();
                HTTPResponseType responseType = HTTPUtils.getResponseType(responseCode);
                String responseDescription = HTTPUtils.getResponseDescription(context, responseCode);

                Log.v(SERVICE_TAG, String.format("Response: %d", responseCode));
                Log.v(SERVICE_TAG, String.format("Response Type: %s", responseType.name()));
                Log.v(SERVICE_TAG, String.format("Response Message: %s", responseMsg));

                responseBundle.putInt(RESULT_RESPONSE_CODE, responseCode);
                responseBundle.putInt(RESULT_RESPONSE_TYPE, responseType.ordinal());
                responseBundle.putString(RESULT_RESPONSE_MESSAGE, responseMsg);
                if(HTTPUtils.isResponseGood(responseCode)){
                    Log.v(SERVICE_TAG, "Connection Established. Downloading Document\u2026");
                    Document doc = response.parse();
                    Log.v(SERVICE_TAG, "HTML Document Download Successful! Converting to String\u2026");
                    String html = doc.html();
                    String title = doc.title();

                    Log.v(SERVICE_TAG, "Conversion Successful! Cleaning via whitelist\u2026");
                    String safeHTML;
                    String whitelistType = preferences.getString(getString(R.string.pref_key_whitelist_type), "basic");
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

                    responseBundle.putString(RESULT_SANITIZED, safeHTML);
                    responseBundle.putString(RESULT_TITLE, title);
                    responseBundle.putInt(RESULT_CODE, EVENT_REQUEST_SUCCESSFUL);
                    wasResponseGood = true;
                }
                else{
                    if(searchEnginePref.equals("none")) {
                        Log.v(SERVICE_TAG, "Forwarding Bad Response");
                        responseBundle.putInt(RESULT_CODE, EVENT_RESPONSE_BAD);
                        responseBundle.putString(
                                RESULT_SANITIZED,
                                getErrorHTML(
                                        String.format(Locale.US, "%s - %d", responseType.toString(), responseCode),
                                        String.format(
                                                Locale.US,
                                                "%d %s</br>%s",
                                                responseCode,
                                                responseMsg,
                                                responseDescription
                                        )
                                )
                        );
                    }
                }
            }
            else{       //Network Unavailable
                Log.e(SERVICE_TAG, "Network is Currently Unavailable.");
                responseBundle.putInt(RESULT_CODE, EVENT_NETWORK_UNAVAILABLE);
                responseBundle.putString(
                        RESULT_SANITIZED,
                        getErrorHTML(
                                "Network Unavailable",
                                "The application was unable to connect to the Network."
                        )
                );
            }
        }
        catch(IOException io){
            Log.e(SERVICE_TAG, "An IOException has occurred");
            Log.e(SERVICE_TAG, io.getMessage());
            String stackTrace = mergeStackTrace(io.getStackTrace());
            String message = String.format(
                    "%s</br></br>%s:</br>%s",
                    "The application has encountered an unexpected IO Exception, and cannot load the requested page.",
                    io.getMessage(),
                    stackTrace
            );

            responseBundle.putString(
                    RESULT_SANITIZED,
                    getErrorHTML("IOException", message)
            );
            responseBundle.putInt(RESULT_CODE, EVENT_RESPONSE_EXCEPTION);
        }

        responseBundle.putString(RESULT_URL, url);
        if(!wasResponseGood){
            if(useSearchPreference){
                Log.v(SERVICE_TAG, "Reformatting as Search Query");
                String searchQuery;
                if(searchEnginePref.equals("google")) {
                    searchQuery = AppUtils.formatSearchQuery(context, originalRequest, R.string.query_search_google);
                    Log.v(SERVICE_TAG, String.format("Search Query: %s", searchQuery));
                    handleActionSearch(searchQuery, false);
                    return;
                }
                else if(searchEnginePref.equals("ask")) {
                    searchQuery = AppUtils.formatSearchQuery(context, originalRequest, R.string.query_search_ask);
                    Log.v(SERVICE_TAG, String.format("Search Query: %s", searchQuery));
                    handleActionSearch(searchQuery, false);
                    return;
                }
            }
        }

        publishResults(responseBundle);
    }

    private String getErrorHTML(String header, String body){
        String htmlTemplate = "<html>" + "<head>" + "<style>" +
                "body{" +
                "font-family:sans-serif;" +
                "}" +
                "pre{" +
                "padding:1em;" +
                "white-space:pre-wrap;" +
                "}" +
                "</style>" + "</head>" +
                "<body>" +
                "<h2>%s</h2>" +
                "<pre>%s</pre>" +
                "</body></html>";
        return String.format(htmlTemplate, header, body);
    }

    private void publishResults(Bundle resultBundle) {
        Intent intent = new Intent(BROADCAST_RESULTS);
        intent.putExtra(RESULT_BUNDLE, resultBundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private static String mergeStackTrace(StackTraceElement[] stackTrace){
        StringBuilder sb = new StringBuilder();
        for(StackTraceElement element : stackTrace)
            sb.append(String.format("%s\n", element.toString()));
        return sb.toString();
    }
}
