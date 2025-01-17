package myFtpServer.ftpServerStates;

import commandHandlers.*;
import enums.ServerMode;
import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import userMemento.UserCaretaker;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class UserLoggedInServerState implements FtpServerState {
    private final Socket clientSocket;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;
    private ServerMode serverMode;

    public UserLoggedInServerState(FtpServer ftpServer) {
        this.clientSocket = ftpServer.getClientSocket();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler;
        switch (command) {
            case "RETR":
                if (serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new RetrieveCommandHandler(activeDataSocket, user.getHomeDirectory());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new RetrieveCommandHandler(passiveDataServerSocket.accept(), user.getHomeDirectory());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "STOR":
                // зробити із 8 лр
                break;
            case "DELE":
                // зробити із 8 лр
                break;
            case "TYPE":
                commandHandler = new TypeCommandHandler();
                break;
            case "LIST":
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new ListCommandHandler(activeDataSocket, user.getHomeDirectory());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new ListCommandHandler(passiveDataServerSocket.accept(), user.getHomeDirectory());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "PWD":
                commandHandler = new PwdCommandHandler(user.getHomeDirectory());
                break;
            case "ALTER":
                commandHandler = new AlterCommandHandler();
                break;
            case "RESTORE":
                commandHandler = new RestoreCommandHandler();
            case "ACCT":
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":
                commandHandler = new QuitCommandHandler();
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return new FtpResponse(502, "Command not implemented");
    }
}