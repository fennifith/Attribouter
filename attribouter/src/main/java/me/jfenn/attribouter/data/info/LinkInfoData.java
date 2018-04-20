package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.utils.UrlClickListener;

public class LinkInfoData extends InfoData<LinkInfoData.ViewHolder> {

    @Nullable
    private String name;
    @Nullable
    private String url;
    @Nullable
    private String icon;

    public LinkInfoData(@Nullable String name, @Nullable String url, @Nullable String icon) {
        super(R.layout.item_attribouter_link);
        this.name = name;
        this.url = url;
        this.icon = icon;
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
        if (url != null)
            return new UrlClickListener(ResourceUtils.getString(context, url));
        else return null;
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
        return obj != null && obj instanceof LinkInfoData && (url != null ? url.equals(((LinkInfoData) obj).url) : super.equals(obj));
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

    public static class ViewHolder extends InfoData.ViewHolder {

        private TextView nameView;
        private ImageView iconView;

        ViewHolder(View v) {
            super(v);
            nameView = v.findViewById(R.id.name);
            iconView = v.findViewById(R.id.icon);
        }
    }

}
