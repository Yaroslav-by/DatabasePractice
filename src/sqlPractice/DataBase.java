package sqlPractice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	
	private final String DB_DRIVER = "org.sqlite.JDBC";
	private final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\My_cats.db";
	
	public static void main(String[] args) {
		
		DataBase db = new DataBase();
		
		db.makeConnection();
		db.CreateTable();
		db.insertType("����������� �����");
		db.insertType("������������� ����");
		db.insertType("������������ ��������������");
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
	
	public void insertType(String type) {
		
		String query = "SELECT type FROM types";
		boolean isDuplicate = false;
		
		try {
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				if (type.equals(resultSet.getString("type"))) {
					isDuplicate = true;
					break;
				}
			}	
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if (!isDuplicate) {
			try {
				query = "INSERT INTO 'types' ('type')" +
						"VALUES ('" + type + "');";
				statement.executeUpdate(query);
				System.out.println(type + " ���������!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(type + " ��� ���������� � ���� ������!");
		}

	}
	
	public void closeAllConnections() {
		
		try {
			resultSet.close();
			statement.close();
			connection.close();
			System.out.println("���������� �������!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
