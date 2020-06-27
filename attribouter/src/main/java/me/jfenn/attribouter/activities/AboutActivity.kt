package me.jfenn.attribouter.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import me.jfenn.androidutils.autoSystemUiColors
import me.jfenn.androidutils.bind
import me.jfenn.attribouter.Attribouter
import me.jfenn.attribouter.R
import me.jfenn.attribouter.fragments.AboutFragment

class AboutActivity : AppCompatActivity() {

    private val toolbar: Toolbar? by bind(R.id.toolbar)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        setTheme(bundle?.getInt(Attribouter.EXTRA_THEME_RES, R.style.AttribouterTheme_DayNight) ?: R.style.AttribouterTheme_DayNight)

        setContentView(R.layout.attribouter_activity_about)
        setSupportActionBar(toolbar)

        val fragment = AboutFragment()

        if (bundle != null) fragment.arguments = bundle
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().add(R.id.fragment, fragment).commit()
        else supportFragmentManager.beginTransaction().replace(R.id.fragment, fragment).commit()

        // handle light status/nav bar colors
        window.autoSystemUiColors()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(Bundle(), outPersistentState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(Bundle())
    }
}