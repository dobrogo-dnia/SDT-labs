package myFtpServer.protocol;

public class FtpRequest {
    private String command;
    private String arguments;

    public FtpRequest(String userInput) {
        if(userInput != null) {
            String[] commandParts = userInput.split(" ");
            this.command = commandParts[0].toUpperCase();
            this.arguments = commandParts.length > 1 ? userInput.substring(commandParts[0].length() + 1) : null;
        }
    }

    public String getCommand() {
        return command;
    }

    public String getArguments() {
        return arguments;
    }
}
