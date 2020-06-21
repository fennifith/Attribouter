package me.jfenn.attribouter.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import me.jfenn.attribouter.R
import me.jfenn.attribouter.adapters.WedgeAdapter
import me.jfenn.attribouter.utils.ResourceUtils.getString
import me.jfenn.attribouter.utils.ResourceUtils.getThemeResourceAttribute
import me.jfenn.attribouter.utils.bind
import me.jfenn.attribouter.utils.loadDrawable
import me.jfenn.attribouter.wedges.ContributorWedge
import me.jfenn.attribouter.wedges.LinkWedge

open class UserDialog(
        context: Context,
        private val contributor: ContributorWedge
) : AppCompatDialog(
        context,
        getThemeResourceAttribute(context, R.styleable.AttribouterTheme_personDialogTheme, R.style.AttribouterTheme_Dialog)
) {

    private val nameView: TextView? by bind(R.id.name)
    private val taskView: TextView? by bind(R.id.task)
    private val imageView: ImageView? by bind(R.id.image)
    private val bioView: TextView? by bind(R.id.description)
    private val recycler: RecyclerView? by bind(R.id.links)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attribouter_dialog_user)

        nameView?.text = getString(context, contributor.getCanonicalName())
        taskView?.text = getString(context, contributor.task)
        bioView?.text = getString(context, contributor.bio)

        context.loadDrawable(contributor.avatarUrl, R.drawable.attribouter_image_avatar) {
            imageView?.setImageDrawable(it)
        }

        val links = contributor.getTypedChildren<LinkWedge>()
        if (links.isNotEmpty()) {
            recycler?.apply {
                visibility = View.VISIBLE
                layoutManager = FlexboxLayoutManager(context).apply {
                    flexDirection = FlexDirection.ROW
                    justifyContent = JustifyContent.FLEX_START
                }
                adapter = WedgeAdapter(links.filter { !it.isHidden }.sorted())
            }
        } else recycler?.visibility = View.GONE
    }

}