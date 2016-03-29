package tomas.giggle;

/**
 * Created by Tomas on 24/03/2016.
 */
public class UserPermission {
    private String userName;
    private boolean permission;

    public UserPermission(String userName, boolean permission) {
        this.userName = userName;
        this.permission = permission;
    }

    public String getUserName() {
        return this.userName;
    }

    public boolean getPermission() {
        return this.permission;
    }
}
