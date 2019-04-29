package com.android.timlin.ivedioplayer.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.business.list.VideoInfoFragment;
import com.android.timlin.ivedioplayer.common.utils.FileUtils;
import com.android.timlin.ivedioplayer.common.utils.ScreenUtil;
import com.android.timlin.ivedioplayer.business.list.video.loader.VideoItem;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.List;

/**
 * Created by linjintian on 2019/4/24.
 */
public class BottomSheetDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "BottomSheetDialog";
    public static final String KEY_VIDEO_ITEM = "VIDEO_ITEM";
    private TextView mTvName;
    private LinearLayout mLlRename;
    private LinearLayout mLlInfo;
    private LinearLayout mLlDelete;
    private VideoItem mVideoItem;
    private RefreshCallback mRefreshCallback;

    public static BottomSheetDialog newInstance(VideoItem videoItem) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_VIDEO_ITEM, videoItem);
        BottomSheetDialog fragment = new BottomSheetDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(getContentView());
        final Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = ScreenUtil.INSTANCE.getScreenWidthInPixel();
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lp.gravity = Gravity.BOTTOM;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
        }
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void show(FragmentManager manager, RefreshCallback refreshCallback) {
        mRefreshCallback = refreshCallback;
        initVideoItem();
        super.show(manager, "BottomSheetDialogTag");
    }

    private void initVideoItem() {
        if (getArguments() != null) {
            mVideoItem = getArguments().getParcelable(KEY_VIDEO_ITEM);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private View getContentView() {
        final View contentView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_more, null);
        initView(contentView);
        initClickListener();
        if (getArguments() != null) {
            getArguments().getParcelable(KEY_VIDEO_ITEM);
        }
        setVideoName();
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void setVideoName() {
        if (getArguments() != null) {
            mTvName.setText(mVideoItem.displayName);
        } else {
            mTvName.setText("video name");
        }

    }

    private void initClickListener() {
        mLlRename.setOnClickListener(this);
        mLlInfo.setOnClickListener(this);
        mLlDelete.setOnClickListener(this);
    }

    private void initView(View view) {
        mTvName = view.findViewById(R.id.tv_name);
        mLlRename = view.findViewById(R.id.ll_rename);
        mLlInfo = view.findViewById(R.id.ll_info);
        mLlDelete = view.findViewById(R.id.ll_delete);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_rename:
                showRenameDialog();
                break;
            case R.id.ll_info:
                showInfoDialog();
                break;
            case R.id.ll_delete:
                showDeleteDialog();
                break;
        }
    }

    private void showInfoDialog() {
        VideoInfoFragment.newInstance(mVideoItem.uri)
                .show(getFragmentManager());
    }

    private void showDeleteDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.delete)
                .setMessage(getString(R.string.following_file_will_be_delete) + mVideoItem.displayName)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUri(getContext(), mVideoItem.uri);
                    }
                }).show();
    }

    private void showRenameDialog() {
        final EditText editText = new EditText(getContext());
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.rename_to)
                .setView(editText)
                .setPositiveButton(getContext().getString(R.string.sure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mVideoItem == null) {
                            Log.e(TAG, "onClick: mVideoItem == null ");
                            return;
                        }
                        Log.d(TAG, "onAction: data mVideoItem.uri =  " + mVideoItem.uri);
                        final String realFilePath = FileUtils.INSTANCE.getRealFilePath(getContext(), mVideoItem.uri);
                        Log.d(TAG, "onClick: realFilePath = " + realFilePath);
                        doRename(editText);
                    }
                })
                .setNegativeButton(getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void doRename(EditText editText) {
        final String path = FileUtils.INSTANCE.getRealFilePath(getContext(), mVideoItem.uri);
        File fromFile = new File(path);
        if (fromFile.exists()) {
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            File toFile = new File(fromFile.getParent(), editText.getText().toString() + suffix);
            showToast(fromFile.renameTo(toFile) ? R.string.rename_success : R.string.rename_fail);
        } else {
            showToast(R.string.file_not_exist_rename_fail);
        }
    }

    public void deleteUri(final Context context, final Uri uri) {
        checkStoragePermission(context, uri);
    }

    private void checkStoragePermission(final Context context, final Uri uri) {
        AndPermission.with(context)
                .runtime()
                .permission(Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        doDelete(context, uri);
                    }
                }).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                showToast(R.string.need_storage_permission_to_delete);
            }
        }).start();
    }

    private void doDelete(Context context, Uri uri) {
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            int rowDelete = context.getContentResolver().delete(uri, null, null);
            if (rowDelete >= 0) {
                showToast(R.string.delete_success);
                refreshList();
            } else showToast(R.string.delete_fail);
        } else {
            File file = new File(FileUtils.INSTANCE.getRealFilePath(context, uri));
            if (file.exists() && file.isFile()) {
                if (file.delete()) {
                    showToast(R.string.delete_success);
                    refreshList();
                } else showToast(R.string.delete_fail);
            } else {
                showToast(R.string.fileDoesntExist);
            }
        }
    }

    private void refreshList() {
        if (mRefreshCallback != null) {
            mRefreshCallback.refresh();
        }
    }

    private void showToast(int strId) {
        Toast.makeText(getContext(), getString(strId), Toast.LENGTH_SHORT).show();
    }

    public interface RefreshCallback {
        void refresh();
    }
}
