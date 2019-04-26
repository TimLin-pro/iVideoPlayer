package com.android.timlin.ivedioplayer.player.subtitles;

import android.util.Log;

import com.android.timlin.ivedioplayer.common.EncodingDetect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Auther jixiongxu
 * @date 2017/9/20.
 * @descraption 用于解析字幕
 */

public class SubtitlesParser {
    private static final String TAG = "SubtitlesParser";
    private final static int ONE_SECOND = 1000;
    private final static int ONE_MINUTE = 60 * ONE_SECOND;
    private final static int ONE_HOUR = 60 * ONE_MINUTE;
    /**
     * 正则表达式，判断是否是字幕中时间的格式
     */
    private final static String TIME_REGEX = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
    private static final Pattern PATTERN = Pattern.compile(TIME_REGEX);

    /**
     * 解析字幕文件
     */
    public static List<SubtitlesEntry> parseSrtFile(String srtFilePath) {
        File subtitlesFile = new File(srtFilePath);
        if (!subtitlesFile.exists() || !subtitlesFile.isFile()) {
            Log.e(TAG, "file not exist");
            return Collections.emptyList();
        }
        FileInputStream is;
        BufferedReader bufferedReader = null;
        final String charset = EncodingDetect.getJavaEncode(subtitlesFile);
        List<SubtitlesEntry> entryList = new ArrayList<>();
        String line;
        try {
            is = new FileInputStream(subtitlesFile);
            bufferedReader = new BufferedReader(new InputStreamReader(is, charset));
            while ((line = bufferedReader.readLine()) != null) {
                parseSentence(line, bufferedReader, entryList);
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "parseFile:FileNotFoundException  ", e);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "parseFile:UnsupportedEncodingException  ", e);
        } catch (IOException e) {
            Log.e(TAG, "parseFile:IOException", e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return entryList;
    }

    private static void parseSentence(String line, BufferedReader bufferedReader, List<SubtitlesEntry> entryList) throws IOException {
        //以时间为起点解析句子，不符合则跳过
        if (isSubtitleLine(line)) {
            SubtitlesEntry entry = new SubtitlesEntry();
            // 填充开始时间数据
            entry.startTime = parseTimeLine(line.substring(0, 12));
            // 填充结束时间数据
            entry.endTime = parseTimeLine(line.substring(17, 29));
            // 填充中文数据
            entry.chinese = bufferedReader.readLine();
            // 填充英文数据
            entry.english = bufferedReader.readLine();
            // 当前字幕的节点位置
            entry.node = entryList.size() + 1;
            entryList.add(entry);
        }
    }

    private static boolean isSubtitleLine(String line) {
        return PATTERN.matcher(line).matches();
    }

    /**
     * @param line
     * @return 字幕所在的时间节点
     * @descraption 将String类型的时间转换成int的时间类型
     */
    private static int parseTimeLine(String line) {
        try {
            return Integer.parseInt(line.substring(0, 2)) * ONE_HOUR// 时
                    + Integer.parseInt(line.substring(3, 5)) * ONE_MINUTE// 分
                    + Integer.parseInt(line.substring(6, 8)) * ONE_SECOND// 秒
                    + Integer.parseInt(line.substring(9));// 毫秒
        } catch (NumberFormatException e) {
            Log.e(TAG, "parseTimeLine: ", e);
        }
        return -1;
    }
}
