package com.hiddify.clash

import android.net.Uri
import java.net.URLDecoder
import kotlin.random.Random

class VlessProxy : IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("vless://")
    }

    //    vless://6e1ff0da-8eb3-4127-deb6-9e549206b0cc@v2rngy.tk:58993?security=tls&encryption=none&headerType=none&type=tcp&sni=v2rngy.tk#test
    //vless://5c3d0247-d6d5-4139-c8e5-67154de39fdf@ov2germany.ronaghi.tk:8443?type=tcp&security=xtls&flow=xtls-rprx-direct#Vless%20Germany%202
    override fun decode(_url: String): LinkedHashMap<String, Any> {
        var yml = LinkedHashMap<String, Any>()
        var url = _url
        if (!canDecode(url))
            throw Exception("Can not decode $url")
        if (!url.contains('@')) url = Utils.safeDecodeURLBase64(url)
        val uri = Uri.parse(url)
        yml["name"] = "${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 100000)}"
        yml["type"] = "vless"
        if (uri.userInfo == null || uri.userInfo!!.isEmpty())
            throw Exception("no user info")
        yml["uuid"] = uri.userInfo!!
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp"] = true
        yml["tls"]="tls"==(Utils.getQueryParamIgnoreCase(uri,"security")?:"")
        yml["servername"] = Utils.getQueryParamIgnoreCase(uri,"sni") ?: Utils.getQueryParamIgnoreCase(uri,"host") ?: uri.host!!
        yml["skip-cert-verify"] = true
        if (Utils.getQueryParamIgnoreCase(uri,"flow")!=null)
            yml["flow"]=Utils.getQueryParamIgnoreCase(uri,"flow")!!
        var localType = Utils.getQueryParamIgnoreCase(uri,"type") ?: "tcp"
        when (localType) {
            "ws" -> {
                yml["network"] = "ws"

                var wsOpts= mutableMapOf(
                    "path" to (URLDecoder.decode(Utils.getQueryParamIgnoreCase(uri,"path") ?: "", "UTF-8")),
                )
                if ((Utils.getQueryParamIgnoreCase(uri,"host")?:"")!=""){
                    wsOpts["headers"]= mutableMapOf(
                        "host" to (URLDecoder.decode(Utils.getQueryParamIgnoreCase(uri,"host") ?: "", "UTF-8"))
                    ).toString()
                }
                yml["ws-opts"]=wsOpts
            }
            "tcp" -> {
            }
            "http" -> {
            }
            "grpc" -> {
                yml["network"] = "grpc"
                yml["grpc-opts"] = mutableMapOf(
                    "grpc-service-name" to (Utils.getQueryParamIgnoreCase(uri,"serviceName") ?: ""),
                )
            }
            else -> throw Exception("$localType Not Supported")
        }
        return yml
    }
}