///*
// * Copyright (C) 2015 Bilibili
// * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.android.timlin.ivedioplayer.example.activities;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Environment;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.android.timlin.ivedioplayer.R;
//import com.android.timlin.ivedioplayer.example.application.AppActivity;
//import com.android.timlin.ivedioplayer.example.application.Settings;
//import com.android.timlin.ivedioplayer.example.eventbus.FileExplorerEvents;
//import com.android.timlin.ivedioplayer.list.file.FileListFragment;
//import com.squareup.otto.Subscribe;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Arrays;
//
//
//public class FileExplorerActivity extends AppActivity {
//    private Settings mSettings;
//    private static final int REQUEST_CODE_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            "android.permission.READ_EXTERNAL_STORAGE",
//            "android.permission.WRITE_EXTERNAL_STORAGE"};
//    private static final String TAG = "FileExplorerActivity ";
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        if (mSettings == null) {
//            mSettings = new Settings(this);
//        }
//        verifyStoragePermissions(this);
//
//    }
//
//    public void verifyStoragePermissions(Activity activity) {
//        try {
//            Log.i(TAG, "verifyStoragePermissions: ");
//            //检测是否有写的权限
//            int permission = ActivityCompat.checkSelfPermission(activity,
//                    "android.permission.WRITE_EXTERNAL_STORAGE");
//            if (permission != PackageManager.PERMISSION_GRANTED) {
//                // 没有写的权限，去申请写的权限，会弹出对话框
//                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_CODE_EXTERNAL_STORAGE);
//            } else {
//                openRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath(), false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        FileExplorerEvents.getBus().register(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        FileExplorerEvents.getBus().unregister(this);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Log.d(TAG, "onRequestPermissionsResult: " + requestCode + " permission = " + Arrays.toString(permissions) + " result  = " + Arrays.toString(grantResults));
//        if (requestCode == REQUEST_CODE_EXTERNAL_STORAGE) {
////            String lastDirectory = mSettings.getLastDirectory();
//////            if (!TextUtils.isEmpty(lastDirectory) && new File(lastDirectory).isDirectory())
//////                doOpenDirectory(lastDirectory, false);
//////            else
//            openRootDirectory(Environment.getExternalStorageDirectory().getAbsolutePath(), false);
//        }
//    }
//
//    private void openRootDirectory(String absolutePath, boolean b) {
//        doOpenDirectory(absolutePath, b);
//    }
//
//    private void doOpenDirectory(String path, boolean addToBackStack) {
//        Fragment newFragment = FileListFragment.newInstance(path);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//
//        transaction.replace(R.id.body, newFragment);
//
//        if (addToBackStack)
//            transaction.addToBackStack(null);
//        transaction.commit();
//    }
//
//    @Subscribe
//    public void onClickFile(FileExplorerEvents.OnClickFile event) {
//        File f = event.mFile;
//        try {
//            f = f.getAbsoluteFile();
//            f = f.getCanonicalFile();
//            if (TextUtils.isEmpty(f.toString()))
//                f = new File("/");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        if (f.isDirectory()) {
//            String path = f.toString();
//            mSettings.setLastDirectory(path);
//            openRootDirectory(path, true);
//        } else if (f.exists()) {
//            VideoActivity.intentTo(this, f.getPath(), f.getName());
//        }
//    }
//}
