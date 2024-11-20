package myFtpServer.ftpServerStates;

import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UserLoggedInServerState implements FtpServerState {
    private final Socket clientSocket;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;

    public UserLoggedInServerState(FtpServer ftpServer) {
        this.clientSocket = ftpServer.getClientSocket();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        switch (command) {
            case "RETR":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            case "DEL":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            case "TYPE":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            case "LIST":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            case "PWD":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            case "ACCT":
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":
                // TODO: реалізація пізніше з використанням хендлера
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return new FtpResponse(502, "Command not implemented");
    }
}