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
import kotlinx.android.synthetic.main.activity_app.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable

class SecondActivity : BaseWebActivity() {
    var uploadDialog: AlertDialog ? = null
    var uploadProgressBar: HorizontalProgressbarWithProgress? = null
    var pageParams: MutableMap<String, Any> ? = null

    override fun getActivityLayoutId(): Int {
        return R.layout.activity_second
    }

    override fun createAfter() {
        appTitle.text = intent.getStringExtra("titleName")
        if (intent.getSerializableExtra("pageOpts") != null) {
            val pageOptsSerializable = intent.getSerializableExtra("pageOpts") as PageOptsSerializable
            val titleDos = pageOptsSerializable.getTitleDos()
            pageParams = pageOptsSerializable.getPageParams()
        }
    }

    override fun getPageOpt(): PageOpt {
        val pageOpt = super.getPageOpt()
        if (intent.getBooleanExtra("fullPage", false)) {
            pageOpt.setWebViewFull(true)
                    .setTitleBarTransparency(true)
        } else {
            pageOpt.setTitleBarColor(intent.getStringExtra("titleBarColor"))
        }
        if (intent.getBooleanExtra("titleBarHighlight", false)) {
            pageOpt.setStatusDark(false)
                    .setTitleBarHighlight(true)
        }
        if (pageParams != null) {
            pageOpt.setPageParams(pageParams)
        }
        return pageOpt
    }

    override fun setPageUrl(): String {
        return intent.getStringExtra("webUri")
    }

    override fun getCropImage(bitmap: Bitmap?, cropUri: Uri?, cropFile: File?, webEventName: String?) {
        OkGo.post<String>(Urls.URL_BEFORE + Urls.URL_FILE_UPLOAD)
                .tag(this)
                .params("files", cropFile)
                .execute(object: StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        val data = JSONObject(response!!.body())
                        if (data.has("code") && data.getString("code") == "1") {
                            ComFun.showToast(this@SecondActivity, "上传成功", Toast.LENGTH_SHORT)
                            val mutableList: MutableList<String> = mutableListOf()
                            for (i in 0..(data.getJSONArray("obj").length() - 1)) {
                                mutableList.add((data.getJSONArray("obj")[i] as JSONObject).getString("url"))
                            }
                            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", webEventName, Gson().toJson(mutableList))
                        } else {
                            ComFun.showToast(this@SecondActivity, "上传失败", Toast.LENGTH_LONG)
                        }
                    }

                    override fun uploadProgress(progress: Progress?) {
                        if (uploadDialog == null || (uploadDialog != null && !uploadDialog!!.isShowing)) {
                            uploadDialog = AlertDialog.Builder(this@SecondActivity, R.style.MyDialogStyle).setCancelable(false).create()
                            uploadDialog!!.show()
                            val win: Window = uploadDialog!!.window
                            val uploadView: View = this@SecondActivity.layoutInflater.inflate(R.layout.upload_dialog, null)
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
                            ComFun.showToast(this@SecondActivity, "上传成功", Toast.LENGTH_SHORT)
                            val mutableList: MutableList<String> = mutableListOf()
                            for (i in 0..(data.getJSONArray("obj").length() - 1)) {
                                mutableList.add((data.getJSONArray("obj")[i] as JSONObject).getString("url"))
                            }
                            mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", webEventName, Gson().toJson(mutableList))
                        } else {
                            ComFun.showToast(this@SecondActivity, "上传失败", Toast.LENGTH_LONG)
                        }
                    }

                    override fun uploadProgress(progress: Progress?) {
                        if (uploadDialog == null || (uploadDialog != null && !uploadDialog!!.isShowing)) {
                            uploadDialog = AlertDialog.Builder(this@SecondActivity, R.style.MyDialogStyle).setCancelable(false).create()
                            uploadDialog!!.show()
                            val win: Window = uploadDialog!!.window
                            val uploadView: View = this@SecondActivity.layoutInflater.inflate(R.layout.upload_dialog, null)
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

    class PageOptsSerializable(dos: String, params: String) : Serializable {
        private var titleDos: String = dos
        private var pageParams: String = params

        fun getTitleDos () : MutableList<MutableMap<String, Any>> {
            val titleDosArr = JSONArray(titleDos)
            val mList: MutableList<MutableMap<String, Any>> = mutableListOf()
            for (i in 0..(titleDosArr.length() - 1)) {
                val mMap: MutableMap<String, Any> = mutableMapOf()
                for (k in (titleDosArr[i] as JSONObject).keys()) {
                    mMap[k] = (titleDosArr[i] as JSONObject).get(k)
                }
                mList.add(mMap)
            }
            return mList
        }
        fun getPageParams () : MutableMap<String, Any> {
            val pageParamsObj = JSONObject(pageParams)
            val mMap: MutableMap<String, Any> = mutableMapOf()
            for (k in pageParamsObj.keys()) {
                mMap[k] = pageParamsObj.get(k)
            }
            return mMap
        }
    }
}
