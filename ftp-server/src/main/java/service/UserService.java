package service;

import model.User;
import repository.implementations.UserRepositoryImpl;
import repository.interfaces.UserRepository;

import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private static UserService userService;

    private UserService() {
        this.userRepository = new UserRepositoryImpl();
    }

    public static UserService getUserService() {
        if(userService == null)
            userService = new UserService();
        return userService;
    }

    public User getById(int id) {
        return userRepository.getById(id).get();
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username).orElse(null);
    }


    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(User modifiedUser) {
        return userRepository.updateUser(modifiedUser);
    }

    public void deleteById(int userId) {
        userRepository.deleteUser(userId);

    }

}