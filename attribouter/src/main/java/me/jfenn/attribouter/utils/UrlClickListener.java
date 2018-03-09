package me.jfenn.attribouter.utils;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

public class UrlClickListener implements View.OnClickListener {

    private Uri uri;

    public UrlClickListener(String url) {
        uri = Uri.parse(url);
    }

    @Override
    public void onClick(View v) {
        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}
