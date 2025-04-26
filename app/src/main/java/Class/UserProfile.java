package Class;

public class UserProfile {
    private String login;
    private String email;

    public UserProfile(String login, String email) {
        this.login = login;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }
}
