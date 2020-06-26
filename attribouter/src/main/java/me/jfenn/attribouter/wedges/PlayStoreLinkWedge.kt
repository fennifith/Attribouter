package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import me.jfenn.attribouter.utils.UrlClickListener

class PlayStoreLinkWedge @JvmOverloads constructor(url: String? = null, priority: Int = 0) : LinkWedge(
        id = "playStore",
        name = "@string/attribouter_title_play_store",
        url = url,
        icon = "@drawable/attribouter_ic_play_store",
        priority = priority
) {

    override fun getListener(context: Context): View.OnClickListener? {
        return UrlClickListener(url ?: run {
            "https://play.google.com/store/apps/details?id=${context.applicationInfo.packageName}"
        })
    }
}
