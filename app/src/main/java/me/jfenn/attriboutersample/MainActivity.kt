package me.jfenn.attriboutersample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.jfenn.attribouter.attribouterActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // immediately start the Attribouter activity
        attribouterActivity {
            withFile(R.xml.about)
            withTheme(R.style.AttribouterTheme_DayNight)
            withGitHubToken(BuildConfig.GITHUB_TOKEN)
        }
    }

}