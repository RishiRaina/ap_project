package edu.univ.erp.access;

import java.util.logging.Level;
import java.util.logging.Logger;
import edu.univ.erp.data.SettingDAO;
import edu.univ.erp.domain.Setting;

/**
 * MaintenanceChecker — uses SettingDAO to read/write the 'maintenance_on' flag
 * stored in the ERP DB settings table.
 *
 * Behavior:
 * - If DB read fails -> treat as ON (fail-safe)
 * - If row missing -> create it with default "false"
 * - If value invalid -> reset to "false" and return false
 *
 * Thread-safety: synchronized on the class to avoid races in simple apps.
 */
public class MaintenanceChecker {

    private static final Logger logger = Logger.getLogger(MaintenanceChecker.class.getName());
    private static final String KEY = "maintenance_on";

    /**
     * Returns true if maintenance mode is currently ON.
     * Uses SettingDAO to fetch the 'maintenance_on' setting.
     */
    public static boolean isMaintenanceOn() {
        synchronized (MaintenanceChecker.class) {
            SettingDAO dao = new SettingDAO();
            try {
                String value = dao.getSetting(KEY);

                if (value == null) {
                    // Row missing: initialize it with default "false" and return false
                    boolean added = dao.addSetting(new Setting(KEY, "false"));
                    if (added) {
                        logger.info("Initialized maintenance_on flag in DB to false");
                    } else {
                        logger.warning("Failed to initialize maintenance_on flag in DB.");
                    }
                    return false;
                }

                value = value.trim().toLowerCase();
                if (!value.equals("true") && !value.equals("false")) {
                    // Invalid stored value -> reset to false for safety and return false
                    logger.warning("Invalid maintenance value stored in db:  " + value + ". resetting it to false.");
                    dao.updateSetting(KEY, "false");
                    return false;
                }

                return Boolean.parseBoolean(value);

            } catch (Exception e) {
                // Any DB/DAO exception -> log and return true (fail-safe: block writes)
                logger.log(Level.SEVERE, "Error while reading maintenance flag from DB", e);
                return true;
            }
        }
    }


    public static void setMaintenance(boolean status) {
        synchronized (MaintenanceChecker.class) {
            SettingDAO dao = new SettingDAO();
            String val = Boolean.toString(status);
            try {
                boolean updated = dao.updateSetting(KEY, val);
                if (!updated) {
                    // update failed (maybe row missing) -> try insert
                    boolean added = dao.addSetting(new Setting(KEY, val));
                    if (!added) {
                        logger.severe("Failed to update or insert maintenance flag.");
                    } else {
                        logger.info("Inserted maintenance flag " + val);
                    }
                } else {
                    logger.info("Maintenance mode updated: " + val);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to set maintenance flag in DB", e);
            }
        }
    }

}
