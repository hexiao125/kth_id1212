package se.kth.id1212.filemanage.server.integration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import se.kth.id1212.filemanage.common.Credentials;
import se.kth.id1212.filemanage.common.FileInfo;

public class JDBCHandler {

    private static final String TABLE_NAME_USER_CREDENTIALS = "users";
    private static final String TABLE_NAME_FILES = "files";
    Connection connection;

    private PreparedStatement createUserStmt;
    private PreparedStatement findAllUserStmt;
    private PreparedStatement deleteUserStmt;
    private PreparedStatement checkUserNameStmt;
    private PreparedStatement getPasswordStmt;
    private PreparedStatement getUserPassStmt;

    private PreparedStatement createFileStmt;
    private PreparedStatement checkFileExistStmt;
    private PreparedStatement listFileStmt;
    private PreparedStatement getFileOwnerStmt;
    private PreparedStatement getFileInformStmt;
    private PreparedStatement deleteFileStmt;
    private PreparedStatement getFileAttrStmt;
    private PreparedStatement updateFileOwnerStmt;
    private PreparedStatement updateFileOtherStmt;
    
    public void start() {
        try {
            Class.forName("org.apache.derby.jdbc.ClientXADataSource");
            connection = DriverManager.getConnection(
                    "jdbc:derby://localhost:1527/FileManagerDB", "filemanager",
                    "filemanager");
            createUserTable(connection);
            createFileTable(connection);
            prepareStatements(connection);
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
    }

    // user handle functions
    public boolean addUser(Credentials credentials) {
        try {
            checkUserNameStmt.setString(1, credentials.getUsername());
            ResultSet rs = checkUserNameStmt.executeQuery();
            if (!rs.next()) {
                createUserStmt.setString(1, credentials.getUsername());
                createUserStmt.setString(2, credentials.getPassword());
                createUserStmt.executeUpdate();
                listAllUsers();
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(Credentials credentials) {
        try {
            getUserPassStmt.setString(1, credentials.getUsername());
            ResultSet rs = getUserPassStmt.executeQuery();
            if (rs.next()) {
                if (rs.getString(2).equals(credentials.getPassword())) {
                    deleteUserStmt.setString(1, credentials.getUsername());
                    deleteUserStmt.executeUpdate();
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private void createUserTable(Connection connection) throws SQLException {
        if (!userTableExists(connection)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                    "create table " + TABLE_NAME_USER_CREDENTIALS
                    + " (username varchar(32) primary key, "
                    + "password varchar(32))");
        }
    }

    private boolean userTableExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableMetaData = metaData.getTables(null, null, null, null);
        while (tableMetaData.next()) {
            String tableName = tableMetaData.getString(3);
            if (tableName.equalsIgnoreCase(TABLE_NAME_USER_CREDENTIALS)) {
                return true;
            }
        }
        return false;
    }

    public boolean userAuthentication(Credentials crdt) throws SQLException {
        listAllUsers();
        getPasswordStmt.setString(1, crdt.getUsername());
        ResultSet rs = getPasswordStmt.executeQuery();
        if (rs.next()) {
            if (rs.getString(1).equalsIgnoreCase(crdt.getPassword())) {
                return true;
            }
        }
        return false;
    }

    private void listAllUsers() throws SQLException {
        ResultSet persons = findAllUserStmt.executeQuery();
        while (persons.next()) {
            System.out.println(
                    "username: " + persons.getString(1) + ", password: " + persons.getString(2));
        }
    }

    // file handle functions
    private void createFileTable(Connection connection) throws SQLException {
        if (!fileTableExists(connection)) {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(
                    "create table " + TABLE_NAME_FILES
                    + " (name varchar(32) primary key, "
                    + "size varchar(32), "
                    + "owner varchar(32), "
                    + "privacy smallint, "
                    + "readable smallint, "
                    + "writable smallint, "
                    + "inform smallint)");
        }
    }

    private boolean fileTableExists(Connection connection) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tableMetaData = metaData.getTables(null, null, null, null);
        while (tableMetaData.next()) {
            String tableName = tableMetaData.getString(3);
            if (tableName.equalsIgnoreCase(TABLE_NAME_FILES)) {
                return true;
            }
        }
        return false;
    }

    public boolean addFile(FileInfo file) {
        try {
            if (!fileExists(file.getName())) {
                createFileStmt.setString(1, file.getName());
                createFileStmt.setString(2, file.getSize());
                createFileStmt.setString(3, file.getOwner());
                createFileStmt.setBoolean(4, file.getPublicPrivacy());
                createFileStmt.setBoolean(5, file.getRead());
                createFileStmt.setBoolean(6, file.getWrite());
                createFileStmt.setBoolean(7, file.getInform());
                createFileStmt.executeUpdate();
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean getFile(String username, String fileName) throws SQLException {
        getFileAttrStmt.setString(1, fileName);
        ResultSet rs = getFileAttrStmt.executeQuery();
        if (rs.next() && (rs.getString(3).equals(username)||(rs.getBoolean(4) && rs.getBoolean(5)))) {
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteFile(String userName, String fileName) {
        try {
            getFileOwnerStmt.setString(1, fileName);
            ResultSet rs = getFileOwnerStmt.executeQuery();
            if (rs.next()) {
                if (rs.getString(1).equals(userName)) {
                    deleteFileStmt.setString(1, fileName);
                    deleteFileStmt.executeUpdate();
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public String getFileOwner(String filename) throws SQLException {
        getFileOwnerStmt.setString(1, filename);
        ResultSet rs = getFileOwnerStmt.executeQuery();
        if (rs.next()) {
            return rs.getString(1);
        }
        return "notFound";
    }
    
    public boolean getFileInform(String filename) throws SQLException {
        getFileInformStmt.setString(1, filename);
        ResultSet rs = getFileInformStmt.executeQuery();
        if (rs.next()) {
            return rs.getBoolean(1);
        }
        return false;
    }            
                    
    private boolean fileExists(String filename) throws SQLException {
        checkFileExistStmt.setString(1, filename);
        ResultSet rs = checkFileExistStmt.executeQuery();
        if (rs.next()) {
            return true;
        }
        return false;
    }

    public String listFiles(String username) throws SQLException {
        listFileStmt.setString(1, username);
        ResultSet rs = listFileStmt.executeQuery();
        String allFile = "Search result:";
        while (rs.next()) {
            allFile = allFile + "\n" + "name: " + rs.getString(1) + " | size: " + rs.getString(2)
                    + " | owner: " + rs.getString(3) + " | privacy(public): " + rs.getBoolean(4)
                    + " | read: " + rs.getBoolean(5) + " | write: " + rs.getBoolean(6)
                    + " | inform: " + rs.getBoolean(7);
        }
        return allFile;
    }

    public boolean updateFile(String username, FileInfo file) {
        try {
            getFileAttrStmt.setString(1, file.getName());
            ResultSet rs = getFileAttrStmt.executeQuery();
            if (rs.next()) {
                if (rs.getString(3).equals(username)) {
                    updateFileOwnerStmt.setString(1, file.getSize());
                    updateFileOwnerStmt.setBoolean(2, file.getPublicPrivacy());
                    updateFileOwnerStmt.setBoolean(3, file.getRead());
                    updateFileOwnerStmt.setBoolean(4, file.getWrite());
                    updateFileOwnerStmt.setBoolean(5, file.getInform());
                    updateFileOwnerStmt.setString(6, file.getName());
                    updateFileOwnerStmt.executeUpdate();
                    return true;
                } else if ((rs.getBoolean(4) && rs.getBoolean(6))) {
                    updateFileOtherStmt.setString(1, file.getSize());
                    updateFileOtherStmt.setString(2, file.getName());
                    updateFileOtherStmt.executeUpdate();
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }
        
    // common functions
    private void prepareStatements(Connection connection) throws SQLException {
        createUserStmt = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME_USER_CREDENTIALS + " VALUES (?,?)");
        findAllUserStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME_USER_CREDENTIALS);
        deleteUserStmt = connection.prepareStatement("DELETE FROM "
                + TABLE_NAME_USER_CREDENTIALS
                + " WHERE username = ?");
        checkUserNameStmt = connection.prepareStatement("SELECT username FROM "
                + TABLE_NAME_USER_CREDENTIALS
                + " WHERE username = ?");
        getPasswordStmt = connection.prepareStatement("SELECT password FROM "
                + TABLE_NAME_USER_CREDENTIALS
                + " WHERE username = ?");
        getUserPassStmt = connection.prepareStatement("SELECT * FROM "
                + TABLE_NAME_USER_CREDENTIALS
                + " WHERE username = ?");

        createFileStmt = connection.prepareStatement("INSERT INTO "
                + TABLE_NAME_FILES + " VALUES (?,?,?,?,?,?,?)");
        listFileStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME_FILES
                + " WHERE owner = ? OR privacy = 1");
        checkFileExistStmt = connection.prepareStatement("SELECT name FROM "
                + TABLE_NAME_FILES
                + " WHERE name = ?");
        getFileOwnerStmt = connection.prepareStatement("SELECT owner FROM "
                + TABLE_NAME_FILES
                + " WHERE name = ?");
        getFileInformStmt = connection.prepareStatement("SELECT inform FROM "
                + TABLE_NAME_FILES
                + " WHERE name = ?");
        deleteFileStmt = connection.prepareStatement("DELETE FROM "
                + TABLE_NAME_FILES
                + " WHERE name = ?");
        getFileAttrStmt = connection.prepareStatement("SELECT * from "
                + TABLE_NAME_FILES
                + " WHERE name = ?");
        updateFileOwnerStmt = connection.prepareStatement("UPDATE "+ TABLE_NAME_FILES
                + " SET size = ? , privacy = ? , readable = ? , writable = ? , inform = ? "
                + " WHERE name = ?");
        updateFileOtherStmt = connection.prepareStatement("UPDATE "+ TABLE_NAME_FILES
                + " SET size = ?"
                + " WHERE name = ?");
    }

}
