package me.jfenn.attribouter.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.Attribouter
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.provider.net.github.GitHubService
import me.jfenn.attribouter.provider.wedge.XMLWedgeProvider
import me.jfenn.attribouter.wedges.Wedge
import java.util.*

class AboutFragment : Fragment(), Notifiable {

    private var recycler: RecyclerView? = null
    private var adapter: WedgeAdapter? = null

    private var wedges: MutableList<Wedge<*>>? = null
    private var gitHubToken: String? = null

    private var providers: Array<RequestProvider>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        recycler = inflater.inflate(R.layout.fragment_attribouter_about, container, false) as RecyclerView

        val args = arguments
        var fileRes = R.xml.attribouter
        if (args != null) {
            gitHubToken = args.getString(Attribouter.EXTRA_GITHUB_OAUTH_TOKEN, null)
            fileRes = args.getInt(Attribouter.EXTRA_FILE_RES, fileRes)
        }

        providers = arrayOf(GitHubService.withToken(gitHubToken).create())

        wedges = ArrayList()
        val parser = resources.getXml(fileRes)
        wedges?.addAll(XMLWedgeProvider(parser).map { xmlProvider, item ->
            providers?.forEach {
                item.withProvider<Wedge<*>>(it)
            }

            item.withProvider<Wedge<*>>(xmlProvider)
                    .withNotifiable(this)
        }.getAllWedges())

        parser.close()

        adapter = WedgeAdapter(wedges)
        recycler?.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = this@AboutFragment.adapter
        }

        return recycler
    }

    override fun onItemChanged(changed: Wedge<*>) {
        wedges?.indexOf(changed)?.let { adapter?.notifyItemChanged(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        providers?.forEach { it.destroy() }
    }
}
