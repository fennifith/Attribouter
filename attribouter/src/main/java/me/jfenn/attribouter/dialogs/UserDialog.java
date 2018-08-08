package me.jfenn.attribouter.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.InfoAdapter;
import me.jfenn.attribouter.data.info.ContributorInfoData;
import me.jfenn.attribouter.data.info.InfoData;
import me.jfenn.attribouter.data.info.link.LinkInfoData;
import me.jfenn.attribouter.utils.ResourceUtils;

public class UserDialog extends AppCompatDialog {

    private ContributorInfoData contributor;

    public UserDialog(Context context, ContributorInfoData contributor) {
        super(context, ResourceUtils.getThemeResourceAttribute(context, R.styleable.AttribouterTheme_personDialogTheme, R.style.AttribouterTheme_Dialog));
        this.contributor = contributor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_attribouter_user);

        TextView nameView = findViewById(R.id.name);
        TextView taskView = findViewById(R.id.task);
        ImageView imageView = findViewById(R.id.image);
        TextView bioView = findViewById(R.id.description);
        RecyclerView recycler = findViewById(R.id.links);

        nameView.setText(ResourceUtils.getString(getContext(), contributor.getName()));
        taskView.setText(ResourceUtils.getString(getContext(), contributor.task));

        String url = ResourceUtils.getString(getContext(), contributor.avatarUrl);
        if (url != null)
            ResourceUtils.setImage(getContext(), contributor.avatarUrl, imageView);
        else imageView.setVisibility(View.GONE);

        bioView.setText(ResourceUtils.getString(getContext(), contributor.bio));

        List<LinkInfoData> links = contributor.getChildren(LinkInfoData.class);
        if (links.size() > 0) {
            Collections.sort(links, new LinkInfoData.Comparator(getContext()));

            List<InfoData> linksList = new ArrayList<>();
            for (LinkInfoData link : links) {
                if (!link.isHidden())
                    linksList.add(link);
            }

            recycler.setVisibility(View.VISIBLE);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(new InfoAdapter(linksList));
        } else recycler.setVisibility(View.GONE);
    }
}
