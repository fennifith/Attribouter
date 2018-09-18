package me.jfenn.attribouter.wedges;

import android.content.Context;
import android.content.res.XmlResourceParser;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.github.ContributorsData;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;
import me.jfenn.attribouter.dialogs.OverflowDialog;
import me.jfenn.attribouter.interfaces.Mergeable;
import me.jfenn.attribouter.utils.ResourceUtils;

public class TranslatorsWedge extends Wedge<TranslatorsWedge.ViewHolder> {

    @Nullable
    private String translatorsTitle;
    private List<Wedge> sortedTranslators;
    private int overflow;

    public TranslatorsWedge(XmlResourceParser parser) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_translators);
        translatorsTitle = parser.getAttributeValue(null, "title");
        if (translatorsTitle == null)
            translatorsTitle = "@string/title_attribouter_translators";
        overflow = parser.getAttributeIntValue(null, "overflow", -1);

        addChildren(parser);
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof ContributorsData) {
            if (((ContributorsData) data).contributors != null) {
                for (ContributorsData.ContributorData contributor : ((ContributorsData) data).contributors) {
                    if (contributor.login == null)
                        continue;

                    Wedge child = addChild(new TranslatorWedge(
                            contributor.login,
                            null,
                            contributor.avatar_url,
                            null,
                            null,
                            null
                    ));

                    if (child instanceof Mergeable && !((Mergeable) child).hasAll())
                        addRequest(new UserData(contributor.login));
                }
            }
        } else if (data instanceof UserData) {
            UserData user = (UserData) data;
            addChild(0, new TranslatorWedge(
                    user.login,
                    user.name,
                    user.avatar_url,
                    null,
                    user.blog,
                    user.email
            ));
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        int remaining = overflow;
        List<Wedge> sortedList = new ArrayList<>();
        sortedTranslators = new ArrayList<>();
        for (String language : Locale.getISOLanguages()) {
            boolean isHeader = false;
            for (TranslatorWedge translator : getChildren(TranslatorWedge.class)) {
                if (translator.locales == null || translator.locales.length() < 1)
                    continue;

                boolean isLocale = false;
                for (String locale : translator.locales.split(",")) {
                    if (language.equals(locale)) {
                        isLocale = true;
                        break;
                    }
                }

                if (isLocale) {
                    if (!isHeader) {
                        Wedge header = new HeaderWedge(new Locale(language).getDisplayLanguage());
                        sortedTranslators.add(header);
                        if (remaining != 0)
                            sortedList.add(header);

                        isHeader = true;
                    }

                    sortedTranslators.add(translator);
                    if (remaining != 0) {
                        sortedList.add(translator);
                        remaining--;
                    }
                }
            }
        }

        if (overflow == 0) {
            viewHolder.titleView.setVisibility(View.GONE);
            viewHolder.recycler.setVisibility(View.GONE);
            viewHolder.expand.setVisibility(View.GONE);

            viewHolder.overflow.setVisibility(View.VISIBLE);
            viewHolder.overflow.setText(ResourceUtils.getString(context, translatorsTitle));

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OverflowDialog(v.getContext(), translatorsTitle, sortedTranslators).show();
                }
            });
            return;
        } else {
            viewHolder.titleView.setVisibility(View.VISIBLE);
            viewHolder.recycler.setVisibility(View.VISIBLE);
            viewHolder.expand.setVisibility(View.VISIBLE);
            viewHolder.overflow.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(null);
        }

        if (translatorsTitle != null)
            viewHolder.titleView.setText(ResourceUtils.getString(context, translatorsTitle));

        viewHolder.recycler.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recycler.setAdapter(new InfoAdapter(sortedList));

        if (sortedTranslators.size() > sortedList.size()) {
            viewHolder.expand.setVisibility(View.VISIBLE);
            viewHolder.expand.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new OverflowDialog(v.getContext(), translatorsTitle, sortedTranslators).show();
                }
            });
        } else viewHolder.expand.setVisibility(View.GONE);
    }

    protected class ViewHolder extends Wedge.ViewHolder {

        protected TextView titleView;
        protected RecyclerView recycler;
        protected View expand;
        protected TextView overflow;

        protected ViewHolder(View v) {
            super(v);

            titleView = v.findViewById(R.id.contributorsTitle);
            recycler = v.findViewById(R.id.recycler);
            expand = v.findViewById(R.id.expand);
            overflow = v.findViewById(R.id.overflow);
        }
    }

}
