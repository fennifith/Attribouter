package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import me.jfenn.attribouter.dialogs.LicenseDialog
import me.jfenn.attribouter.utils.UrlClickListener

class LicenseLinkWedge(
        private val license: LicenseWedge, priority: Int
) : LinkWedge(
        id = "license",
        name = "@string/attribouter_title_license",
        icon = "@drawable/attribouter_ic_copyright",
        priority = priority
) {

    override fun getListener(context: Context): View.OnClickListener? {
        return when {
            license.licenseBody != null ->
                View.OnClickListener { v ->
                    LicenseDialog(v.context, license).show()
                }
            license.licenseUrl != null -> UrlClickListener(license.licenseUrl)
            else -> null
        }
    }
}
