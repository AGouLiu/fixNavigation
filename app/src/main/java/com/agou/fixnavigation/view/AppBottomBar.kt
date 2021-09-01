package com.agou.fixnavigation.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import com.agou.fixnavigation.MainNavConfig
import com.agou.fixnavigation.R
import com.agou.nav_api.NavConfig
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode



class AppBottomBar : BottomNavigationView {

    companion object {
        val icons = intArrayOf(
            R.drawable.ic_home_black_24dp, R.drawable.ic_dashboard_black_24dp,
            R.drawable.ic_notifications_black_24dp
        )
    }

    constructor(context: Context) : this(context, null) {}

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0) {}

    @SuppressLint("RestrictedApi")
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) :
            super(context, attributeSet, defStyleAttr) {

        val bottomBar = MainNavConfig.getBottomBar(context,"fixnavigation_main_tabs_config.json")
        val tabs = bottomBar.tabs

        //二位数组，用来定义底部按钮在选中或者未被选中时两种状态的颜色
        val states = Array(2) { intArrayOf() }
        states[0] = intArrayOf(android.R.attr.state_selected)
        states[1] = intArrayOf()
        val colors = intArrayOf(
            //被选中的颜色
            Color.parseColor("#ff0000"),
            //按钮默认的颜色
            Color.parseColor(bottomBar.inActiveColor)
        )
        val colorStateList = ColorStateList(states, colors)
        //设置颜色
        itemTextColor = colorStateList
        itemIconTintList = colorStateList

        //所有的按钮在任何时候都需要显示文本
        labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED
        //设置默认选中的按钮
        selectedItemId = bottomBar.selectTab

        //将按钮添加到 BottomBar 上面
        for (tab in tabs) {
            if (!tab.enable) {
                return
            }
            val itemId = MainNavConfig.getDestIDFromUrl(context,tab.pageUrl,"fixnavigation_destination.json")
            if (itemId < 0) {
                return
            }
            val item = menu.add(0, itemId, tab.index, tab.title)
            item?.setIcon(icons[tab.index])
        }
        //按钮 Icon 设置大小
        tabs.forEach {
            val menuView = getChildAt(0) as BottomNavigationMenuView
            val itemView = menuView.getChildAt(it.index) as BottomNavigationItemView
            itemView.setIconSize(dp2px(it.size))

            //给中间的按钮设置着色
            if (TextUtils.isEmpty(it.title)) {
                itemView.setIconTintList(ColorStateList.valueOf(Color.parseColor(it.tintColor)))
                //点击的时候不会有上下浮动的效果
                itemView.setShifting(false)
            }
        }
    }

    private fun dp2px(dpValue: Int): Int {
        val value = context.resources.displayMetrics.density * dpValue + 0.5f
        return value.toInt()
    }

}