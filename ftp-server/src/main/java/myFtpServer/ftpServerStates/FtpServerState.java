package myFtpServer.ftpServerStates;

import model.User;
import myFtpServer.protocol.FtpRequest;
import myFtpServer.protocol.FtpResponse;

import java.io.IOException;

public interface FtpServerState {
    FtpResponse handleCommand(User user, FtpRequest ftpRequest) throws IOException;
}