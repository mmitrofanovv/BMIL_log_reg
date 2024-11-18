package org.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserManager {
    private final UserRepository userRepository = new UserRepository();
    private final Scanner scanner = new Scanner(System.in);


    // Регистрация
    public void register() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();
        if (userRepository.isUserExists(username)) {
            System.out.println("Ошибка: Логин уже существует. Попробуйте другой.");
            return;
        }

        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        // Проверка пароля на соответствие требованиям
        if (!PasswordValidator.validatePassword(password)) {
            System.out.println("Регистрация не выполнена. Пароль не соответствует требованиям.");
            return;
        }

        if (userRepository.registerUser(username, password)) {
            System.out.println("Регистрация успешна.");
        } else {
            System.out.println("Ошибка при регистрации.");
        }
    }

    private List<Long> collectTypingTimes(String passwordPrompt) {
        System.out.print(passwordPrompt);
        List<Long> intervals = new ArrayList<>();
        long lastTime = System.nanoTime();

        for (char ignored : scanner.nextLine().toCharArray()) {
            long currentTime = System.nanoTime();
            intervals.add(currentTime - lastTime);
            lastTime = currentTime;
        }
        return intervals;
    }

    // Логин
    public void login() {
        System.out.print("Введите логин: ");
        String username = scanner.nextLine();

        System.out.println("Введите пароль (закончите ввод нажатием Enter):");
        List<Long> typingIntervals = new ArrayList<>();
        long lastPressTime = System.nanoTime(); // Фиксация времени перед вводом первого символа

        StringBuilder passwordBuilder = new StringBuilder();

        try {
            while (true) {
                if (System.in.available() > 0) { // Проверяем, есть ли символы во входящем потоке
                    int key = System.in.read(); // Читаем один символ
                    if (key == '\n') { // Завершаем ввод на Enter
                        break;
                    }

                    char c = (char) key;
                    passwordBuilder.append(c);

                    long currentPressTime = System.nanoTime();
                    typingIntervals.add(currentPressTime - lastPressTime);
                    lastPressTime = currentPressTime;
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка ввода: " + e.getMessage());
            return;
        }

        String password = passwordBuilder.toString();

        System.out.println("Проверка данных...");
        if (userRepository.authenticate(username, password)) {
            System.out.println("Вы успешно вошли в систему.");
            System.out.println("Время между нажатиями клавиш (в наносекундах):");
            for (int i = 0; i < typingIntervals.size(); i++) {
                System.out.printf("Интервал %d: %d наносекунд\n", i + 1, typingIntervals.get(i) / 1000000000);
            }
        } else {
            System.out.println("Ошибка: Неправильное имя пользователя или пароль.");
        }
    }



    public void deleteUsersWithSpecificCharacters() {
        String chars = "asdfg";
        int deletedCount = userRepository.deleteUsersWithSpecificCharacters(chars);
        if (deletedCount > 0) {
            System.out.println("Удалено пользователей: " + deletedCount);
        } else {
            System.out.println("Нет пользователей для удаления.");
        }
    }

    // Старт приложения
    public void start() {
        while (true) {
            System.out.println("\nСистема управления пользователями");
            System.out.println("1. Регистрация");
            System.out.println("2. Вход");
            System.out.println("3. Удаление пользователей с символами 'asdfg' в пароле");
            System.out.println("4. Выход");
            System.out.print("Введите ваш выбор: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка ввода

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    deleteUsersWithSpecificCharacters();
                    break;
                case 4:
                    System.out.println("Выход...");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }
    }
}
