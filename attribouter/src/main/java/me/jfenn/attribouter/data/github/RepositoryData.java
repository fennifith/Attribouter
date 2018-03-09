package me.jfenn.attribouter.data.github;

public class RepositoryData extends GitHubData {

    private String html_url;
    private String description;
    private String homepage;
    private LicenseData license;

    public RepositoryData(String repo) {
        super("https://api.github.com/repos/" + repo);
    }

    public static class LicenseData {

        private String key;
        private String name;
        private String url;

    }

}
