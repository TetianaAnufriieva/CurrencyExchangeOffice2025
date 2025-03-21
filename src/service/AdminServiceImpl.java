package service;

import model.Role;
import model.User;
import repository.UserRepository;

public class AdminServiceImpl implements AdminService {
    private  UserRepository userRepository;

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void promoteToAdmin(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new RuntimeException("Пользователь с таким email не найден.");
        }
        if (user.getRole() == Role.ADMIN) {
            throw new RuntimeException("Пользователь уже является администратором.");
        }

        user.setRole(Role.ADMIN);


    }

    @Override
    public void blockUser(int userId) {

        User user = userRepository.findById(userId);
        if (user == null) {
            throw new RuntimeException("Пользователь с таким id не найден.");
        }
        if(user.getRole() == Role.BLOCKED) {
            throw new RuntimeException("Пользователь уже заблокирован.");
        }
        user.setRole(Role.BLOCKED);

    }

}
