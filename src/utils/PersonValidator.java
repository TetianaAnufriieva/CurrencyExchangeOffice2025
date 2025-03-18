package utils;

public class PersonValidator {

    public static void validateEmail(String email) throws EmailValidateException {
        // 1. Должна присутствовать @
        int indexAt = email.indexOf('@');
        if (indexAt == -1 || indexAt != email.lastIndexOf('@')) throw new EmailValidateException("@ error");

        // 2. Точка после собаки
        if (email.indexOf('.', indexAt + 2) == -1) throw new EmailValidateException(". after @ error");

        // 3. После последней точки должно быть минимум 2 символа
        if (email.lastIndexOf('.') >= email.length() - 2) throw new EmailValidateException("last . error");

        for (int i = 0; i < email.length(); i++) {
            char ch = email.charAt(i);
            if (!(Character.isAlphabetic(ch)
                    || Character.isDigit(ch)
                    || ch == '_'
                    || ch == '-'
                    || ch == '.'
                    || ch == '@')) { // Символ НЕ подходит
                throw new EmailValidateException("illegal symbol");
            }
        }

        // 5. До собаки должен быть хотя бы 1 символ
        if (indexAt == 0) throw new EmailValidateException("@ should not be first");

        if (!Character.isAlphabetic(email.charAt(0))) throw new EmailValidateException("first symbol should be letter");


    }

    public static void validatePassword(String password) throws PasswordValidateException{
        if (password == null || password.length() < 8){
            System.out.println("Password should be 8 symbols");
            throw new PasswordValidateException("Amount of symbols error");
        }

        String symbols = "!%$@&*()[]";
        // Альтернативный способ объявления пременных
        boolean[] result = new boolean[4]; // false, false, false, false

        for (int i = 0;  i < password.length(); i++){
            char ch = password.charAt(i);

            if (Character.isDigit(ch)) result[0] = true;
            if (Character.isUpperCase(ch)) result[1] = true;
            if (Character.isLowerCase(ch)) result[2] = true;
            if (symbols.indexOf(ch) >= 0) result[3] = true;
        }

        // Если хотя бы в одной переменной останется значение false,
        // то весь пароль не будет признана валидным = из метода вернется false
//        return  result[0] && result[1] && result[2] && result[3];
        if (!result[0]) throw new PasswordValidateException("should be a digit in password");
        if (!result[1]) throw new PasswordValidateException("should be a upper case letter in password");
        if (!result[2]) throw new PasswordValidateException("should be a lower case letter in password");
        if (!result[3]) throw new PasswordValidateException("should be a special symbol in password");

    }
}
