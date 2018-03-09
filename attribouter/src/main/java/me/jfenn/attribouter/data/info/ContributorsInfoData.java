package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.github.ContributorsData;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;

public class ContributorsInfoData extends InfoData<ContributorsInfoData.ViewHolder> {

    private List<ContributorData> contributors;

    public ContributorsInfoData(XmlResourceParser parser, String repo) {
        super(R.layout.item_attribouter_contributors);
        contributors = new ArrayList<>();
        addRequest(new UserData("TheAndroidMaster")); //hey, that's me
        addRequest(new ContributorsData(repo));
    }

    @Override
    public void onInit(GitHubData data) {
        if (data instanceof ContributorsData) {
            if (((ContributorsData) data).contributors != null) {
                for (ContributorsData.ContributorData contributor : ((ContributorsData) data).contributors) {
                    if (contributor.login == null)
                        continue;

                    boolean shouldDoSomething = true;
                    for (ContributorData contributor2 : contributors) {
                        if (contributor.login.equals(contributor2.login)) {
                            shouldDoSomething = !contributor2.hasEverything();
                        }
                    }

                    if (shouldDoSomething)
                        addRequest(new UserData(contributor.login));
                }
            }
        } else if (data instanceof UserData) {
            UserData user = (UserData) data;
            ContributorData contributor = new ContributorData(user.login, user.name, user.bio, user.blog);
            if (!contributors.contains(contributor))
                contributors.add(0, contributor);
            else contributors.get(contributors.indexOf(contributor)).merge(contributor);
        }
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public void bind(Context context, ViewHolder viewHolder) {

    }

    class ViewHolder extends InfoData.ViewHolder {

        private View topThreeView;
        private View firstView;
        private ImageView firstImageView;
        private TextView firstNameView;
        private TextView firstTaskView;
        private View secondView;
        private ImageView secondImageView;
        private TextView secondNameView;
        private TextView secondTaskView;
        private View thirdView;
        private ImageView thirdImageView;
        private TextView thirdNameView;
        private TextView thirdTaskView;
        private RecyclerView recycler;

        ViewHolder(View v) {
            super(v);

            topThreeView = v.findViewById(R.id.topThree);
            firstView = v.findViewById(R.id.first);
            firstImageView = v.findViewById(R.id.firstImage);
            firstNameView = v.findViewById(R.id.firstName);
            firstTaskView = v.findViewById(R.id.firstTask);
            secondView = v.findViewById(R.id.second);
            secondImageView = v.findViewById(R.id.secondImage);
            secondNameView = v.findViewById(R.id.secondName);
            secondTaskView = v.findViewById(R.id.secondTask);
            thirdView = v.findViewById(R.id.third);
            thirdImageView = v.findViewById(R.id.thirdImage);
            thirdNameView = v.findViewById(R.id.thirdName);
            thirdTaskView = v.findViewById(R.id.thirdTask);
            recycler = v.findViewById(R.id.recycler);
        }
    }

    public static class ContributorData {

        private String login;
        private String name;
        private String bio;
        private String blog;

        private ContributorData(String login, String name, String bio, String blog) {
            this.login = login;
            this.name = name;
            this.bio = bio;
            this.blog = blog;
        }

        private void merge(ContributorData contributor) {
            if ((name == null || !name.startsWith("^")) && contributor.name != null)
                name = contributor.name;
            if ((bio == null || !bio.startsWith("^")) && contributor.bio != null)
                bio = contributor.bio;
            if ((blog == null || !blog.startsWith("^")) && contributor.blog != null)
                blog = contributor.blog;
        }

        private boolean hasEverything() {
            return name != null && name.startsWith("^") && bio != null && bio.startsWith("^") && blog != null && blog.startsWith("^");
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ContributorData && ((ContributorData) obj).login.equals(login);
        }
    }

}
