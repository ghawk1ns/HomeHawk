package com.ghawk1ns.homehawk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by guyhawkins on 2/14/14.
 * A class to handle the displaying of alert dialogs
 */
public class DialogHandler {

    private final static String OK = "ok";

    @SuppressWarnings("deprecation")
    public static void showAlertDialog(Context context, String title, String message, Boolean status, String okButton, DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon(R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton(okButton,onClickListener);

        // Showing Alert Message
        alertDialog.show();
    }


}
