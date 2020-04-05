package me.jfenn.attribouter.provider

import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.wedges.Wedge

class LifecycleInstance(
        val providers: List<RequestProvider> = listOf(),
        val notifiable: Notifiable? = null
) {

    fun getProvider(id: String? = null): RequestProvider? {
        return id?.let {
            providers.firstOrNull { it.id == id }
        } ?: providers.firstOrNull()
    }

    fun notifyItemChanged(wedge: Wedge<*>) {
        notifiable?.onItemChanged(wedge)
    }

}