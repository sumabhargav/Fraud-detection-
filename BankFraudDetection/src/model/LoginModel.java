package model;
public class LoginModel implements java.io.Serializable {

    public String _user_id = "";
    public String _password = "";
    public String _role = "";
    public String _role_id = "";
    public String _customer_id = "";
    public boolean isValidUser() {

        if (checkUserInformationFromDB(_user_id, _password)) {
            SetUserInformation();
            return true;
        }
        return false;
    }

    private void SetUserInformation() {

        this._role_id = "";
        this._role = "";
        this._customer_id = "";
    }

    private boolean checkUserInformationFromDB(String user_id, String password) {
        if ("user1".equals(user_id) && "abc123".equals(password)) {
            return true;
        }
        return false;
    }

    public LoginModel getUserInformation() {
        return this;
    }

}
