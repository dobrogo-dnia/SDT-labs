package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;

public class EpsvCommandHandler extends BaseCommandHandler{
    private final ServerSocket dataServerSocket;

    public EpsvCommandHandler(ServerSocket dataServerSocket) {
        this.dataServerSocket = dataServerSocket;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        int passivePort = dataServerSocket.getLocalPort();
        return new FtpResponse(229, "Entering Extended Passive Mode (|||" + passivePort + "|)");
    }
}
