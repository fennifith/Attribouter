package me.jfenn.attribouter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.XmlRes;
import android.support.v4.app.Fragment;

import me.jfenn.attribouter.activities.AboutActivity;
import me.jfenn.attribouter.fragments.AboutFragment;

public class Attribouter {

    public static final String EXTRA_FILE_RES = "me.jfenn.attribouter.EXTRA_FILE_RES";

    private Context context;
    private int fileRes;

    private Attribouter(Context context) {
        this.context = context;
    }

    public Attribouter withFile(@XmlRes int fileRes) {
        this.fileRes = fileRes;
        return this;
    }

    public void show() {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(EXTRA_FILE_RES, fileRes);
        context.startActivity(intent);
    }

    public Fragment toFragment() {
        Bundle args = new Bundle();
        args.putInt(EXTRA_FILE_RES, fileRes);

        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Attribouter from(Context context) {
        return new Attribouter(context);
    }

}
