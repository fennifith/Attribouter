package me.jfenn.attribouter.provider

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.provider.net.RequestProviderDelegate
import me.jfenn.attribouter.provider.net.ServiceBuilder
import me.jfenn.attribouter.wedges.Wedge

class LifecycleInstance(
        services: List<ServiceBuilder<*>>,
        val scope: LifecycleCoroutineScope? = null,
        val notifiable: Notifiable? = null
) {

    val provider = RequestProviderDelegate(services)

    fun notifyItemChanged(wedge: Wedge<*>) {
        notifiable?.onItemChanged(wedge)
    }

    fun launch(routine: suspend CoroutineScope.() -> Unit) {
        (scope ?: GlobalScope).launch(block = routine)
    }

}