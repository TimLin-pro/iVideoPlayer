package com.android.timlin.ivedioplayer.business.list

import android.arch.lifecycle.MutableLiveData
import android.media.MediaPlayer
import android.media.ThumbnailUtils
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.android.timlin.ivedioplayer.business.list.file.FileEntry
import com.android.timlin.ivedioplayer.business.list.video.VideoEntry
import com.android.timlin.ivedioplayer.common.utils.FileUtils
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * detect video file
 * */
object VideoFileDetector {
    private val TAG = "VideoFileDetector"
    private var mFileList: ArrayList<File> = ArrayList()
    private var mFileEntryList: ArrayList<FileEntry> = ArrayList()
    private var mVideoEntryList: ArrayList<VideoEntry> = ArrayList()
    var mFileEntryListLiveData = MutableLiveData<List<FileEntry>>()
    var mVideoEntryListLiveData = MutableLiveData<List<VideoEntry>>()
    //todo 留意内存问题
    private var mMediaPlayer = MediaPlayer()
    private val FORMAT = "HH:mm:ss"
    private var mFormatter = SimpleDateFormat(FORMAT)

    fun getRootFileData() {
        mFileEntryList.clear()
        mFileList.clear()

        val rootFile = Environment.getExternalStorageDirectory()
        if (rootFile != null) {
            Observable.just(rootFile)
                    .flatMap { file -> listFiles(file) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<File> {
                        override fun onSubscribe(d: Disposable) {

                        }

                        override fun onNext(file: File) {
                            //将遍历得到的所有视频文件存放在list中
                            mFileList.add(file)
                        }

                        override fun onComplete() {
                            groupByFiles(mFileList)
                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "onError: ", e)
                        }
                    })
        }
    }

    /**
     * 获取某一个文件夹中的视频文件
     */
    fun getVideoData(directory: File?) {
        mVideoEntryList.clear()
        if (directory != null && directory.isDirectory) {
            Observable.fromArray<File>(*directory.listFiles())
                    .filter { file -> file.exists() && file.canRead() && FileUtils.isVideoFile(file) }
                    .subscribeOn(Schedulers.io())
                    .subscribe(object : Observer<File> {
                        override fun onSubscribe(d: Disposable) {
                        }

                        override fun onNext(file: File) {
                            mVideoEntryList.add(VideoEntry(file.name, file.absolutePath, "unknown", "0", ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)))
//                            mVideoEntryList.add(VideoEntry(file.name, file.absolutePath, FileUtils.formatFileSize(file.length()), getFormattedDuration(file), ThumbnailUtils.createVideoThumbnail(file.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)))
                            //TODO "视频时长"
                        }

                        override fun onError(e: Throwable) {
                            Log.e(TAG, "getVideoDataError", e)
                        }

                        override fun onComplete() {
                            mVideoEntryListLiveData.postValue(mVideoEntryList)
                        }
                    })
        }
    }

    private fun getFormattedDuration(file: File): String {
        mMediaPlayer.setDataSource(file.path)
        //todo 可能比较耗时，需要修改实现方式
        mMediaPlayer.prepare()
        return mFormatter.format(mMediaPlayer.duration)
    }

    private fun groupByFiles(fileList: List<File>) {
        Observable.fromIterable(fileList)
                .groupBy { file -> file.parentFile }//以视频文件的父文件夹路径进行分组
                .subscribeOn(Schedulers.io())
                .subscribe(object : Observer<GroupedObservable<File, File>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "groupBy onCompleted")
                        mFileEntryListLiveData.postValue(mFileEntryList)
                    }

                    override fun onError(e: Throwable) {
                        Log.e(TAG, "groupByFiles error", e)
                    }

                    override fun onNext(fileGroupedObservable: GroupedObservable<File, File>) {
                        fileGroupedObservable.subscribe(object : Observer<File> {
                            var mCount = 0
                            var mParentName: String? = null

                            override fun onSubscribe(d: Disposable) {

                            }

                            override fun onNext(file: File) {
                                mCount++
                                mParentName = file.parentFile.name
                            }

                            override fun onComplete() {
                                mFileEntryList.add(FileEntry(fileGroupedObservable.key.toString(), mParentName!!, mCount))
                                mCount = 0
                                mParentName = null
                            }

                            override fun onError(e: Throwable) {}
                        })
                    }
                })
    }

    /**
     * rxJava 递归查询内存中的视频文件
     * todo 在此方法中应该可以做 groupBy 操作，这样能简化操作，日后再研究
     */
    private fun listFiles(f: File): Observable<File> {
        return if (f.isDirectory) {
            Observable.fromArray(*f.listFiles()!!)
                    .flatMap { file -> listFiles(file) }
        } else {
            /**filter操作符过滤视频文件,是视频文件就通知观察者 */
            Observable.just(f)
                    .filter { file -> file.exists() && file.canRead() && FileUtils.isVideoFile(file) }
        }
    }
}