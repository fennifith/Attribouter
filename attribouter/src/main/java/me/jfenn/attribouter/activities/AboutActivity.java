package me.jfenn.attribouter.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toolbar;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ColorUtils;

public class AboutActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribouter_about);

        toolbar = findViewById(R.id.toolbar);

        toolbar.getContext().setTheme(ColorUtils.isColorLight(ContextCompat.getColor(this, R.color.colorPrimary)) ? R.style.Theme_AppCompat_Light : R.style.Theme_AppCompat);
    }
}
