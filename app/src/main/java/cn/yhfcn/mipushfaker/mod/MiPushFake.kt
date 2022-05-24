package cn.yhfcn.mipushfaker.mod

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MiPushFake : IXposedHookLoadPackage {
    val props: Map<String, String> = mapOf(
        Pair("ro.miui.ui.version.name", "V12"),
        Pair("ro.miui.ui.version.code", "10"),
        Pair("ro.miui.version.code_time", "1592409600"),

        Pair("product.manufacturer", "Xiaomi"),
        Pair("ro.product.vendor.manufacturer", "Xiaomi"),
        Pair("ro.product.brand", "Xiaomi"),
        Pair("ro.product.vendor.brand", "Xiaomi"),
    )

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // find mipush sdk
        val serviceExist = try {
            lpparam.classLoader.loadClass("com.xiaomi.push.service.XMPushService")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
        val messageHandlerExist = try {
            lpparam.classLoader.loadClass("com.xiaomi.mipush.sdk.PushMessageHandler")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
        val hasXiaomiPush = serviceExist or messageHandlerExist
        if (!hasXiaomiPush) {
            return
        }
        /**
         * fake miui12 hook
         * code from https://github.com/MiPushFramework/MiPushEnhancement
         * @author <a href="https://github.com/MlgmXyysd">Jaida Wu</a>
         * @author <a href="https://github.com/Trumeet">Yuuta Liang</a>
         * @see <a href="https://github.com/MiPushFramework/MiPushEnhancement/blob/master/app/src/main/java/org/meowcat/xposed/mipush/Enhancement.java">MiPushEnhancement</a>
         */
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
            "android.os.SystemProperties",
            lpparam.classLoader
        ),
            "native_get",
            String::class.java,
            String::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val key = param.args[0].toString()
                    if (props.containsKey(key)) {
                        param.result = props[key]
                    }
                }
            })

        // android.os.SystemProperties.native_get_int(String,int)

        // android.os.SystemProperties.native_get_int(String,int)
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
            "android.os.SystemProperties",
            lpparam.classLoader
        ),
            "native_get_int",
            String::class.java,
            Int::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val key = param.args[0].toString()
                    if (props.containsKey(key)) {
                        param.result = props[key]
                    }
                }
            })

        // android.os.SystemProperties.native_get_long(String,long)

        // android.os.SystemProperties.native_get_long(String,long)
        XposedHelpers.findAndHookMethod(XposedHelpers.findClass(
            "android.os.SystemProperties",
            lpparam.classLoader
        ),
            "native_get_long",
            String::class.java,
            Long::class.javaPrimitiveType,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val key = param.args[0].toString()
                    if (props.containsKey(key)) {
                        param.result = props[key]
                    }
                }
            })

        XposedHelpers.setStaticObjectField(Build::class.java, "MANUFACTURER", "Xiaomi")
        XposedHelpers.setStaticObjectField(Build::class.java, "BRAND", "Xiaomi")
    }
}