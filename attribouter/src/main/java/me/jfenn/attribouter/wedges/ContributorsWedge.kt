package me.jfenn.attribouter.wedges

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.jfenn.attribouter.addDefaults
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.net.data.UserData

open class ContributorsWedge : ListWedge("@string/attribouter_title_contributors", false) {

    var repo: ProviderString? by attrProvider("repo")
    var showDefaults: Boolean by attr("showDefaults", true)

    override fun onCreate() {
        if (showDefaults)
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

    override fun getListItems(): List<Wedge<*>> {
        return getTypedChildren<ContributorWedge>().filter { !it.isHidden }.sorted()
    }

}
