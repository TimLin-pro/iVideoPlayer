package com.android.timlin.ivedioplayer.file.data;

import java.io.Serializable;

/**
 * SD卡上的视频信息存放类
 * Created by linjintian on 2019/2/17.
 */
public class FileEntry implements Serializable {
    /**
     * 文件夹路径
     **/
    public String path;
    /**
     * 文件夹里有多少个视频文件
     **/
    public int count;
    private static final long serialVersionUID = 1L;
    public String name;

    public FileEntry() {
    }

    public FileEntry(String path, int count) {
        this.path = path;
        this.count = count;
    }

    public FileEntry(String path, String name, int count) {
        this.path = path;
        this.name = name;
        this.count = count;

    }
}





