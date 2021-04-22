package com.veve.videomemegenerator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class custom_progressDialog extends Dialog {

    public custom_progressDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        setCancelable(false);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.custom_layout,null);
        setContentView(view);
    }
}
