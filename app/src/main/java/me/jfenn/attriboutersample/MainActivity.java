package me.jfenn.attriboutersample;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.jfenn.attribouter.Attribouter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Attribouter.from(this)
                .withFile(R.xml.about)
                .show();

        finish();
    }
}
