package service;

import model.User;
import repository.implementations.UserRepositoryImpl;
import repository.interfaces.UserRepository;

import java.util.List;

public class UserService {
    UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepositoryImpl();
    }

    public User getById(int id) {
        return userRepository.getById(id).get();
    }

    public User getByUsername(String username) {
        return userRepository.getByUsername(username).get();
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

}