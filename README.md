# fixNavigation
[![](https://jitpack.io/v/AGouLiu/fixNavigation.svg)](https://jitpack.io/#AGouLiu/fixNavigation)
* 在国内开发可能希望每次使用 `navigation` 时候 不会希望每次都是调用 `replace` （创建并销毁的方式 ）去切换 `navigation`，开销很大。查了诸多资料和源码。根据现有的资源弄了一个符合组件化开发的  `fixNavigation`。

### 支持组件化。多个 `Activity` 在不同组件中多个 `navigation` 的使用

### 添加 `Gradle` 依赖

* 把 `maven { url 'https://jitpack.io' }` 加入到 repositories 中
* 添加如下依赖，末尾的「latestVersion」 [![Download](https://jitpack.io/v/AGouLiu/fixNavigation.svg)](https://jitpack.io/#AGouLiu/fixNavigation)里的版本名称，请自行替换。
```groovy
dependencies {
    implementation 'com.github.AGouLiu.fixNavigation:nav_api:latestVersion'
    annotationProcessor 'com.github.AGouLiu.fixNavigation:nav_compiler:latestVersion'
    //kapt
    kapt 'com.github.AGouLiu.fixNavigation:nav_compiler:latestVersion'
}
```

### 使用

* 在组建中添加注解

![image](https://user-images.githubusercontent.com/16461557/134899259-b6804f09-1379-4764-a651-4c2be16ebd8a.png)


* asStarter 代表是否是第一个进入的 `home` 页面 每个navigation 只有一个值是true


* 编译后产物 

![image](https://user-images.githubusercontent.com/16461557/134898628-c5cea857-346e-4abb-9b38-2b32faa8da26.png)

* 根据产物进行初始化 `navigation`，在 `app` 下有示例
```
 NavGraphBuilder.build(this, navController!!, this, fragment.id,  MainNavConfig,"fixnavigation_destination.json")
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavigationUI.setupWithNavController(bottomBar, navController!!)
```

* 在没有添加 `bottomBar` 情况 根据 `url` 方式跳转

```
  findNavController().navigate(
                StudentOrderNavConfig.getDestIDFromUrl(
                    RxTool.getContext(),
                    "main/tabs/DashboardFragment",
                    "fixnavigation_destination.json"
                ), args
            )
```

