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
package com.android.timlin.ivedioplayer.list.video.loader;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;


public class VideoItem implements Parcelable {
    public final long id;
    public final String mimeType;
    public final String displayName;
    public final Uri uri;
    public final long size;
    public final long duration;

    private VideoItem(long id, String mimeType, String displayName,long size, long duration) {
        this.id = id;
        this.mimeType = mimeType;
        this.displayName = displayName;
        Uri contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        this.uri = ContentUris.withAppendedId(contentUri, id);
        this.size = size;
        this.duration = duration;
    }

    protected VideoItem(Parcel in) {
        id = in.readLong();
        mimeType = in.readString();
        displayName = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readLong();
        duration = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(mimeType);
        dest.writeString(displayName);
        dest.writeParcelable(uri, flags);
        dest.writeLong(size);
        dest.writeLong(duration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel in) {
            return new VideoItem(in);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    public static VideoItem valueOf(Cursor cursor) {
        return new VideoItem(cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)),
                cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE)),
                cursor.getLong(cursor.getColumnIndex("duration")));
    }

    public Uri getContentUri() {
        return uri;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof VideoItem)) {
            return false;
        }

        VideoItem other = (VideoItem) obj;
        return id == other.id
                && (mimeType != null && mimeType.equals(other.mimeType)
                || (mimeType == null && other.mimeType == null))
                && (uri != null && uri.equals(other.uri)
                || (uri == null && other.uri == null))
                && size == other.size
                && duration == other.duration;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Long.valueOf(id).hashCode();
        if (mimeType != null) {
            result = 31 * result + mimeType.hashCode();
        }
        result = 31 * result + uri.hashCode();
        result = 31 * result + Long.valueOf(size).hashCode();
        result = 31 * result + Long.valueOf(duration).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "id=" + id +
                ", mimeType='" + mimeType + '\'' +
                ", uri=" + uri +
                ", size=" + size +
                ", duration=" + duration +
                '}';
    }
}
