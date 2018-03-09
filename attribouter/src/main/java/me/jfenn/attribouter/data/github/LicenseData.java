package me.jfenn.attribouter.data.github;

public class LicenseData extends GitHubData {

    public String key;
    public String name;
    public String html_url;
    public String description;
    public String[] permissions;
    public String[] conditions;
    public String[] limitations;
    public String body;

    public LicenseData(String key) {
        super("https://api.github.com/licenses/" + key);
    }
}
