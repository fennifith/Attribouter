package me.jfenn.attribouter

import me.jfenn.attribouter.provider.wedge.XMLWedgeProvider
import me.jfenn.attribouter.wedges.AppWedge
import me.jfenn.attribouter.wedges.ContributorsWedge
import me.jfenn.attribouter.wedges.LicensesWedge
import me.jfenn.attribouter.wedges.TranslatorsWedge
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class XMLParserTest {

    private fun getXmlParser(xml: String) : XmlPullParser {
        val factory = XmlPullParserFactory.newInstance().apply {
            isNamespaceAware = false
        }

        return factory.newPullParser().apply {
            setInput(StringReader(xml.trimIndent()))
        }
    }

    @Test
    fun parseSimpleXml() {
        val parser = getXmlParser("""
            <about>
                <AppWedge/>
                <ContributorsWedge/>
                <TranslatorsWedge/>
                <LicensesWedge/>
            </about>
        """)

        val wedges = XMLWedgeProvider(parser).getAllWedges()
        assert(wedges[0] is AppWedge)
        assert(wedges[1] is ContributorsWedge)
        assert(wedges[2] is TranslatorsWedge)
        assert(wedges[3] is LicensesWedge)
    }

    @Test
    fun parseXmlProperties() {
        val provider = XMLWedgeProvider(getXmlParser("""
            <about>
                <AppWedge
                    title="Test Title">
                </AppWedge>
            </about>
        """.trimIndent()))

        val wedges = provider.map { _, wedge ->
            wedge.withWedgeProvider(provider)
        }.getAllWedges()

        assert(wedges[0] is AppWedge)
        assertEquals("Test Title", (wedges[0] as? AppWedge)?.title)
    }

}
