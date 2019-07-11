package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.data.github.GitHubData
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.wedge.WedgeProvider
import java.util.*
import kotlin.reflect.KProperty

abstract class Wedge<T : Wedge.ViewHolder>(@param:LayoutRes val layoutRes: Int) : GitHubData.OnInitListener {
    private val children: MutableList<Wedge<*>> = ArrayList()
    private val requests: MutableList<GitHubData> = ArrayList()
    private val attributes: ArrayList<attr<*,*>> = ArrayList()

    private var listener: OnRequestListener? = null

    fun withProvider(provider: WedgeProvider): Wedge<T> {
        for (attribute in attributes)
            attribute.withProvider(provider)

        addChildren(provider.getWedges(this))
        onCreate()
        return this
    }

    fun <R : Wedge<T>> create() : R {
        onCreate()
        return this as R
    }

    open fun onCreate() {}

    internal fun addChildren(children: Collection<Wedge<*>>) {
        for (c in children) addChild(c)
    }

    internal fun addChild(child: Wedge<*>): Wedge<*> {
        return addChild(children.size, child)
    }

    internal fun addChild(index: Int, child: Wedge<*>): Wedge<*> {
        if (!children.contains(child)) {
            children.add(index, child)
        } else {
            val merger = children[children.indexOf(child)]
            (merger as? Mergeable<Wedge<*>>)?.let {
                it.merge(child)
                return merger
            } ?: run { children.add(index, child) }
        }

        if (listener != null)
            child.setOnRequestListener(listener)

        return child
    }

    fun getChildren(): List<Wedge<*>> {
        return children
    }

    fun <X : Wedge<*>> getChildren(type: Class<X>): List<X> {
        val children = ArrayList<X>()
        for (info in getChildren()) {
            if (type.isInstance(info))
                children.add(info as X)
        }

        return children
    }

    fun addRequest(request: GitHubData) {
        request.addOnInitListener(this)
        requests.add(request)
        listener?.onRequest(this, request)
    }

    fun setOnRequestListener(listener: OnRequestListener?) {
        this.listener = listener
        if (listener != null) {
            for (request in requests)
                listener.onRequest(this, request)
        }

        for (child in children)
            child.setOnRequestListener(listener)
    }

    fun getRequests(): List<GitHubData> {
        return requests
    }

    fun hasRequest(request: GitHubData): Boolean {
        return requests.contains(request)
    }

    abstract fun getViewHolder(v: View): T

    abstract fun bind(context: Context, viewHolder: T)

    override fun onInit(data: GitHubData) {}

    override fun onFailure(data: GitHubData) {}

    open class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

    interface OnRequestListener {
        fun onRequest(info: Wedge<*>, request: GitHubData)
    }

    open inner class attr<in R : Wedge<*>, T>(
            val attribute: String,
            protected var property: T? = null
    ) {

        init {
            attributes.add(this)
        }

        open fun withProvider(provider: WedgeProvider) {
            property = provider.getAttribute(this@Wedge, attribute, property) ?: property
        }

        operator fun getValue(thisRef: R?, prop: KProperty<*>): T? {
            return property
        }

        open operator fun setValue(thisRef: R?, prop: KProperty<*>, value: T) {
            property = value
        }
    }

    inner class attrInt<in R: Wedge<*>, T>(
            attribute: String,
            property: T?
    ) : attr<R, T>(attribute, property) {

        override fun withProvider(provider: WedgeProvider) {
            property = provider.getAttribute(this@Wedge, attribute, property.toString())?.let {
                Integer.parseInt(it) as? T
            }
        }

    }

}
