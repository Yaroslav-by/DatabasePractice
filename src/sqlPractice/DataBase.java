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
		db.addAllTypes();
		db.closeAllConnections();

	}
	
	public void makeConnection() {
		
		connection = null;
		
		try {
			Class.forName(DB_DRIVER);
			connection = DriverManager.getConnection(DB_URL);
			System.out.println("База данных подключена!");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.out.println("Драйвер не найден!");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("БД не найдена!");
		}
		
	}
	
	public void CreateTable() {
		
		try {
			statement = connection.createStatement();
			String query = "CREATE TABLE if not exists 'types' (" +
						   "'id' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," + 
						   "'type' VARCHAR(100) NOT NULL);";
			statement.execute(query);
			System.out.println("Таблица создана!");
		} catch (SQLException e) {
			System.out.println("Ошибка sql или соединение закрыто!");
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
				System.out.println(type + " добавлена!");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(type + " уже существует в базе данных!");
		}

	}
	
	public void addAllTypes() {
		
		String[] types = {"Абиссинская кошка",
	            "Австралийский мист",
	            "Американская жесткошерстная",
	            "Американская короткошерстная",
	            "Американский бобтейл",
	            "Американский кёрл",
	            "Балинезийская кошка",
	            "Бенгальская кошка",
	            "Бирманская кошка",
	            "Бомбейская кошка",
	            "Бразильская короткошёрстная",
	            "Британская длинношерстная",
	            "Британская короткошерстная",
	            "Бурманская кошка",
	            "Бурмилла кошка",
	            "Гавана",
	            "Гималайская кошка",
	            "Девон-рекс",
	            "Донской сфинкс",
	            "Европейская короткошерстная",
	            "Египетская мау",
	            "Канадский сфинкс",
	            "Кимрик",
	            "Корат",
	            "Корниш-рекс",
	            "Курильский бобтейл",
	            "Лаперм",
	            "Манчкин",
	            "Мейн-ку́н",
	            "Меконгский бобтейл",
	            "Мэнкс кошка",
	            "Наполеон",
	            "Немецкий рекс",
	            "Нибелунг",
	            "Норвежская лесная кошка",
	            "Ориентальная кошка",
	            "Оцикет",
	            "Персидская кошка",
	            "Петерболд",
	            "Пиксибоб",
	            "Рагамаффин",
	            "Русская голубая кошка",
	            "Рэгдолл",
	            "Саванна",
	            "Селкирк-рекс",
	            "Сиамская кошка",
	            "Сибирская кошка",
	            "Сингапурская кошка",
	            "Скоттиш-фолд",
	            "Сноу-шу",
	            "Сомалийская кошка",
	            "Тайская кошка",
	            "Тойгер",
	            "Тонкинская кошка",
	            "Турецкая ангорская кошка",
	            "Турецкий ван",
	            "Украинский левкой",
	            "Чаузи",
	            "Шартрез",
	            "Экзотическая короткошерстная",
	            "Японский бобтейл"
	    };
		
		for (String type : types) {
			insertType(type);
		}
	}
	
	public void closeAllConnections() {
		
		try {
			resultSet.close();
			statement.close();
			connection.close();
			System.out.println("Соединения закрыты!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
