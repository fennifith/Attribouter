package me.jfenn.attribouter.dialogs

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.widget.Toolbar
import me.jfenn.androidutils.autoSystemUiColors
import me.jfenn.androidutils.bind
import me.jfenn.attribouter.R
import me.jfenn.attribouter.utils.ResourceUtils.getString
import me.jfenn.attribouter.utils.UrlClickListener
import me.jfenn.attribouter.utils.getThemeAttr
import me.jfenn.attribouter.utils.toListString
import me.jfenn.attribouter.wedges.LicenseWedge

class LicenseDialog(
        context: Context,
        private val license: LicenseWedge
) : AppCompatDialog(
        context,
        context.getThemeAttr(R.attr.attribouter_licenseDialogTheme, R.style.AttribouterTheme_Dialog_Fullscreen)
) {

    val toolbar: Toolbar? by bind(R.id.toolbar)
    val nameView: TextView? by bind(R.id.name)
    val descriptionView: TextView? by bind(R.id.description)
    val permissionsTextView: TextView? by bind(R.id.permissionsText)
    val conditionsTextView: TextView? by bind(R.id.conditionsText)
    val limitationsTextView: TextView? by bind(R.id.limitationsText)
    val bodyView: TextView? by bind(R.id.body)
    val moreInfoButton: View? by bind(R.id.moreInfo)

    override fun onStart() {
        super.onStart()
        window?.autoSystemUiColors()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.attribouter_dialog_license)

        val licenseName = getString(context, license.licenseName)

        toolbar?.apply {
            title = licenseName
            setNavigationOnClickListener { dismiss() }
        }

        nameView?.text = licenseName
        descriptionView?.text = getString(context, license.licenseDescription)

        license.licensePermissions?.let { permissions ->
            permissionsTextView?.text = permissions.toListString()
        }

        license.licenseConditions?.let { conditions ->
            conditionsTextView?.text = conditions.toListString()
        }

        license.licenseLimitations?.let { limitations ->
            limitationsTextView?.text = limitations.toListString()
        }

        bodyView?.text = getString(context, license.licenseBody)?.trimIndent()

        val moreInfo = getString(context, license.licenseUrl)
        moreInfo?.let {
            moreInfoButton?.setOnClickListener(UrlClickListener(it))
        } ?: run {
            moreInfoButton?.visibility = View.GONE
        }
    }

}