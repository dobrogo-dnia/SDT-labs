package myFtpServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            FtpServer server = new FtpServer();
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}