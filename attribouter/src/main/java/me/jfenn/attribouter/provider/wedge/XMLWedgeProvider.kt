package me.jfenn.attribouter.provider.wedge

import android.content.res.XmlResourceParser
import android.util.Log
import me.jfenn.attribouter.provider.reflect.ClassInstantiator
import me.jfenn.attribouter.wedges.Wedge
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.util.*

class XMLWedgeProvider(private val parser: XmlResourceParser) : WedgeProvider {

    private var map: (WedgeProvider, Wedge<*>) -> Wedge<*> = { _, i -> i }

    override fun map(map: (WedgeProvider, Wedge<*>) -> Wedge<*>): XMLWedgeProvider {
        this.map = map
        return this
    }

    private fun getWedge(className: String): Wedge<*>? {
        val instantiator: ClassInstantiator<*>

        try {
            instantiator = ClassInstantiator.fromString(className) ?: run { return null }
        } catch (e: ClassNotFoundException) {
            Log.e("Attribouter", "Class name \"$className\" not found - you should probably check your configuration file for typos.")
            e.printStackTrace()
            return null
        }

        try {
            return instantiator.instantiate() as Wedge<*>
        } catch (e: NoSuchMethodException) {
            Log.e("Attribouter", "Class \"$className\" definitely exists, but doesn't have the correct constructor. Check that you have defined one that accepts no arguments.")
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassCastException) {
            Log.e("Attribouter", "Class \"$className\" has been instantiated correctly, but it must extend \'${Wedge::class.java.name}\' to be worthy of the great RecyclerView adapter.")
            e.printStackTrace()
        }

        return null
    }

    // JVM Overload
    fun getAllWedges(): List<Wedge<*>> = getWedges()

    override fun getWedges(parent: Wedge<*>?): List<Wedge<*>> {
        val wedges = ArrayList<Wedge<*>>()

        try {
            while (parser.next() != XmlResourceParser.END_TAG || parser.name != parent?.javaClass?.name) {
                if (parser.name == "about")
                    continue

                if (parser.eventType == XmlPullParser.START_TAG) {
                    getWedge(parser.name)?.let {
                        wedges.add(map(this, it).create())
                    }
                }

                if (parser.eventType == XmlPullParser.END_DOCUMENT)
                    break
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        }

        return wedges
    }

    override fun <T> getAttribute(wedge: Wedge<*>, attribute: String, defaultValue: T?): T? {
        if (defaultValue is Boolean)
            return parser.getAttributeBooleanValue(null, attribute, defaultValue) as? T
        if (defaultValue is Int)
            return parser.getAttributeIntValue(null, attribute, defaultValue) as? T

        return parser.getAttributeValue(null, attribute) as? T
    }

}
