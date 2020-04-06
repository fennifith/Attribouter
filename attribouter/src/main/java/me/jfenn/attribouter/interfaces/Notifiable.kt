package me.jfenn.attribouter.interfaces

import me.jfenn.attribouter.wedges.Wedge

interface Notifiable {

    fun onItemChanged(changed: Wedge<*>)

}