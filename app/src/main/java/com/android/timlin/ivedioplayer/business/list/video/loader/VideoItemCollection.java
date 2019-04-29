/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.timlin.ivedioplayer.business.list.video.loader;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

/**
 * 二级目录
 */
public class VideoItemCollection implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int LOADER_ID = 2;
    private static final String ARGS_ID = "id";
    private WeakReference<Context> mContext;
    private LoaderManager mLoaderManager;
    private VideoItemCallbacks mCallbacks;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }

        String albumId = args.getString(ARGS_ID);
        if (TextUtils.isEmpty(albumId)) {
            return null;
        }
        return VideoItemLoader.newInstance(context, albumId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onVideoItemsLoad(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Context context = mContext.get();
        if (context == null) {
            return;
        }

        mCallbacks.onVideoItemsReset();
    }

    public void onCreate(@NonNull FragmentActivity context, @NonNull VideoItemCallbacks callbacks) {
        mContext = new WeakReference<Context>(context);
        mLoaderManager = context.getSupportLoaderManager();
        mCallbacks = callbacks;
    }

    public void onDestroy() {
        if (mLoaderManager != null) {
            mLoaderManager.destroyLoader(LOADER_ID);
        }
        mCallbacks = null;
    }

    public void startLoadVideoData(@Nullable String id) {
        Bundle args = initArgs(id);
        mLoaderManager.initLoader(LOADER_ID, args, this);
    }

    public void reloadVideoData(@Nullable String id) {
        Bundle args = initArgs(id);
        mLoaderManager.restartLoader(LOADER_ID, args, this);
    }

    @NotNull
    private Bundle initArgs(@Nullable String id) {
        Bundle args = new Bundle();
        args.putString(ARGS_ID, id);
        return args;
    }

    public interface VideoItemCallbacks {
        void onVideoItemsLoad(Cursor cursor);

        void onVideoItemsReset();
    }
}
