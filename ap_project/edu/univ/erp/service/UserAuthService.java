package edu.univ.erp.service;

import edu.univ.erp.data.UserAuthDAO;
import edu.univ.erp.domain.UserAuth;

import java.time.LocalDateTime;
import java.util.List;

public class UserAuthService {

    private UserAuthDAO userAuthDAO;

    public UserAuthService() {
        this.userAuthDAO = new UserAuthDAO();
    }

    public boolean addUser(String username, String role, String passwordHash, String status) {
        UserAuth user = new UserAuth();
        user.setUsername(username);
        user.setRole(role);
        user.setPasswordHash(passwordHash);
        user.setStatus(status != null ? status : "Active"); 
        user.setLastLogin(null); 

        return userAuthDAO.addUser(user);
    }

   
    public UserAuth login(String username, String passwordHash) {
        UserAuth user = userAuthDAO.getUserByUsername(username);

        if (user == null) return null; 
        if (!user.getPasswordHash().equals(passwordHash)) return null; 

       
        userAuthDAO.updateLastLogin(user.getUserId());
        user.setLastLogin(LocalDateTime.now()); 

        return user;
    }


    public UserAuth getUserByUsername(String username) {
        return userAuthDAO.getUserByUsername(username);
    }

    public boolean updateStatus(int userId, String status) {
        if (status == null || status.isBlank()) return false; 
        return userAuthDAO.updateStatus(userId, status);
    }

    public boolean deleteUser(int userId) {
        return userAuthDAO.deleteUser(userId);
    }

    public List<UserAuth> getAllUsers() {
        return userAuthDAO.getAllUsers();
    }
}
