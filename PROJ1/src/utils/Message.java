package utils;

import java.net.DatagramPacket;


public class Message {
    public static final int MAX_CHUNK_SIZE = 64 * 1024;

    private String message;
    private String header;
    private String body;
    private String[] headerFields;

    public Message(DatagramPacket packet) {
        message = new String(packet.getData(), packet.getOffset(), packet.getLength());

        String[] tokens = message.split("[\\r\\n]+");
        header = tokens[0];
        body = tokens[1];
        headerFields = tokens[0].split("\\s");
    }

    public String getMessage() {
        return message;
    }

    public String getHeader() {
        return header;
    }

    public String getBody() {
        return body;
    }

    public String[] getHeaderFields() {
        return headerFields;
    }
}
