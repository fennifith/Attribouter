package me.jfenn.attribouter.data.github;

public class UserData extends GitHubData {

    private String login;
    private String name;
    private String avatar_url;
    private String html_url;
    private String blog;
    private String email;
    private String bio;

    public UserData(String login) {
        super("https://api.github.com/users/" + login);
    }

}
