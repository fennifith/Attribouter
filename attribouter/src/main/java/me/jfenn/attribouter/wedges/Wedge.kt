package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.interfaces.Mergeable
import me.jfenn.attribouter.provider.LifecycleInstance
import me.jfenn.attribouter.provider.net.ProviderString
import me.jfenn.attribouter.provider.wedge.WedgeProvider
import kotlin.reflect.KProperty

abstract class Wedge<T : Wedge.ViewHolder>(@param:LayoutRes val layoutRes: Int) {
    private val children: MutableList<Wedge<*>> = ArrayList()
    private val attributes: ArrayList<attr<*,*>> = ArrayList()
    internal var lifecycle: LifecycleInstance? = null

    fun <R: Wedge<*>> create(lifecycle: LifecycleInstance? = null) : R {
        this.lifecycle = lifecycle

        onCreate()
        return this as R
    }

    fun withWedgeProvider(provider: WedgeProvider) : Wedge<*> {
        for (attribute in attributes)
            attribute.withProvider(provider)

        addChildren(provider.getWedges(this))
        return this
    }

    open fun onCreate() {}

    /**
     * Shortcut function for LifecycleInstance
     */
    internal fun notifyItemChanged() {
        lifecycle?.notifyItemChanged(this)
    }

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

    inline fun <reified X : Wedge<*>> getTypedChildren(): List<X> {
        return getChildren().filterIsInstance<X>()
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

    inner class attrInt<in R: Wedge<*>>(
            attribute: String,
            property: Int?
    ) : attr<R, Int?>(attribute, property) {

        override fun withProvider(provider: WedgeProvider) {
            property = provider.getAttribute(this@Wedge, attribute, property.toString())?.let {
                Integer.parseInt(it)
            }
        }

    }

    inner class attrProvider<in R: Wedge<*>>(
            attribute: String,
            property: String? = null
    ) : attr<R, ProviderString?>(attribute, property?.let { ProviderString(it) }) {

        override fun withProvider(provider: WedgeProvider) {
            property = provider.getAttribute(this@Wedge, attribute, property.toString())?.let {
                ProviderString(it)
            }
        }

    }

}
