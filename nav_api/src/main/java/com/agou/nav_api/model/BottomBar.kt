package com.agou.nav_api.model

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.model
 * @author 345 QQ:1831712732
 * @time 2020/3/10 21:46
 * @description
 */
data class BottomBar(
    //被选中的颜色
    val activeColor: String,
    //默认的颜色
    val inActiveColor: String,
    //默认选中的 tabId
    val selectTab: Int,
    val tabs: List<Tab>
)

data class Tab(
    //是否可用
    val enable: Boolean,
    val index: Int,
    val pageUrl: String,
    val size: Int,
    val tintColor: String?,
    val title: String
)