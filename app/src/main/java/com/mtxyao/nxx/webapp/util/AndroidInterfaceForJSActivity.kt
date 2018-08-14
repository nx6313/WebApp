package com.mtxyao.nxx.webapp.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.util.TypedValue
import android.view.*
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
import com.mtxyao.nxx.webapp.SecondActivity
import com.mtxyao.nxx.webapp.entity.UserData
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.Serializable
import java.math.BigDecimal

class AndroidInterfaceForJSActivity(fgt: BaseWebActivity, agentWeb: AgentWeb, statusBar: View?, titleWrap: View?) {
    private var deliver: Handler = Handler(Looper.getMainLooper())
    private var activity: BaseWebActivity = fgt
    private var mAgentWeb: AgentWeb = agentWeb
    private var sBar: View ? = statusBar
    private var tWrap: View ? = titleWrap
    private var breviaryBitmaps: MutableList<Uri> = mutableListOf()
    private var masterBitmaps: MutableList<Uri> = mutableListOf()
    private var selectIndexList: MutableList<Int> = mutableListOf()

    @JavascriptInterface
    open fun callAndroid (msg: String, params: String) {
        when (msg) {
            "saveUserInfo" -> {
                deliver.post {
                    callByAndroid(msg, null, null)
                }
            }
            "updateTitleBar" -> {
                deliver.post {
                    val pars = JSONObject(params)
                    if (pars.has("title")) {
                        tWrap!!.findViewById<TextView>(R.id.appTitle).text = pars["title"] as String
                    }
                    if (pars.has("bg") && pars["bg"] != "") {
                        sBar!!.setBackgroundColor(Color.parseColor(pars["bg"] as String))
                        tWrap!!.setBackgroundColor(Color.parseColor(pars["bg"] as String))
                    }
                    if (pars.has("dos")) {
                        if (pars.getBoolean("dos")) {
                            tWrap!!.findViewById<LinearLayout>(R.id.titleBtnWrap).visibility = View.VISIBLE
                        } else {
                            tWrap!!.findViewById<LinearLayout>(R.id.titleBtnWrap).visibility = View.GONE
                        }
                    }
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
                    val has = if (pars.has("has")) { pars.getInt("has") } else { 0 }
                    val max = if (pars.has("max")) { pars.getInt("max") } else { 0 }
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
                            BaseWebActivity.webEventName = null
                            breviaryBitmaps.clear()
                            masterBitmaps.clear()
                            selectIndexList.clear()
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
                            contentView.findViewById<TextView>(R.id.getRecentlyLoading).visibility = View.VISIBLE
                            contentView.findViewById<HorizontalScrollView>(R.id.recentlyPicScrollView).visibility = View.GONE
                            contentView.findViewById<TextView>(R.id.getPicByPhoto).tag = "photo"
                            contentView.findViewById<TextView>(R.id.getPicByPhoto).setOnClickListener {
                                if (it.tag.toString() == "photo") {
                                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), BaseWebActivity.REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE)
                                    } else {
                                        BaseWebActivity.webEventName = event
                                        val intent = Intent(Intent.ACTION_GET_CONTENT, null)
                                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                                        activity.startActivityForResult(intent, BaseWebActivity.PICKER_PIC)
                                    }
                                } else if (it.tag.toString() == "breviary") {
                                    val selectUriList: MutableList<Uri> = mutableListOf()
                                    for (i in selectIndexList) {
                                        selectUriList.add(breviaryBitmaps[i])
                                    }
                                    val bundle = Bundle()
                                    bundle.putSerializable("baseWebSerializable", BaseWebActivity.BaseWebSerializable(selectUriList))
                                    bundle.putString("webEventName", event)
                                    val msg = Message()
                                    msg.what = BaseWebActivity.MSG_SELECT_IMAGE
                                    msg.data = bundle
                                    BaseWebActivity.baseWebHandler!!.sendMessage(msg)
                                }
                                if (window.isShowing) {
                                    window.dismiss()
                                    breviaryBitmaps.clear()
                                    masterBitmaps.clear()
                                    selectIndexList.clear()
                                }
                            }
                            contentView.findViewById<TextView>(R.id.getPicByCamera).tag = "camera"
                            contentView.findViewById<TextView>(R.id.getPicByCamera).setOnClickListener {
                                if (it.tag.toString() == "camera") {
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

                                    BaseWebActivity.webEventName = event
                                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, BaseWebActivity.imageUri)
                                    activity.startActivityForResult(intent, BaseWebActivity.PICKER_PHOTO)
                                } else if (it.tag.toString() == "master") {
                                    val selectUriList: MutableList<Uri> = mutableListOf()
                                    for (i in selectIndexList) {
                                        selectUriList.add(masterBitmaps[i])
                                    }
                                    val bundle = Bundle()
                                    bundle.putSerializable("baseWebSerializable", BaseWebActivity.BaseWebSerializable(selectUriList))
                                    bundle.putString("webEventName", event)
                                    val msg = Message()
                                    msg.what = BaseWebActivity.MSG_SELECT_IMAGE
                                    msg.data = bundle
                                    BaseWebActivity.baseWebHandler!!.sendMessage(msg)
                                }
                                if (window.isShowing) {
                                    window.dismiss()
                                    breviaryBitmaps.clear()
                                    masterBitmaps.clear()
                                    selectIndexList.clear()
                                }
                            }
                            contentView.findViewById<TextView>(R.id.getPicCancel).setOnClickListener {
                                if (window.isShowing) {
                                    window.dismiss()
                                    breviaryBitmaps.clear()
                                    masterBitmaps.clear()
                                    selectIndexList.clear()
                                }
                            }
                            window.showAtLocation(activity.findViewById<View>(android.R.id.content), Gravity.BOTTOM, 0, 0)
                            val lp = activity.window.attributes
                            lp.alpha = 0.3f
                            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                            activity.window.attributes = lp
                            val recentlyHandler = @SuppressLint("HandlerLeak")
                            object : Handler() {
                                override fun handleMessage(msg: Message?) {
                                    super.handleMessage(msg)
                                    when (msg!!.what) {
                                        0 -> {
                                            contentView.findViewById<HorizontalScrollView>(R.id.recentlyPicScrollView).visibility = View.VISIBLE
                                            val recentlyImages = (msg!!.data.getSerializable("recentlySerializable") as RecentlySerializable).recentlyImages
                                            for ((recentlyIndex, recently) in recentlyImages.withIndex()) {
                                                val imageBitmapMaster = ComFun.getBitMapByPath(recently.second, activity)
                                                val imageBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(recently.second), 100, 140)
                                                val sizeMaster = ComFun.getBitmapSize(imageBitmapMaster)
                                                val size = ComFun.getBitmapSize(imageBitmap)

                                                breviaryBitmaps.add(ComFun.bitmap2Uri(activity, imageBitmap))
                                                masterBitmaps.add(ComFun.bitmap2Uri(activity, imageBitmapMaster))

                                                val recentlyItemLayout = RelativeLayout(activity)
                                                val layoutLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                                                layoutLs.setMargins(DisplayUtil.dip2px(activity, 4f), 0, DisplayUtil.dip2px(activity, 4f), 0)
                                                recentlyItemLayout.layoutParams = layoutLs
                                                recentlyItemLayout.isClickable = true
                                                recentlyItemLayout.isFocusable = true
                                                recentlyItemLayout.tag = "$size/$sizeMaster/$recentlyIndex"
                                                recentlyItemLayout.setOnClickListener {
                                                    val recentlyImg = it.findViewWithTag<ShadeImageView>("recentlyImg")
                                                    val selectImg = it.findViewWithTag<ImageView>("selectImg")
                                                    recentlyImg.shade(!recentlyImg.isShade())
                                                    if (recentlyImg.isShade()) {
                                                        selectImg.visibility = View.VISIBLE
                                                    } else {
                                                        selectImg.visibility = View.GONE
                                                    }
                                                    var selectCount = 0
                                                    var breviarySelectSize = 0.0
                                                    var masterSelectSize = 0.0
                                                    selectIndexList.clear()
                                                    for (c in 0..((it.parent as ViewGroup).childCount - 1)) {
                                                        if ((it.parent as ViewGroup).getChildAt(c).findViewWithTag<ShadeImageView>("recentlyImg").isShade()) {
                                                            selectCount++
                                                            selectIndexList.add(c)
                                                            val breviarySize = ((it.parent as ViewGroup).getChildAt(c).tag as String).split("/")[0].toDouble() / 1024f
                                                            val masterSize = ((it.parent as ViewGroup).getChildAt(c).tag as String).split("/")[1].toDouble() / 1024f
                                                            breviarySelectSize += breviarySize
                                                            masterSelectSize += masterSize
                                                        }
                                                    }
                                                    if (selectCount > max - has) {
                                                        ComFun.showToast(activity, "最多可选择 $max 张图片", Toast.LENGTH_SHORT)
                                                        if (recentlyImg.isShade()) {
                                                            selectIndexList.remove((it.tag as String).split("/")[2].toInt())
                                                            recentlyImg.shade(!recentlyImg.isShade())
                                                            selectImg.visibility = View.GONE
                                                        }
                                                    } else {
                                                        if (selectCount > 0) {
                                                            var selectImageSizeStr: String
                                                            selectImageSizeStr = if (masterSelectSize < 1024f) {
                                                                val selectImageSize = BigDecimal(masterSelectSize).setScale(2, BigDecimal.ROUND_DOWN)
                                                                "$selectImageSize KB"
                                                            } else {
                                                                val selectImageSize = BigDecimal(masterSelectSize / 1024f).setScale(2, BigDecimal.ROUND_DOWN)
                                                                "$selectImageSize MB"
                                                            }
                                                            contentView.findViewById<TextView>(R.id.getPicByPhoto).tag = "breviary"
                                                            contentView.findViewById<TextView>(R.id.getPicByPhoto).text = "发送 $selectCount 张照片"
                                                            contentView.findViewById<TextView>(R.id.getPicByCamera).tag = "master"
                                                            contentView.findViewById<TextView>(R.id.getPicByCamera).text = "发送 $selectCount 张原图（共 $selectImageSizeStr）"
                                                        } else {
                                                            contentView.findViewById<TextView>(R.id.getPicByPhoto).tag = "photo"
                                                            contentView.findViewById<TextView>(R.id.getPicByPhoto).text = "相册"
                                                            contentView.findViewById<TextView>(R.id.getPicByCamera).tag = "camera"
                                                            contentView.findViewById<TextView>(R.id.getPicByCamera).text = "拍摄"
                                                        }
                                                    }
                                                }

                                                val recentlyImg = ShadeImageView(activity)
                                                val recentlyLs = RelativeLayout.LayoutParams(DisplayUtil.dip2px(activity, 100f), DisplayUtil.dip2px(activity, 140f))
                                                recentlyImg.layoutParams = recentlyLs
                                                Picasso.with(activity).load(ComFun.bitmap2Uri(activity, imageBitmapMaster)).noFade().centerInside().fit().into(recentlyImg)
                                                // recentlyImg.setImageBitmap(imageBitmapMaster)
                                                recentlyImg.tag = "recentlyImg"
                                                recentlyItemLayout.addView(recentlyImg)

                                                val selectImg = ImageView(activity)
                                                val selectLs = RelativeLayout.LayoutParams(DisplayUtil.dip2px(activity, 20f), DisplayUtil.dip2px(activity, 20f))
                                                selectLs.setMargins(DisplayUtil.dip2px(activity, 8f), DisplayUtil.dip2px(activity, 8f), 0, 0)
                                                selectImg.layoutParams = selectLs
                                                selectImg.setImageResource(R.drawable.sure)
                                                selectImg.tag = "selectImg"
                                                selectImg.visibility = View.GONE
                                                recentlyItemLayout.addView(selectImg)

                                                contentView.findViewById<LinearLayout>(R.id.recentlyPic).addView(recentlyItemLayout)
                                            }
                                            contentView.findViewById<TextView>(R.id.getRecentlyLoading).visibility = View.GONE
                                        }
                                        1 -> {
                                            contentView.findViewById<TextView>(R.id.getRecentlyLoading).visibility = View.GONE
                                            contentView.findViewById<HorizontalScrollView>(R.id.recentlyPicScrollView).visibility = View.GONE
                                            ComFun.showToast(activity, "最近暂无图片", Toast.LENGTH_SHORT)
                                        }
                                    }
                                }
                            }
                            Thread(Runnable {
                                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), BaseWebActivity.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE)
                                } else {
                                    val recentlyImages = ComFun.getRecentlyPhotoPath(activity, 6)
                                    if (recentlyImages.size > 0) {
                                        val bundle = Bundle()
                                        bundle.putSerializable("recentlySerializable", RecentlySerializable(recentlyImages))
                                        val msg = Message()
                                        msg.what = 0
                                        msg.data = bundle
                                        recentlyHandler.sendMessage(msg)
                                    } else {
                                        val msg = Message()
                                        msg.what = 1
                                        recentlyHandler.sendMessage(msg)
                                    }
                                }
                            }).start()
                        }
                    }
                }
            }
            "skipPage" -> {
                val pars = JSONObject(params)
                val webPath = pars["path"] as String
                val title = pars["title"] as String
                val titleDos = (pars["titleDos"] as JSONArray).toString()
                val titleBarColor = pars["titleBarColor"] as String
                val statusBarStyle = pars["statusBarStyle"] as String
                val fullPage = pars["fullPage"] as Boolean
                val pageParams = (pars["pageParams"] as JSONObject).toString()
                val skipIntent = Intent(activity, SecondActivity::class.java)
                skipIntent.putExtra("webUri", webPath)
                skipIntent.putExtra("titleName", title)
                skipIntent.putExtra("pageOpts", SecondActivity.PageOptsSerializable(titleDos, pageParams))
                skipIntent.putExtra("titleBarColor", titleBarColor)
                skipIntent.putExtra("titleBarHighlight", statusBarStyle == "highlight")
                skipIntent.putExtra("fullPage", fullPage)
                activity.startActivity(skipIntent)
            }
            "addTitleBtn" -> {
                deliver.post {
                    if (params != "") {
                        val pars = JSONArray(params)
                        val titleBtnWrap = activity.findViewById<LinearLayout>(R.id.titleBtnWrap)
                        if (titleBtnWrap != null) {
                            titleBtnWrap.visibility = View.VISIBLE
                            titleBtnWrap.removeAllViews()
                            for (b in 0..(pars.length() - 1)) {
                                val btnObj = pars.getJSONObject(b)
                                val event = btnObj.getString("event")
                                val txt = btnObj.getString("txt")
                                with(titleBtnWrap) {
                                    val titleBtn = Button(activity)
                                    val ls = LinearLayout.LayoutParams(DisplayUtil.dip2px(activity, 40f), DisplayUtil.dip2px(activity, 26f))
                                    if (b > 0) {
                                        ls.setMargins(DisplayUtil.dip2px(activity, -1f), 0, 0, 0)
                                    }
                                    titleBtn.layoutParams = ls
                                    if (this.tag == "highlight") {
                                        titleBtn.setBackgroundResource(R.drawable.border_line_while_)
                                        titleBtn.setTextColor(Color.parseColor("#40AAE6"))
                                        if (b == 0) {
                                            titleBtn.setBackgroundResource(R.drawable.border_line_while_left)
                                            titleBtn.setTextColor(Color.parseColor("#ffffff"))
                                        }
                                        if (b == pars.length() - 1) {
                                            titleBtn.setBackgroundResource(R.drawable.border_line_while_right_)
                                        }
                                    } else if (this.tag == "dark") {
                                        titleBtn.setBackgroundResource(R.drawable.border_line_dark_)
                                        titleBtn.setTextColor(Color.parseColor("#A8A8A8"))
                                        if (b == 0) {
                                            titleBtn.setBackgroundResource(R.drawable.border_line_dark_left)
                                            titleBtn.setTextColor(Color.parseColor("#464646"))
                                        }
                                        if (b == pars.length() - 1) {
                                            titleBtn.setBackgroundResource(R.drawable.border_line_dark_right_)
                                        }
                                    }
                                    titleBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                                    titleBtn.tag = event
                                    titleBtn.text = txt
                                    addView(titleBtn)
                                    titleBtn.setOnClickListener {
                                        if ((it.parent as View).tag == "highlight") {
                                            var index = 0
                                            for (c in 0..((it.parent as ViewGroup).childCount - 1)) {
                                                if ((it.parent as ViewGroup).getChildAt(c).tag == it.tag) {
                                                    index = c
                                                }
                                                (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_while_)
                                                if (c == 0) {
                                                    (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_while_left_)
                                                }
                                                if (c == (it.parent as ViewGroup).childCount - 1) {
                                                    (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_while_right_)
                                                }
                                                ((it.parent as ViewGroup).getChildAt(c) as Button).setTextColor(Color.parseColor("#40AAE6"))
                                            }
                                            (it as Button).setBackgroundResource(R.drawable.border_line_while)
                                            if (index == 0) {
                                                it.setBackgroundResource(R.drawable.border_line_while_left)
                                            }
                                            if (index == (it.parent as ViewGroup).childCount - 1) {
                                                it.setBackgroundResource(R.drawable.border_line_while_right)
                                            }
                                            it.setTextColor(Color.parseColor("#ffffff"))
                                        } else if ((it.parent as View).tag == "dark") {
                                            var index = 0
                                            for (c in 0..((it.parent as ViewGroup).childCount - 1)) {
                                                if ((it.parent as ViewGroup).getChildAt(c).tag == it.tag) {
                                                    index = c
                                                }
                                                (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_dark_)
                                                if (c == 0) {
                                                    (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_dark_left_)
                                                }
                                                if (c == (it.parent as ViewGroup).childCount - 1) {
                                                    (it.parent as ViewGroup).getChildAt(c).setBackgroundResource(R.drawable.border_line_dark_right_)
                                                }
                                                ((it.parent as ViewGroup).getChildAt(c) as Button).setTextColor(Color.parseColor("#A8A8A8"))
                                            }
                                            (it as Button).setBackgroundResource(R.drawable.border_line_dark)
                                            if (index == 0) {
                                                it.setBackgroundResource(R.drawable.border_line_dark_left)
                                            }
                                            if (index == (it.parent as ViewGroup).childCount - 1) {
                                                it.setBackgroundResource(R.drawable.border_line_dark_right)
                                            }
                                            it.setTextColor(Color.parseColor("#464646"))
                                        }
                                        callByAndroid("androidEvent", it.tag.toString(), "")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // 调用web的方法
    private fun callByAndroid (jsFunName: String, event: String?, params: String?) {
        when (jsFunName) {
            "saveUserInfo" -> {
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

    private class RecentlySerializable(recentlyList: MutableList<Pair<Long, String>>) : Serializable {
        open val recentlyImages: MutableList<Pair<Long, String>> = recentlyList
    }
}