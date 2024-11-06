package repository.interfaces;

import model.File;

import java.util.List;

public interface FileRepository {
    File getById(int fileId);
    List<File> getByUserId(int userId);
    File createFile(File file);
    File updateFile(File modifiedFile);
    void deleteById(int fileId);

}