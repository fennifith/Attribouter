package me.jfenn.attribouter.data;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;

import me.jfenn.attribouter.R;

public class LicensesInfoData extends InfoData {

    public LicensesInfoData(XmlResourceParser parser) {
        super(R.layout.item_attribouter_licenses);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {

    }
}
