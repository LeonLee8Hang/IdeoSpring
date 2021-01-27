package com.ideonet.orm;


public class SessionFactory {

    private static Session session = Session.getSession();

    private SessionFactory() {

    }

    public static Session getSession() {
        return session;
    }
}
