package edu.univ.erp.util;

import java.util.logging.Logger;

public class AppLogger {
    public static Logger getLogger(Class<?> cls) {
        return Logger.getLogger(cls.getName());
    }
}
