package me.jfenn.attribouter.data.github;

import com.google.gson.Gson;

public class ContributorsData extends GitHubData {

    public ContributorData[] contributors;

    public ContributorsData(String repo) {
        super("https://api.github.com/repos/" + repo + "/contributors");
    }

    @Override
    protected void initJson(Gson gson, String json) {
        contributors = gson.fromJson(json, ContributorData[].class);
    }

    public static class ContributorData {

        public String login;
        public String avatar_url;
        public String html_url;

    }

}
