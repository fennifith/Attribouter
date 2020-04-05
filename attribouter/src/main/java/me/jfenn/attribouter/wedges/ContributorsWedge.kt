package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.addDefaults
import me.jfenn.attribouter.dialogs.OverflowDialog
import me.jfenn.attribouter.dialogs.UserDialog
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.net.data.UserData
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.getProviderOrNull
import java.util.*

class ContributorsWedge : Wedge<ContributorsWedge.ViewHolder>(R.layout.item_attribouter_contributors) {

    private var repo: String? by attr("repo")
    private var contributorsTitle: String? by attr("title", "@string/title_attribouter_contributors")
    private var overflow: Int? by attr("overflow", -1)
    private var showDefaults: Boolean? by attr("showDefaults", true)

    override fun onCreate() {
        if (showDefaults != false)
            addDefaults()

        repo?.let {
            requestContributors(it)
        }
    }

    fun requestContributors(repo: String) {
        GlobalScope.launch { // TODO: use, err, the non-global scope...
            withContext(Dispatchers.IO) {
                getProvider(repo.getProviderOrNull())?.getContributors(repo)
            }?.forEach { contributor ->
                onContributor(contributor)
            }
        }
    }

    fun onContributor(data: UserData, pass: Int = 0) {
        val contributor = ContributorWedge(
                login = data.login,
                name = data.name,
                avatarUrl = data.avatarUrl,
                task = if (data.login?.let { repo?.startsWith(it) } == true) "Owner" else "Contributor",
                bio = data.bio,
                blog = data.websiteUrl,
                email = data.email
        )

        val info = addChild(0, contributor.withProviders<ContributorWedge>(getProviders())
                .withNotifiable<ContributorWedge>(notifiable)
                .create())

        if (info is Mergeable<*> && !info.hasAll() && pass < 3) data.login?.let { login ->
            GlobalScope.launch { // TODO: use, err, the non-global scope...
                withContext(Dispatchers.IO) {
                    getProvider()?.getUser(login)
                }?.let { onContributor(it, pass + 1) }
            }
        }

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
            ResourceUtils.setImage(context, contributor.avatarUrl, R.drawable.ic_attribouter_avatar, this)
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

    public override fun bind(context: Context, viewHolder: ViewHolder) {
        viewHolder.titleView?.apply {
            visibility = if (overflow!! > 0) {
                contributorsTitle?.let { text = ResourceUtils.getString(context, contributorsTitle) }
                View.VISIBLE
            } else View.GONE
        }

        var first: ContributorWedge? = null
        var second: ContributorWedge? = null
        var third: ContributorWedge? = null
        val remainingContributors = ArrayList<Wedge<*>>()
        var hiddenContributors = 0
        for (contributor in getChildren(ContributorWedge::class.java)) {
            if (contributor.isHidden) {
                hiddenContributors++
                continue
            }

            when {
                contributor.position == 1 -> first = first ?: contributor
                contributor.position == 2 -> second = second ?: contributor
                contributor.position == 3 -> third = third ?: contributor
                else -> overflow?.let {
                    if (remainingContributors.size < it || it == -1)
                        remainingContributors.add(contributor)
                }
            }
        }

        if (first != null && second != null && third != null) {
            viewHolder.topThreeView?.visibility = View.VISIBLE

            bindContributorView(context, viewHolder.firstView, viewHolder.firstImageView, viewHolder.firstNameView, viewHolder.firstTaskView, first)
            bindContributorView(context, viewHolder.secondView, viewHolder.secondImageView, viewHolder.secondNameView, viewHolder.secondTaskView, second)
            bindContributorView(context, viewHolder.thirdView, viewHolder.thirdImageView, viewHolder.thirdNameView, viewHolder.thirdTaskView, third)
        } else {
            viewHolder.topThreeView?.visibility = View.GONE
            third?.let { remainingContributors.add(0, third) }
            second?.let { remainingContributors.add(0, second) }
            first?.let { remainingContributors.add(0, first) }
        }

        viewHolder.recycler?.apply {
            visibility = if (remainingContributors.isNotEmpty()) {
                layoutManager = LinearLayoutManager(context)
                adapter = WedgeAdapter(remainingContributors)
                View.VISIBLE
            } else View.GONE
        }

        viewHolder.expand?.apply {
            visibility = if (overflow != 0 && remainingContributors.size + (if (first != null && second != null && third != null) 3 else 0) < getChildren().size - hiddenContributors) {
                setOnClickListener { v ->
                    OverflowDialog(
                            v.context,
                            contributorsTitle,
                            getChildren(ContributorWedge::class.java).filter { !it.isHidden }
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
        var topThreeView: View? = v.findViewById(R.id.topThree)
        var firstView: View? = v.findViewById(R.id.first)
        var firstImageView: ImageView? = v.findViewById(R.id.firstImage)
        var firstNameView: TextView? = v.findViewById(R.id.firstName)
        var firstTaskView: TextView? = v.findViewById(R.id.firstTask)
        var secondView: View? = v.findViewById(R.id.second)
        var secondImageView: ImageView? = v.findViewById(R.id.secondImage)
        var secondNameView: TextView? = v.findViewById(R.id.secondName)
        var secondTaskView: TextView? = v.findViewById(R.id.secondTask)
        var thirdView: View? = v.findViewById(R.id.third)
        var thirdImageView: ImageView? = v.findViewById(R.id.thirdImage)
        var thirdNameView: TextView? = v.findViewById(R.id.thirdName)
        var thirdTaskView: TextView? = v.findViewById(R.id.thirdTask)
        var expand: View? = v.findViewById(R.id.expand)
        var overflow: TextView? = v.findViewById(R.id.overflow)
        var recycler: RecyclerView? = v.findViewById(R.id.recycler)
    }

}
