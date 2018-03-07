package me.jfenn.attribouter.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.Toolbar;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ColorUtils;

public class AboutActivity extends AppCompatActivity {

    public static final String EXTRA_FILE_RES = "me.jfenn.attribouter.EXTRA_FILE_RES";

    private Toolbar toolbar;
    private RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attribouter_about);

        toolbar = findViewById(R.id.toolbar);
        recycler = findViewById(R.id.recycler);

        toolbar.getContext().setTheme(ColorUtils.isColorLight(ContextCompat.getColor(this, R.color.colorPrimary)) ? R.style.Theme_AppCompat_Light : R.style.Theme_AppCompat);

        int fileRes = getIntent().getIntExtra(EXTRA_FILE_RES, -1);
    }
}
