package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import me.jfenn.attribouter.utils.UrlClickListener

class PlayStoreLinkWedge : LinkWedge {

    @JvmOverloads
    constructor(url: String? = null, priority: Int = 0) : super(
            id = "playStore",
            name = "@string/title_attribouter_rate",
            url = url,
            icon = "@drawable/ic_attribouter_rate",
            priority = priority
    )

    override fun getListener(context: Context): View.OnClickListener? {
        return UrlClickListener(url ?: run {
            "https://play.google.com/store/apps/details?id=${context.applicationInfo.packageName}"
        })
    }
}
