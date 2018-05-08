package me.jfenn.attribouter.data.github;

public class UserData extends GitHubData {

    public String login;
    public String name;
    public String avatar_url;
    public String html_url;
    public String blog;
    public String email;
    public String bio;

    public UserData(String login) {
        super("https://api.github.com/users/" + login);
    }

}
