package myFtpServer.protocol;

public class FtpResponse {
    private final int statusCode;
    private final String message;

    public FtpResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return statusCode + " " + message + ".";
    }
}
