package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.isResourceMutable
import java.util.*

open class TranslatorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        locales: String? = null,
        blog: String? = null,
        email: String? = null
) : Wedge<TranslatorWedge.ViewHolder>(R.layout.attribouter_item_translator), Mergeable<TranslatorWedge> {

    var login: ProviderString? by attrProvider("login", login)
    var name: String? by attr("name", name)
    var avatarUrl: String? by attr("avatar", avatarUrl)
    var locales: String? by attr("locales", locales)
    var blog: String? by attr("blog", blog)
    var email: String? by attr("email", email)
    override val isHidden: Boolean = false

    // whether the translator is the first of a locale in the sorted list (i.e. should display its header)
    var isFirst: Boolean = false

    override fun onCreate() {
        login?.let {
            if (!hasEverything()) lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.provider?.getUser(it)
                }?.let { user -> onTranslator(user) }
            }
        }
    }

    open fun onTranslator(data: UserData) {
        merge(TranslatorWedge(
                data.login,
                data.name,
                data.avatarUrl,
                null,
                data.websiteUrl,
                data.email
        ).create())

        notifyItemChanged()
    }

    open fun getDisplayName(): String? {
        return name ?: login?.id
    }

    override fun merge(contributor: TranslatorWedge): TranslatorWedge {
        if (name.isResourceMutable())
            contributor.name?.let { name = it }
        if (avatarUrl.isResourceMutable())
            contributor.avatarUrl?.let { avatarUrl = it }
        if (blog.isResourceMutable() && !contributor.blog.isNullOrEmpty())
            contributor.blog?.let { blog = it }
        if (email.isResourceMutable() && !contributor.email.isNullOrEmpty())
            contributor.email?.let { email = it }
        if (locales.isResourceMutable())
            contributor.locales?.let { locales = it }

        return this
    }

    override fun hasAll(): Boolean {
        return false
    }

    private fun hasEverything(): Boolean {
        return !name.isResourceMutable() && !blog.isResourceMutable()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? TranslatorWedge)?.let {
            login?.id?.equals(it.login?.id, ignoreCase = true)
        } ?: super.equals(other)
    }

    fun clone() : TranslatorWedge {
        return TranslatorWedge(
                login.toString(),
                name,
                avatarUrl,
                locales,
                blog,
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
            ResourceUtils.setImage(context, avatarUrl, R.drawable.attribouter_image_avatar, this)
        }

        viewHolder.nameView?.apply {
            text = ResourceUtils.getString(context, getDisplayName())
        }

        viewHolder.itemView.apply {
            ResourceUtils.getString(context, blog)?.let {
                setOnClickListener(UrlClickListener(it))
            } ?: login?.let {
                setOnClickListener(UrlClickListener("https://github.com/$it"))
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
