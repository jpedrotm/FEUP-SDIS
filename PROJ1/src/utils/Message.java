package utils;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Message {
    public static final int MAX_CHUNK_SIZE = 65507;

    private String message;
    private String header;
    private String body;

    public Message(DatagramPacket packet) {
        message = new String(packet.getData(), packet.getOffset(), packet.getLength(), StandardCharsets.US_ASCII);

        String[] tokens = message.split("(\\r\\n){2}");
        header = tokens[0];

        try {
            body = tokens[1];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            body = null;
        }
    }

    public Message(String header, String body) {
        this.header = header;
        this.body = body;
        this.message = this.header + " \r\n\r\n" + this.body;
    }

    public Message(String header){
        this.header=header;
        this.body="";
        this.message=this.header+ " \r\n\r\n" + this.body;
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
        return header.split("\\s");
    }

    public static String buildHeader(String messageType, String version, String senderId, String fileId, String chunkNo, String replicationDeg) {
        return messageType + " " + version + " " + senderId + " " + fileId + " " + chunkNo + " " + replicationDeg;
    }

    public static String buildHeader(String messageType, String version, String senderId, String fileId, String chunkNo){
        return messageType + " " + version + " " + senderId + " " + fileId + " " + chunkNo;
    }

    public static String buildHash(String fileId){
        StringBuffer hexString;
        MessageDigest hashAlgorithm=null;
        try {
            hashAlgorithm = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hash= new byte[0];
        try {
            hash = hashAlgorithm.digest(fileId.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        hexString=new StringBuffer();
        for (int j = 0; j < hash.length; j++) { //verificar fiabilidade e eficiências
            String hex = Integer.toHexString(0xff & hash[j]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
