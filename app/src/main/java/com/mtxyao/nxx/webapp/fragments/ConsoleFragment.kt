package com.mtxyao.nxx.webapp.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.mtxyao.nxx.webapp.BaseFragment
import com.mtxyao.nxx.webapp.ClientInActivity
import com.mtxyao.nxx.webapp.R
import com.mtxyao.nxx.webapp.util.ComFun
import com.mtxyao.nxx.webapp.util.DisplayUtil
import com.mtxyao.nxx.webapp.util.ObservableScrollView

class ConsoleFragment : BaseFragment(false), ObservableScrollView.ScrollViewListener {
    private var apps = mapOf(
            Pair("擂台争霸", mapOf(
                    R.drawable.do_yjpm to "业绩排名",
                    R.drawable.do_jfph to "积分排名",
                    R.drawable.do_cgjj to "晋级闯关",
                    R.drawable.do_pyxb to "评优选拔",
                    R.drawable.do_ambjs to "阿米巴竞赛"
            )),
            Pair("我的钱袋", mapOf(
                    R.drawable.do_jsjx to "即时绩效",
                    R.drawable.do_kpi to "KPI考核",
                    R.drawable.do_yjjl to "佣金奖励"
            )),
            Pair("客户营销", mapOf(
                    R.drawable.do_qkhx to "潜客画像",
                    R.drawable.do_ddgl to "订单管理",
                    R.drawable.do_qkfx to "潜客分析",
                    R.drawable.do_cjfx to "成交分析",
                    R.drawable.do_zbfx to "战败分析",
                    R.drawable.do_kczy to "库存资源"
            )),
            Pair("对标对比", mapOf(
                    R.drawable.do_zcdb to "政策对标",
                    R.drawable.do_dwfx to "多维分析",
                    R.drawable.do_sjdb to "数据对比"
            )),
            Pair("独立核算", mapOf(
                    R.drawable.do_mbdc to "目标达成",
                    R.drawable.do_tdyj to "团队业绩",
                    R.drawable.do_fyhs to "费用核算",
                    R.drawable.do_xyzd to "效益最大",
                    R.drawable.do_sjcb to "时间成本"
            ))
    )

    private var scrollView: ObservableScrollView ? = null
    private var mainTopMenu_1: LinearLayout ? = null
    private var mainTopMenu_2: LinearLayout ? = null

    override fun getFragmentView(inflater: LayoutInflater, container: ViewGroup?): View {
        return inflater.inflate(R.layout.fragment_console, container, false)
    }

    override fun initPageData(fragmentView: View) {
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
    }

    private fun <K, V> initApps (appsWrap: LinearLayout, apps: Map<K, V>) {
        var itemIndex = 0
        for (item in apps) {
            val itemLayout = RelativeLayout(this.context)
            itemLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val itemTitleTv = TextView(this.context)
            val itemTitleLs = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
            itemTitleLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 4f), 0, 0)
            itemTitleTv.layoutParams = itemTitleLs
            itemTitleLs
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
                for ((k, v) in (item.toPair().second as Map<*, *>)) {
                    val appTitle: String = v as String
                    val appDrawable: Int = k as Int

                    val itemAppItemLayout = LinearLayout(this.context)
                    itemAppItemLayout.gravity = Gravity.CENTER
                    itemAppItemLayout.orientation = LinearLayout.VERTICAL
                    itemAppItemLayout.setPadding(DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f),DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f))
                    val itemAppItemLayoutLs = GridLayout.LayoutParams()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        itemAppItemLayoutLs.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                        itemAppItemLayoutLs.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                    }
                    itemAppItemLayout.layoutParams = itemAppItemLayoutLs
                    val appImg = ImageView(this.context)
                    appImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(this.context!!, 44f), DisplayUtil.dip2px(this.context!!, 44f))
                    appImg.setImageResource(appDrawable)
                    itemAppItemLayout.addView(appImg)
                    val appTxt = TextView(this.context)
                    val appTxtLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    appTxtLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 6f), 0, 0)
                    appTxt.layoutParams = appTxtLs
                    appTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                    appTxt.text = appTitle
                    itemAppItemLayout.addView(appTxt)
                    appsGridLayout.addView(itemAppItemLayout)
                }
                if ((item.toPair().second as Map<*, *>).size < 4) {
                    while (needFillCount > 0) {
                        val itemAppItemLayout = LinearLayout(this.context)
                        itemAppItemLayout.gravity = Gravity.CENTER
                        itemAppItemLayout.orientation = LinearLayout.VERTICAL
                        itemAppItemLayout.setPadding(DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f),DisplayUtil.dip2px(this.context!!, 6f), DisplayUtil.dip2px(this.context!!, 6f))
                        val itemAppItemLayoutLs = GridLayout.LayoutParams()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            itemAppItemLayoutLs.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                            itemAppItemLayoutLs.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1.0f)
                        }
                        itemAppItemLayout.layoutParams = itemAppItemLayoutLs
                        val appImg = ImageView(this.context)
                        appImg.layoutParams = LinearLayout.LayoutParams(DisplayUtil.dip2px(this.context!!, 44f), DisplayUtil.dip2px(this.context!!, 44f))
                        itemAppItemLayout.addView(appImg)
                        val appTxt = TextView(this.context)
                        val appTxtLs = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                        appTxtLs.setMargins(0, DisplayUtil.dip2px(this.context!!, 6f), 0, 0)
                        appTxt.layoutParams = appTxtLs
                        appTxt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
                        itemAppItemLayout.addView(appTxt)
                        appsGridLayout.addView(itemAppItemLayout)
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
            "writeFollowUp" -> {}
            "newTask" -> {}
            "newOrder" -> {}
        }
    }

}