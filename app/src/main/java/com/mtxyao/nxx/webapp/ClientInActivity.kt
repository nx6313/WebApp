package com.mtxyao.nxx.webapp

import android.graphics.Bitmap
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.view.View
import android.view.Window
import android.widget.Toast
import com.google.gson.Gson
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Progress
import com.lzy.okgo.model.Response
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.HorizontalProgressbarWithProgress
import com.mtxyao.nxx.webapp.util.PageOpt
import com.mtxyao.nxx.webapp.util.Urls
import org.json.JSONObject
import java.io.File

class ClientInActivity : BaseWebActivity() {
    var uploadDialog: AlertDialog ? = null
    var uploadProgressBar: HorizontalProgressbarWithProgress? = null

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_client_in
    }

    override fun getPageOpt(): PageOpt {
        return super.getPageOpt().setStatusDark(true).setFreeStyleCropEnabled(true)
    }

    override fun setPageUrl(): String {
        return "app-client-in"
    }

    override fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?, webEventName: String?) {
        OkGo.post<String>(Urls.URL_BEFORE + Urls.URL_FILE_UPLOAD)
                .tag(this)
                .params("files", cropFile)
                .execute(object: StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        val data = JSONObject(response!!.body())
                        if (data.has("code") && data.getString("code") == "1") {
                            ComFun.showToast(this@ClientInActivity, "上传成功", Toast.LENGTH_SHORT)
                            val mutableList: MutableList<String> = mutableListOf()
                            for (i in 0..(data.getJSONArray("obj").length() - 1)) {
                                mutableList.add((data.getJSONArray("obj")[i] as JSONObject).getString("url"))
                            }
                            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", webEventName, Gson().toJson(mutableList))
                        } else {
                            ComFun.showToast(this@ClientInActivity, "上传失败", Toast.LENGTH_LONG)
                        }
                    }

                    override fun uploadProgress(progress: Progress?) {
                        if (uploadDialog == null || (uploadDialog != null && !uploadDialog!!.isShowing)) {
                            uploadDialog = AlertDialog.Builder(this@ClientInActivity, R.style.MyDialogStyle).setCancelable(false).create()
                            uploadDialog!!.show()
                            val win: Window = uploadDialog!!.window
                            val uploadView: View = this@ClientInActivity.layoutInflater.inflate(R.layout.upload_dialog, null)
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

    override fun getLatelyImage(bitmaps: MutableList<Bitmap>?, latelyUris: MutableList<Uri>?, latelyFiles: MutableList<File>?, webEventName: String?) {
        OkGo.post<String>(Urls.URL_BEFORE + Urls.URL_FILE_UPLOAD)
                .tag(this)
                .addFileParams("files", latelyFiles)
                .execute(object: StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        val data = JSONObject(response!!.body())
                        if (data.has("code") && data.getString("code") == "1") {
                            ComFun.showToast(this@ClientInActivity, "上传成功", Toast.LENGTH_SHORT)
                            val mutableList: MutableList<String> = mutableListOf()
                            for (i in 0..(data.getJSONArray("obj").length() - 1)) {
                                mutableList.add((data.getJSONArray("obj")[i] as JSONObject).getString("url"))
                            }
                            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", webEventName, Gson().toJson(mutableList))
                        } else {
                            ComFun.showToast(this@ClientInActivity, "上传失败", Toast.LENGTH_LONG)
                        }
                    }

                    override fun uploadProgress(progress: Progress?) {
                        if (uploadDialog == null || (uploadDialog != null && !uploadDialog!!.isShowing)) {
                            uploadDialog = AlertDialog.Builder(this@ClientInActivity, R.style.MyDialogStyle).setCancelable(false).create()
                            uploadDialog!!.show()
                            val win: Window = uploadDialog!!.window
                            val uploadView: View = this@ClientInActivity.layoutInflater.inflate(R.layout.upload_dialog, null)
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
