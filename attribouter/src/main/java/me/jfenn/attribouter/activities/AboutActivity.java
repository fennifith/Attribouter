package me.jfenn.attribouter.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

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

        int color = ColorUtils.isColorLight(ContextCompat.getColor(this, R.color.colorPrimary)) ? Color.BLACK : Color.WHITE;

        toolbar.setTitleTextColor(color);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Drawable backArrow = VectorDrawableCompat.create(getResources(), R.drawable.ic_attribouter_arrow_back, getTheme());
        backArrow.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(backArrow);

        Bundle bundle = getIntent().getExtras();
        AboutFragment fragment = new AboutFragment();
        if (bundle != null)
            fragment.setArguments(bundle);

        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.fragment, fragment).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
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
