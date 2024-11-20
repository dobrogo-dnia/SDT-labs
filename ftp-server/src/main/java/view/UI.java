package view;

import model.User;
import myFtpServer.protocol.FtpResponse;

import java.io.*;
import java.util.List;

public class UI {
    private final BufferedReader reader;
    private final BufferedWriter writer;

    public UI(InputStream inputStream, OutputStream outputStream) {
        this.writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
    }

    public String acceptUserInput() throws IOException {
        return reader.readLine();
    }

    public void displayFtpResponse(FtpResponse ftpResponse) throws IOException {
        writer.write(ftpResponse.toString());
        writer.newLine();
        writer.flush();
    }

    public void closeStreams() throws IOException {
        writer.close();
        reader.close();
    }

}