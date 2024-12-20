package commandHandlers;

import model.User;
import myFtpServer.protocol.FtpResponse;
import service.UserService;
import visitor.CreateVisitor;

import java.io.IOException;

public class CreateCommandHandler extends BaseCommandHandler {
    private final UserService userService = UserService.getUserService();

    @Override
    protected boolean authorize(User user) {
        return user != null && user.getIsAdmin();
    }

    @Override
    protected FtpResponse executeCommand(String arguments, User user) throws IOException {
        String[] argumentsSplit = arguments.split(" ");
        if(argumentsSplit.length != 3)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Try like this: CREATE [[username, password, isAdmin]]");

        String username = argumentsSplit[0];
        String password = argumentsSplit[1];
        Boolean isAdmin = argumentsSplit[2].equalsIgnoreCase("true") ? Boolean.TRUE : argumentsSplit[2].equalsIgnoreCase("false") ? Boolean.FALSE : null;

        if(isAdmin == null)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Argument 3 must be true or false");

        boolean usernameIsBusy = userService.getByUsername(username) != null;
        if(usernameIsBusy)
            return new FtpResponse(501, "Syntax error in parameters or arguments. Username is already occupied");

        User newUser = new User (username, password, isAdmin);
        newUser.accept(new CreateVisitor());
        return new FtpResponse(230, "User successfully created");
    }
}
