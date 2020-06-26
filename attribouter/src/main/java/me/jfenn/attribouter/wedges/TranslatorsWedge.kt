package me.jfenn.attribouter.wedges

import java.util.*

open class TranslatorsWedge : ListWedge("@string/attribouter_title_translators", true) {

    override fun getListItems(): List<Wedge<*>> {
        val sortedList = ArrayList<Wedge<*>>()

        for (language in Locale.getISOLanguages()) {
            var isHeader = false
            for (translator in getTypedChildren<TranslatorWedge>().filter { !it.locales.isNullOrEmpty() }) {
                var isLocale = false
                translator.locales?.split(",")?.forEach { locale ->
                    if (language == locale)
                        isLocale = true
                }

                if (isLocale) {
                    var item = translator
                    if (!isHeader) {
                        item = translator.clone().apply {
                            locales = language
                            isFirst = true
                        }

                        isHeader = true
                    }

                    sortedList.add(item)
                }
            }
        }

        return sortedList
    }

}
