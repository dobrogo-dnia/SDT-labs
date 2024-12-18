package repository.interfaces;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Optional<User> getById(int id);
    User getByUsername(String username);
    List<User> getAllUsers();
    User createUser(User user);
    User updateUser(User modifiedUser);
    void deleteUser(int userId);
}
