package com.hiddify.clash

import android.net.Uri
import kotlin.random.Random

//
class SSProxy : IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("ss://")
    }
    var availableChipers= hashSetOf("aes-128-gcm", "aes-192-gcm", "aes-256-gcm",
        "aes-128-cfb", "aes-192-cfb", "aes-256-cfb",
        "aes-128-ctr","aes-192-ctr","aes-256-ctr",
    "rc4-md5","chacha20-ietf","xchacha20",
    "chacha20-ietf-poly1305","xchacha20-ietf-poly1305")
    override fun decode(_url: String): LinkedHashMap<String, Any> {
        var yml = LinkedHashMap<String, Any>()
        var url = _url
        if (!canDecode(url))
            throw Exception("Can not decode $url")
        if (!url.contains('@')) {
            var splt=url.split("#")
            var name=if(splt.size==2) splt[1] else "new"
            var left=splt[0].substringAfter("ss://")
            var splt2=left.split("/")
            var path=if (splt2.size==2) splt2[1] else ""

            var hostpart = Utils.decodeBase64(splt2[0])
            url="ss://$hostpart/$path#$name"

        }
        val uri = Uri.parse(url)
        yml["name"] = "${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 100000)}"
        yml["type"] = "ss"
        if (uri.userInfo == null)
            throw Exception("no user info")
        var userinfo=uri.userInfo!!
        if(userinfo.indexOf(":")<0)
            userinfo=Utils.decodeBase64(userinfo)

        val splt = userinfo.split(":").toTypedArray()
        yml["cipher"] = splt[0]
        if (!availableChipers.contains(splt[0]))
            throw Exception("Chipper ${splt[0]} not supported! use one of ${availableChipers}")
        yml["password"] = splt[1]
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp_over_tcp"] = true//Utils.getQueryParamIgnoreCase(uri,"udp-over-tcp") == "true"
        var plugin = Utils.getQueryParamIgnoreCase(uri,"plugin") ?: ""
        var pluginDecode = Utils.splitPluginOpts(plugin)
        when (pluginDecode["plugin"]) {
            "v2ray-plugin" -> {
                yml["plugin"] = "v2ray-plugin"
                yml["plugin-opts"] = mutableMapOf(
                    "mode" to (pluginDecode["mode"] ?: "websocket"),
                    "tls" to (pluginDecode["tls"] ?: "true"),
                    "skip-cert-verify" to "true",
                    "host" to (pluginDecode["host"] ?: "bing.com"),
                    "path" to (pluginDecode["path"] ?: "/")
                )
            }
            "obfs-local" -> {
                yml["plugin"] = "obfs"
                yml["plugin-opts"] = mutableMapOf(
                    "mode" to (pluginDecode["obfs"] ?: "tls"),
                    "host" to (pluginDecode["obfs-host"] ?: ""),
                )
            }
            ""->{
            }
            else -> throw Exception("Not Supported")
        }
        return yml
    }

}