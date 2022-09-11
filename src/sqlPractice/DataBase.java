package sqlPractice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	
	private Connection connection;
	private Statement statement;
	
	private final String DB_DRIVER = "org.sqlite.JDBC";
	private final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\My_cats.db";
	
	public static void main(String[] args) {
		
		DataBase db = new DataBase();
		
		db.makeConnection();
		db.CreateTable();
		db.closeAllConnections();

	}
	
	public void makeConnection() {
		
		connection = null;
		
		try {
			Class.forName(DB_DRIVER);
			connection = DriverManager.getConnection(DB_URL);
			System.out.println("���� ������ ����������!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("������� �� ������!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("�� �� �������!");
		}
		
	}
	
	public void CreateTable() {
		
		try {
			statement = connection.createStatement();
			String query = "CREATE TABLE if not exists 'types' (" +
						   "'id' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," + 
						   "'type' VARCHAR(100) NOT NULL);";
			statement.execute(query);
			System.out.println("������� �������!");
		} catch (SQLException e) {
			System.out.println("������ sql ��� ���������� �������!");
			e.printStackTrace();
		}
		
	}
	
	public void closeAllConnections() {
		
		try {
			statement.close();
			connection.close();
			System.out.println("���������� �������!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	
}
