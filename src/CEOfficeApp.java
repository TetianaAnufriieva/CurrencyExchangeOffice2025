import model.Transaction;
import repository.*;
import service.*;
import view.Menu;

import java.util.ArrayList;
import java.util.List;

public class CEOfficeApp {

    public static void main(String[] args) {
        // Инициализация репозиториев
        UserRepository userRepository = new UserRepositoryImpl();
        CurrencyRepository currencyRepository = new CurrencyRepositoryImpl();
        AccountRepository accountRepository = new AccountRepositoryImpl(userRepository, currencyRepository);

        // Создание списка транзакций
        List<Transaction> transactions = new ArrayList<>();
        TransactionRepository transactionRepository = new TransactionRepositoryImpl(accountRepository);

        // Создание сервисного слоя
        UserService userService = new UserServiceImpl(userRepository);
        AccountService accountService = new AccountServiceImpl(accountRepository, transactionRepository);
        AdminService adminService = new AdminServiceImpl(userRepository);
        CurrencyService currencyService = new CurrencyServiceImpl(currencyRepository, accountRepository);
        ExchangeService exchangeService = new ExchangeServiceImpl(accountRepository, transactionRepository, currencyRepository);
        TransactionService transactionService = new TransactionServiceImpl(transactionRepository, accountRepository, userService);

        // Создание меню
        Menu menu = new Menu(userService, currencyService, transactionService, accountService, adminService, exchangeService);

        // Запуск приложения
        menu.start();
    }
}