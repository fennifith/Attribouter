package me.jfenn.attribouter.provider

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.jfenn.attribouter.BuildConfig
import me.jfenn.attribouter.interfaces.Notifiable
import me.jfenn.attribouter.wedges.Wedge
import me.jfenn.gitrest.base.ServiceBuilder
import me.jfenn.gitrest.gitrest
import me.jfenn.gitrest.service.DiskCache
import me.jfenn.gitrest.service.MemoryCache

class LifecycleInstance(
        context: Context,
        providers: List<ServiceBuilder<*>>,
        val scope: LifecycleCoroutineScope? = null,
        val notifiable: Notifiable? = null
) {

    val client = gitrest {
        this.providers = providers.toTypedArray()
        cache = MemoryCache(DiskCache(this, context.cacheDir))
        logDebug = {
            if (BuildConfig.DEBUG) Log.d("me.jfenn.gitrest", it)
        }
        logError = {
            if (BuildConfig.DEBUG) Log.e("me.jfenn.gitrest", it)
        }
    }

    fun notifyItemChanged(wedge: Wedge<*>) {
        notifiable?.onItemChanged(wedge)
    }

    fun launch(routine: suspend CoroutineScope.() -> Unit) {
        (scope ?: GlobalScope).launch(block = routine)
    }

}