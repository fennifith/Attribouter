package me.jfenn.attribouter.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.adapters.WedgeAdapter;
import me.jfenn.attribouter.utils.ResourceUtils;
import me.jfenn.attribouter.wedges.ContributorWedge;
import me.jfenn.attribouter.wedges.LinkWedge;
import me.jfenn.attribouter.wedges.Wedge;

public class UserDialog extends AppCompatDialog {

    private ContributorWedge contributor;

    public UserDialog(Context context, ContributorWedge contributor) {
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

        nameView.setText(ResourceUtils.getString(getContext(), contributor.getCanonicalName()));
        taskView.setText(ResourceUtils.getString(getContext(), contributor.getTask()));

        String url = ResourceUtils.getString(getContext(), contributor.getAvatarUrl());
        if (url != null)
            ResourceUtils.setImage(getContext(), contributor.getAvatarUrl(), R.drawable.ic_attribouter_avatar, imageView);
        else imageView.setVisibility(View.GONE);

        bioView.setText(ResourceUtils.getString(getContext(), contributor.getBio()));

        List<LinkWedge> links = new ArrayList<>();
        for (Wedge wedge : contributor.getChildren()) {
            if (wedge instanceof LinkWedge)
                links.add((LinkWedge) wedge);
        }

        if (links.size() > 0) {
            Collections.sort(links, new LinkWedge.Comparator(getContext()));

            List<Wedge> linksList = new ArrayList<>();
            for (LinkWedge link : links) {
                if (!link.isHidden())
                    linksList.add(link);
            }

            recycler.setVisibility(View.VISIBLE);

            FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(getContext());
            layoutManager.setFlexDirection(FlexDirection.ROW);
            layoutManager.setJustifyContent(JustifyContent.FLEX_START);
            recycler.setLayoutManager(layoutManager);
            recycler.setAdapter(new WedgeAdapter(linksList));
        } else recycler.setVisibility(View.GONE);
    }
}
