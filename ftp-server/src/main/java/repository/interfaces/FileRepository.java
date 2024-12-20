package repository.interfaces;

import model.File;

import java.util.List;
import java.util.Optional;

public interface FileRepository {
    Optional<File> getById(int fileId);
    List<File> getByUserId(int userId);
    File createFile(File file);
    File updateFile(File modifiedFile);
    void deleteFile(int fileId);
    File getByPath(String filePath);
    void deleteById(int fileId);
}
