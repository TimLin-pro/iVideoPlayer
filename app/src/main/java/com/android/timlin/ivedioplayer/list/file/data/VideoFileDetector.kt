package com.android.timlin.ivedioplayer.list.file.data

import android.arch.lifecycle.MutableLiveData
import android.os.Environment
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observables.GroupedObservable
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * detect video file
 * */
object VideoFileDetector {
    private val TAG = "VideoFileDetector"
    private var mFileList: ArrayList<File> = ArrayList()
    private var mFileEntryList: ArrayList<FileEntry> = ArrayList()
    private var mFileListLiveData = MutableLiveData<List<File>>()
    var mFileEntryListLiveData = MutableLiveData<List<FileEntry>>()

    fun getRootFileData() {
        mFileEntryList.clear()
        mFileList.clear()

        val rootFile = Environment.getExternalStorageDirectory()
        if (rootFile != null) {
            Observable.just(rootFile)
                    .flatMap { file -> listFiles(file) }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
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

    private fun groupByFiles(fileList: List<File>) {
        Observable.fromIterable(fileList)
                .groupBy { file -> file.parentFile }//以视频文件的父文件夹路径进行分组
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<GroupedObservable<File, File>> {
                    override fun onSubscribe(d: Disposable) {

                    }

                    override fun onComplete() {
                        Log.d(TAG, "groupBy onCompleted")
                        mFileEntryListLiveData.value = mFileEntryList
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
                                mFileEntryList.add(FileEntry(fileGroupedObservable.key.toString(), mParentName, mCount))
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