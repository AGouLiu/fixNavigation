package com.agou.nav_compiler

import com.agou.nav_annotation.ActivityDestination
import com.agou.nav_annotation.FragmentDestination
import com.google.auto.service.AutoService
import com.google.gson.Gson

import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation
import kotlin.collections.HashMap


@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@SupportedAnnotationTypes(
    "com.agou.nav_annotation.ActivityDestination",
    "com.agou.nav_annotation.FragmentDestination"
)
class NavProcessor : AbstractProcessor() {

    private var message: Messager? = null
    private var filer: Filer? = null
    private val OUTPUT_FILE_NAME = "_destination.json"
    private var PRO_OUTPUT_FILE_NAME = ""

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        //日志打印，java 环境下不能使用 log
        message = processingEnv.messager
        message!!.printMessage(Diagnostic.Kind.NOTE, "init")

        //文件处理工具
        filer = processingEnv.filer
    }

    override fun process(
        annotations: Set<TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        message?.printMessage(Diagnostic.Kind.NOTE, "process")
        //通过注解处理器环境上下文 roundEnv 分别获取项目中标记的注解
        val fragmentDestination = roundEnv.getElementsAnnotatedWith(FragmentDestination::class.java)
        val activityDestination = roundEnv.getElementsAnnotatedWith(ActivityDestination::class.java)

        if (fragmentDestination.isNotEmpty() || activityDestination.isNotEmpty()) {

            val destMap = hashMapOf<String?, HashMap<String, Any?>?>()

            //分别处理 FragmentDestination 和 ActivityDestination 注解类型
            //并收集到 destMap 中，以此就能记录下所有的页面信息了
            handleDestination(fragmentDestination, FragmentDestination::class.java, destMap)
            handleDestination(activityDestination, ActivityDestination::class.java, destMap)
            message?.printMessage(Diagnostic.Kind.NOTE, "destMap$destMap")
            //生成文件的位置
            //app/src/main/assets

            //filer.createResource ：创建源文件
            //CLASS_OUTPUT：java 文件生成 class 文件的位置，/app/build/intermediates/javac/debug/classes/目录下
            //SOURCE_OUTPUT：java文件的位置，一般在/ppjoke/app/build/generated/source/apt/目录下
            //CLASS_PATH 和 StandardLocation.SOURCE_PATH用的不多，指的了这个参数，就要指定生成文件的pkg包名了
            val resource =
                filer!!.createResource(StandardLocation.CLASS_OUTPUT, "",PRO_OUTPUT_FILE_NAME+ OUTPUT_FILE_NAME)


            val resourcePath = resource.toUri().path

            message?.printMessage(Diagnostic.Kind.NOTE, "resourcePath = $resourcePath")

            //由于要将生成的 json 文件生成在 app/src/main/assets/目录下，所以这里可以对字符串做一个截取
            //以此便能准确的获取每个电脑上的 app/src/main/assets 的路径
            val appPath = resourcePath.substring(0, resourcePath.lastIndexOf("build"))

            val assetsPath = appPath + "src/main/assets/"

            message?.printMessage(Diagnostic.Kind.NOTE, "assetsPath = $assetsPath")

            File(assetsPath).let { file ->
                if (!file.exists()) file.mkdir()
                File(file, PRO_OUTPUT_FILE_NAME+OUTPUT_FILE_NAME).let { newFile ->
                    if (newFile.exists()) newFile.delete()
                    newFile.createNewFile()
                    val content = Gson().toJson(destMap)
                    newFile.outputStream().bufferedWriter().use {
                        it.write(content)
                        it.flush()
                    }
                }
            }
        }
        return true
    }

    private fun handleDestination(
        elements: Set<Element>,
        clazz: Class<out Annotation>,
        destMap: HashMap<String?, HashMap<String, Any?>?>
    ) {
        var count = 0
        for (element in elements) {
            // TypeElement 是 Element 的一种
            // 如果注解标记在了类名上，可以直接强转一哈，使用他可以拿到全类名

            val typeElement = element as TypeElement
            val simpleName = typeElement.simpleName.toString()
            message?.printMessage(Diagnostic.Kind.NOTE, "typeElement$typeElement")
            message?.printMessage(Diagnostic.Kind.NOTE, "simpleName$simpleName")
            //获取全类名
            val clazzName = typeElement.qualifiedName.toString()
            val split = clazzName.split(".")
            if (count == 0) {
                PRO_OUTPUT_FILE_NAME = split[2]
            }
            count++
            message?.printMessage(Diagnostic.Kind.NOTE, " split[2] " + split[2])
            //页面的 id 不能重复，使用 hashcode 即可，
            val id = kotlin.math.abs(clazzName.hashCode())
            var pageUrl: String? = null
            var needLogin = false
            var asStarter = false
            var isFragment = false

            //获取具体的注解
            val annotation = element.getAnnotation(clazz)
            if (annotation is FragmentDestination) {
                pageUrl = annotation.pageUrl
                asStarter = annotation.asStarter
                needLogin = annotation.needLogin
                isFragment = true
            } else if (annotation is ActivityDestination) {
                pageUrl = annotation.pageUrl
                asStarter = annotation.asStarter
                needLogin = annotation.needLogin
            }

            if (destMap.containsKey(pageUrl)) {
                message?.printMessage(Diagnostic.Kind.ERROR, "不同的页面不允许使用相同的pageUrl $clazzName")
            } else {
                val data = HashMap<String, Any?>()
                data["id"] = id
                data["needLogin"] = needLogin
                data["asStarter"] = asStarter
                data["pageUrl"] = pageUrl
                data["className"] = clazzName
                data["isFragment"] = isFragment
                destMap[pageUrl] = data
            }
        }
    }
}