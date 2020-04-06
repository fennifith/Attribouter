package me.jfenn.attribouter.provider

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.provider.net.RequestProvider
import me.jfenn.attribouter.wedges.Wedge

class LifecycleInstance(
        val providers: List<RequestProvider> = listOf(),
        val scope: LifecycleCoroutineScope? = null,
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

    fun launch(routine: suspend CoroutineScope.() -> Unit) {
        (scope ?: GlobalScope).launch(block = routine)
    }

}