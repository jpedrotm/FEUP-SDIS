package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Message {
    public static final int MAX_CHUNK_SIZE = 65507;

    private byte[] message;
    private String header;
    private byte[] body;

    public Message(DatagramPacket packet) {
        message = Arrays.copyOfRange(packet.getData(), packet.getOffset(), packet.getLength());

        String messageString = new String(packet.getData(), packet.getOffset(), packet.getLength());
        String[] tokens = messageString.split("( \\r\\n\\r\\n)", 2);

        header = new String(tokens[0].getBytes(), StandardCharsets.US_ASCII);

        try {
            body = Arrays.copyOfRange(message, header.length() + 5, message.length);
        }
        catch (ArrayIndexOutOfBoundsException e) {
            body = null;
        }
    }

    public Message(String header, byte[] body) throws IOException {
        this.header = header;
        this.body = body;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header.getBytes(StandardCharsets.US_ASCII));
        baos.write(" \r\n\r\n".getBytes());
        baos.write(body);

        this.message = baos.toByteArray();
    }

    public Message(String header) throws IOException {
        this.header = header;
        this.body = null;


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(header.getBytes(StandardCharsets.US_ASCII));
        baos.write(" \r\n\r\n".getBytes());

        this.message = baos.toByteArray();
    }

    public byte[] getMessage() {
        return message;
    }

    public String getHeader() {
        return header;
    }

    public byte[] getBody() {
        return body;
    }

    public String[] getHeaderFields() {
        return header.split("\\s");
    }

    public static String buildHeader(String messageType, String version, String senderId, String fileId, String chunkNo, String replicationDeg) {
        return messageType + " " + version + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDeg;
    }

    public static String buildHeader(String messageType, String version, String senderId, String fileId, String chunkNo){
        return messageType + " " + version + " " + senderId + " " + fileId + " " + chunkNo;
    }

    public static String buildHeader(String messageType, String version, String senderId, String fileId){
        return messageType + " " + version + " " + senderId + " " + fileId;
    }

    public static String buildHash(String fileId) {
        StringBuffer hexString;
        MessageDigest hashAlgorithm=null;
        try {
            hashAlgorithm = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error: " + e.getMessage());
        }
        byte[] hash= new byte[0];
        try {
            hash = hashAlgorithm.digest(fileId.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error: " + e.getMessage());
        }
        hexString=new StringBuffer();
        for (int j = 0; j < hash.length; j++) { //verificar fiabilidade e eficiências
            String hex = Integer.toHexString(0xff & hash[j]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }



    public static class FieldIndex {
        public static final int MessageType = 0;
        public static final int Version = 1;
        public static final int SenderId = 2;
        public static final int FileId = 3;
        public static final int ChunkNo = 4;
        public static final int ReplicationDeg = 5;
    }
}
