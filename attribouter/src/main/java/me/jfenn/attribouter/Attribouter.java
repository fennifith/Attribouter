package me.jfenn.attribouter;

import android.content.Context;
import android.content.Intent;

import me.jfenn.attribouter.activities.AboutActivity;

public class Attribouter {

    public static final String EXTRA_FILE_RES = "me.jfenn.attribouter.EXTRA_FILE_RES";

    private Context context;
    private int fileRes;

    private Attribouter(Context context) {
        this.context = context;
    }

    public void show() {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(EXTRA_FILE_RES, fileRes);
        context.startActivity(intent);
    }

    public static Attribouter from(Context context) {
        return new Attribouter(context);
    }

}
