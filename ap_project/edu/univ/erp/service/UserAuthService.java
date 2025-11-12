package edu.univ.erp.service;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.UserAuth;

public class UserAuthService {
    private UserAuthDAO userAuthDAO;

    public UserAuthService() {
        userAuthDAO = new UserAuthDAO();
    }

    public UserAuth login(String username, String password) {
        UserAuth user = userAuthDAO.getUserByUsername(username);
        if (user != null) {
            return user;
        }
        return null;
    }

    public boolean addUser(UserAuth user) {
        return userAuthDAO.addUser(user);
    }
}
