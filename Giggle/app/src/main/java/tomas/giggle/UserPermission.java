package tomas.giggle;

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

    public void flipPermission() {
        this.permission = !this.permission;
    }
}
