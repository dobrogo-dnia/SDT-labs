package commandHandlers;

import model.User;
import myFtpServer.FtpServer;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public class TypeCommandHandler extends BaseCommandHandler {
    private final FtpServer ftpServer;

    public TypeCommandHandler(FtpServer ftpServer) {
        this.ftpServer = ftpServer;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if (arguments == null || arguments.isEmpty()) {
            return new FtpResponse(501, "Syntax error in parameters or arguments");
        }

        switch (arguments.toUpperCase()) {
            case "A":
                ftpServer.setTransferType("A");
                return new FtpResponse(200, "Type set to A (ASCII mode)");
            case "I":
                ftpServer.setTransferType("I");
                return new FtpResponse(200, "Type set to I (Binary mode)");
            default:
                return new FtpResponse(504, "Command not implemented for that parameter");
        }
    }
}
