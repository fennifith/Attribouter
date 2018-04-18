package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.Language;
import me.jfenn.attribouter.data.github.ContributorsData;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;
import me.jfenn.attribouter.utils.ResourceUtils;

public class TranslatorsInfoData extends InfoData<TranslatorsInfoData.ViewHolder> {

    @Nullable
    private String translatorsTitle;
    private List<TranslatorInfoData> translators;

    public TranslatorsInfoData(XmlResourceParser parser) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_translators);
        translators = new ArrayList<>();
        translatorsTitle = parser.getAttributeValue(null, "title");
        while (parser.getEventType() != XmlResourceParser.END_TAG || parser.getName().equals("translator")) {
            parser.next();
            if (parser.getEventType() == XmlResourceParser.START_TAG && parser.getName().equals("translator")) {

                TranslatorInfoData translator = new TranslatorInfoData(
                        parser.getAttributeValue(null, "login"),
                        parser.getAttributeValue(null, "name"),
                        parser.getAttributeValue(null, "avatar"),
                        parser.getAttributeValue(null, "locales"),
                        parser.getAttributeValue(null, "blog"),
                        parser.getAttributeValue(null, "email"));

                if (!translators.contains(translator))
                    translators.add(translator);
                else translators.get(translators.indexOf(translator)).merge(translator);

                if (translator.login != null && !translator.hasEverything())
                    addRequest(new UserData(translator.login));
            }
        }
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof ContributorsData) {
            if (((ContributorsData) data).contributors != null) {
                for (ContributorsData.ContributorData contributor : ((ContributorsData) data).contributors) {
                    if (contributor.login == null)
                        continue;

                    TranslatorInfoData mergeTranslator = new TranslatorInfoData(
                            contributor.login,
                            null,
                            contributor.avatar_url,
                            null,
                            null,
                            null
                    );

                    TranslatorInfoData translatorInfo = mergeTranslator;

                    if (translators.contains(mergeTranslator)) {
                        translatorInfo = translators.get(translators.indexOf(mergeTranslator));
                        translatorInfo.merge(mergeTranslator);
                    } else translators.add(translatorInfo);

                    if (!translatorInfo.hasEverything())
                        addRequest(new UserData(contributor.login));
                }
            }
        } else if (data instanceof UserData) {
            UserData user = (UserData) data;
            TranslatorInfoData translator = new TranslatorInfoData(
                    user.login,
                    user.name,
                    user.avatar_url,
                    null,
                    user.blog,
                    user.email
            );

            if (!translators.contains(translator))
                translators.add(0, translator);
            else translators.get(translators.indexOf(translator)).merge(translator);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {
        if (translatorsTitle != null)
            viewHolder.titleView.setText(ResourceUtils.getString(context, translatorsTitle));

        List<InfoData> sortedList = new ArrayList<>();
        for (Language language : Language.values()) {
            boolean isHeader = false;
            for (TranslatorInfoData translator : translators) {
                if (translator.locales == null)
                    continue;

                boolean isLocale = false;
                for (String locale : translator.locales.split(",")) {
                    if (language.includesLocale(locale)) {
                        isLocale = true;
                        break;
                    }
                }

                if (isLocale) {
                    if (!isHeader) {
                        sortedList.add(new TextInfoData(language.getName(context), false));
                        isHeader = true;
                    }

                    sortedList.add(translator);
                }
            }
        }


        viewHolder.recycler.setLayoutManager(new LinearLayoutManager(context));
        viewHolder.recycler.setAdapter(new InfoAdapter(sortedList));
    }

    class ViewHolder extends InfoData.ViewHolder {

        private TextView titleView;
        private RecyclerView recycler;

        ViewHolder(View v) {
            super(v);

            titleView = v.findViewById(R.id.contributorsTitle);
            recycler = v.findViewById(R.id.recycler);
        }
    }

}
