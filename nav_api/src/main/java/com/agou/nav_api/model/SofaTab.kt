package com.agou.nav_api.model

data class SofaTab(
    val activeColor: String, // #ED7282 选中颜色
    val activeSize: Int, // 16
    val normalColor: String, // #666666 未选中元素
    val normalSize: Int, // 14
    val select: Int, // 0  默认选中
    val tabGravity: Int, // 0 位置
    val tabs: List<Tab>
) {
    data class Tab(
        val enable: Boolean, // true 是否显示
        val index: Int, // 0
        val tag: String, // pics 名称
        val title: String // 图片
    )
}