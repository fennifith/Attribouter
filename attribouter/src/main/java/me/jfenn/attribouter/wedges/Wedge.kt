package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.data.RequestProvider
import me.jfenn.attribouter.provider.wedge.WedgeProvider
import kotlin.reflect.KProperty

abstract class Wedge<T : Wedge.ViewHolder>(@param:LayoutRes val layoutRes: Int) {
    private val children: MutableList<Wedge<*>> = ArrayList()
    private val attributes: ArrayList<attr<*,*>> = ArrayList()
    private val providers: ArrayList<RequestProvider> = ArrayList()

    fun <R: Wedge<*>> withProvider(provider: WedgeProvider): R {
        for (attribute in attributes)
            attribute.withProvider(provider)

        addChildren(provider.getWedges(this))
        return this as R
    }

    fun <R: Wedge<*>> withProvider(provider: RequestProvider): R {
        providers.add(provider)
        return this as R
    }

    fun <R: Wedge<*>> create() : R {
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

        return child
    }

    fun getChildren(): List<Wedge<*>> {
        return children
    }

    fun <X : Wedge<*>> getChildren(type: Class<X>): List<X> {
        val children = ArrayList<X>()
        for (info in getChildren().filter { type.isInstance(it) })
            children.add(info as X)

        return children
    }

    internal fun getProvider(id: String? = null): RequestProvider? {
        return id?.let {
            providers.firstOrNull { it.id == id }
        } ?: providers.firstOrNull()
    }

    abstract fun getViewHolder(v: View): T

    abstract fun bind(context: Context, viewHolder: T)

    open class ViewHolder(v: View) : RecyclerView.ViewHolder(v)

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
