package com.ideonet.orm.util;

import com.ideonet.exception.IdeoException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

public class JdbcUtil {

    static {
        try {
            init();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            initPool();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static ArrayList<Connection> pool;
    private static Properties property;
    private static String url;
    private static int min_size;
    private static int max_size;

    private static void init() throws SQLException {
        pool = new ArrayList<>();
        property = new Properties();
        try {
            InputStream is = JdbcUtil.class.getClassLoader().getResourceAsStream("dump.properties");
            property.load(is);
        } catch (IOException e) {
            throw new RuntimeException("load properties failed", e);
        }
        url = property.getProperty("url");
        String driver = property.getProperty("driver");
        min_size = Integer.parseInt(property.getProperty("min_conn"));
        max_size = Integer.parseInt(property.getProperty("max_conn"));
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new IdeoException("driver not found", e);
        }
        initPool();
    }

    private static void initPool() throws SQLException {
        pool.clear();
        for (int i = 0; i < min_size; i++) {
            pool.add(DriverManager.getConnection(url, property));
        }
    }

    private static Connection getConnection() {
        Connection conn = null;
        if (pool.size() <= 0) {
            try {
                conn = DriverManager.getConnection(url, property);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            conn = pool.remove(0);
            try {
                if (conn == null || conn.isClosed()) {
                    initPool();
                    conn = pool.remove(0);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static int executeUpdate(String sql, Object... o) {
        Connection conn = getConnection();
        PreparedStatement ps = null;
        try {
            assert conn != null;
            ps = conn.prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int result = 0;
        try {
            assert ps != null;
            if (o != null) {
                for (int i = 0; i < o.length; i++) {
                    ps.setObject(i + 1, o[i]);
                }
            }
            result = ps.executeUpdate();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            freeConnection(conn);
        }
        return result;
    }


    public static ResultSet executeQuery(String sql, Object... o) {
        Connection conn = getConnection();
        PreparedStatement ps;
        ResultSet res = null;
        try {
            ps = conn.prepareStatement(sql);
            if (o != null) {
                for (int i = 0; i < o.length; i++) {
                    ps.setObject(i + 1, o[i]);
                }
            }
            res = ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            freeConnection(conn);
        }
        return res;
    }


    public static String getTablePri(String tableName) {
        String pkStr = "";
        ResultSet rs;
        try {
            rs = getConnection().getMetaData().getPrimaryKeys(null, null, tableName);
            if (null == rs) {
                return pkStr;
            }
            while (rs.next()) {
                pkStr = rs.getString("COLUMN_NAME");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pkStr;
    }

    private static void freeConnection(Connection conn) {
        if (conn != null) {
            if (pool.size() < max_size) {
                pool.add(conn);
            } else {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void freeResultSet(ResultSet res) {
        if (res != null) {
            try {
                res.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
