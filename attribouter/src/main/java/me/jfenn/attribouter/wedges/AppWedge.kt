package me.jfenn.attribouter.wedges

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.androidutils.getThemedColor
import me.jfenn.androidutils.setBackgroundTint
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils
import me.jfenn.attribouter.utils.isResourceMutable
import me.jfenn.gitrest.model.Repo

open class AppWedge: Wedge<AppWedge.ViewHolder>(R.layout.attribouter_item_app_info) {

    val icon: String? by attr("icon")
    var title: String? by attr("title")
    var description: String? by attr("description")
    val repo: String? by attr("repo")
    var repoUrl: String? by attr("repoUrl")
    var websiteUrl: String? by attr("websiteUrl")
    var playStoreUrl: String? by attr("playStoreUrl")

    override fun onCreate() {
        initChildren()

        repo?.let { lifecycle?.launch {
            withContext(Dispatchers.IO) {
                lifecycle?.provider?.getRepo(it)
            }?.let { data -> onRepository(data) }
        }}
    }

    fun initChildren() {
        repoUrl?.let { addChild(RepoLinkWedge(it, 0).create(lifecycle)) }
        websiteUrl?.let { addChild(WebsiteLinkWedge(it, 0).create(lifecycle)) }
        playStoreUrl?.let { addChild(PlayStoreLinkWedge(it, 0).create(lifecycle)) }
    }

    fun onRepository(repo: Repo) {
        if (description.isResourceMutable())
            repo.description?.let { description = it }
        if (repoUrl.isResourceMutable())
            repo.url?.let { repoUrl = it }

        if (repo.websiteUrl?.startsWith("https://play.google.com/") == true) {
            if (playStoreUrl.isResourceMutable())
                playStoreUrl = repo.websiteUrl
        } else if (websiteUrl.isResourceMutable())
            repo.websiteUrl?.let { websiteUrl = it }

        initChildren()
    }

    override fun getViewHolder(v: View): ViewHolder {
        return ViewHolder(v)
    }

    override fun bind(context: Context, viewHolder: ViewHolder) {
        val info = context.applicationInfo
        viewHolder.appIconView?.apply {
            ResourceUtils.setImage(context, icon, info.icon, this)
        }

        run { // get app label from string (safely)
            ResourceUtils.getString(context, title) ?: if (info.labelRes == 0)
                info.nonLocalizedLabel?.toString()
            else context.getString(info.labelRes)
        }?.let {
            viewHolder.nameTextView?.apply {
                visibility = View.VISIBLE
                text = it
            }
        } ?: run { // hide view if not specified
            viewHolder.nameTextView?.visibility = View.GONE
        }

        viewHolder.versionTextView?.apply {
            try {
                val packageInfo = context.packageManager.getPackageInfo(info.packageName, 0)
                text = String.format(context.getString(R.string.attribouter_title_version), packageInfo.versionName)
                visibility = View.VISIBLE
            } catch (e: PackageManager.NameNotFoundException) {
                visibility = View.GONE
            }
        }

        viewHolder.descriptionTextView?.apply {
            ResourceUtils.getString(context, description)?.let {
                visibility = View.VISIBLE
                text = it
            } ?: run {
                visibility = View.GONE
            }
        }

        val links = getTypedChildren<LinkWedge>().filter { !it.isHidden }

        viewHolder.linkViews.forEachIndexed { index, view ->
            view?.apply {
                links.getOrNull(index)?.let { link ->
                    link.bind(context, LinkWedge.ViewHolder(this))
                    visibility = View.VISIBLE
                } ?: run {
                    visibility = View.GONE
                }

                val icon: ImageView? = view.findViewById(R.id.icon)
                icon?.apply {
                    val color = context.getThemedColor(R.attr.attribouter_textColorAccent)
                    setBackgroundTint(color)
                    setColorFilter(color)
                }
            }
        }

        /*viewHolder.links?.apply {
            val children = getTypedChildren<LinkWedge>()
            if (children.isNotEmpty()) {
                val links = children.filter { link -> !link.isHidden }
                Collections.sort(links, LinkWedge.Comparator(context))

                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.CENTER
                }
                adapter = WedgeAdapter(links)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }*/
    }

    open class ViewHolder(v: View) : Wedge.ViewHolder(v) {

        var appIconView: ImageView? = v.findViewById(R.id.appIcon)
        var nameTextView: TextView? = v.findViewById(R.id.appName)
        var versionTextView: TextView? = v.findViewById(R.id.appVersion)
        var descriptionTextView: TextView? = v.findViewById(R.id.description)
        //var links: RecyclerView? = v.findViewById(R.id.appLinks)

        var linkViews: Array<View?> = arrayOf(
                v.findViewById(R.id.link_1),
                v.findViewById(R.id.link_2),
                v.findViewById(R.id.link_3),
                v.findViewById(R.id.link_4)
        )
    }
}
