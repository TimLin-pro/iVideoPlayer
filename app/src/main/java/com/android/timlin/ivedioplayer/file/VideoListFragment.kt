package com.android.timlin.ivedioplayer.file

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.timlin.ivedioplayer.InjectorUtils
import kotlinx.android.synthetic.main.fragment_vedio_list.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [VideoListFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [VideoListFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class VideoListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    //    private var listener: OnFragmentInteractionListener? = null
    //内存读写权限
    private val PERMISSIONS_STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_PERMISSION_CODE = 102

    private lateinit var mViewModel: FileListViewModel
    private lateinit var mVideoFileListAdapter: VideoFileListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(com.android.timlin.ivedioplayer.R.layout.fragment_vedio_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        videoRecyclerView.layoutManager = LinearLayoutManager(activity)
        mVideoFileListAdapter = VideoFileListAdapter()
        videoRecyclerView.adapter = mVideoFileListAdapter
        if (checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE)
        } else {
            observerFileData()
        }
    }

    private fun observerFileData() {
        val factory = InjectorUtils.provideFileViewModelFactory()
        mViewModel = ViewModelProviders.of(this, factory).get(FileListViewModel::class.java)
        mViewModel.mFileEntryList.observe(this, Observer {
            it?.let { it1 -> mVideoFileListAdapter.swapFileEntryList(it1) }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (grantResults[0] == PERMISSION_GRANTED && requestCode == REQUEST_PERMISSION_CODE) {
            observerFileData()
        } else {
            AlertDialog.Builder(context!!)
                    .setMessage(getString(com.android.timlin.ivedioplayer.R.string.need_open_permission_before_use))
                    .setPositiveButton(getString(com.android.timlin.ivedioplayer.R.string.go_setting)) { _, _ ->
                        jump2SettingPage()
                    }
                    .setNegativeButton(getString(com.android.timlin.ivedioplayer.R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            TODO("点击拒绝后，用户到设置页面提供权限，返回后，需要检查权限，重新拉取数据")
        }
    }

    private fun jump2SettingPage() {
        val intent = getAppDetailSettingIntent(context!!)
        startActivity(intent)
    }

    /**
     * 获取应用详情页面的 intent
     */
    private fun getAppDetailSettingIntent(context: Context): Intent {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
//        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        if (context is OnFragmentInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
//        }
    }

    override fun onDetach() {
        super.onDetach()
//        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
//    interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        fun onFragmentInteraction(uri: Uri)
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VideoListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                VideoListFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

}
