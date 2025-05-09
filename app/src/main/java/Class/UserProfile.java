package Class;

public class UserProfile extends User {
    private String login;

    public UserProfile(){}

    public UserProfile(String login, String email) {
        super(email);
        this.login = login;
    }
}
