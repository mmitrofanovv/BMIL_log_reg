package org.example;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class UserRepository {
    Properties properties = new Properties();
    InputStream input;
    {
        try {
            input = new FileInputStream("src/main/resources/sql.properties");
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final String url = properties.getProperty("database.url");
    private final String username = properties.getProperty("login");
    private final String password = properties.getProperty("password");

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Регистрация нового пользователя
    public boolean registerUser(String login, String password) {
        String query = "INSERT INTO users (login, password) VALUES (?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    // Поиск пользователя по логину
    public boolean isUserExists(String login) {
        String query = "SELECT COUNT(*) FROM users WHERE login = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }

    // Аутентификация пользователя
    public boolean authenticate(String login, String password) {
        String query = "SELECT * FROM users WHERE login = ? AND password = ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error during authentication: " + e.getMessage());
        }
        return false;
    }

    public int deleteUsersWithSpecificCharacters(String chars) {
        String query = "DELETE FROM users WHERE password REGEXP ?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            String regex = "[" + chars + "]"; // Регулярное выражение для поиска символов
            preparedStatement.setString(1, regex);
            return preparedStatement.executeUpdate(); // Возвращает количество удалённых строк
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении пользователей: " + e.getMessage());
        }
        return 0;
    }

}
