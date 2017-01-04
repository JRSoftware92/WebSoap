package com.jrsoftware.websoap.settings;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.jrsoftware.websoap.R;

public class LicenseFragment extends DialogFragment {

    WebView webView;

    private String filePath = null;

    public static LicenseFragment newInstance() {
        return new LicenseFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_license, container, false);

        //Load License Information FilePath
        filePath = getString(R.string.path_license_information);

        //Initialize Web View with provided filepath
        webView = (WebView) rootView.findViewById(R.id.webView);
        if(webView != null && filePath != null)
            webView.loadUrl(filePath);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.text_license_information))
                .setView(webView)
                .setPositiveButton(R.string.text_ok, null)
                .create();
    }
}