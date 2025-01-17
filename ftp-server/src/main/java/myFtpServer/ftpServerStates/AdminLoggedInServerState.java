package myFtpServer.ftpServerStates;

import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;
import view.UI;
import enums.ServerMode;
import commandHandlers.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AdminLoggedInServerState implements FtpServerState {
    private final UI ui;
    private final Socket clientSocket;
    private final StringBuilder currentDirectoryPath;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;
    private ServerMode serverMode;

    public AdminLoggedInServerState(FtpServer ftpServer, String currentDirectoryPath) {
        this.currentDirectoryPath = new StringBuilder(currentDirectoryPath);
        this.clientSocket = ftpServer.getClientSocket();
        this.ui = ftpServer.getUi();
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler;
        switch (command) {
            case "RETR":
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new RetrieveCommandHandler(activeDataSocket, currentDirectoryPath.toString());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new RetrieveCommandHandler(passiveDataServerSocket.accept(), currentDirectoryPath.toString());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "STOR":
                // зробити одразу із 8 лр
                break;
            case "DELE":
                // зробити одразу із 8 лр
                break;
            case "TYPE":
                commandHandler = new TypeCommandHandler();
                break;
            case "CDUP":
                commandHandler = new CdupCommandHandler(currentDirectoryPath);
                break;
            case "LIST":
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new ListCommandHandler(activeDataSocket, currentDirectoryPath.toString());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new ListCommandHandler(passiveDataServerSocket.accept(), currentDirectoryPath.toString());
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                break;
            case "CWD":
                commandHandler = new CwdCommandHandler(currentDirectoryPath);
                break;
            case "PWD":
                commandHandler = new PwdCommandHandler(currentDirectoryPath.toString());
                break;
            case "CREATE":
                // зробити разом із 8 лр
                break;
            case "USERS":
                commandHandler = new UsersCommandHandler(ui);
                break;
            case "LOG":
                commandHandler = new LogCommandHandler(ui);
                break;
            case "ACCT":
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":
                commandHandler = new QuitCommandHandler();
                passiveDataServerSocket.close();
                clientSocket.close();
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return new FtpResponse(502, "Command not implemented");
    }
}
