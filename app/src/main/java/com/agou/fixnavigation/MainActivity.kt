package com.agou.fixnavigation

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.agou.fixnavigation.databinding.ActivityMainBinding
import com.agou.fixnavigation.view.AppBottomBar
import com.agou.nav_api.NavConfig
import com.agou.nav_api.NavGraphBuilder
import com.agou.nav_api.model.BottomBar
import com.agou.nav_api.model.Destination
import com.agou.nav_api.model.SofaTab

class MainActivity(
) : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null
    lateinit var bottomBar: AppBottomBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomBar =binding.navView
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
         navController = fragment!!.findNavController()
        NavGraphBuilder.build(this, navController!!, this, fragment.id,  MainNavConfig,"fixnavigation_destination.json")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavigationUI.setupWithNavController(bottomBar, navController!!)



        //AppBottomBar的点击事件 和 navController 关联起来
        bottomBar.setOnNavigationItemSelectedListener(this::onNavItemSelected)
    }

    private fun onNavItemSelected(menuItem: MenuItem): Boolean {
      /*  AppConfig.getDestConfig(this).forEach {
            val value = it.value
            if ((!UserManager.isLogin()) && value.needLogin && value.id == menuItem.itemId) {
                UserManager.login(this).observe(this,
                    Observer<User> { bottomBar.selectedItemId = menuItem.itemId })
                return@forEach
            }
        }*/
        navController?.navigate(menuItem.itemId)
        //返回 false 代表按钮没有被选中，也不会着色。如果为 true，就会着色，有一个上下浮动的效果
        return !TextUtils.isEmpty(menuItem.title)
    }
}
