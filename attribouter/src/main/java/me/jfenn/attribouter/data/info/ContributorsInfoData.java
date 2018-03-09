package me.jfenn.attribouter.data.info;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.jfenn.attribouter.R;
import me.jfenn.attribouter.data.github.ContributorsData;
import me.jfenn.attribouter.data.github.GitHubData;
import me.jfenn.attribouter.data.github.UserData;
import me.jfenn.attribouter.utils.ResourceUtils;

public class ContributorsInfoData extends InfoData<ContributorsInfoData.ViewHolder> {

    private String repo;
    private List<ContributorData> contributors;

    public ContributorsInfoData(XmlResourceParser parser, String repo) throws XmlPullParserException, IOException {
        super(R.layout.item_attribouter_contributors);
        this.repo = repo;
        contributors = new ArrayList<>();
        while (parser.getEventType() != XmlResourceParser.END_TAG || parser.getName().equals("contributor")) {
            parser.next();
            String login = parser.getAttributeValue(null, "login");
            int position = parser.getAttributeIntValue(null, "position", -1);

            if (login != null) {
                ContributorData contributor = new ContributorData(
                        login,
                        parser.getAttributeValue(null, "name"),
                        parser.getAttributeValue(null, "avatar"),
                        parser.getAttributeValue(null, "task"),
                        position != -1 ? position : null,
                        parser.getAttributeValue(null, "bio"),
                        parser.getAttributeValue(null, "blog"));

                if (!contributors.contains(contributor))
                    contributors.add(contributor);
                else contributors.get(contributors.indexOf(contributor)).merge(contributor);
            }
        }

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
            ContributorData contributor = new ContributorData(user.login, user.name, user.avatar_url, repo.startsWith(user.login) ? "Owner" : "Contributor", null, user.bio, user.blog);
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
        ContributorData first = null, second = null, third = null;
        List<ContributorData> remainingContributors = new ArrayList<>(contributors);
        for (ContributorData contributor : contributors) {
            if (contributor.position != null) {
                if (first == null && contributor.position == 1) {
                    first = contributor;
                    remainingContributors.remove(contributor);
                } else if (second == null && contributor.position == 2) {
                    second = contributor;
                    remainingContributors.remove(contributor);
                } else if (third == null && contributor.position == 3) {
                    third = contributor;
                    remainingContributors.remove(contributor);
                }
            }
        }

        viewHolder.topThreeView.setVisibility(first != null || second != null || third != null ? View.VISIBLE : View.GONE);

        if (first != null) {
            viewHolder.firstView.setVisibility(View.VISIBLE);
            viewHolder.firstNameView.setText(ResourceUtils.getString(context, first.getName()));
            if (first.task != null) {
                viewHolder.firstTaskView.setVisibility(View.VISIBLE);
                viewHolder.firstTaskView.setText(ResourceUtils.getString(context, first.task));
            } else viewHolder.firstTaskView.setVisibility(View.GONE);
        } else viewHolder.firstView.setVisibility(View.GONE);

        if (second != null) {
            viewHolder.secondView.setVisibility(View.VISIBLE);
            viewHolder.secondNameView.setText(ResourceUtils.getString(context, second.getName()));
            if (second.task != null) {
                viewHolder.secondTaskView.setVisibility(View.VISIBLE);
                viewHolder.secondTaskView.setText(ResourceUtils.getString(context, second.task));
            } else viewHolder.secondTaskView.setVisibility(View.GONE);
        } else viewHolder.secondView.setVisibility(View.GONE);

        if (third != null) {
            viewHolder.thirdView.setVisibility(View.VISIBLE);
            viewHolder.thirdNameView.setText(ResourceUtils.getString(context, third.getName()));
            if (third.task != null) {
                viewHolder.thirdTaskView.setVisibility(View.VISIBLE);
                viewHolder.thirdTaskView.setText(ResourceUtils.getString(context, third.task));
            } else viewHolder.thirdTaskView.setVisibility(View.GONE);
        } else viewHolder.thirdView.setVisibility(View.GONE);

        if (remainingContributors.size() > 0) {
            viewHolder.recycler.setVisibility(View.VISIBLE);
            viewHolder.recycler.setLayoutManager(new LinearLayoutManager(context));
        } else viewHolder.recycler.setVisibility(View.GONE);
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

        @NonNull
        private String login;
        @Nullable
        private String name;
        @Nullable
        private String avatarUrl;
        @Nullable
        private String bio;
        @Nullable
        private String blog;
        @Nullable
        private Integer position;
        @Nullable
        private String task;

        private ContributorData(@NonNull String login, @Nullable String name, @Nullable String avatarUrl, @Nullable String task, @Nullable Integer position, @Nullable String bio, @Nullable String blog) {
            this.login = login;
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.task = task;
            this.position = position;
            this.bio = bio;
            this.blog = blog;
        }

        private String getName() {
            return name != null ? name : login;
        }

        private void merge(ContributorData contributor) {
            if ((name == null || !name.startsWith("^")) && contributor.name != null)
                name = contributor.name;
            if ((avatarUrl == null || !avatarUrl.startsWith("^")) && contributor.avatarUrl != null)
                avatarUrl = contributor.avatarUrl;
            if ((bio == null || !bio.startsWith("^")) && contributor.bio != null)
                bio = contributor.bio;
            if ((blog == null || !blog.startsWith("^")) && contributor.blog != null)
                blog = contributor.blog;
            if ((task == null || !task.startsWith("^")) && contributor.task != null)
                task = contributor.task;
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
