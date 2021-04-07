package com.rever.myforum.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.fragment.app.Fragment;

public class mAlertDialog extends Fragment {


    public static AlertDialog createAlertDialog(
            Context context, String title, String yes, String no,
            DialogInterface.OnClickListener yesToDo, DialogInterface.OnClickListener noToDo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(title);
        alertDialog.setPositiveButton(yes, yesToDo);
        alertDialog.setNegativeButton(no, noToDo);
        alertDialog.setCancelable(false);
        return alertDialog.show();
    }

}
