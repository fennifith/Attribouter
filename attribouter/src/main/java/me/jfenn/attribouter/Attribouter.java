package me.jfenn.attribouter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.XmlRes;
import androidx.fragment.app.Fragment;

import me.jfenn.attribouter.activities.AboutActivity;
import me.jfenn.attribouter.fragments.AboutFragment;

public class Attribouter {

    public static final String EXTRA_FILE_RES = "me.jfenn.attribouter.EXTRA_FILE_RES";
    public static final String EXTRA_GITHUB_OAUTH_TOKEN = "me.jfenn.attribouter.EXTRA_GITHUB_OAUTH_TOKEN";

    private Context context;
    private Integer fileRes;
    private String gitHubToken;

    private Attribouter(Context context) {
        this.context = context;
    }

    public Attribouter withFile(@XmlRes int fileRes) {
        this.fileRes = fileRes;
        return this;
    }

    public Attribouter withGitHubToken(String token) {
        gitHubToken = token;
        return this;
    }

    public void show() {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(EXTRA_FILE_RES, fileRes);
        intent.putExtra(EXTRA_GITHUB_OAUTH_TOKEN, gitHubToken);
        context.startActivity(intent);
    }

    public Fragment toFragment() {
        Bundle args = new Bundle();
        if (fileRes != null)
            args.putInt(EXTRA_FILE_RES, fileRes);
        args.putString(EXTRA_GITHUB_OAUTH_TOKEN, gitHubToken);

        AboutFragment fragment = new AboutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static Attribouter from(Context context) {
        return new Attribouter(context);
    }

}
