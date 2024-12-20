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
import java.util.Arrays;

public class AdminLoggedInServerState implements FtpServerState {
    private final FtpServer ftpServer;
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
        this.ftpServer = ftpServer;
    }

    @Override
    public FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException {
        String command = ftpRequest.getCommand();
        String arguments = ftpRequest.getArguments();

        BaseCommandHandler commandHandler = null;
        switch (command) {
            case "SITE":
                String[] siteArgs = arguments.split(" ");
                if (siteArgs.length < 2) {
                    return new FtpResponse(501, "Syntax error in SITE command arguments");
                }
                String subCommand = siteArgs[0].toUpperCase();
                switch (subCommand) {
                    case "CHMOD":
                        if (siteArgs.length >= 3) {
                            String chmodArgs = String.join(" ", siteArgs[1], siteArgs[2]);
                            commandHandler = new SiteChmodCommandHandler();
                            return commandHandler.handleCommand(chmodArgs, user);
                        } else {
                            return new FtpResponse(501, "Syntax error in SITE CHMOD command arguments");
                        }
                    case "SPEED":
                        if (siteArgs.length >= 3) {
                            if (siteArgs[1].equalsIgnoreCase("GLOBAL")) {
                                commandHandler = new SiteSpeedGlobalCommandHandler(ftpServer);
                                return commandHandler.handleCommand(siteArgs[2], user);
                            } else if (siteArgs[1].equalsIgnoreCase("USER")) {
                                String userArgs = String.join(" ", Arrays.copyOfRange(siteArgs, 2, siteArgs.length));
                                commandHandler = new SiteSpeedUserCommandHandler(ftpServer);
                                return commandHandler.handleCommand(userArgs, user);
                            } else {
                                return new FtpResponse(501, "Invalid SITE SPEED command format");
                            }
                        } else {
                            return new FtpResponse(501, "Syntax error in SITE SPEED command arguments");
                        }
                    default:
                        return new FtpResponse(501, "Unknown SITE sub-command.");
                }
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
                if(serverMode.equals(ServerMode.ACTIVE))
                    commandHandler = new RetrieveCommandHandler(activeDataSocket, currentDirectoryPath.toString(), ftpServer);
                else if(serverMode.equals(ServerMode.PASSIVE))
                    commandHandler = new RetrieveCommandHandler(passiveDataServerSocket.accept(), currentDirectoryPath.toString(), ftpServer);
                else
                    return new FtpResponse(425, "Can't open data connection. Choose FTP server mode");
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
            case "CDUP":
                commandHandler = new CdupCommandHandler(currentDirectoryPath);
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
            case "CWD":
                commandHandler = new CwdCommandHandler(currentDirectoryPath);
                break;
            case "PWD":
                commandHandler = new PwdCommandHandler(currentDirectoryPath.toString());
                break;
            case "CREATE":
                commandHandler = new CreateCommandHandler();
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
                break;
            default:
                return new FtpResponse(502, "Command not implemented");
        }
        return commandHandler.handleCommand(arguments, user);
    }
}
