package service;

import model.File;
import model.User;
import repository.implementations.FileRepositoryImpl;
import repository.interfaces.FileRepository;

import java.util.List;

public class FileService {
    private final FileRepository fileRepository;
    private static FileService fileService;

    public FileService() {
        this.fileRepository = new FileRepositoryImpl();
    }

    public static FileService getFileService() {
        if(fileService == null)
            fileService = new FileService();
        return fileService;
    }

    public File getById(int fileId) {
        return fileRepository.getById(fileId).get();
    }

    public List<File> getByUserId(int userId) {
        return fileRepository.getByUserId(userId);
    }

    public File createFile(File file) {
        return fileRepository.createFile(file);
    }

    public File updateFile(File modifiedFile) {
        return fileRepository.updateFile(modifiedFile);
    }

    public void deleteFile(int fileId) {
        fileRepository.deleteFile(fileId);
    }

    public File getFileByUserAndFileName(User user, String fileName) {
        List<model.File> userFiles = fileRepository.getByUserId(user.getUserId());

        for (model.File file : userFiles) {
            String fullPath = file.getLocation() + "\\" + file.getName();
            if (file.getName().equals(fileName) || fullPath.equals(fileName)) {
                return file;
            }
        }
        return null;
    }

    public void deleteById(int fileId) {
        fileRepository.deleteById(fileId);
    }
}
