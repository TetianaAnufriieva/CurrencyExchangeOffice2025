package service;

import model.Role;
import model.User;
import repository.AccountRepository;
import repository.UserRepository;

public class AdminServiceImpl implements AdminService {


    @Override
    public void promoteToAdmin(String userId) {

        User user = UserRepository.findById(Integer.parseInt(userId));
        if (user != null && user.getRole()  != Role.ADMIN) {
            user.setRole(Role.ADMIN);
        }

    }

    @Override
    public void blockUser(int userId) {

        User user = UserRepository.findById(userId);
        if (user != null && user.getRole() != Role.BLOCKED) {
            user.setRole(Role.BLOCKED);
        }

    }

}
