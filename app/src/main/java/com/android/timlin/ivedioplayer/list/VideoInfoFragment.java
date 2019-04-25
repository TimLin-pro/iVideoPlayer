package com.android.timlin.ivedioplayer.list;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.timlin.ivedioplayer.R;
import com.android.timlin.ivedioplayer.common.media_retriever.MediaMetadataRepository;
import com.android.timlin.ivedioplayer.common.media_retriever.Metadata;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by linjintian on 2019/4/25.
 */

public class VideoInfoFragment extends DialogFragment {
    private static final String TAG = "VideoInfoFragment";
    public static final String KEY_URI = "URI";
    private MetadataListAdapter mAdapter;

    public static VideoInfoFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(KEY_URI, uri);
        VideoInfoFragment fragment = new VideoInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void show(FragmentManager manager) {
        super.show(manager, "VideoInfoFragmentTag");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Uri uri = getArguments().getParcelable(KEY_URI);
        Bundle args = new Bundle();
        args.putParcelable(KEY_URI, uri);
        List<Metadata> metadataList = MediaMetadataRepository.getMetaDataList(getContext(), uri);
        mAdapter.setMetadata(metadataList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void initView(View v) {
        mAdapter = new MetadataListAdapter(getContext());
        ListView listView = v.findViewById(R.id.list_view);
        listView.setAdapter(mAdapter);
    }

    private static class MetadataListAdapter extends ArrayAdapter<Metadata> {
        private final LayoutInflater mInflater;

        public MetadataListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2);
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setMetadata(List<Metadata> metadataList) {
            clear();
            if (metadataList != null && !metadataList.isEmpty()) {
                addAll(metadataList);
            }
        }

        @Override
        public View getView(int position, View convertView, @NotNull ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(android.R.layout.simple_list_item_2, parent, false);
            } else {
                view = convertView;
            }
            Metadata item = getItem(position);
            ((TextView) view.findViewById(android.R.id.text1)).setText(item.getKey());
            ((TextView) view.findViewById(android.R.id.text2)).setText((String) item.getValue());
            return view;
        }
    }
}