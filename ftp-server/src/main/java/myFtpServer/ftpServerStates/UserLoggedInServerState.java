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
import java.util.concurrent.ConcurrentHashMap;

public class UserLoggedInServerState implements FtpServerState {
    private final FtpServer ftpServer;
    private static final ConcurrentHashMap<Integer, UserCaretaker> userCaretakers = new ConcurrentHashMap<>();
    private final Socket clientSocket;
    private final StringBuilder currentDirectoryPath;
    private ServerSocket passiveDataServerSocket;
    private Socket activeDataSocket;
    private ServerMode serverMode;

    public UserLoggedInServerState(FtpServer ftpServer, String currentDirectoryPath) {
        this.currentDirectoryPath = new StringBuilder(currentDirectoryPath);
        this.clientSocket = ftpServer.getClientSocket();
        this.ftpServer = ftpServer;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler = null;
        switch (command) {
            case "PASV":
                if (passiveDataServerSocket != null && !passiveDataServerSocket.isClosed()) {
                    passiveDataServerSocket.close();
                }
                passiveDataServerSocket = new ServerSocket(0);
                serverMode = ServerMode.PASSIVE;
                commandHandler = new PasvCommandHandler(passiveDataServerSocket);
                break;
            case "EPSV":
                serverMode = ServerMode.PASSIVE;
                passiveDataServerSocket = new ServerSocket(0);
                commandHandler = new EpsvCommandHandler(passiveDataServerSocket);
                break;
            case "RETR":
                if (serverMode == null) {
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                }
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new RetrieveCommandHandler(activeDataSocket, currentDirectoryPath.toString(), ftpServer);
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new RetrieveCommandHandler(passiveDataServerSocket.accept(), currentDirectoryPath.toString(), ftpServer);
                break;
            case "STOR":
                if (serverMode == null) {
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                }
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new StorCommandHandler(activeDataSocket, currentDirectoryPath.toString(), ftpServer);
                else if (serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new StorCommandHandler(passiveDataServerSocket.accept(), currentDirectoryPath.toString(), ftpServer);
                break;
            case "DELE":
                commandHandler = new DeleCommandHandler(currentDirectoryPath.toString());
                break;
            case "TYPE":
                commandHandler = new TypeCommandHandler(ftpServer);
                break;
            case "LIST":
                if (serverMode == null) {
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
                }
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new ListCommandHandler(activeDataSocket, user.getHomeDirectory());
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new ListCommandHandler(passiveDataServerSocket.accept(), user.getHomeDirectory());
                break;
            case "PWD":
                commandHandler = new PwdCommandHandler(user.getHomeDirectory());
                break;
            case "CWD":
                commandHandler = new CwdCommandHandler(currentDirectoryPath);
                break;
            case "CDUP":
                return new FtpResponse(550, "Permission denied");
            case "ALTER":
                commandHandler = new AlterCommandHandler();
                break;
            case "RESTORE":
                commandHandler = new RestoreCommandHandler();
                break;
            case "ACCT":
                return new FtpResponse(230, "username: " + user.getUsername() + "; has admin rights: " + user.getIsAdmin());
            case "SYST":
                return new FtpResponse(215, "NAME " + System.getProperty("os.name") + " VERSION " + System.getProperty("os.version"));
            case "QUIT":
                commandHandler = new QuitCommandHandler();;
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return commandHandler.handleCommand(arguments, user);
    }

    public static UserCaretaker getUserCaretaker(int id) {
        return userCaretakers.computeIfAbsent(id, k -> new UserCaretaker());
    }
}
