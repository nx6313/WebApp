package com.mtxyao.nxx.webapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.StringCallback
import com.lzy.okgo.model.Response
import com.mtxyao.nxx.webapp.*
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.DisplayUtil
import com.mtxyao.nxx.webapp.util.ObservableScrollView
import com.mtxyao.nxx.webapp.util.Urls
import org.json.JSONObject

class ConsoleFragment : BaseFragment(false), ObservableScrollView.ScrollViewListener {
    private var topApps = mapOf(
            R.drawable.do_lhb to listOf("龙虎榜", "app-winner-list", false, true, "#007EC8"),
            R.drawable.do_znfx to listOf("即时绩效", "app-inteligent-analysis", false, true, "#007EC8"),
            R.drawable.do_khyx to listOf("客户营销", "app-client-kpi", false, true, "#007EC8"),
            R.drawable.do_dbdb to listOf("智能分析", "h5/kaohe/kaohe.html", false, true, "#004E97"),
            R.drawable.do_dlhs to listOf("独立核算", "h5/hesuan/hesuan.html", false, true, "#004E97")
    )
    private var apps = mapOf(
            Pair("人事管理", mapOf(
                    R.drawable.do_kqdk to listOf("考勤打卡", "", false, true, "#004E97"),
                    R.drawable.do_sp to listOf("审批", "", false, true, "#004E97"),
                    R.drawable.do_qj to listOf("请假", "", false, false, ""),
                    R.drawable.do_chuc to listOf("出差", "", false, false, "")
            )),
            Pair("OMS系统", mapOf(
                    R.drawable.do_xcyd to listOf("新车预定", "", false, false, ""),
                    R.drawable.do_xcxs to listOf("新车销售", "", false, false, ""),
                    R.drawable.do_dlhs2 to listOf("独立核算", "", false, true, "#1C6EC8")
            )),
            Pair("CRM系统", mapOf(
                    R.drawable.do_crm to listOf("CRM", "", false, true, "#004E97"),
                    R.drawable.do_add to listOf("添加", "", false, true, "")
            ))
    )

    private var scrollView: ObservableScrollView ? = null
    private var mainTopMenu_1: LinearLayout ? = null
    private var mainTopMenu_2: LinearLayout ? = null

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_console, container, false)
    }

    override fun initPageData(fragmentView: View) {
        val topAppsWrap = fragmentView.findViewById<LinearLayout>(R.id.topAppsWrap)
        initTopApps(topAppsWrap, topApps)
        val appsWrap = fragmentView.findViewById<LinearLayout>(R.id.appsWrap)
        initApps(appsWrap, apps)
        scrollView = fragmentView.findViewById(R.id.mainScrollView)
        scrollView!!.setListener(this)
        mainTopMenu_1 = fragmentView.findViewById(R.id.mainTopMenu_1)
        mainTopMenu_2 = fragmentView.findViewById(R.id.mainTopMenu_2)
        mainTopMenu_2!!.visibility = View.GONE
        mainTopMenu_2!!.setPadding(0, ComFun.getStateBarHeight(), 0, DisplayUtil.dip2px(this.context!!, 10f))

        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_1_clientIn).setOnClickListener {
            toDo("clientIn")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_2_clientIn).setOnClickListener {
            toDo("clientIn")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_1_writeFollowUp).setOnClickListener {
            toDo("writeFollowUp")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_2_writeFollowUp).setOnClickListener {
            toDo("writeFollowUp")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_1_newTask).setOnClickListener {
            toDo("newTask")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_2_newTask).setOnClickListener {
            toDo("newTask")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_1_newOrder).setOnClickListener {
            toDo("newOrder")
        }
        fragmentView.findViewById<LinearLayout>(R.id.mainTopMenu_2_newOrder).setOnClickListener {
            toDo("newOrder")
        }

        val informMsg = fragmentView.findViewById<TextView>(R.id.informMsg)
        OkGo.get<String>(Urls.URL_BEFORE + Urls.URL_INFORM_MSG)
                .tag(this)
                .params("page", 1)
                .params("rows", 1)
                .execute(object: StringCallback() {
                    override fun onSuccess(response: Response<String>?) {
                        if (response != null) {
                            val data = JSONObject(response!!.body())
                            if (data.has("code") && data.getString("code") == "1") {
                                informMsg.text = data.getJSONArray("list").getJSONObject(0).getString("noticeContent")
                            }
                        }
                    }
                })
    }

    private fun <K, V> initTopApps (appsWrap: LinearLayout, topApps: Map<K, V>) {
        for ((icon, data) in (topApps as Map<Int, List<Any>>)) {
            val appTitle: String = data[0] as String
            val webUri: String = data[1] as String
            val fullPage: Boolean = data[2] as Boolean
            val titleBarHighlight: Boolean = data[3] as Boolean
            val titleBarColor: String = data[4] as String

            with(appsWrap) {
                val itemAppItemLayout = LinearLayout(this.context)
                itemAppItemLayout.gravity = Gravity.CENTER
                itemAppItemLayout.orientation = LinearLayout.VERTICAL
                itemAppItemLayout.setPadding(DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f))
                val itemAppItemLayoutLs = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                itemAppItemLayout.layoutParams = itemAppItemLayoutLs
                val appImg = ImageView(this.context)
                appImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(this.context!!, 50f), DisplayUtil.dip2px(this.context!!, 50f))
                appImg.setImageResource(icon)
                itemAppItemLayout.addView(appImg)
                val appTxt = TextView(this.context)
                val appTxtLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                appTxtLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 6f), 0, 0)
                appTxt.layoutParams = appTxtLs
                appTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                appTxt.text = appTitle
                itemAppItemLayout.addView(appTxt)
                itemAppItemLayout.setOnClickListener {
                    val appIntent = Intent(this@ConsoleFragment.context, AppActivity::class.java)
                    appIntent.putExtra("titleName", appTitle)
                    appIntent.putExtra("webUri", webUri)
                    appIntent.putExtra("fullPage", fullPage)
                    appIntent.putExtra("titleBarHighlight", titleBarHighlight)
                    appIntent.putExtra("titleBarColor", titleBarColor)
                    this@ConsoleFragment.context!!.startActivity(appIntent)
                }
                addView(itemAppItemLayout)
            }
        }
    }

    private fun <K, V> initApps (appsWrap: LinearLayout, apps: Map<K, V>) {
        var itemIndex = 0
        for (item in apps) {
            val itemLayout = RelativeLayout(this.context)
            itemLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val itemTitleTv = TextView(this.context)
            val itemTitleLs = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            itemTitleLs.setMargins(DisplayUtil.dip2px(this.context!!, 10f), DisplayUtil.dip2px(this.context!!, 4f), 0, 0)
            itemTitleTv.layoutParams = itemTitleLs
            itemTitleTv.text = item.toPair().first.toString()
            itemTitleTv.paint.isFakeBoldText = true
            itemTitleTv.setTextColor(Color.parseColor("#4b4b4b"))
            itemTitleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            itemLayout.addView(itemTitleTv)
            val appsGridLayout = GridLayout(this.context)
            val appsGridLayoutLs = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            appsGridLayoutLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 30f), 0, DisplayUtil.dip2px(this.context!!, 6f))
            appsGridLayout.layoutParams = appsGridLayoutLs
            appsGridLayout.rowCount = Math.ceil(((item.toPair().second as Map<*, *>).size.toDouble() / 4)).toInt()
            appsGridLayout.columnCount = 4
            appsGridLayout.orientation = GridLayout.HORIZONTAL

            with(appsGridLayout) {
                var needFillCount: Int = 4 - (item.toPair().second as Map<*, *>).size
                for ((k, v) in (item.toPair().second as Map<Int, List<Any>>)) {
                    val appTitle: String = v[0] as String
                    val webUri: String = v[1] as String
                    val fullPage: Boolean = v[2] as Boolean
                    val titleBarHighlight: Boolean = v[3] as Boolean
                    val titleBarColor: String = v[4] as String
                    val appDrawable: Int = k

                    val itemAppItemLayout = LinearLayout(this.context)
                    if (k === R.drawable.do_add) {
                        itemAppItemLayout.tag = "add-apps"
                    }
                    itemAppItemLayout.gravity = Gravity.CENTER
                    itemAppItemLayout.orientation = LinearLayout.VERTICAL
                    itemAppItemLayout.setPadding(0, DisplayUtil.dip2px(this.context!!, 6f), 0, DisplayUtil.dip2px(this.context!!, 6f))
                    val itemAppItemLayoutLs = GridLayout.LayoutParams()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        itemAppItemLayoutLs.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                        itemAppItemLayoutLs.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                    }
                    itemAppItemLayout.layoutParams = itemAppItemLayoutLs
                    val appImg = ImageView(this.context)
                    appImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(this.context!!, 50f), DisplayUtil.dip2px(this.context!!, 50f))
                    appImg.setImageResource(appDrawable)
                    itemAppItemLayout.addView(appImg)
                    val appTxt = TextView(this.context)
                    val appTxtLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    appTxtLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 6f), 0, 0)
                    appTxt.layoutParams = appTxtLs
                    appTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    appTxt.text = appTitle
                    itemAppItemLayout.addView(appTxt)
                    itemAppItemLayout.setOnClickListener {
                        if (it.tag === "add-apps") {

                        } else {
                            val appIntent = Intent(this@ConsoleFragment.context, AppActivity::class.java)
                            appIntent.putExtra("titleName", appTitle)
                            appIntent.putExtra("webUri", webUri)
                            appIntent.putExtra("fullPage", fullPage)
                            appIntent.putExtra("titleBarHighlight", titleBarHighlight)
                            appIntent.putExtra("titleBarColor", titleBarColor)
                            this@ConsoleFragment.context!!.startActivity(appIntent)
                        }
                    }
                    addView(itemAppItemLayout)
                }
                if ((item.toPair().second as Map<*, *>).size < 4) {
                    while (needFillCount > 0) {
                        val itemAppItemLayout = LinearLayout(this.context)
                        itemAppItemLayout.gravity = Gravity.CENTER
                        itemAppItemLayout.orientation = LinearLayout.VERTICAL
                        itemAppItemLayout.setPadding(0, DisplayUtil.dip2px(this.context!!, 6f), 0, DisplayUtil.dip2px(this.context!!, 6f))
                        val itemAppItemLayoutLs = GridLayout.LayoutParams()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            itemAppItemLayoutLs.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                            itemAppItemLayoutLs.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                        }
                        itemAppItemLayout.layoutParams = itemAppItemLayoutLs
                        val appImg = ImageView(this.context)
                        appImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(this.context!!, 50f), DisplayUtil.dip2px(this.context!!, 50f))
                        itemAppItemLayout.addView(appImg)
                        val appTxt = TextView(this.context)
                        val appTxtLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        appTxtLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 6f), 0, 0)
                        appTxt.layoutParams = appTxtLs
                        appTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                        itemAppItemLayout.addView(appTxt)
                        addView(itemAppItemLayout)
                        needFillCount--
                    }
                }
            }

            itemLayout.addView(appsGridLayout)
            appsWrap.addView(itemLayout)

            if (itemIndex < apps.size - 1) {
                val lineView = View(this.context)
                val lineViewLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(this.context!!, 0.6f))
                lineViewLs.setMargins(DisplayUtil.dip2px(this.context!!, 8f), DisplayUtil.dip2px(this.context!!, 10f), DisplayUtil.dip2px(this.context!!, 8f), DisplayUtil.dip2px(this.context!!, 20f))
                lineView.layoutParams = lineViewLs
                lineView.setBackgroundColor(Color.parseColor("#F3F3F3"))
                appsWrap.addView(lineView)
            }

            itemIndex++
        }
    }

    override fun onScrollChanged(scrollView: ObservableScrollView, x: Int, y: Int, oldx: Int, oldy: Int) {
        val location = IntArray(2)
        mainTopMenu_1!!.getLocationOnScreen(location)
        val locationY = location[1]

        if (locationY <= ComFun.getStateBarHeight()) {
            mainTopMenu_2!!.visibility = View.VISIBLE
        } else {
            mainTopMenu_2!!.visibility = View.GONE
        }
    }

    private fun toDo (type: String) {
        when (type) {
            "clientIn" -> {
                val clientInIntent = Intent(this.context, ClientInActivity::class.java)
                startActivity(clientInIntent)
            }
            "writeFollowUp" -> {
                val clientFollowIntent = Intent(this.context, ClientFollowActivity::class.java)
                startActivity(clientFollowIntent)
            }
            "newTask" -> {}
            "newOrder" -> {}
        }
    }

}