package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ResourceUtils;

public class HeaderInfoData extends InfoData {

    private String text;

    public HeaderInfoData(String text) {
        super(R.layout.item_attribouter_header);
        this.text = text;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        ((TextView) viewHolder.itemView).setText(ResourceUtils.getString(context, this.text));
    }

}
