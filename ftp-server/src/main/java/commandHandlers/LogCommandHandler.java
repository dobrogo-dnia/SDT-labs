package commandHandlers;

import fileHandling.FileHandler;
import model.User;
import myFtpServer.protocol.FtpResponse;
import view.UI;

import java.io.IOException;
import java.util.List;

public class LogCommandHandler extends BaseCommandHandler {
    private final UI ui;

    public LogCommandHandler(UI ui) {
        this.ui = ui;
    }

    @Override
    protected boolean authorize(User user) {
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        List<String> fileContent = FileHandler.getLogFileContent();
        ui.displayLogFileContent(fileContent);
        return new FtpResponse(212, "Transfer complete. Log file content successfully displayed");
    }
}
