package com.agou.nav_api

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

/**
 * @name ppjoke
 * @class name：com.lvkang.ppjoke.ui
 * @author 345 QQ:1831712732
 * @time 2020/3/11 21:28
 * @description 定制的Fragment导航器，替换ft.replace(mContainerId, frag);为 hide()/show()
 */
@Suppress("UNCHECKED_CAST")
@Navigator.Name("fixFragment")
class FixFragmentNavigator(
    private val context: Context,
    private val mManager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, mManager, containerId) {


    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (mManager.isStateSaved) {
            Log.e(
                "FixFragmentNavigator：", "Ignoring navigate() call: FragmentManager has already"
                        + " saved its state"
            )
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }
//        val frag = instantiateFragment(context, manage, className, args)
//        frag.arguments = args
        val ft = mManager.beginTransaction()

        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        //当前正在显示的 fragment
        val fragment = mManager.primaryNavigationFragment
        if (fragment != null) {
            //隐藏正在显示的页面，准备显示新的界面
            ft.hide(fragment)
        }
        var frag: Fragment?

        val tag = destination.id.toString()
        //获取要显示的 fragment
        frag = mManager.findFragmentByTag(tag)
        if (frag != null) {
            ft.show(frag)
        } else {
            //如果为 null，则创建
            frag = instantiateFragment(context, mManager, className, args)
            frag.arguments = args
            ft.add(containerId, frag, tag)
        }

//        ft.replace(mContainerId, frag)
        ft.setPrimaryNavigationFragment(frag)

        @IdRes val destId = destination.id
        //mBackStack 是私有的，这里无法获取，需要通过反射获取
        //反射获取字段，并且获取值
        val field = FragmentNavigator::class.java.getDeclaredField("mBackStack")
        field.isAccessible = true
        val mBackStack: ArrayDeque<Int> = field.get(this) as ArrayDeque<Int>

        val initialNavigation = mBackStack.isEmpty()
        // TODO Build first class singleTop behavior for fragments
        // TODO Build first class singleTop behavior for fragments
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)

        val isAdded: Boolean
        isAdded = if (initialNavigation) {
            true
        } else if (isSingleTopReplacement) { // Single Top means we only want one instance on the back stack
            if (mBackStack.size > 1) { // If the Fragment to be replaced is on the FragmentManager's
                // back stack, a simple replace() isn't enough so we
                // remove it from the back stack and put our replacement
                // on the back stack in its place
                mManager.popBackStack(
                    generateBackStackName(mBackStack.size, mBackStack.peekLast()!!),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
            }
            false
        } else {
            ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            true
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()
        // The commit succeeded, update our view of the world
        // The commit succeeded, update our view of the world
        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }
}