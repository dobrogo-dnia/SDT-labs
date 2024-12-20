package myFtpServer;

import controller.FtpServerController;
import logger.Logger;
import model.User;
import myFtpServer.ftpServerStates.FtpServerState;
import myFtpServer.ftpServerStates.NotLoggedInServerState;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpServer {
    private static final int PORT = 21;
    private static final int MAX_CONNECTION_NUM = 5;
    private final FtpServerController controller = new FtpServerController();
    private Socket clientSocket;
    private UI ui;
    private final Logger logger;
    private User currentUser;
    private FtpServerState serverState;

    public FtpServer() throws IOException {
        logger = Logger.getLogger();
    }

    public void start() {
        try (ServerSocket commandServerSocket = new ServerSocket(PORT)) {
            System.out.println("FTP server started on port " + PORT);

            while (true) {
                try {
                    clientSocket = commandServerSocket.accept();
                    clientSocket.setSoTimeout(300000);
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                    ui = new UI(clientSocket.getInputStream(), clientSocket.getOutputStream());
                    serverState = new NotLoggedInServerState(this);

                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting FTP server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            if (!controller.newUserCanConnect(MAX_CONNECTION_NUM)) {
                ui.displayFtpResponse(new FtpResponse(421, "Connection limit reached. Please try again later."));
                Thread.sleep(30000);
                return;
            }

            ui.displayFtpResponse(new FtpResponse(220, "Successfully connected"));

            currentUser = new User();
            while (true) {
                String userInput = ui.acceptUserInput();
                if (userInput == null) {
                    System.out.println("Client disconnected.");
                    break;
                }
                if (userInput.isEmpty()) {
                    continue;
                }

                System.out.println("Received input: " + userInput);

                FtpRequest ftpRequest = new FtpRequest(userInput);
                FtpResponse ftpResponse = handleCommands(currentUser, ftpRequest);

                System.out.println("Sending response: " + ftpResponse.toString());
                ui.displayFtpResponse(ftpResponse);

                if (ftpResponse.getStatusCode() == 221) {
                    System.out.println("221 Service closing control connection.");
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("I/O Error handling client: " + e.getMessage());
            logger.writeErrorEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername(), e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Thread interrupted: " + e.getMessage());
        } finally {
            cleanUpResources();
        }
    }

    private void cleanUpResources() {
        try {
            if (currentUser != null) {
                controller.deactivateSessionIfActive(currentUser.getUserId());
                logger.writeLogOutEventToFile(clientSocket.getInetAddress().getHostAddress(), currentUser.getUsername());
            }

            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
                System.out.println("Client socket closed.");
            }

            if (ui != null) {
                ui.closeStreams();
                System.out.println("UI streams closed.");
            }
        } catch (IOException e) {
            System.err.println("Error cleaning up resources: " + e.getMessage());
        }
    }

    public FtpResponse handleCommands(User currentUser, FtpRequest ftpRequest) throws IOException {
        return serverState.handleCommand(currentUser, ftpRequest);
    }

    public void setState(FtpServerState ftpServerState) {
        this.serverState = ftpServerState;
    }

    public void setUser(User loggedInUser) {
        currentUser.setUserId(loggedInUser.getUserId());
        currentUser.setUsername(loggedInUser.getUsername());
        currentUser.setPassword(loggedInUser.getPassword());
        currentUser.setHomeDirectory(loggedInUser.getHomeDirectory());
        currentUser.setAdmin(loggedInUser.getIsAdmin());
    }

    private String transferType = "I";

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public FtpServerController getController() {
        return controller;
    }

    public UI getUi() {
        return ui;
    }
}
