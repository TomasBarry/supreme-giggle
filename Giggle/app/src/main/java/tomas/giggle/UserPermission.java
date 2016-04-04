package tomas.giggle;


public class UserPermission {


    private String userName;
    private boolean permission;

    /**
     * Constructor
     *
     * @param userName: the user name
     * @param permission: the permission for the user
     */
    public UserPermission(String userName, boolean permission) {
        this.userName = userName;
        this.permission = permission;
    }


    // GETTERS


    public String getUserName() {
        return this.userName;
    }

    public boolean getPermission() {
        return this.permission;
    }


    // SETTERS


    public void flipPermission() {
        this.permission = !this.permission;
    }
}
