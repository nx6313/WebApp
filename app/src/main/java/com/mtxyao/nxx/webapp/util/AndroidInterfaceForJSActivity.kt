package com.mtxyao.nxx.webapp.util

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.widget.*
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.google.gson.Gson
import com.just.agentweb.AgentWeb
import com.mtxyao.nxx.webapp.BaseWebActivity
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.entity.UserData
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class AndroidInterfaceForJSActivity(fgt: BaseWebActivity, agentWeb: AgentWeb) {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var activity: BaseWebActivity = fgt
    private var mAgentWeb: AgentWeb = agentWeb

    @JavascriptInterface
    open fun callAndroid (msg: String, params: String) {
        when (msg) {
            "saveUserInfoForAndroid" -> {
                deliver.post {
                    callByAndroid(msg, null, null)
                }
            }
            "back" -> {
                deliver.post {
                    activity.finish()
                }
            }
            "callPhone" -> {
                deliver.post {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CALL_PHONE), BaseWebActivity.REQUEST_CODE_ASK_CALL_PHONE)
                        } else {
                            val callIntent = Intent(Intent.ACTION_CALL)
                            val data = Uri.parse("tel:$params")
                            callIntent.data = data
                            activity.startActivity(callIntent!!)
                        }
                    } else {
                        val callIntent = Intent(Intent.ACTION_CALL)
                        val data = Uri.parse("tel:$params")
                        callIntent.data = data
                        activity.startActivity(callIntent!!)
                    }
                }
            }
            "selectPic" -> {
                deliver.post {
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), BaseWebActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
                    } else {
                        val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                        activity.startActivityForResult(intent, BaseWebActivity.PICKER_PIC)
                    }
                }
            }
            "selectPhoto" -> {
                deliver.post {
                    val outputImage = File(activity.externalCacheDir, "output_image.jpg")
                    if (outputImage.exists()) {
                        outputImage.delete()
                    }
                    outputImage.createNewFile()
                    BaseWebActivity.imageUri = if (Build.VERSION.SDK_INT >= 24) {
                        FileProvider.getUriForFile(activity, MyApplication.instance!!.applicationContext.packageName + ".provider", outputImage)
                    } else {
                        Uri.fromFile(outputImage)
                    }

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, BaseWebActivity.imageUri)
                    activity.startActivityForResult(intent, BaseWebActivity.PICKER_PHOTO)
                }
            }
            "pickerView" -> {
                deliver.post {
                    ComFun.closeIME(activity, activity.findViewById(android.R.id.content))
                    val pars = JSONObject(params)
                    val event = pars.getString("event")
                    val type = pars.getString("type")
                    val title = if (pars.has("title")) { pars.getString("title") } else { "" }
                    val outSideCancelable = if (pars.has("outSideCancelable")) { pars.getBoolean("outSideCancelable") } else { true }
                    val cyclic = if (pars.has("cyclic")) { pars.getBoolean("cyclic") } else { false }
                    val items = if (pars.has("items")) { pars.getJSONArray("items") } else { JSONArray() }
                    when (type) {
                        "time" -> {
                            val pvTime = TimePickerBuilder(activity, OnTimeSelectListener { date, v ->
                                callByAndroid("androidEvent", event, date.time.toString())
                            }).setType(booleanArrayOf(true, true, true, true, true, true)) // 默认全部显示
                                    .setCancelText("取消") // 取消按钮文字
                                    .setSubmitText("确认") // 确认按钮文字
                                    .setContentTextSize(18) // 滚轮文字大小
                                    .setTitleSize(20) // 标题文字大小
                                    .setTitleText(title) // 标题文字
                                    .setOutSideCancelable(outSideCancelable) // 点击屏幕，点在控件外部范围时，是否取消显示
                                    .isCyclic(cyclic) // 是否循环滚动
                                    .setTitleColor(Color.parseColor("#2B2B2B")) // 标题文字颜色
                                    .setSubmitColor(Color.parseColor("#484848")) // 确定按钮文字颜色
                                    .setCancelColor(Color.parseColor("#8C8C8C")) // 取消按钮文字颜色
                                    .setTitleBgColor(Color.parseColor("#FFFFFF")) // 标题背景颜色 Night mode
                                    .setBgColor(Color.parseColor("#FFFFFF")) // 滚轮背景颜色 Night mode
                                    // .setDate(selectedDate) // 如果不设置的话，默认是系统时间*/
                                    // .setRangDate(startDate, endDate)//起始终止年月日设定
                                    .setLabel("年","月","日","时","分","秒") // 默认设置为年月日时分秒
                                    .isCenterLabel(false) // 是否只显示中间选中项的label文字，false则每项item全部都带有label
                                    .isDialog(false) // 是否显示为对话框样式
                                    .build()
                            pvTime.show()
                        }
                        "option" -> {
                            val pvOptions = OptionsPickerBuilder(activity, OnOptionsSelectListener { options1, options2, options3, v ->
                                callByAndroid("androidEvent", event, Gson().toJson(mapOf(
                                        "index1" to options1,
                                        "index2" to options2,
                                        "index3" to options3
                                )))
                            }).setCancelText("取消") // 取消按钮文字
                                    .setSubmitText("确认") // 确认按钮文字
                                    .setContentTextSize(18) // 滚轮文字大小
                                    .setTitleSize(20) // 标题文字大小
                                    .setTitleText(title) // 标题文字
                                    .setOutSideCancelable(outSideCancelable) // 点击屏幕，点在控件外部范围时，是否取消显示
                                    .setTitleColor(Color.parseColor("#2B2B2B")) // 标题文字颜色
                                    .setSubmitColor(Color.parseColor("#484848")) // 确定按钮文字颜色
                                    .setCancelColor(Color.parseColor("#8C8C8C")) // 取消按钮文字颜色
                                    .setTitleBgColor(Color.parseColor("#FFFFFF")) // 标题背景颜色 Night mode
                                    .setBgColor(Color.parseColor("#FFFFFF")) // 滚轮背景颜色 Night mode.build<String>()
                                    .build<String>()
                            val pickerItem = mutableListOf<String>()
                            for (i in 0..(items.length() - 1)) {
                                pickerItem.add(i, items.getJSONObject(i).getString("val"))
                            }
                            pvOptions.setPicker(pickerItem)
                            pvOptions.show()
                        }
                        "picture" -> {
                            ComFun.showLoading(activity, "获取图片中...", false)
                            val contentView = LayoutInflater.from(activity).inflate(R.layout.picture_dialog, null)
                            val window = PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT, true)
                            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                            window.isOutsideTouchable = true
                            window.isTouchable = true
                            window.animationStyle = R.style.animTranslate
                            window.setOnDismissListener {
                                val lp = activity.window.attributes
                                lp.alpha = 1.0f
                                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                                activity.window.attributes = lp
                            }
                            val recently = ComFun.getRecentlyPhotoPath(activity)
                            if (recently !== "") {
                                val recentlyImg = ImageView(activity)
                                recentlyImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(activity, 100f), DisplayUtil.dip2px(activity, 140f))
                                recentlyImg.setImageBitmap(ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(recently), 100, 140))
                                contentView.findViewById<LinearLayout>(R.id.recentlyPic).addView(recentlyImg)
                            } else {
                                contentView.findViewById<HorizontalScrollView>(R.id.recentlyPicScrollView).visibility = View.GONE
                            }
                            contentView.findViewById<TextView>(R.id.getPicByPhoto).setOnClickListener {
                                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), BaseWebActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
                                } else {
                                    val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                                    intent.putExtra("webEventName", event)
                                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                                    activity.startActivityForResult(intent, BaseWebActivity.PICKER_PIC)
                                }
                                if (window.isShowing) {
                                    window.dismiss()
                                }
                            }
                            contentView.findViewById<TextView>(R.id.getPicByCamera).setOnClickListener {
                                val outputImage = File(activity.externalCacheDir, "output_image.jpg")
                                if (outputImage.exists()) {
                                    outputImage.delete()
                                }
                                outputImage.createNewFile()
                                BaseWebActivity.imageUri = if (Build.VERSION.SDK_INT >= 24) {
                                    FileProvider.getUriForFile(activity, MyApplication.instance!!.applicationContext.packageName + ".provider", outputImage)
                                } else {
                                    Uri.fromFile(outputImage)
                                }

                                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, BaseWebActivity.imageUri)
                                intent.putExtra("webEventName", event)
                                activity.startActivityForResult(intent, BaseWebActivity.PICKER_PHOTO)
                                if (window.isShowing) {
                                    window.dismiss()
                                }
                            }
                            contentView.findViewById<TextView>(R.id.getPicCancel).setOnClickListener {
                                if (window.isShowing) {
                                    window.dismiss()
                                }
                            }
                            window.showAtLocation(activity.findViewById<View>(android.R.id.content), Gravity.BOTTOM, 0, 0)
                            ComFun.hideLoading()
                            val lp = activity.window.attributes
                            lp.alpha = 0.3f
                            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                            activity.window.attributes = lp
                        }
                    }
                }
            }
        }
    }

    // 调用web的方法
    private fun callByAndroid (jsFunName: String, event: String?, params: String?) {
        when (jsFunName) {
            "saveUserInfoForAndroid" -> {
                val userData: UserData ? = UserDataUtil.getUserData(activity)
                mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName, Gson().toJson(userData))
            }
            "androidEvent" -> {
                mAgentWeb!!.jsAccessEntrace.quickCallJs(jsFunName, event, params)
            }
        }
    }

    @JavascriptInterface
    open fun setTimeOut (event: String, duration: Int) {
        Handler().postDelayed({
            deliver.post {
                mAgentWeb!!.jsAccessEntrace.quickCallJs("androidEvent", event)
            }
        }, duration.toLong())
    }
}