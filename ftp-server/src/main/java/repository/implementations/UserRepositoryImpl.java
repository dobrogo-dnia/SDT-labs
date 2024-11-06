package repository.implementations;

import jakarta.persistence.*;
import model.User;
import repository.interfaces.UserRepository;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public UserRepositoryImpl(){
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public Optional<User> getById(int userId) {
        User user = entityManager.find(User.class, userId);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> getByUsername(String username) {
        User user = entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .orElse(null);

        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getAllUsers() {
        String queryText = "SELECT u FROM User u";
        TypedQuery<User> query = entityManager.createQuery(queryText, User.class);
        return query.getResultList();
    }

    @Override
    public User createUser(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(user);
        transaction.commit();
        return user;
    }

    @Override
    public User updateUser(User modifiedUser) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        User updatedUser = entityManager.merge(modifiedUser);
        transaction.commit();
        return updatedUser;
    }

}