package me.jfenn.attribouter.dialogs

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.utils.ResourceUtils.getString
import me.jfenn.attribouter.utils.ResourceUtils.getThemeResourceAttribute
import me.jfenn.attribouter.utils.bind
import me.jfenn.attribouter.wedges.Wedge

open class OverflowDialog(
        context: Context,
        val title: String,
        val items: List<Wedge<*>>
) : AppCompatDialog(
        context,
        getThemeResourceAttribute(context, R.styleable.AttribouterTheme_overflowDialogTheme, R.style.AttribouterTheme_Dialog_Fullscreen)
) {

    private val toolbar: Toolbar? by bind(R.id.toolbar)
    private val recyclerView: RecyclerView? by bind(R.id.recycler)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attribouter_dialog_overflow)

        toolbar?.apply {
            title = getString(context, this@OverflowDialog.title)
            setNavigationIcon(R.drawable.attribouter_ic_arrow_back)
            setNavigationOnClickListener { dismiss() }
        }

        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = WedgeAdapter(items)
        }
    }

}