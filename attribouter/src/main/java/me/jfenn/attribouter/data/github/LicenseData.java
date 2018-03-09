package me.jfenn.attribouter.data.github;

public class LicenseData extends GitHubData {

    private String key;
    private String name;
    private String html_url;
    private String description;
    private String[] permissions;
    private String[] conditions;
    private String[] limitations;
    private String body;

    public LicenseData(String key) {
        super("https://api.github.com/licenses/" + key);
    }
}
