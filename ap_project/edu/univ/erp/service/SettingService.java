package edu.univ.erp.service;

import edu.univ.erp.data.SettingDAO;
import edu.univ.erp.domain.Setting;

public class SettingService {

    private SettingDAO settingDAO;

    public SettingService() {
        this.settingDAO = new SettingDAO();
    }
    
    public boolean addSetting(String key, String value) {
        Setting s = new Setting(key, value);
        return settingDAO.addSetting(s);
    }

    public String getSetting(String key) {
        return settingDAO.getSetting(key);
    }

    public boolean updateSetting(String key, String value) {
        return settingDAO.updateSetting(key, value);
    }
}
