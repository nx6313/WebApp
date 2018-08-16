package com.mtxyao.nxx.webapp

import com.mtxyao.nxx.webapp.util.PageOpt
import kotlinx.android.synthetic.main.activity_app.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable

class SecondActivity : BaseWebActivity() {
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
