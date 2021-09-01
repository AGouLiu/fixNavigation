package com.agou.nav_api.model

/**
 * @author 345 QQ:1831712732
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.model
 * @time 2020/3/9 21:47
 * @description 注解使用的 Bean
 */
data class Destination(
    val asStarter: Boolean,
    val className: String,
    val id: Int,
    val isFragment: Boolean,
    val needLogin: Boolean,
    val pageUrl: String
)