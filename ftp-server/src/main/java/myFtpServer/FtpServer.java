package myFtpServer;

import controller.FtpServerController;
import logger.Logger;
import model.User;
import myFtpServer.ftpServerStates.FtpServerState;
import myFtpServer.ftpServerStates.NotLoggedInServerState;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FtpServer {
    private static final int PORT = 21;
    private static final int MAX_CONNECTION_NUM = 10;
    private final FtpServerController controller = new FtpServerController();
    private Socket clientSocket;
    private Logger logger;
    private User currentUser;
    private FtpServerState serverState;

    public FtpServer() throws IOException {
        logger = Logger.getLogger();
    }

    public void start() {
        try {
            ServerSocket comandServerSocket = new ServerSocket(PORT);
            System.out.println("FTP server started on port " + PORT);

            while (true) {
                clientSocket = comandServerSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                serverState = new NotLoggedInServerState(this);
                Thread clientThread = new Thread(() -> handleClient(clientSocket));
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
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

    public Socket getClientSocket() {
        return clientSocket;
    }

    public FtpServerController getController() {
        return controller;
    }

}