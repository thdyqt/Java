package Utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/QuanLyKhachSan";
    private static final String USER = "root";
    private static final String PASSWORD = "Ptd_2907";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Kết nối Database thành công!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Kết nối Database thất bại!");
        }
        return connection;
    }
}
