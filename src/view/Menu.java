package view;

import model.Currency;
import model.User;
import model.Role;
import service.*;
import utils.EmailValidateException;
import utils.PasswordValidateException;
import utils.PersonValidator;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private final UserService userService;
    private final CurrencyService currencyService;
    private final TransactionService transactionService;
    private final AccountService accountService;
    private final AdminService adminService;
    private final ExchangeService exchangeService;
    private final Scanner scanner = new Scanner(System.in);
    public static final String COLOR_GREEN = "\u001B[32m";
    public static final String COLOR_YELLOW = "\u001B[33m";
    public static final String COLOR_BLUE = "\u001B[34m";
    public static final String COLOR_RESET = "\u001B[0m";

    public Menu(UserService userService, CurrencyService currencyService, TransactionService transactionService,
                AccountService accountService, AdminService adminService, ExchangeService exchangeService) {
        this.userService = userService;
        this.currencyService = currencyService;
        this.transactionService = transactionService;
        this.accountService = accountService;
        this.adminService = adminService;
        this.exchangeService = exchangeService;
    }

    // Главный стартовый метод для начала работы
    public void start() {
        System.out.println(COLOR_YELLOW + "Добро пожаловать в систему 'Обменный пункт валюты'!" + COLOR_GREEN);
        inputUser();
    }

    // Метод для входа пользователя в систему
    private int inputUser() {
        while (true) {
            System.out.println("\n");
            System.out.println("1. Авторизация");
            System.out.println("2. Регистрация нового пользователя");
            System.out.println("0. Выход из системы");
            System.out.print(COLOR_RESET + "\nСделайте выбор пункта меню:");

            int input = getIntInput();
            switch (input) {
                case 0:
                    System.out.println("До свидания!");
                    System.exit(0);
                    break;
                case 1:
                    User authorizationUser = authorizationUser();
                    if (authorizationUser != null) {
                        showMenu(authorizationUser);
                    }
                    break;
                case 2:
                    User registrationUser = registrationUser();
                    if (registrationUser != null) {
                        showMenu(registrationUser);
                    }
                    break;
                default:
                    System.out.println("Сделайте корректный выбор");
                    break;
            }
        }
    }

    private User authorizationUser() {
        try {
            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();
            PersonValidator.validateEmail(email);

            if (!userService.isEmailExist(email)) {
                System.out.println("\nНеверный email или пароль.");
                return null;
            }
            if (userService.isUserBlocked(email)) {
                System.out.println("\nВаша учетная запись заблокирована.");
                return null;
            }
            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();
            PersonValidator.validatePassword(password);

            return userService.loginUser(email, password);
        } catch (EmailValidateException | PasswordValidateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при авторизации. Попробуйте снова. Ошибка: " + e.getMessage());
        }
        return null;
    }

    private User registrationUser() {
        try {
            System.out.println("\nРегистрация нового пользователя!");
            System.out.print("Введите email: ");
            String email = scanner.nextLine().trim();
            PersonValidator.validateEmail(email);

            System.out.print("Введите пароль: ");
            String password = scanner.nextLine().trim();
            PersonValidator.validatePassword(password);

            User newUser = userService.registerUser(email, password);
            if (newUser != null) {
                System.out.println("Пользователь " + email + " успешно зарегистрирован.");
                return newUser;
            } else {
                System.out.println("Регистрация не удалась.");
            }
        } catch (EmailValidateException | PasswordValidateException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла ошибка при регистрации. Попробуйте снова.");
        }
        return null;
    }

    // Главное меню
    private void showMenu(User user) {
        while (true) {
            System.out.println(COLOR_GREEN + "\n1. Меню пользователя");
            System.out.println("2. Меню администратора");
            System.out.println("0. Выход" + COLOR_RESET);
            System.out.print("\nВыберите действие: ");
            int choice = getIntInput();

            if (choice == 0) {
                System.out.println("Выход...");
                break;
            }
            if (user.getRole() == Role.ADMIN && choice == 2) {
                showAdminMenu(user);
            } else if (user.getRole() == Role.USER && choice == 1) {
                showUserMenu(user);
            } else {
                System.out.println("Неверный выбор.");
            }
        }
    }

    // Меню пользователя
    private void showUserMenu(User user) {
        while (true) {
            System.out.println("\n1. Просмотр баланса");
            System.out.println("2. Пополнение счета");
            System.out.println("3. Снятие средств");
            System.out.println("4. Открытие нового счета");
            System.out.println("5. Закрытие счета");
            System.out.println("6. Просмотр истории операций по всем счетам");
            System.out.println("7. Просмотр истории операций по валюте");
            System.out.println("8. Обмен валют");
            System.out.println("0. Выход");
            System.out.print("\nВыберите действие: ");
            int choice = getIntInput();

            if (choice == 0) {
                break;
            }
            showUserMenuCase(choice, user);
        }
    }

    private void showUserMenuCase(int choice, User user) {
        try {
            switch (choice) {
                case 1:
                    showBalance(user);
                    break;
                case 2:
                    depositMoney(user);
                    break;
                case 3:
                    withdrawMoney(user);
                    break;
                case 4:
                    openNewAccount(user);
                    break;
                case 5:
                    closeAccount(user);
                    break;
                case 6:
                    showUserTransactionHistory(user);
                    break;
                case 7:
                    showUserTransactionHistoryByCurrency(user);
                    break;
                case 8:
                    exchangeCurrency(user);
                    break;
                 default:
                    System.out.println("Неверный выбор.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showBalance(User user) {
        System.out.print("Введите валюту для просмотра баланса: ");
        String currency = scanner.nextLine();
        double balance = accountService.checkBalance(user.getUserId(), currency);
        System.out.println("Баланс: " + balance + " " + currency);
    }

    private void depositMoney(User user) {
        try {
            System.out.print("Введите валюту для пополнения: ");
            String currency = scanner.nextLine();
            System.out.print("Введите сумму для пополнения: ");
            double amount = getDoubleInput();

            if (amount <= 0) {
                throw new IllegalArgumentException("Сумма должна быть положительной.");
            }

            transactionService.deposit(user.getUserId(), currency, amount);
            System.out.println("Счет успешно пополнен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void withdrawMoney(User user) {
        try {
            System.out.print("Введите валюту для снятия: ");
            String currency = scanner.nextLine();
            System.out.print("Введите сумму для снятия: ");
            double amount = getDoubleInput();

            if (amount <= 0) {
                throw new IllegalArgumentException("Сумма должна быть положительной.");
            }

            if (transactionService.withdraw(user.getUserId(), currency, amount)) {
                System.out.println("Средства сняты.");
            } else {
                System.out.println("Ошибка: недостаточно средств.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void exchangeCurrency(User user) {
        try {
            System.out.print("Введите валюту для обмена: ");
            String fromCurrency = scanner.nextLine();
            System.out.print("Введите валюту для получения: ");
            String toCurrency = scanner.nextLine();
            System.out.print("Введите сумму для обмена: ");
            double amount = getDoubleInput();

            exchangeService.exchange(user.getUserId(), fromCurrency, toCurrency, amount);
            System.out.println("Обмен выполнен успешно.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void showAllTransactionHistory() {
        try {
            System.out.println("История всех операций пользователя:");
            transactionService.getAllTransactions().forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Ошибка при получении истории операций: " + e.getMessage());
        }
    }

    private void showUserTransactionHistory(User user) {
        try {
            System.out.println("История всех операций пользователя:");
            transactionService.getUserTransactions(user.getUserId()).forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Ошибка при получении истории операций: " + e.getMessage());
        }
    }

    private void showAllTransactionHistoryByCurrency() {
        try {
            System.out.print("Введите валюту для просмотра истории: ");
            String currency = scanner.nextLine().trim();
            if (currency.isEmpty()) {
                System.out.println("Ошибка: валюта не может быть пустой.");
                return;
            }
            transactionService.getAllTransactionsByCurrency(currency).forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Ошибка при получении истории операций по валюте: " + e.getMessage());
        }
    }

    private void showUserTransactionHistoryByCurrency(User user) {
        try {
            System.out.print("Введите валюту для просмотра истории: ");
            String currency = scanner.nextLine().trim();
            if (currency.isEmpty()) {
                System.out.println("Ошибка: валюта не может быть пустой.");
                return;
            }
            transactionService.getUserTransactionsByCurrency(user.getUserId(), currency).forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("Ошибка при получении истории операций по валюте: " + e.getMessage());
        }
    }

    private void openNewAccount(User user) {
        System.out.print("Введите валюту для открытия счета: ");
        String currency = scanner.nextLine();
        accountService.createAccount(user.getUserId(), currency);
    }

    private void closeAccount(User user) {
        System.out.print("Введите валюту для закрытия счета: ");
        String currency = scanner.nextLine();
        accountService.close(user.getUserId(), currency);
    }

    // Меню администратора
    private void showAdminMenu(User user) {
        while (true) {
            System.out.println("\n1. Изменение курса валют");
            System.out.println("2. Добавление валюты");
            System.out.println("3. Удаление валюты");
            System.out.println("4. Просмотр операций пользователя по всем счетам");
            System.out.println("5. Просмотр операций по валюте");
            System.out.println("6. Назначить администратора");
//            System.out.println("7. Блокировать пользователя (опционально)");
            System.out.println("0. Выход");
            System.out.print("\nВыберите действие: ");
            int choice = getIntInput();

            if (choice == 0) {
                break;
            }
            showAdminMenuCase(choice, user);
        }
    }

    private void showAdminMenuCase(int choice, User user) {
        switch (choice) {
            case 1:
                updateExchangeRate(user);
                break;
            case 2:
                addCurrency(user);
                break;
            case 3:
                removeCurrency(user);
                break;
            case 4:
                showAllTransactionHistory();
                break;
            case 5:
                showAllTransactionHistoryByCurrency();
                break;
            case 6:
                promoteToAdmin();
                break;
            case 7:
                // Todo опционально
//                blockUser();
                break;
            default:
                System.out.println("Неверный выбор.");
        }
    }

    private void updateExchangeRate(User user) {
        try {
            System.out.print("Введите валюту: ");
            String currency = scanner.nextLine();
            System.out.print("Введите новый курс: ");
            double newRate = getDoubleInput();

            if (newRate <= 0) {
                throw new IllegalArgumentException("Курс должен быть положительным числом.");
            }

            currencyService.updateCurrency(user, currency, newRate);
            System.out.println("Курс успешно обновлен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void addCurrency(User user) {
        try {
            System.out.print("Введите код валюты для добавления: ");
            String code = scanner.nextLine();
            System.out.print("Введите курс валюты: ");
            double rate = getDoubleInput();

            if (rate <= 0) {
                throw new IllegalArgumentException("Курс должен быть положительным числом.");
            }

            currencyService.addCurrency(user, code, rate);
            System.out.println("Валюта успешно добавлена.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void removeCurrency(User user) {
        try {
            System.out.print("Введите код валюты для удаления: ");
            String code = scanner.nextLine();

            if (code.isBlank()) {
                throw new IllegalArgumentException("Код валюты не может быть пустым.");
            }

            currencyService.removeCurrency(user, code);
            System.out.println("Валюта успешно удалена.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void promoteToAdmin() {
        try {
            System.out.print("Введите ID пользователя для повышения: ");
            String userId = scanner.nextLine();

            if (userId.isBlank()) {
                throw new IllegalArgumentException("ID пользователя не может быть пустым.");
            }

            adminService.promoteToAdmin(userId);
            System.out.println("Пользователь успешно повышен до администратора.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private int getIntInput() {
        while (true) {
            try {
                int input = scanner.nextInt();
                scanner.nextLine(); // Очищаем буфер после nextInt
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите целое число.");
                scanner.nextLine();
            }
        }
    }

    private double getDoubleInput() {
        while (true) {
            try {
                return scanner.nextDouble();
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: введите корректное число (например, 10.5).");
                scanner.next(); // Очистка некорректного ввода
            }
        }
    }

}