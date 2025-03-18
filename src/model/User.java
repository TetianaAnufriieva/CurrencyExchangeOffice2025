package model;

import java.util.Map;
import java.util.Objects;

public class User {

    private int userId;
    private String email;
    private String password;
    private Role role;

    private Map<Integer, Account> accounts;

    public User(int userId, String email, String password, Role role, Map<Integer, Account> accounts) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.accounts = accounts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Map<Integer, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(Map<Integer, Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return userId == user.userId && Objects.equals(email, user.email) && Objects.equals(password, user.password) && role == user.role && Objects.equals(accounts, user.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, email, password, role, accounts);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", accounts=" + accounts +
                '}';
    }
}
