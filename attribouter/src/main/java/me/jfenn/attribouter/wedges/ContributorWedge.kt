package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.dialogs.UserDialog
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.isResourceMutable

open class ContributorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        task: String? = null,
        position: Int = Int.MAX_VALUE,
        bio: String? = null,
        blog: String? = null,
        email: String? = null
) : Wedge<ContributorWedge.ViewHolder>(R.layout.attribouter_item_contributor), Mergeable<ContributorWedge>, Comparable<ContributorWedge> {

    var login: ProviderString? by attrProvider("login", login)
    var name: String? by attr("name", name)
    var avatarUrl: String? by attr("avatar", avatarUrl)
    var task: String? by attr("task", task)
    var position: Int by attr("position", position)
    var bio: String? by attr("bio", bio)
    var blog: String? by attr("blog", blog)
    var email: String? by attr("email", email)
    override var isHidden: Boolean by attr("hidden", false)

    override fun onCreate() {
        email?.let { addChild(EmailLinkWedge(it, 0)) }
        login?.let { addChild(ProfileLinkWedge("https://github.com/${it.id}", 1)) }
        blog?.let { addChild(WebsiteLinkWedge(it, 2)) }

        if (!hasAll()) login?.let {
            lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.provider?.getUser(it)
                }?.let { user -> onContributor(user) }
            }
        }
    }

    open fun onContributor(data: UserData) {
        merge(ContributorWedge(
                data.login,
                data.name,
                data.avatarUrl,
                if (task == null) "Contributor" else null,
                -1,
                data.bio,
                data.websiteUrl,
                data.email
        ).create())

        lifecycle?.notifyItemChanged(this)
    }

    fun getCanonicalName(): String? {
        return name ?: login?.id
    }

    override fun merge(contributor: ContributorWedge): ContributorWedge {
        if (name.isResourceMutable())
            contributor.name?.let { name = it }
        if (avatarUrl.isResourceMutable())
            contributor.avatarUrl?.let { avatarUrl = it }
        if (bio.isResourceMutable() && !contributor.bio.isNullOrEmpty())
            contributor.bio?.let { bio = it }
        if (blog.isResourceMutable() && !contributor.blog.isNullOrEmpty())
            contributor.blog?.let { blog = it }
        if (email.isResourceMutable() && !contributor.email.isNullOrEmpty())
            contributor.email?.let { email = it }
        if (task.isResourceMutable())
            contributor.task?.let { task = it }

        addChildren(contributor.getChildren())
        return this
    }

    override fun hasAll(): Boolean {
        return !name.isResourceMutable()
                && !bio.isResourceMutable()
                && !blog.isResourceMutable()
                && !email.isResourceMutable()
    }

    override fun equals(other: Any?): Boolean {
        return (other as? ContributorWedge)?.let {
            login?.id?.equals(it.login?.id, ignoreCase = true)
        } ?: super.equals(other)
    }

    override fun compareTo(other: ContributorWedge): Int {
        val a = position ?: Int.MAX_VALUE
        val b = other.position ?: Int.MAX_VALUE
        return a - b
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.imageView?.apply {
            ResourceUtils.setImage(context, avatarUrl, R.drawable.attribouter_image_avatar, this)
        }

        viewHolder.nameView?.apply {
            text = ResourceUtils.getString(context, getCanonicalName())
        }

        viewHolder.taskView?.apply {
            task?.let {
                text = ResourceUtils.getString(context, it)
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        val links = getTypedChildren<LinkWedge>().filter { !it.isHidden }.sorted()

        viewHolder.firstLinkView?.apply {
            links.getOrNull(0)?.let { link ->
                link.bind(context, LinkWedge.ViewHolder(this))
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        viewHolder.secondLinkView?.apply {
            links.getOrNull(1)?.let { link ->
                link.bind(context, LinkWedge.ViewHolder(this))
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        viewHolder.itemView.apply {
            if (ResourceUtils.getString(context, bio) != null) {
                setOnClickListener {
                    UserDialog(context, this@ContributorWedge).show()
                }
            } else {
                var importantLink: LinkWedge? = null
                var clickListener: View.OnClickListener? = null
                for (link in links) {
                    if (importantLink == null || link.priority > importantLink.priority) {
                        link.getListener(context)?.let {
                            importantLink = link
                            clickListener = it
                        }
                    }
                }

                setOnClickListener(clickListener)
            }
        }
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var imageView: ImageView? = v.findViewById(R.id.image)
        var nameView: TextView? = v.findViewById(R.id.name)
        var taskView: TextView? = v.findViewById(R.id.task)

        var firstLinkView: View? = v.findViewById(R.id.link_1)
        var secondLinkView: View? = v.findViewById(R.id.link_2)
    }
}
