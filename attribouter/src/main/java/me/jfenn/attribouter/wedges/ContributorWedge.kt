package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.dialogs.UserDialog
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.equalsProvider
import me.jfenn.attribouter.utils.loadDrawable
import me.jfenn.attribouter.utils.toTitleString
import me.jfenn.gitrest.model.ProviderString
import me.jfenn.gitrest.model.User

open class ContributorWedge(
        login: String? = null,
        name: String? = null,
        avatarUrl: String? = null,
        profileUrl: String? = null,
        websiteUrl: String? = null,
        task: String? = "Contributor",
        position: Int = 0,
        bio: String? = null,
        email: String? = null
) : Wedge<ContributorWedge.ViewHolder>(R.layout.attribouter_item_contributor), Comparable<ContributorWedge> {

    var login: String? by attr("login", login)
    var name: String? by attr("name", name)
    var avatar: String? by attr("avatar", avatarUrl)
    var profileUrl: String? by attr("profileUrl", profileUrl)
    var websiteUrl: String? by attr("websiteUrl", websiteUrl)
    var task: String? by attr("task", task)
    var position: Int by object : attr<ContributorWedge, Int>("position", position) {
        override fun apply(original: Int?, value: Int?): Int? = if (value != null && value != 0) value else original
    }
    var bio: String? by attr("bio", bio)
    var email: String? by attr("email", email)
    override var isHidden: Boolean by attr("hidden", false)

    override fun onCreate() {
        initChildren()

        login?.let {
            lifecycle?.launch {
                withContext(Dispatchers.IO) {
                    lifecycle?.client?.getUser(it)
                }.let { user ->
                    user?.let { onContributor(user) }
                }
            }
        }
    }

    fun initChildren() {
        // try to guess profile URL from id
        (profileUrl ?: login?.let { ProviderString(it).inferUrl() })?.let { url ->
            login?.let { userId ->
                val id = ProviderString(userId)
                addChild(ProfileLinkWedge(
                        name = id.provider.toTitleString(),
                        url = url,
                        icon = "@drawable/attribouter_ic_${id.provider}",
                        priority = 0
                ).create(lifecycle))
            } ?: addChild(ProfileLinkWedge(url = url, priority = 1).create(lifecycle))
        }

        email?.let { addChild(EmailLinkWedge(it, 0)) }
        websiteUrl?.let { addChild(WebsiteLinkWedge(it, 2)) }
    }

    open fun onContributor(data: User) {
        login = data.providerString.toString()
        name = data.name
        avatar = data.avatarUrl
        profileUrl = data.url
        websiteUrl = data.websiteUrl
        bio = data.bio
        email = data.email

        initChildren()
        notifyItemChanged()
    }

    fun getCanonicalName(): String? {
        return name ?: login
    }

    override fun equals(other: Any?): Boolean {
        return (other as? ContributorWedge)?.let {
            login.equalsProvider(other.login)
        } ?: super.equals(other)
    }

    override fun compareTo(other: ContributorWedge): Int {
        if (position == 0)
            return 1;
        if (other.position == 0)
            return -1;
        return position - other.position
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.imageView?.apply {
            context.loadDrawable(avatar, R.drawable.attribouter_image_avatar) {
                setImageDrawable(it)
            }
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
