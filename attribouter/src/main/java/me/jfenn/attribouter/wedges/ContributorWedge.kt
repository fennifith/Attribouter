package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.data.github.UserData
import me.jfenn.attribouter.dialogs.UserDialog
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.isResourceMutable
import me.jfenn.attribouter.wedges.link.EmailLinkWedge
import me.jfenn.attribouter.wedges.link.GitHubLinkWedge
import me.jfenn.attribouter.wedges.link.LinkWedge
import me.jfenn.attribouter.wedges.link.WebsiteLinkWedge

class ContributorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        task: String? = null,
        position: Int = -1,
        bio: String? = null,
        blog: String? = null,
        email: String? = null
) : Wedge<ContributorWedge.ViewHolder>(R.layout.item_attribouter_contributor), Mergeable<ContributorWedge> {

    var login: String? by attr("login", login)
    private var name: String? by attr("name", name)
    var avatarUrl: String? by attr("avatar", avatarUrl)
    var task: String? by attr("task", task)
    var position: Int? by attr("position", position)
    var bio: String? by attr("bio", bio)
    var blog: String? by attr("blog", blog)
    private var email: String? by attr("email", email)
    private var isHidden: Boolean? by attr("hidden", false)

    override fun onCreate() {
        login?.let { addChild(GitHubLinkWedge(it, 1)) }
        blog?.let { addChild(WebsiteLinkWedge(it, 2)) }
        email?.let { addChild(EmailLinkWedge(it, -1)) }

        if (!hasAll()) login?.let {
            getProvider()?.getUser(it)?.subscribe { user -> onContributor(user) }
        }
    }

    fun getAbsolutePosition(): Int? = position?.let { if (it >= 0) it else null }

    fun onContributor(data: UserData) {
        merge(ContributorWedge(
                data.login,
                data.name,
                data.avatar_url,
                if (task == null) "Contributor" else null,
                -1,
                data.bio,
                data.blog,
                data.email
        ).create())
    }

    fun getCanonicalName(): String? {
        return name ?: login
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

    override fun isHidden(): Boolean {
        return isHidden == true
    }

    override fun equals(obj: Any?): Boolean {
        return (obj as? ContributorWedge)?.let {
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

        viewHolder.taskView?.apply {
            task?.let {
                text = ResourceUtils.getString(context, it)
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        viewHolder.itemView.apply {
            if (ResourceUtils.getString(context, bio) != null) {
                setOnClickListener { view ->
                    UserDialog(view.context, this@ContributorWedge)
                            .show()
                }
            } else {
                var importantLink: LinkWedge? = null
                var clickListener: View.OnClickListener? = null
                for (link in getChildren(LinkWedge::class.java).filter { !it.isHidden }) {
                    if (importantLink == null || link.priority() > importantLink.priority()) {
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

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var imageView: ImageView? = v.findViewById(R.id.image)
        var nameView: TextView? = v.findViewById(R.id.name)
        var taskView: TextView? = v.findViewById(R.id.task)
    }
}
