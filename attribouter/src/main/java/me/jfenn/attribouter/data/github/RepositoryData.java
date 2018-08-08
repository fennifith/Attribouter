package me.jfenn.attribouter.data.github;

public class RepositoryData extends GitHubData {

    public String html_url;
    public String description;
    public String homepage;
    public LicenseData license;

    public RepositoryData(String repo) {
        super("https://api.github.com/repos/" + repo);
        addTag(repo);
    }

    public static class LicenseData {

        public String key;
        public String name;
        public String url;

    }

}
