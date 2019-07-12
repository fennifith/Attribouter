package me.jfenn.attribouter.data.github;

public class RepositoryData {

    public String html_url;
    public String description;
    public String homepage;
    public LicenseData license;

    public static class LicenseData {

        public String key;
        public String name;
        public String url;

    }

}
