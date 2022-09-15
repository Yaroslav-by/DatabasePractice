package sqlPractice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Random;

public class DataBase {
	
	private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	
	private final String DB_DRIVER = "org.sqlite.JDBC";
	private final String DB_URL = "jdbc:sqlite:" + System.getProperty("user.dir") + "\\My_cats.db";
	
	public static void main(String[] args) {
		
		DataBase db = new DataBase();
		
		db.makeConnection();
		db.getCat(20);
		db.getCatWhere("id < 20 AND name LIKE '%я'");
		db.getAllCats();
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
		} catch (SQLException e) {
			System.out.println("Ошибка sql или соединение закрыто!");
			e.printStackTrace();
		}
		
	}
	
	public void CreateSecondTable() {
		
		try {
			statement = connection.createStatement();
			String query = "CREATE TABLE if not exists 'cats' (" + 
						   "'id' INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, " + 
						   "'name' VARCHAR(20) NOT NULL, " + 
						   "'type_id' INTEGER NOT NULL REFERENCES 'types' (id), " + 
						   "'age' INTEGER NOT NULL, " + 
						   "'weight' DOUBLE);";
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void insertType(String type) {
		
		String query = "SELECT type FROM types";
		boolean isDuplicate = false;
		
		try {
			statement = connection.createStatement();
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
	
	public void insertCat(String name, String type, int age, Double weight) {
		
		try {
			statement = connection.createStatement();
			String query = "SELECT id FROM types WHERE type = '" + type + "';";
			resultSet = statement.executeQuery(query);
			
			if (resultSet.getInt("id") == 0) {
				insertType(type);
				
				query = "SELECT id FROM types WHERE type = '" + type + "';";
				resultSet = statement.executeQuery(query);
				query = "INSERT INTO cats (name, type_id, age, weight) "
						  + "VALUES ('" + name + "', " 
						  + resultSet.getInt("id") + ", "
						  + age + ", "
						  + weight + ");";
				statement.executeUpdate(query);
				System.out.println("Кот с именем " + name + " добавлен в базу данных!");
				
			} else {
				query = "INSERT INTO cats (name, type_id, age, weight) "
					  + "VALUES ('" + name + "', " 
					  + resultSet.getInt("id") + ", "
					  + age + ", "
					  + weight + ");";
				statement.executeUpdate(query);
				System.out.println("Кот с именем " + name + " добавлен в базу данных!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public void deleteCat(int id) {
		
		try {
			statement = connection.createStatement();
			String query = "DELETE FROM cats " + 
						   "WHERE id = " + id + ";";
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void deleteCat(String where) {
		
		try {
			statement = connection.createStatement();
			String query = "DELETE FROM cats " + 
						   "WHERE " + where + ";";
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	public void updateCat(int id, String set) {
		
		try {
			statement = connection.createStatement();
			String query = "UPDATE cats " + 
						   "SET name = '" + set + "' " +
						   "WHERE id = " + id + ";";
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void updateCat(String where, String set) {
		
		try {
			statement = connection.createStatement();
			String query = "UPDATE cats " + 
						   "SET name = '" + set + "' " +
						   "WHERE " + where + ";";
			statement.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getCat(int id) {
		
		try {
			String query = "SELECT * FROM cats WHERE id = " + id + ";";
			String q = "SELECT type FROM types WHERE id IN (SELECT type_id FROM cats WHERE id = " + id + ");";
			
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			Statement st = connection.createStatement();
			ResultSet rS = st.executeQuery(q);
			
			System.out.println("Имя: " + resultSet.getString("name"));
			System.out.println("Порода: " + rS.getString("type"));
			System.out.println("Возраст: " + resultSet.getInt("age") + " годиков");
			System.out.println("Вес: " + resultSet.getDouble("weight") + " килограммиков");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getCatWhere(String where) {
		
		String query = "SELECT * FROM cats WHERE " + where + ";";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			
			while (resultSet.next()) {
				String q = "SELECT type FROM types WHERE id IN (SELECT type_id FROM cats WHERE id = " + resultSet.getInt("id") + " );";
				Statement st = connection.createStatement();
				ResultSet rS = st.executeQuery(q);
				
				System.out.println("Имя: " + resultSet.getString("name"));
				System.out.println("Порода: " + rS.getString("type"));
				System.out.println("Возраст: " + resultSet.getInt("age") + " годиков");
				System.out.println("Вес: " + resultSet.getDouble("weight") + " килограммиков");
				System.out.println("-----------------------------------");
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getAllCats() {
		
		String query = "SELECT * FROM cats";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				String q = "SELECT type FROM types WHERE id IN (SELECT type_id FROM cats WHERE id = " + resultSet.getInt("id") + " );";
				Statement st = connection.createStatement();
				ResultSet rS = st.executeQuery(q);
				
				System.out.println("Имя: " + resultSet.getString("name"));
				System.out.println("Порода: " + rS.getString("type"));
				System.out.println("Возраст: " + resultSet.getInt("age") + " годиков");
				System.out.println("Вес: " + resultSet.getDouble("weight") + " килограммиков");
				System.out.println("-----------------------------------");
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addMoreCats(int n) {
		
		Random random = new Random();
		DecimalFormat dF = new DecimalFormat("#.##");
		
		String[] names = {"Гарфилд","Том","Гудвин","Рокки","Ленивец","Пушок","Спорти","Бегемот","Пират",
				"Гудини","Зорро","Саймон","Альбус","Базилио","Леопольд","Нарцисс","Атос","Каспер","Валлито",
				"Оксфорд","Бисквит","Соня","Клеопатра","Цунами","Забияка","Матильда","Кнопка","Масяня",
				"Царапка","Серсея","Ворсинка","Амели","Наоми","Маркиза","Изольда","Вальс","Несквик","Златан",
				"Баскет","Изюм","Цукат","Мокко","Месси","Кокос","Адидас","Бейлиз","Тайгер","Зефир","Мохи",
				"Валенсия","Баунти","Свити","Текила","Ириска","Карамель","Виски","Кукуруза","Гренка",
				"Фасолька","Льдинка","Китана","Офелия","Дайкири","Брусника","Аватар","Космос","Призрак",
				"Изумруд","Плинтус","Яндекс","Крисмас","Метеор","Оптимус","Смайлик","Цельсий","Краска",
				"Дейзи","Пенка","Веста","Астра","Эйприл","Среда","Бусинка","Гайка","Елка","Золушка","Мята",
				"Радость","Сиам","Оскар","Феликс","Гарри","Байрон","Чарли","Симба","Тао","Абу","Ватсон",
				"Енисей","Измир","Кайзер","Васаби","Байкал","Багира","Айрис","Диана","Мими","Сакура","Индия",
				"Тиффани","Скарлетт","Пикси","Лиззи","Алиса","Лило","Ямайка","Пэрис","Мальта","Аляска"};
				
		String[] types = {"Абиссинская кошка", "Австралийский мист", "Американская жесткошерстная",
	            "Американская короткошерстная", "Американский бобтейл", "Американский кёрл",
	            "Балинезийская кошка", "Бенгальская кошка", "Бирманская кошка", "Бомбейская кошка",
	            "Бразильская короткошёрстная", "Британская длинношерстная", "Британская короткошерстная",
	            "Бурманская кошка", "Бурмилла кошка", "Гавана", "Гималайская кошка", "Девон-рекс",
	            "Донской сфинкс", "Европейская короткошерстная", "Египетская мау", "Канадский сфинкс",
	            "Кимрик", "Корат", "Корниш-рекс", "Курильский бобтейл", "Лаперм", "Манчкин", "Мейн-ку́н",
	            "Меконгский бобтейл", "Мэнкс кошка", "Наполеон", "Немецкий рекс", "Нибелунг",
	            "Норвежская лесная кошка", "Ориентальная кошка", "Оцикет", "Персидская кошка", "Петерболд",
	            "Пиксибоб", "Рагамаффин", "Русская голубая кошка", "Рэгдолл", "Саванна", "Селкирк-рекс",
	            "Сиамская кошка", "Сибирская кошка", "Сингапурская кошка", "Скоттиш-фолд", "Сноу-шу",
	            "Сомалийская кошка", "Тайская кошка", "Тойгер", "Тонкинская кошка", "Турецкая ангорская кошка",
	            "Турецкий ван", "Украинский левкой", "Чаузи", "Шартрез", "Экзотическая короткошерстная",
	            "Японский бобтейл"
	    };
		
		for (int i = 0; i < n; i++) {
			
			insertCat(
					  names[random.nextInt(0, names.length - 1)], 
					  types[random.nextInt(0, types.length - 1)], 
					  random.nextInt(0, 20), 
					  Double.parseDouble(dF.format(random.nextDouble(1, 10)).replace(',', '.'))
					  );
			
		}
		
	}
	
	public void deleteType(int id) {
		
		try {
			String query = "DELETE FROM types " + 
					   "WHERE id = " + id + ";";
			statement = connection.createStatement();
			statement.execute(query);
			System.out.println(id + " удален!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void UpdateType(int id, String newType) {
		
		try {
			String query = "UPDATE types " + 
						   "SET type = '" + newType + "' " + 
						   "WHERE id = " + id + ";";
			statement = connection.createStatement();
			statement.executeUpdate(query);
			System.out.println("Порода кота с id = " + id + " изменена на " + newType);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void getType(int id) {
		
		String query = "SELECT * FROM types;";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				if (resultSet.getInt("id") == id) {
					System.out.println("id = " + id + ": " + resultSet.getString("type"));
					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getTypeWhere(String where) {
		
		String query = "SELECT type FROM types WHERE " + where + ";";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			System.out.println("По Вашему запросу нашлось:");
			System.out.println("--------------------------");
			while (resultSet.next()) {
				System.out.println(resultSet.getString("type"));
			}
			System.out.println("--------------------------");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getAllTypes() {
		
		String query = "SELECT type FROM types;";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			while (resultSet.next()) {
				System.out.println(resultSet.getString("type"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
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
			//resultSet.close();
			statement.close();
			connection.close();
			System.out.println("Соединения закрыты!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
