package com.heshun.hslibrary.common.helper;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.heshun.hslibrary.R;
import com.heshun.hslibrary.widget.MaterialDialog;


/**
 * 对话框帮助类,对话框创建唯一入口
 *
 * @author huangxz
 */
public class DialogHelper {
    private static MaterialDialog mDialog;

    public static void showLoadingDialog(Context c, String title) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_progressbar, null);
        showDialog(c, null, v, title);

    }

    public static View showLoadingDialog(Context c, String title, int persent) {
        View v = LayoutInflater.from(c).inflate(R.layout.item_progressbar_with_persent, null);

        showDialog(c, null, v, title);

        return v;
    }

    public static void showLoadingDialog(Context c) {
        showLoadingDialog(c, "请稍候");
    }

    public static void showCustomDialog(Context c, View v, String... title) {
        showDialog(c, null, v, title);
    }

    public static void showDialog(Context c, OnClickListener plistener, View v, String... content) {
        showDialog(c, plistener, v, false, content);
    }

    public static void showDialog(Context c, OnClickListener plistener, View v, boolean hasNega, String... content) {

        String title = null, message = null, nText = null, pText = null;
        MaterialDialog newDialog = new MaterialDialog(c, v, content);


        if (mDialog != null && mDialog.equals(newDialog) ) {
            return;
        }
        mDialog = newDialog;
        try {
            title = content[0];
            message = content[1];
            nText = content[2];
            pText = content[3];
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        mDialog.setTitle(TextUtils.isEmpty(title) ? c.getString(R.string.default_dialog_name) : title)
                .setMessage(TextUtils.isEmpty(message) ? "" : message);
        if (null != plistener)
            mDialog.setPositiveButton(TextUtils.isEmpty(pText) ? c.getString(R.string.done) : pText, plistener);
        if (hasNega)
            mDialog.setNegativeButton(TextUtils.isEmpty(nText) ? c.getString(R.string.cancel) : nText,
                    new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            dismiss();
                        }
                    });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialog = null;
            }
        });
        mDialog.setContentView(v).show();
    }

    public static void dismiss() {
        if (mDialog != null && mDialog.isShow()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

}
