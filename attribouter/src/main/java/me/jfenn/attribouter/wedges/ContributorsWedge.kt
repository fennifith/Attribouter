package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.addDefaults
import me.jfenn.attribouter.dialogs.OverflowDialog
import me.jfenn.attribouter.dialogs.UserDialog
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener

class ContributorsWedge : Wedge<ContributorsWedge.ViewHolder>(R.layout.attribouter_item_contributors) {

    private var repo: ProviderString? by attrProvider("repo")
    private var contributorsTitle: String? by attr("title", "@string/title_attribouter_contributors")
    private var overflow: Int? by attr("overflow", Int.MAX_VALUE)
    private var showDefaults: Boolean? by attr("showDefaults", true)

    override fun onCreate() {
        if (showDefaults != false)
            addDefaults()

        repo?.let {
            requestContributors(it)
        }
    }

    fun requestContributors(repo: ProviderString) {
        lifecycle?.launch {
            withContext(Dispatchers.IO) {
                lifecycle?.provider?.getContributors(repo)
            }?.forEach { contributor ->
                onContributor(contributor)
            }
        }
    }

    fun onContributor(data: UserData) {
        val contributor = ContributorWedge(
                login = data.login,
                name = data.name,
                avatarUrl = data.avatarUrl,
                task = if (data.login?.let { repo?.id?.startsWith(it) } == true) "Owner" else "Contributor",
                bio = data.bio,
                blog = data.websiteUrl,
                email = data.email
        )

        addChild(0, contributor.create(lifecycle))
        notifyItemChanged()
    }

    public override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    private fun bindContributorView(
            context: Context,
            view: View?,
            imageView: ImageView?,
            nameView: TextView?,
            taskView: TextView?,
            contributor: ContributorWedge
    ) {
        nameView?.text = ResourceUtils.getString(context, contributor.getCanonicalName())
        imageView?.apply {
            ResourceUtils.setImage(context, contributor.avatarUrl, R.drawable.attribouter_image_avatar, this)
        }

        taskView?.apply {
            visibility = contributor.task?.let {
                text = ResourceUtils.getString(context, it)
                View.VISIBLE
            } ?: View.GONE
        }

        view?.apply {
            tag = contributor
            setOnClickListener(
                    when {
                        ResourceUtils.getString(context, contributor.bio) != null -> View.OnClickListener { v ->
                            (v.tag as? ContributorWedge)?.let { UserDialog(v.context, it).show() }
                        }
                        ResourceUtils.getString(context, contributor.blog) != null -> UrlClickListener(ResourceUtils.getString(context, contributor.blog))
                        contributor.login != null -> UrlClickListener("https://github.com/${contributor.login}")
                        else -> null
                    }
            )
        }
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            visibility = if (overflow!! > 0) {
                contributorsTitle?.let { text = ResourceUtils.getString(context, contributorsTitle) }
                View.VISIBLE
            } else View.GONE
        }

        val contributors = getTypedChildren<ContributorWedge>().filter { !it.isHidden }.sorted()
        val displayContributors = contributors.take(overflow ?: Int.MAX_VALUE)

        viewHolder.recycler?.apply {
            visibility = if (displayContributors.isNotEmpty()) {
                layoutManager = LinearLayoutManager(context)
                adapter = WedgeAdapter(displayContributors)
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.expand?.apply {
            visibility = if (overflow != 0 && displayContributors.size < contributors.size) {
                setOnClickListener { v ->
                    OverflowDialog(
                            v.context,
                            contributorsTitle,
                            contributors
                    ).show()
                }

                View.VISIBLE
            } else View.GONE
        }

        viewHolder.overflow?.apply {
            visibility = if (overflow == 0) {
                text = ResourceUtils.getString(context, contributorsTitle)
                viewHolder.itemView.setOnClickListener { v ->
                    OverflowDialog(v.context, contributorsTitle, getChildren()).show()
                }
                View.VISIBLE
            } else {
                viewHolder.itemView.setOnClickListener(null)
                View.GONE
            }
        }
    }

    class ViewHolder(v: View) : Wedge.ViewHolder(v) {
        var titleView: TextView? = v.findViewById(R.id.contributorsTitle)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
    }

}
