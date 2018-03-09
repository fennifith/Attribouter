package me.jfenn.attribouter.data.github;

import com.google.gson.Gson;

public class ContributorsData extends GitHubData {

    private ContributorData[] contributors;

    public ContributorsData(String repo) {
        super("https://api.github.com/repos/" + repo + "/contributors");
    }

    @Override
    protected void initJson(Gson gson, String json) {
        contributors = gson.fromJson(json, ContributorData[].class);
    }

    public static class ContributorData {

        private String login;

    }

}
