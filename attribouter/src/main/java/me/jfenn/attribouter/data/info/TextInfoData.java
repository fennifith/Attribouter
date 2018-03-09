package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import me.jfenn.attribouter.R;

public class TextInfoData extends InfoData {

    private String text;
    private boolean isHeader;
    private boolean isCentered;

    public TextInfoData(XmlResourceParser parser) throws XmlPullParserException {
        super(R.layout.item_attribouter_text);
        text = parser.getAttributeValue(null, "text");
        isHeader = parser.getAttributeBooleanValue(null, "header", false);
        isCentered = parser.getAttributeBooleanValue(null, "centered", false);
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        TextView textView = (TextView) viewHolder.itemView;
        textView.setText(text);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            textView.setTextAlignment(isCentered ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_GRAVITY);
    }

}
