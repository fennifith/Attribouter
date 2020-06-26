package me.jfenn.attribouter.wedges

import android.view.View
import me.jfenn.attribouter.addDefaults

open class LicensesWedge : ListWedge("@string/attribouter_title_licenses", true) {

    var showDefaults: Boolean by attr("showDefaults", true)

    override fun onCreate() {
        if (showDefaults)
            addDefaults()
    }

    override fun getListItems(): List<Wedge<*>> {
        return getTypedChildren<LicenseWedge>().filter { !it.isHidden }
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

}
