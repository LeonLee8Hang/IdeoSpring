package com.ideonet.beans.util;

import com.ideonet.beans.ApplicationContext;


public class ApplicationContextUtils {

    private static ApplicationContext applicationContext;

    public static void refresh() {
        applicationContext = new ApplicationContext();
    }

    public static ApplicationContext getContext() {
        return applicationContext;
    }
}
