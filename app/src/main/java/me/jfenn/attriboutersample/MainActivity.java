package me.jfenn.attriboutersample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.jfenn.attribouter.Attribouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Attribouter.from(this)
                .withFile(R.xml.about)
                .withGitHubToken("8acce9d4520d571a7f8d4b5ec38c1db358cf8880")
                .show();

        finish();
    }
}
