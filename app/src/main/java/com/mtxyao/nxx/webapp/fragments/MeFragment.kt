package com.mtxyao.nxx.webapp.fragments

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.entity.UserData
import com.mtxyao.nxx.webapp.util.*
import org.json.JSONObject
import java.io.File

class MeFragment : BaseFragment(true) {
    var uploadDialog: AlertDialog ? = null
    var uploadProgressBar: HorizontalProgressbarWithProgress ? = null

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    override fun getPageOpt(): PageOpt {
        return PageOpt().setShowTitleBar(false).setCanRef(false)
    }

    override fun setPageUrl(): String {
        return "app-me"
    }

    override fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?) {
        val userData: UserData? = UserDataUtil.getUserData(this.context!!)
        OkGo.post<String>(Urls.URL_BEFORE + Urls.URL_FILE_UPLOAD)
                .tag(this)
                .params("file", cropFile)
                .execute(object: StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        val data = JSONObject(response!!.body())
                        if (data.has("success") && data.getString("success") == "1") {
                            ComFun.showToast(this@MeFragment.context!!, "头像上传成功", Toast.LENGTH_SHORT)
                            userData!!.user!!.photo = data.getString("fid")
                            UserDataUtil.setUserData(this@MeFragment.context!!, userData)
                            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", "userHeadUploadSuccess", Gson().toJson(mapOf(
                                    "fid" to data.getInt("fid")
                            )))
                        } else {
                            ComFun.showToast(this@MeFragment.context!!, "头像上传失败", Toast.LENGTH_LONG)
                        }
                    }

                    override fun uploadProgress(progress: Progress?) {
                        if (uploadDialog == null || (uploadDialog != null && !uploadDialog!!.isShowing)) {
                            uploadDialog = AlertDialog.Builder(this@MeFragment.context!!, R.style.MyDialogStyle).setCancelable(false).create()
                            uploadDialog!!.show()
                            val win: Window = uploadDialog!!.window
                            val uploadView: View = this@MeFragment.activity!!.layoutInflater.inflate(R.layout.upload_dialog, null)
                            win.setContentView(uploadView)
                            uploadProgressBar = uploadView.findViewById(R.id.progressBar)
                            uploadProgressBar!!.max = progress!!.totalSize.toInt()
                        }
                        uploadProgressBar!!.progress = progress!!.currentSize.toInt()
                        if (progress.currentSize.toInt() == progress.totalSize.toInt()) {
                            uploadDialog!!.dismiss()
                            uploadDialog = null
                        }
                    }
                })
    }

}