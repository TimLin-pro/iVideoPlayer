package com.android.timlin.ivedioplayer.common;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.player.activities.VideoActivity;

/**
 * Created by linjintian on 2019/4/28.
 */
public class VideoAddressInputHelper {
    private Context mContext;
    private View mView;

    public VideoAddressInputHelper(View inputEntranceView) {
        mContext = inputEntranceView.getContext();
        mView = inputEntranceView;
        init();
    }

    private void init() {
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressInputDialog();
            }
        });
    }

    private void showAddressInputDialog() {
        final EditText urlEt = new EditText(mContext);
        new AlertDialog.Builder(mContext)
                .setTitle(R.string.please_enter_video_address)
                .setView(urlEt)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String urlStr = urlEt.getText().toString().trim();
                        if (TextUtils.isEmpty(urlStr)) {
                            Toast.makeText(mContext, R.string.address_cannot_be_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            VideoActivity.intentTo(mContext, Uri.parse(urlStr), "");
                        }
                    }
                }).show();
    }

}
