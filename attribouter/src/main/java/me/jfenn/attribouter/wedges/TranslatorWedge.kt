package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.data.github.UserData
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.isResourceMutable

class TranslatorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        locales: String? = null,
        blog: String? = null,
        email: String? = null
) : Wedge<TranslatorWedge.ViewHolder>(R.layout.item_attribouter_translator), Mergeable<TranslatorWedge> {

    private var login: String? by attr("login", login)
    private var name: String? by attr("name", name)
    private var avatarUrl: String? by attr("avatar", avatarUrl)
    var locales: String? by attr("locales", locales)
    private var blog: String? by attr("blog", blog)
    private var email: String? by attr("email", email)

    override fun onCreate() {
        login?.let {
            if (!hasEverything()) getProvider()?.getUser(it)?.subscribe { user ->
                onTranslator(user)
            }
        }
    }

    internal fun onTranslator(data: UserData) {
        merge(TranslatorWedge(
                data.login,
                data.name,
                data.avatar_url,
                null,
                data.blog,
                data.email
        ).create())
    }

    fun getCanonicalName(): String? {
        return name ?: login
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

    override fun isHidden(): Boolean {
        return false
    }

    private fun hasEverything(): Boolean {
        return !name.isResourceMutable() && !blog.isResourceMutable()
    }

    override fun equals(obj: Any?): Boolean {
        return (obj as? TranslatorWedge)?.let {
            login?.toLowerCase().equals(it.login?.toLowerCase())
        } ?: super.equals(obj)
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.imageView?.apply {
            ResourceUtils.setImage(context, avatarUrl, R.drawable.ic_attribouter_avatar, this)
        }

        viewHolder.nameView?.apply {
            text = ResourceUtils.getString(context, getCanonicalName())
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

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var imageView: ImageView? = v.findViewById(R.id.image)
        var nameView: TextView? = v.findViewById(R.id.name)
    }
}
