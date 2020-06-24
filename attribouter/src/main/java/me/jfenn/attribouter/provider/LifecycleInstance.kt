package me.jfenn.attribouter.provider

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.wedges.Wedge
import me.jfenn.gitrest.RequestProviderDelegate
import me.jfenn.gitrest.base.ServiceBuilder

class LifecycleInstance(
        services: List<ServiceBuilder<*>>,
        val scope: LifecycleCoroutineScope? = null,
        val notifiable: Notifiable? = null
) {

    val provider = RequestProviderDelegate(services.toTypedArray())

    fun notifyItemChanged(wedge: Wedge<*>) {
        notifiable?.onItemChanged(wedge)
    }

    fun launch(routine: suspend CoroutineScope.() -> Unit) {
        (scope ?: GlobalScope).launch(block = routine)
    }

}