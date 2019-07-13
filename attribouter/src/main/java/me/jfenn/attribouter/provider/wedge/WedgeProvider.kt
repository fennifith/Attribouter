package me.jfenn.attribouter.provider.wedge

import me.jfenn.attribouter.wedges.Wedge

interface WedgeProvider {

    fun getWedges(parent: Wedge<*>? = null): List<Wedge<*>>
    fun <T> getAttribute(wedge: Wedge<*>, attribute: String, defaultValue: T? = null): T?
    fun  map(map: (WedgeProvider, Wedge<*>) -> Wedge<*>): WedgeProvider

}
