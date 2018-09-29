package me.jfenn.attribouter.wedges.link;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.jfenn.attribouter.R;
import me.jfenn.attribouter.interfaces.Mergeable;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;
import me.jfenn.attribouter.wedges.Wedge;

public class LinkWedge extends Wedge<LinkWedge.ViewHolder> implements Mergeable<LinkWedge> {

    @Nullable
    private String id;
    @Nullable
    private String name;
    @Nullable
    private String url;
    @Nullable
    private String icon;
    private boolean isHidden;
    int priority;

    public LinkWedge(XmlResourceParser parser) {
        this(parser.getAttributeValue(null, "id"),
                parser.getAttributeValue(null, "name"),
                parser.getAttributeValue(null, "url"),
                parser.getAttributeValue(null, "icon"),
                parser.getAttributeBooleanValue(null, "hidden", false),
                0);

        String priorityString = parser.getAttributeValue(null, "priority");
        if (priorityString != null)
            priority = Integer.parseInt(priorityString);
    }

    public LinkWedge(@Nullable String id, @Nullable String name, @Nullable String url, @Nullable String icon, boolean isHidden, int priority) {
        super(R.layout.item_attribouter_link);
        this.id = id;
        this.name = name;
        if (url != null && url.length() > 0)
            this.url = url.startsWith("http") ? url : "http://" + url;
        this.icon = icon;
        this.isHidden = isHidden;
        this.priority = priority;
    }

    /**
     * Returns the human-readable "name" of the link.
     *
     * @param context the current context
     * @return a string name that describes the link
     */
    @Nullable
    public String getName(Context context) {
        return ResourceUtils.getString(context, name);
    }

    /**
     * Returns a View.OnClickListener that opens the link.
     *
     * @param context the current context
     * @return a click listener to be applied to the respective view
     */
    @Nullable
    public View.OnClickListener getListener(Context context) {
        if (url != null && url.length() > 0 && URLUtil.isValidUrl(url))
            return new UrlClickListener(ResourceUtils.getString(context, url));
        else return null;
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public LinkWedge merge(LinkWedge mergee) {
        if (id == null && mergee.id != null)
            id = mergee.id;
        if ((name == null || !name.startsWith("^")) && mergee.name != null)
            name = mergee.name;
        if ((url == null || !url.startsWith("^")) && mergee.url != null)
            url = mergee.url;
        if ((icon == null || !icon.startsWith("^")) && mergee.icon != null)
            icon = mergee.icon;
        if (mergee.isHidden)
            isHidden = true;
        if (mergee.priority != 0)
            priority = mergee.priority;

        return this;
    }

    @Override
    public boolean hasAll() {
        return true;
    }

    public boolean isHidden() {
        return isHidden;
    }

    String getUrl() {
        return url;
    }


    /**
     * Loads the link's icon.
     *
     * @param imageView the image view to load the icon into
     */
    public void loadIcon(ImageView imageView) {
        ResourceUtils.setImage(imageView.getContext(), icon, imageView);
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof LinkWedge
                && ((id != null && id.equals(((LinkWedge) obj).id))
                || (url != null ? url.equals(((LinkWedge) obj).url) : super.equals(obj)));
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        viewHolder.nameView.setText(getName(context));
        loadIcon(viewHolder.iconView);
        viewHolder.itemView.setOnClickListener(getListener(context));
    }

    public int compareTo(Context context, @NonNull LinkWedge o) {
        String name = ResourceUtils.getString(context, this.name);
        String oname = ResourceUtils.getString(context, o.name);
        int comparison = name != null && oname != null ? name.compareTo(oname) : 0;
        return ((o.priority - priority) * 2) + (comparison != 0 ? comparison / Math.abs(comparison) : 0);
    }

    protected static class ViewHolder extends Wedge.ViewHolder {

        protected TextView nameView;
        protected ImageView iconView;

        protected ViewHolder(View v) {
            super(v);
            nameView = v.findViewById(R.id.name);
            iconView = v.findViewById(R.id.icon);
        }
    }

    public static class Comparator implements java.util.Comparator<LinkWedge> {

        private Context context;

        public Comparator(Context context) {
            this.context = context;
        }

        @Override
        public int compare(LinkWedge o1, LinkWedge o2) {
            return o1.compareTo(context, o2);
        }
    }

}
