package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.*
import me.jfenn.gitrest.model.ProviderString
import me.jfenn.gitrest.model.User
import java.util.*

open class TranslatorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        locales: String? = null,
        blog: String? = null,
        email: String? = null
) : Wedge<TranslatorWedge.ViewHolder>(R.layout.attribouter_item_translator) {

    var login: String? by attr("login", login)
    var name: String? by attr("name", name)
    var avatar: String? by attr("avatar", avatarUrl)
    var websiteUrl: String? by attr("websiteUrl", blog)
    var locales: String? by attr("locales", locales)
    var email: String? by attr("email", email)

    // whether the translator is the first of a locale in the sorted list (i.e. should display its header)
    var isFirst: Boolean = false

    override fun onCreate() {
        login?.let {
            if (!hasEverything()) lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.client?.getUser(it)
                }?.let { user -> onTranslator(user) }
            }
        }
    }

    open fun onTranslator(data: User) {
        name = data.name
        avatar = data.avatarUrl
        websiteUrl = data.websiteUrl
        email = data.email

        notifyItemChanged()
    }

    open fun getDisplayName(): String? {
        return name ?: login
    }

    private fun hasEverything(): Boolean {
        return !name.isResourceMutable() && !websiteUrl.isResourceMutable()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? TranslatorWedge)?.let {
            login.equalsProvider(other.login)
        } ?: super.equals(other)
    }

    fun clone() : TranslatorWedge {
        return TranslatorWedge(
                login,
                name,
                avatar,
                locales,
                websiteUrl,
                email
        ).also {
            it.addChildren(getChildren())
        }
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.localeView?.apply {
            text = locales?.split(",")?.getOrNull(0)?.let {
                Locale(it).getDisplayName(Locale(it))
            } ?: ""
            visibility = if (isFirst) View.VISIBLE else View.GONE
        }

        viewHolder.imageView?.apply {
            context.loadDrawable(avatar, R.drawable.attribouter_image_avatar) {
                setImageDrawable(it)
            }
        }

        viewHolder.nameView?.apply {
            text = ResourceUtils.getString(context, getDisplayName())
        }

        viewHolder.itemView.apply {
            websiteUrl?.let {
                setOnClickListener(UrlClickListener(it))
            } ?: login?.let {
                setOnClickListener(UrlClickListener(ProviderString(it).inferUrl()))
            } ?: run {
                setOnClickListener(null)
            }
        }
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var localeView: TextView? = v.findViewById(R.id.locale)
        var imageView: ImageView? = v.findViewById(R.id.image)
        var nameView: TextView? = v.findViewById(R.id.name)
    }
}
