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
        super(context, ResourceUtils.getThemeResourceAttribute(context, R.attr.personDialogTheme, R.style.AttribouterTheme_Dialog));
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
        RecyclerView links = findViewById(R.id.links);

        nameView.setText(ResourceUtils.getString(getContext(), contributor.getName()));
        taskView.setText(ResourceUtils.getString(getContext(), contributor.task));

        String url = ResourceUtils.getString(getContext(), contributor.avatarUrl);
        if (url != null)
            ResourceUtils.setImage(getContext(), contributor.avatarUrl, imageView);
        else imageView.setVisibility(View.GONE);

        bioView.setText(ResourceUtils.getString(getContext(), contributor.bio));
        if (contributor.links.size() > 0) {
            Collections.sort(contributor.links, new LinkInfoData.Comparator(getContext()));

            List<InfoData> linksList = new ArrayList<>();
            for (LinkInfoData link : contributor.links) {
                if (!link.isHidden())
                    linksList.add(link);
            }

            links.setVisibility(View.VISIBLE);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            links.setLayoutManager(layoutManager);
            links.setAdapter(new InfoAdapter(linksList));
        } else links.setVisibility(View.GONE);
    }
}
