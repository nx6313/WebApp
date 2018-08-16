package com.mtxyao.nxx.webapp.util

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import cn.jpush.android.api.JPushInterface
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.entity.UserData
import java.io.File
import java.io.FileOutputStream

object ComFun {
    var mToast : Toast ? = null

    /**
     * 显示Toast提示信息
     */
    fun showToast (context: Context, txt: String, duration: Int) {
        if (mToast == null) {
            mToast = Toast.makeText(context, txt, duration)
            mToast!!.setGravity(Gravity.CENTER, 0, 0)
        } else {
            mToast!!.setText(txt)
            mToast!!.duration = duration
        }
        mToast!!.show()
    }

    /**
     * 关闭当前正显示的Toast提示信息
     */
    fun hideToast () {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }

    /**
     * 获取状态栏高度
     */
    fun getStateBarHeight (): Int {
        var stateBarHeight = 0
        val resourceId = MyApplication.instance!!.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            stateBarHeight = MyApplication.instance!!.resources.getDimensionPixelSize(resourceId)
        }
        return stateBarHeight
    }

    private var loadingDialog: AlertDialog ? = null
    /**
     * 显示loading弹窗
     */
    fun showLoading (activity: Activity, loadingTipValue: String, cancelable: Boolean?) {
        loadingDialog = AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(cancelable!!).create()
        loadingDialog!!.show()
        val params: WindowManager.LayoutParams = loadingDialog!!.window.attributes
        params.width = getScreenWidth() * 3 / 4
        loadingDialog!!.window.attributes = params

        val win: Window = loadingDialog!!.window
        val loadingView: View = activity.layoutInflater.inflate(R.layout.loading_dialog, null)
        win.setContentView(loadingView)
        val loadingTip: TextView = loadingView.findViewById(R.id.loadingTip)
        loadingTip.text = loadingTipValue
    }

    /**
     * 隐藏loading弹窗
     */
    fun hideLoading () {
        if (loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
        }
    }

    /**
     * 打开输入法
     */
    fun openIME (context: Context, editText: EditText) {
        val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * 关闭输入法
     */
    fun closeIME (context: Context, view: View) {
        if (view.windowToken != null) {
            val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            if (inputMethodManager.isActive) {
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }

    /**
     * 获取屏幕宽度
     */
    fun getScreenWidth () : Int {
        val wm: WindowManager = MyApplication.instance!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    /**
     * 获取程序版本信息
     */
    fun getVersionInfo () : PackageInfo {
        val packageManager: PackageManager = MyApplication.instance!!.packageManager
        return packageManager.getPackageInfo(MyApplication.instance!!.packageName, 0)
    }

    /**
     * 安装apk
     */
    fun installApk (context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        var data: Uri?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(context, "com.fy.niu.fyreorder.fileprovider", file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } else {
            data = Uri.fromFile(file)
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }

    /**
     * 返回相册或截屏中最新的几张图片
     */
    fun getRecentlyPhotoPath (activity: Activity, getCount: Int) : MutableList<Pair<Long, String>> {
        var picList: MutableList<Pair<Long, String>> = mutableListOf()
        // 拍摄照片的地址
        val CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
        // 截屏照片的地址
        val SCREENSHOTS_IMAGE_BUCKET_NAME = getScreenshotsPath()
        // 拍摄照片的地址ID
        val CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME)
        // 截屏照片的地址ID
        val SCREENSHOTS_IMAGE_BUCKET_ID = getBucketId(SCREENSHOTS_IMAGE_BUCKET_NAME)
        // 查询路径和修改时间
        val projection = arrayOf(MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_MODIFIED)
        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"

        val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)
        val selectionArgsForScreenshots = arrayOf(SCREENSHOTS_IMAGE_BUCKET_ID)

        var cameraPair: Pair<Long, String>
        //检查camera文件夹，查询并排序
        var cursor = activity.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
        while (cursor.moveToNext()) {
            if (picList.size == getCount) {
                break
            }
            cameraPair = Pair(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
            picList.add(cameraPair)
        }

        //检查Screenshots文件夹
        var screenshotsPair: Pair<Long, String>
        //查询并排序
        cursor = activity.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgsForScreenshots,
                MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
        while (cursor.moveToNext()) {
            if (picList.size == getCount) {
                break
            }
            screenshotsPair = Pair(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)),
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
            picList.add(screenshotsPair)
        }

        if (!cursor.isClosed) {
            cursor.close()
        }
        return picList
    }

    private fun getBucketId(path: String): String {
        return path.toLowerCase().hashCode().toString()
    }

    /**
     * 获取截图路径
     */
    private fun getScreenshotsPath(): String {
        var path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        var file: File? = File(path)
        if (!file?.exists()!!) {
            path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots"
        }
        return path
    }

    /**
     * 根据路径生成Bitmap资源
     */
    fun getBitMapByPath (path: String, context: Context) : Bitmap {
        val mediaUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val cursor: Cursor = context.contentResolver.query(mediaUri, null,
                MediaStore.Images.Media.DISPLAY_NAME + "= ?",
                arrayOf(path.substring(path.lastIndexOf("/") + 1)), null)

        var uri: Uri ? = null
        if (cursor.moveToFirst()) {
            uri = ContentUris.withAppendedId(mediaUri, cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID)))
        }
        cursor.close()
        return BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))
    }

    /**
     * Bitmap 转 Uri
     */
    fun bitmap2Uri (context: Context, bitmap: Bitmap) : Uri {
        val file = File("${context.cacheDir}${File.separator}${System.currentTimeMillis()}.jpg")
        val os = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
        os.close()
        return Uri.fromFile(file)
    }

    /**
     * 获取bitmap资源大小 字节
     */
    fun getBitmapSize (bitmap: Bitmap) : Int {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.allocationByteCount
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.byteCount
        }
        return bitmap.rowBytes * bitmap.height
    }

    /**
     * 初始化极光推送服务
     */
    fun initJPushServer (context: Context) {
        val userData: UserData? = UserDataUtil.getUserData(context)
        JPushInterface.resumePush(context)
        Log.d("isPushStopped", JPushInterface.isPushStopped(context).toString())
        JPushInterface.setChannel(context, "channel_${android.os.Build.BRAND}") // APK分发渠道
        JPushInterface.setAlias(context, 1, "${userData!!.user!!.id}")
        JPushInterface.setTags(context, 2, setOf("${userData!!.user!!.companyId}"))
    }
}