package com.android.timlin.ivedioplayer.common.media_retriever;

import android.content.Context;
import android.net.Uri;

import com.dyhdyh.compat.mmrc.MediaMetadataRetrieverCompat;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linjintian on 2019/4/25.
 */
public class MediaMetadataRepository {
    @NotNull
    public static List<Metadata> getMetaDataList(Context context, Uri uri) {
        MediaMetadataRetrieverCompat mmrc = MediaMetadataRetrieverCompat.create(MediaMetadataRetrieverCompat.RETRIEVER_AUTOMATIC);
        mmrc.setDataSource(context, uri);

//        String album = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_ALBUM);
//        String artist = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_ARTIST);
//        String author = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_AUTHOR);
        String composer = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_COMPOSER);
        String date = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DATE);
        String title = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_TITLE);
        String duration = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DURATION);
        String num_tracks = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_NUM_TRACKS);
        String albumartist = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_ALBUMARTIST);
        String disc_number = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_DISC_NUMBER);
        String width = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_WIDTH);
        String height = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_HEIGHT);
        String rotation = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_ROTATION);
        String captureFramerate = mmrc.extractMetadata(MediaMetadataRetrieverCompat.METADATA_KEY_CAPTURE_FRAMERATE);

//        Metadata albumMetadata = new Metadata("album", album);
//        Metadata artistMetadata = new Metadata("artist", artist);
//        Metadata authorMetadata = new Metadata("author", author);
        Metadata composerMetadata = new Metadata("composer", composer);
        Metadata dateMetadata = new Metadata("date", date);
        Metadata titleMetadata = new Metadata("title", title);
        Metadata durationMetadata = new Metadata("duration", duration);
        Metadata num_tracksMetadata = new Metadata("num_tracks", num_tracks);
        Metadata albumartistMetadata = new Metadata("albumartist", albumartist);
        Metadata disc_numberMetadata = new Metadata("disc_number", disc_number);
        Metadata widthMetadata = new Metadata("width", width);
        Metadata heightMetadata = new Metadata("height", height);
        Metadata rotationMetadata = new Metadata("rotation", rotation);
        Metadata captureFramerateMetadata = new Metadata("captureFramerate", captureFramerate);

        List<Metadata> metadataList = new ArrayList<>(13);
//        metadataList.add(albumMetadata);
//        metadataList.add(artistMetadata);
//        metadataList.add(authorMetadata);
        metadataList.add(composerMetadata);
        metadataList.add(dateMetadata);
        metadataList.add(titleMetadata);
        metadataList.add(durationMetadata);
        metadataList.add(num_tracksMetadata);
        metadataList.add(albumartistMetadata);
        metadataList.add(disc_numberMetadata);
        metadataList.add(widthMetadata);
        metadataList.add(heightMetadata);
        metadataList.add(rotationMetadata);
        metadataList.add(captureFramerateMetadata);
        return metadataList;
    }
}
