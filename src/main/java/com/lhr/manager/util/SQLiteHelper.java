package com.lhr.manager.util;

import cn.hutool.core.date.DateTime;
import com.lhr.manager.entity.Share;
import com.lhr.manager.entity.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SQLiteHelper {

    @Value("${database.path}")
    private String dbName;

    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;

    private static Logger log = LoggerFactory.getLogger(SQLiteHelper.class);

    public SQLiteHelper() {
         // only for test
         // dbName = "D:/lhr/info.db";
         // init();
    }

    @PostConstruct
    public void init() {
        try {
            // 注册SQLite驱动
            Class.forName("org.sqlite.JDBC");
            // 连接SQLite数据库
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName + "?date_string_format=yyyy-MM-dd HH:mm:ss");
            log.info("Connected to SQLite database.");

            File file = new File(dbName);
            if (file.length() == 0) {
                log.info("init table");
                this.createTable("tbl_version", "id INTEGER PRIMARY KEY AUTOINCREMENT, filename VARCHAR(255), size INT, time DATE, name VARCHAR(8), path TEXT, note TEXT");
                this.createTable("tbl_share", "id INTEGER PRIMARY KEY AUTOINCREMENT, filename VARCHAR(255), size INT, time DATE, name VARCHAR(8), path TEXT, note TEXT");
            }
        } catch (Exception e) {
            log.error("Error connecting to SQLite database: " + e.getMessage());
        }
    }


    public void createTable(String tableName, String columns) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")");
            log.info("Table " + tableName + " created successfully.");
        } catch (SQLException e) {
            log.error("Error creating table: " + e.getMessage());
        }
    }

    public int insert(String tableName, String columns, String values) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")");

            ResultSet rs = statement.executeQuery("SELECT last_insert_rowid()");
            if (rs.next()) {
                return rs.getInt(1);
            }
            log.info("Data inserted into " + tableName + " successfully.");
        } catch (SQLException e) {
            log.error("Error inserting data: " + e.getMessage());
        }
        return -1;
    }

    public ResultSet select(String tableName, String condition) {
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM " + tableName + " WHERE " + condition);
//            log.info("Data selected successfully.");
            return resultSet;
        } catch (SQLException e) {
            log.error("Error executing select query: " + e.getMessage());
            return null;
        }
    }

    public void update(String tableName, String setClause, String condition) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("UPDATE " + tableName + " SET " + setClause + " WHERE " + condition);
            log.info("Data updated successfully.");
        } catch (SQLException e) {
            log.error("Error updating data: " + e.getMessage());
        }
    }

    public void delete(String tableName, String condition) {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM " + tableName + " WHERE " + condition);
            log.info("Data deleted successfully.");
        } catch (SQLException e) {
            log.error("Error deleting data: " + e.getMessage());
        }
    }

    public void close() {
        try {
            if (resultSet != null) resultSet.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
            log.info("Connection closed.");
        } catch (SQLException e) {
            log.error("Error closing connection: " + e.getMessage());
        }
    }


    // 两个特殊表格的专用方法
    /**
     *  helper.createTable(
     *  "tbl_share",
     *  "id INTEGER PRIMARY KEY AUTOINCREMENT,   id
     *   filename VARCHAR(255),                 文件名
     *   size INT,                              文件大小
     *   time DATE,                             文件分享日期
     *   name VARCHAR(8),                       分享人名
     *   path TEXT,
     *   note TEXT
     *  ");
     */
    public int insertShare(String fileName, long size, String name, String path, String note) {
        DateTime date = new DateTime();
        String cols = "filename, size, time, name, path, note";
        String sb = "'" + fileName + "'" + "," +
                size + "," +
                "'" + date + "'" + "," +
                "'" + name + "'" + "," +
                "'" + path + "'" + "," +
                "'" + note + "'";
        return this.insert("tbl_share", cols, sb);
    }

    public Share selectShareById(int id) {
        ResultSet resultSet = this.select("tbl_share", "id = " + id);
        Share share = new Share();
        try {
            // 只取第一个 何况主键有约束  这里有且只有一个
            if (resultSet.next()) {
                share.setId(resultSet.getInt("id"));
                share.setFileName(resultSet.getString("filename"));
                share.setSize(resultSet.getInt("size"));
                share.setTime(new DateTime(resultSet.getDate("time").getTime()));
                share.setName(resultSet.getString("name"));
                share.setPath(resultSet.getString("path"));
                share.setNote(resultSet.getString("note"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return share;
    }

    public List<Share> selectSharesByFileName(String fileName) {
        ResultSet resultSet = this.select("tbl_share", "filename = " + fileName);
        ArrayList<Share> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Share share = new Share();
                share.setId(resultSet.getInt("id"));
                share.setFileName(resultSet.getString("filename"));
                share.setSize(resultSet.getInt("size"));
                share.setTime(new DateTime(resultSet.getDate("time").getTime()));
                share.setName(resultSet.getString("name"));
                share.setPath(resultSet.getString("path"));
                share.setNote(resultSet.getString("note"));
                list.add(share);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Share> selectSharesByName(String name) {
        ResultSet resultSet = this.select("tbl_share", "name = '" +  name + "'");
        ArrayList<Share> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                Share share = new Share();
                share.setId(resultSet.getInt("id"));
                share.setFileName(resultSet.getString("filename"));
                share.setSize(resultSet.getInt("size"));
                share.setTime(new DateTime(resultSet.getDate("time").getTime()));
                share.setName(resultSet.getString("name"));
                share.setPath(resultSet.getString("path"));
                share.setNote(resultSet.getString("note"));
                list.add(share);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Share> selectShares() {
        ArrayList<Share> list = new ArrayList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM tbl_share");
            while (resultSet.next()) {
                Share share = new Share();
                share.setId(resultSet.getInt("id"));
                share.setFileName(resultSet.getString("filename"));
                share.setSize(resultSet.getInt("size"));
                share.setTime(new DateTime(resultSet.getDate("time").getTime()));
                share.setName(resultSet.getString("name"));
                share.setPath(resultSet.getString("path"));
                share.setNote(resultSet.getString("note"));
                list.add(share);
            }
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
        }
        return list;
    }

    public void clearShares() {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM tbl_share");
            log.info("All rows deleted from tbl_share");
        } catch (SQLException e) {
            log.error("Error deleting all rows from tbl_share: " + e.getMessage());
        }
    }

    /**
     * version 表
     * helper.createTable(
     *  "tbl_version",
     *   "id INTEGER PRIMARY KEY AUTOINCREMENT,     id
     *    filename VARCHAR(255),                    文件名
     *    size INT,                                 文件大小
     *    time DATE,                                更新日期
     *    name VARCHAR(8)                           上传人
     *    path text
     *   ");
     */
    public Version insertVersion(String fileName, long size, String name, String path, String note) {
        DateTime date = new DateTime();
        String cols = "filename, size, time, name, path, note";
        String sb = "'" + fileName + "'" + "," +
                size + "," +
                "'" + date + "'" + "," +
                "'" + name + "'" + "," +
                "'" + path + "'" + "," +
                "'" + note + "'";
        return new Version(this.insert("tbl_version", cols, sb), fileName, size, date, name, path, note);
    }

    public Version selectVersionById(int id) {
        ResultSet resultSet = this.select("tbl_version", "id = " + id);
        Version version = new Version();
        try {
            // 只取第一个 何况主键有约束  这里有且只有一个
            if (resultSet.next()) {
                version.setId(resultSet.getInt("id"));
                version.setFileName(resultSet.getString("filename"));
                version.setSize(resultSet.getInt("size"));
                version.setTime(new DateTime(resultSet.getDate("time").getTime()));
                version.setName(resultSet.getString("name"));
                version.setPath(resultSet.getString("path"));
                version.setNote(resultSet.getString("note"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return version;
    }

    public Version selectLatestVersionByFileName(String fileName) {
        Version version = new Version();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM tbl_version where filename = '" + fileName + "' ORDER BY time DESC LIMIT 1");
            if (resultSet.next()) {
                version.setId(resultSet.getInt("id"));
                version.setFileName(resultSet.getString("filename"));
                version.setSize(resultSet.getInt("size"));
                version.setTime(new DateTime(resultSet.getDate("time").getTime()));
                version.setName(resultSet.getString("name"));
                version.setPath(resultSet.getString("path"));
                version.setNote(resultSet.getString("note"));
            }
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
        }
        return version;
    }

    public List<String> selectVersionFileNames() {
        ArrayList<String> list = new ArrayList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT filename FROM tbl_version");
            while (resultSet.next()) {
                list.add(resultSet.getString("filename"));
            }
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
        }
        return list;
    }

    public List<Version> selectVersionsByFileName(String fileName) {
        ArrayList<Version> list = new ArrayList<>();
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM tbl_version where filename=" + fileName + " ORDER BY time DESC");
            while (resultSet.next()) {
                Version version = new Version();
                version.setId(resultSet.getInt("id"));
                version.setFileName(resultSet.getString("filename"));
                version.setSize(resultSet.getInt("size"));
                version.setTime(new DateTime(resultSet.getDate("time").getTime()));
                version.setName(resultSet.getString("name"));
                version.setPath(resultSet.getString("path"));
                version.setNote(resultSet.getString("note"));
            }
        } catch (SQLException e) {
            System.err.println("Error executing select query: " + e.getMessage());
        }
        return list;
    }

    public void deleteVersionById(int id) {
        this.delete("tbl_version", "id=" + id);
    }

    public void clearVersions() {
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM tbl_version");
            log.info("All rows deleted from tbl_version");
        } catch (SQLException e) {
            log.error("Error deleting all rows from tbl_version: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SQLiteHelper helper = new SQLiteHelper();
        helper.createTable("tbl_version", "id INTEGER PRIMARY KEY AUTOINCREMENT, filename VARCHAR(255), size INT, time DATE, name VARCHAR(8), path TEXT, note TEXT");
        helper.createTable("tbl_share", "id INTEGER PRIMARY KEY AUTOINCREMENT, filename VARCHAR(255), size INT, time DATE, name VARCHAR(8), path TEXT, note TEXT");
        helper.close();
    }
}
