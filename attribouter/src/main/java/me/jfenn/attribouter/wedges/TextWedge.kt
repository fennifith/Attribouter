package me.jfenn.attribouter.wedges

import android.content.Context
import android.os.Build
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils

class TextWedge : Wedge<Wedge.ViewHolder>(R.layout.item_attribouter_text) {

    private val text: String? by attr("text")
    private val isCentered: Boolean? by attr("centered", false)

    override fun getViewHolder(v: View): Wedge.ViewHolder {
        return Wedge.ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: Wedge.ViewHolder) {
        (viewHolder.itemView as? TextView)?.apply {
            movementMethod = LinkMovementMethod()

            val string = ResourceUtils.getString(context, this@TextWedge.text)
            text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Html.fromHtml(string, 0) else Html.fromHtml(string)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                textAlignment = if (isCentered == true) View.TEXT_ALIGNMENT_CENTER else View.TEXT_ALIGNMENT_GRAVITY
        }
    }

}
