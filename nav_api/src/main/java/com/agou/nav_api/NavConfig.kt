package com.agou.nav_api

import android.content.Context
import android.util.Log
import com.agou.nav_api.model.BottomBar
import com.agou.nav_api.model.Destination
import com.agou.nav_api.model.SofaTab
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

abstract  class NavConfig {

    protected abstract var mDestConfig: Map<String, Destination>?
    protected abstract var mBottomBar: BottomBar?

    /**
     * 获取底部 Tab 的信息 通过当前的filename
     */
    open fun getBottomBar(context: Context, filename: String): BottomBar {

        if (mBottomBar == null) {

            mBottomBar = Gson().fromJson<BottomBar>(
                parseFile(context, filename),
                BottomBar::class.java
            )
        }
        return mBottomBar!!
    }

    /**
     * 返回被注解页面的数据
     */
    open fun getDestConfig(context: Context, filename: String): Map<String, Destination> {
        if (mDestConfig.isNullOrEmpty()) {
            mDestConfig = Gson().fromJson<HashMap<String, Destination>>(
                parseFile(context, filename),
                object : TypeToken<HashMap<String, Destination>>() {}.type
            )
        }
        return mDestConfig!!
    }


    /**
     * 根据当前的dest文件获取ID
     */
    open fun getDestIDFromUrl(context: Context, path: String, filename: String): Int {
        val destination = getDestConfig(context, filename)[path] ?: return -1
        return destination.id
    }

    /**
     * 获取文件内容
     */
    private fun parseFile(context: Context, fileName: String): String {
        val assets = context.resources.assets
        assets.open(fileName).use {
            return it.bufferedReader().readText()
        }
    }


}