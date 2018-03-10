package me.jfenn.attribouter.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.fragments.AboutFragment;
import me.jfenn.attribouter.utils.ColorUtils;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribouter_about);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(ColorUtils.isColorLight(ContextCompat.getColor(this, R.color.colorPrimary)) ? Color.BLACK : Color.WHITE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && savedInstanceState == null) {
            AboutFragment fragment = new AboutFragment();
            fragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(new Bundle(), outPersistentState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(new Bundle());
    }
}
