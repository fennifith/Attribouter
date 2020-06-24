package me.jfenn.attribouter.wedges

import android.content.Context
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.provider.LifecycleInstance
import me.jfenn.attribouter.provider.wedge.WedgeProvider
import me.jfenn.attribouter.utils.isResourceMutable
import kotlin.reflect.KProperty

abstract class Wedge<T : Wedge.ViewHolder>(@param:LayoutRes val layoutRes: Int) : Notifiable {

    private val children: MutableList<Wedge<*>> = ArrayList()
    private val attributes: ArrayList<attr<*,*>> = ArrayList()
    internal var lifecycle: LifecycleInstance? = null
    internal var parent: Notifiable? = null

    open var isHidden by object : attr<Wedge<T>, Boolean>("hidden", false) {
        override fun apply(original: Boolean?, value: Boolean?): Boolean? = original == true || value == true
    }

    fun <R: Wedge<*>> create(lifecycle: LifecycleInstance? = null) : R {
        this.lifecycle = lifecycle

        onCreate()
        return this as R
    }

    /**
     * Combine one Wedge's properties with another's...
     *
     * Used to handle duplicate links or contributors, based on `.equals()`
     */
    fun merge(other: Wedge<*>) : Wedge<*> {
        for (otherAttr in other.attributes) {
            attributes.find { it.name == otherAttr.name }?.mergeValue(otherAttr.getValue(null, null))
        }

        notifyItemChanged()
        return this
    }

    fun withWedgeProvider(provider: WedgeProvider) : Wedge<*> {
        for (attribute in attributes)
            attribute.withProvider(provider)

        addChildren(provider.getWedges(this))
        return this
    }

    fun withParent(parent: Notifiable) : Wedge<*> {
        this.parent = parent
        return this
    }

    /**
     * Handle changes of child items.
     *
     * By default, simply forwards events to the next parent,
     * causing the entire view to be re-bound.
     */
    override fun onItemChanged(changed: Wedge<*>) {
        notifyItemChanged()
    }

    open fun onCreate() {}

    /**
     * Shortcut function for LifecycleInstance
     */
    internal fun notifyItemChanged() {
        (parent ?: lifecycle?.notifiable)?.onItemChanged(this)
    }

    internal fun addChildren(children: Collection<Wedge<*>>) {
        for (c in children) addChild(c)
    }

    internal fun addChild(child: Wedge<*>): Wedge<*> {
        return addChild(children.size, child)
    }

    internal fun addChild(index: Int, child: Wedge<*>): Wedge<*> {
        if (!children.contains(child)) {
            children.add(index, child.withParent(this))
        } else {
            children[children.indexOf(child)].merge(child)
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
            val name: String,
            protected var property: T? = null
    ) {

        init {
            attributes.add(this)
        }

        open fun withProvider(provider: WedgeProvider) {
            property = provider.getAttribute(this@Wedge, name, property) ?: property
        }

        open fun apply(original: T?, value: T?) : T? {
            return value ?: original
        }

        operator fun getValue(thisRef: R?, prop: KProperty<*>?): T {
            return if ((property as? String)?.startsWith('^') == true)
                (property as? String)?.substring(1) as T
            else property as T
        }

        open operator fun setValue(thisRef: R?, prop: KProperty<*>?, value: T?) {
            if ((property as? String).isResourceMutable())
                property = apply(property, value)
        }

        open fun mergeValue(value: Any?) {
            (value as? T?).let { setValue(null, null, it) }
        }
    }

    open inner class attrInt<in R: Wedge<*>>(
            attribute: String,
            property: Int? = null
    ) : attr<R, Int>(attribute, property) {

        override fun withProvider(provider: WedgeProvider) {
            provider.getAttribute(this@Wedge, name, property.toString())?.let {
                property = Integer.parseInt(it)
            }
        }

    }

}
