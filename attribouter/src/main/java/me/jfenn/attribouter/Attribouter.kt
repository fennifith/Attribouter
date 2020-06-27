package me.jfenn.attribouter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import androidx.annotation.StyleRes
import androidx.annotation.XmlRes
import androidx.fragment.app.Fragment
import me.jfenn.attribouter.activities.AboutActivity
import me.jfenn.attribouter.fragments.AboutFragment

class Attribouter(
        private val context: Context
) {

    private var themeRes: Int? = null
    private var fileRes: Int? = null
    private val tokens = HashMap<String, String>()

    fun withTheme(@StyleRes themeRes: Int): Attribouter {
        this.themeRes = themeRes
        return this
    }

    fun withFile(@XmlRes fileRes: Int): Attribouter {
        this.fileRes = fileRes
        return this
    }

    fun withGitHubToken(token: String?) = withToken("api.github.com", token)
    fun withGitLabToken(token: String?) = withToken("gitlab.com", token)

    fun withToken(hostname: String, token: String?) : Attribouter {
        token?.let { tokens[hostname] = it }
        return this
    }

    fun show() {
        val intent = Intent(context, AboutActivity::class.java)
        intent.putExtra(EXTRA_THEME_RES, themeRes)
        intent.putExtra(EXTRA_FILE_RES, fileRes)
        tokens.forEach { (hostname, token) ->
            intent.putExtra(EXTRA_TOKEN + hostname, token)
        }

        context.startActivity(intent)
    }

    fun toFragment(): Fragment {
        val args = Bundle()
        themeRes?.let { args.putInt(EXTRA_THEME_RES, it) }
        fileRes?.let { args.putInt(EXTRA_FILE_RES, it) }
        tokens.forEach { (hostname, token) ->
            args.putString(EXTRA_TOKEN + hostname, token)
        }

        val fragment = AboutFragment()
        fragment.arguments = args
        return fragment
    }

    companion object {
        const val EXTRA_THEME_RES = "me.jfenn.attribouter.EXTRA_THEME_RES"
        const val EXTRA_FILE_RES = "me.jfenn.attribouter.EXTRA_FILE_RES"
        const val EXTRA_TOKEN = "me.jfenn.attribouter.EXTRA_TOKEN:"

        @JvmStatic
        fun from(context: Context): Attribouter {
            return Attribouter(context)
        }
    }

}

fun ContextWrapper.attribouter(block: Attribouter.() -> Unit) = Attribouter(this).apply { block() }

fun ContextWrapper.attribouterActivity(block: Attribouter.() -> Unit) = attribouter(block).show()

fun ContextWrapper.attribouterFragment(block: Attribouter.() -> Unit) = attribouter(block).toFragment()
