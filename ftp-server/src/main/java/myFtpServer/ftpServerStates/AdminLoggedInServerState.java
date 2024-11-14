package myFtpServer.ftpServerStates;

import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminLoggedInServerState implements FtpServerState {
    private final Socket clientSocket;
    private final StringBuilder currentDirectoryPath;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;

    public AdminLoggedInServerState(FtpServer ftpServer, String currentDirectoryPath) {
        this.currentDirectoryPath =  new StringBuilder(currentDirectoryPath);
        this.clientSocket = ftpServer.getClientSocket();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        switch (command) {
            case "RETR":

                break;
            case "STOR":

                break;
            case "DEL":

                break;
            case "CDUP":

                break;
            case "LIST":

                break;
            case "CWD":

                break;
            case "PWD":

                break;
            case "CREATE":

                break;
            case "USERS":

                break;
            case "LOG":

                break;
            case "ACCT":
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":

                break;

            default:
                return new FtpResponse(502, "Command not implemented");
        }

    }
}
