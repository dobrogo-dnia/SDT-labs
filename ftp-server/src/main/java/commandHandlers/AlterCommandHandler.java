package commandHandlers;

import model.User;
import myFtpServer.ftpServerStates.UserLoggedInServerState;
import myFtpServer.protocol.FtpResponse;
import service.UserService;
import java.io.IOException;

public class AlterCommandHandler extends BaseCommandHandler {
    private final UserService userService = UserService.getUserService();

    @Override
    protected boolean authorize(User user) {
        return user != null;
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        if(arguments.isEmpty() || arguments.isBlank())
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");

        String[] argumentsSplit = arguments.split(" ");
        if(argumentsSplit.length != 2)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");

        if(argumentsSplit[0].equals("-username"))
            return changeUsername(user, argumentsSplit[1]);
        else if(argumentsSplit[0].equals("-password"))
            return changePassword(user, argumentsSplit[1]);
        else
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: ALTER -username [[new username]] / ALTER -password [[new password]]");
    }

    private FtpResponse changeUsername(User user, String newUsername) {
        boolean usernameIsBusy = userService.getByUsername(newUsername) != null;
        if(usernameIsBusy)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Username is already occupied");

        UserLoggedInServerState.getUserCaretaker(user.getUserId()).saveNewMemento(user.saveToMemento());
        user.setUsername(newUsername);
        userService.updateUser(user);
        return new FtpResponse(200, "User successfully updated");
    }

    private FtpResponse changePassword(User user, String newPassword) {
        if (user == null || user.getPassword() == null) {
            return new FtpResponse(501, "User is not properly authenticated or password is invalid");
        }

        boolean passwordsTheSame = user.getPassword().equals(newPassword);
        if (passwordsTheSame) {
            return new FtpResponse(501, "Syntax error in parameters or arguments. Password is the same");
        }

        UserLoggedInServerState.getUserCaretaker(user.getUserId()).saveNewMemento(user.saveToMemento());
        user.setPassword(newPassword);
        userService.updateUser(user);

        return new FtpResponse(200, "User successfully updated");
    }
}
