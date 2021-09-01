package com.agou.fixnavigation

import com.agou.nav_api.NavConfig
import com.agou.nav_api.model.BottomBar
import com.agou.nav_api.model.Destination

object MainNavConfig :NavConfig(){
    override var mDestConfig: Map<String, Destination>? = null

    override var mBottomBar: BottomBar? = null

}