package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public class TypeCommandHandler extends BaseCommandHandler{
    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments.split(" ").length != 1)
            return new FtpResponse(504, "Command not implemented for that parameter");

        switch (arguments) {
            case "A":
                return new FtpResponse(200, "Type set to A");
            case "I":
                return new FtpResponse(200, "Type set to I");
            default:
                return new FtpResponse(504, "Command not implemented for that parameter");
        }
    }
}
