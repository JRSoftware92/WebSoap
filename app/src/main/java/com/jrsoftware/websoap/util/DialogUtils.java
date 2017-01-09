package com.jrsoftware.websoap.util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.jrsoftware.websoap.R;

/**
 * Created by jriley on 2/20/16.
 * Utility Method Class for Building Standard Dialogs
 */
public class DialogUtils {

    public static AlertDialog getMessageDialog(Context context, int titleId, int messageId){
        return getMessageDialog(context, titleId, messageId, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static AlertDialog getMessageDialog(Context context, int titleId, int messageId,
                                               DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titleId);
        dialog.setMessage(messageId);
        dialog.setNeutralButton(R.string.text_ok, onClickListener);

        return dialog.create();
    }

    public static AlertDialog getMessageDialog(Context context, int titleId, int messageId,
                                               DialogInterface.OnClickListener okListener,
                                               DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titleId);
        dialog.setMessage(messageId);
        dialog.setNegativeButton(R.string.text_ok, okListener);
        dialog.setPositiveButton(R.string.text_cancel, cancelListener);

        return dialog.create();
    }

    public static AlertDialog getMessageDialog(Context context, String title, String message){
        return getMessageDialog(context, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public static AlertDialog getMessageDialog(Context context, String title, String message,
                                               DialogInterface.OnClickListener onClickListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNeutralButton(R.string.text_ok, onClickListener);

        return dialog.create();
    }

    public static AlertDialog getMessageDialog(Context context, String title, String message,
                                               DialogInterface.OnClickListener okListener,
                                               DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setNegativeButton(R.string.text_ok, okListener);
        dialog.setPositiveButton(R.string.text_cancel, cancelListener);

        return dialog.create();
    }


    public static AlertDialog getTextChoiceDialog(Context context, int arrayId, int titleId,
                                                  DialogInterface.OnClickListener onClickListener){
        Resources res = context.getResources();
        final String[] options = res.getStringArray(arrayId);

        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titleId);
        dialog.setItems(options, onClickListener);

        return dialog.create();
    }

    public static AlertDialog getTextInputDialog(Context context, int titleId, EditText editText,
                                                 DialogInterface.OnClickListener okListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(titleId));
        dialog.setView(editText);
        dialog.setNegativeButton(R.string.text_ok, okListener);
        dialog.setPositiveButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return dialog.create();
    }

    public static AlertDialog getTextInputDialog(Context context, int titleId, EditText editText,
                                                 DialogInterface.OnClickListener okListener,
                                                 DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setCancelable(true);
        dialog.setTitle(context.getString(titleId));
        dialog.setView(editText);
        dialog.setNegativeButton(context.getString(R.string.text_ok), okListener);
        dialog.setPositiveButton(context.getString(R.string.text_cancel), cancelListener);

        return dialog.create();
    }

    public static AlertDialog getConfirmationDialog(Context context, DialogInterface.OnClickListener okListener){
        return getConfirmationDialog(context, R.string.title_dialog_confirmation,
                R.string.message_dialog_cannot_be_undone,
                okListener);
    }

    public static AlertDialog getConfirmationDialog(Context context, int titleId, int messageId,
                                                DialogInterface.OnClickListener okListener){
        AlertDialog dialog = DialogUtils.getMessageDialog(
                context, titleId, messageId, okListener,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        return dialog;
    }
}
