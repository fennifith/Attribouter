package me.jfenn.attribouter.data.github;

public class ContributorsData {

    public ContributorData[] contributors;

    public static class ContributorData {

        public String login;
        public String avatar_url;
        public String html_url;

    }

}
