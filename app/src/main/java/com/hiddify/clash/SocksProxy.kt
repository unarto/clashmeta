package com.hiddify.clash

import android.net.Uri
import kotlin.random.Random

//
class SocksProxy: IProxy {
    override fun canDecode(url: String): Boolean {
        return url.startsWith("socks://") ||url.startsWith("socks4://")||url.startsWith("socks4a://")||url.startsWith("socks5://")
    }
    override fun decode(_url: String): LinkedHashMap<String,Any> {
        var yml = LinkedHashMap<String, Any>()
        var url = _url
        if (!canDecode(url))
            throw Exception("Can not decode $url")
        val uri = Uri.parse(url)
        yml["name"] = "${uri.fragment ?: "new"}_${uri.scheme}_${Random.nextInt(0, 100000)}"
        yml["type"] = "socks5"
        if (uri.userInfo != null) {
            val splt = uri.userInfo!!.split(":").toTypedArray()
            yml["username"] = splt[0]
            yml["password"] = splt[1]
        }
        yml["server"] = uri.host ?: ""
        yml["port"] = uri.port
        yml["udp"]=true
        yml["skip-cert-verify"]=true
        if (Utils.getQueryParamIgnoreCase(uri,"tls") == "true")
            yml["tls"]=true

        return yml
    }

}