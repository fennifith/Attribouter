package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ResourceUtils;

public class TextWedge extends Wedge {

    private String text;
    private boolean isCentered;

    public TextWedge(XmlResourceParser parser) throws XmlPullParserException {
        this(parser.getAttributeValue(null, "text"), parser.getAttributeBooleanValue(null, "centered", false));
    }

    public TextWedge(String text, boolean isCentered) {
        super(R.layout.item_attribouter_text);
        this.text = text;
        this.isCentered = isCentered;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        TextView textView = (TextView) viewHolder.itemView;
        textView.setMovementMethod(new LinkMovementMethod());
        String text = ResourceUtils.getString(context, this.text);
        textView.setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? Html.fromHtml(text, 0) : Html.fromHtml(text));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            textView.setTextAlignment(isCentered ? View.TEXT_ALIGNMENT_CENTER : View.TEXT_ALIGNMENT_GRAVITY);
    }

}
