package com.hiddify.clash

import android.net.Uri
import java.net.URLDecoder
import kotlin.random.Random

//trojan://4ae13adb-c848-4060-84b1-aaf90ff39f99@frtr-grpc.createxray.us:443?mode=gun&security=tls&type=grpc&serviceName=trojan-grpc#Trojan_GRPC_createssh.net-sadasda
//trojan://e7fb49fc-9155-4f93-a757-ea964fda0828@frtrws.createxray.us:443?path=%2Ftrojan&security=tls&host=frtrws.createxray.us&type=ws&sni=dsfsd#Trojan+WS+TLS+createssh.net-dsfsdfsd
//trojan://dfc02efb-f435-435c-af19-9d0dfffc1519@frtr.createxray.us:443?security=tls&headerType=none&type=tcp#Trojan_TCP_TLS-createssh.net-asdasd
class TrojanGoProxy : IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("trojan-go://") || url.startsWith("trojan://")
    }

    override fun decode(_url: String): LinkedHashMap<String, Any> {
        var yml = LinkedHashMap<String, Any>()
        var url = _url
        if (!canDecode(url))
            throw Exception("Can not decode $url")
        if (!url.contains('@')) url = Utils.safeDecodeURLBase64(url)

        val uri = Uri.parse(url)
        yml["name"] = "${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 100000)}"
        yml["type"] = "trojan"
        if (uri.userInfo == null || uri.userInfo!!.isEmpty())
            throw Exception("no user info")
        yml["password"] = uri.userInfo!!
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp"] = true //Utils.getQueryParamIgnoreCase(uri,"udp-over-tcp") == "true"
        yml["sni"] = Utils.getQueryParamIgnoreCase(uri,"sni") ?: Utils.getQueryParamIgnoreCase(uri,"host") ?: uri.host!!
        if (Utils.getQueryParamIgnoreCase(uri,"flow")!=null)
            yml["flow"]=Utils.getQueryParamIgnoreCase(uri,"flow")!!
        yml["skip-cert-verify"] = true
        var alpnstr=URLDecoder.decode(Utils.getQueryParamIgnoreCase(uri,"alpn") ?: "", "UTF-8")
        if (alpnstr!=""){
            yml["alpn"]= alpnstr.split(",")
        }

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
            "grpc" -> {
                yml["network"] = "grpc"
                yml["grpc-opts"] = mutableMapOf(
                    "grpc-service-name" to (Utils.getQueryParamIgnoreCase(uri,"serviceName") ?: ""),
                )
            }
            else -> throw Exception("Not Supported")
        }
        return yml
    }


}