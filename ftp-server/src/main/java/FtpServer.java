import controller.FtpServerController;
import model.File;
import model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpServer {
    private static final int PORT = 21;
    private FtpServerController ftpServerController;
    private User currentUser;

    public FtpServer() {
        ftpServerController = new FtpServerController();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("FTP server started on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
            String command;
            while ((command = reader.readLine()) != null) {
                System.out.println("Received command: " + command);
                String[] tokens = command.split(" ");
                String cmd = tokens[0].toUpperCase();
                switch (cmd) {
                    case "USER":
                        handleUserCommand(tokens, writer);
                        break;
                    case "PASS":
                        handlePassCommand(tokens, writer);
                        break;
                    case "LIST":
                        handleListCommand(writer);
                        break;
                    case "RETR":
                        handleRetrCommand(tokens, writer);
                        break;
                    case "STOR":
                        handleStorCommand(tokens, writer);
                        break;
                    case "QUIT":
                        handleQuitCommand(writer);
                        return;
                    default:
                        writer.println("500 Unknown command.");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handleUserCommand(String[] tokens, PrintWriter writer) {
        if (tokens.length < 2) {
            writer.println("501 Syntax error in parameters or arguments.");
            return;
        }
        if (currentUser != null) {
            writer.println("331 User name okay, need password.");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    public void handlePassCommand(String[] tokens, PrintWriter writer) {
        if (tokens.length < 2) {
            writer.println("501 Syntax error in parameters or arguments.");
            return;
        }
        String password = tokens[1];
        if (currentUser == null) {
            writer.println("503 Bad sequence of commands. Use USER command first.");
            return;
        }
        if (currentUser.getPassword().equals(password)) {
            writer.println("230 User logged in, proceed.");
        } else {
            writer.println("530 Not logged in.");
        }
    }

    private void handleListCommand(PrintWriter writer) {
        File[] files = new File("/path/to/directory").listFiles();
        if (files != null) {
            for (File file : files) {
                writer.println(file.getName());
            }
        } else {
            writer.println("550 Failed to list files.");
        }
    }

    private void handleRetrCommand(String[] tokens, PrintWriter writer) {
        if (tokens.length < 2) {
            writer.println("501 Syntax error in parameters or arguments.");
            return;
        }
        String fileName = tokens[1];
        File file = new File("/path/to/directory/" + fileName);
        if (file.exists() && file.isFile()) {
            writer.println("150 Opening data connection.");
            // логіка відправки файлу на клієнт
        } else {
            writer.println("550 File not found.");
        }
    }

    private void handleStorCommand(String[] tokens, PrintWriter writer) {
        if (tokens.length < 2) {
            writer.println("501 Syntax error in parameters or arguments.");
            return;
        }
        String fileName = tokens[1];
        // логіка завантаження
        writer.println("150 OK to send data.");
    }

    private void handleQuitCommand(PrintWriter writer) {
        writer.println("221 Goodbye.");
    }

}