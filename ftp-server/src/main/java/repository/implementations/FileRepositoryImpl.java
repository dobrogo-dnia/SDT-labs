package repository.implementations;

import jakarta.persistence.*;
import model.File;
import repository.interfaces.FileRepository;

import java.util.List;

public class FileRepositoryImpl implements FileRepository {
    @PersistenceContext
    private final EntityManager entityManager;

    public FileRepositoryImpl() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("myPersistenceUnit");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public File getById(int fileId) {
        File file = entityManager.find(File.class, fileId);
        return file;
    }

    @Override
    public List<File> getByUserId(int userId) {
        TypedQuery<File> query = entityManager.createQuery(
                "SELECT f FROM File WHERE f.owner.userId = :userId", File.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }

    @Override
    public File createFile(File file) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        entityManager.persist(file);
        transaction.commit();
        return file;
    }

    @Override
    public File updateFile(File modifiedFile) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        File updatedFile = entityManager.merge(modifiedFile);
        transaction.commit();
        return updatedFile;
    }

    @Override
    public void deleteById(int fileId) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        File file = entityManager.find(File.class, fileId);
        if (file != null) {
            entityManager.remove(file);
            transaction.commit();
        }
    }

}