package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;
import java.net.ServerSocket;

public class PasvCommandHandler extends BaseCommandHandler {
    private final ServerSocket passiveSocket;

    public PasvCommandHandler(ServerSocket passiveSocket) {
        this.passiveSocket = passiveSocket;
        try {
            this.passiveSocket.setSoTimeout(300000);
        } catch (IOException e) {
            System.err.println("Error setting timeout for ServerSocket: " + e.getMessage());
        }
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if (passiveSocket == null || passiveSocket.isClosed()) {
            return new FtpResponse(425, "Cannot enter passive mode. Data connection unavailable");
        }

        String ip = "127,0,0,1";

        int port = passiveSocket.getLocalPort();
        int p1 = port / 256;
        int p2 = port % 256;

        System.out.println("PASV Command issued. Server IP: " + ip + ", Port: " + port);

        return new FtpResponse(227, "Entering Passive Mode (" + ip + "," + p1 + "," + p2 + ")");
    }
}
