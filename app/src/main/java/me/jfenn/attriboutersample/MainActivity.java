package me.jfenn.attriboutersample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.jfenn.attribouter.Attribouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Attribouter attribouter = Attribouter.from(this)
                .withFile(R.xml.about);

        if (BuildConfig.GITHUB_TOKEN != null)
            attribouter.withGitHubToken(BuildConfig.GITHUB_TOKEN);

        attribouter.show();

        finish();
    }
}
