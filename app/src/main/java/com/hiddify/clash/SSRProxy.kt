package com.hiddify.clash

import android.net.Uri
import kotlin.random.Random

//
class SSRProxy : IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("ssr://")
    }

    override fun decode(_url: String): LinkedHashMap<String, Any> {
        var yml = LinkedHashMap<String, Any>()
        var url = _url
        if (!canDecode(url))
            throw Exception("Can not decode $url")
        url = url.substringAfter("ssr://")
        var params = url.split(":")
        var path = params[5].substringAfter("/")
        val uri = Uri.parse("ssr://${params[0]}:${params[1]}/$path")
        yml["name"] = "${uri.fragment ?: Utils.getQueryParamIgnoreCase(uri,"remarks") ?: "new"}_${uri.scheme}_${Random.nextInt(0,100000)}"
        yml["type"] = "ssr"
        yml["protocol"] = params[2]
        yml["obfs"] = params[4]
        yml["cipher"] = params[3]
//        yml["password"] = Utils.safeDecodeURLBase64(params[5].substringBefore("/"))
        yml["password"] = params[5].substringBefore("/")
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp"] = true
        yml["obfs-param"] = Utils.getQueryParamIgnoreCase(uri,"obfsparam") ?: ""
        yml["protocol-param"] = Utils.getQueryParamIgnoreCase(uri,"protoparam") ?: ""
        return yml

    }

}