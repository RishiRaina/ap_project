package edu.univ.erp.service;

import edu.univ.erp.data.SettingDAO;

public class SettingService {
    private SettingDAO settingDAO;

    public SettingService() {
        settingDAO = new SettingDAO();
    }

    public String getSetting(String key) {
        return settingDAO.getSetting(key);
    }
    
}
