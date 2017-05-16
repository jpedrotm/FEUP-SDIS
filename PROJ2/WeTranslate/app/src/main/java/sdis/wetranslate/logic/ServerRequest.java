package sdis.wetranslate.logic;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import sdis.wetranslate.exceptions.ServerRequestException;
import sdis.wetranslate.utils.HttpConnection;
import sdis.wetranslate.utils.RequestMethod;

public class ServerRequest {
    public static JSONArray getRequests(String source, String target) throws IOException, ServerRequestException, JSONException {
        StringBuilder builder = new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/getRequests?");
        builder.append("from="); builder.append("pt");
        builder.append("&to="); builder.append("en");

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpURLConnection connection = (HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.GET);

        if (HttpConnection.getCode(connection) != HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to get requests");

        String msg = HttpConnection.getMessage(connection);

        JSONArray jsonArray = new JSONArray(msg);
        System.out.println(jsonArray);

        return jsonArray;
    }

    public static boolean verifyUserIsValid(String username,String password) throws IOException, ServerRequestException, JSONException {
        StringBuilder builder=new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/login?");
        builder.append("email="); builder.append(username);
        builder.append("&password="); builder.append(password);

        System.out.println(builder.toString());

        HttpURLConnection connection=(HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.POST);

        if(HttpConnection.getCode(connection)!=HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to login.");

        String msg=HttpConnection.getMessage(connection);

        JSONArray jsonArray=new JSONArray(msg);
        System.out.println(jsonArray);

        return true;
    }

    public static boolean verifyUsernameAlreadyExists(String username) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/api/userExists?");
        builder.append("email="); builder.append(username);

        System.out.println(builder.toString());

        HttpURLConnection connection=(HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.GET);

        if(HttpConnection.getCode(connection)!=HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to verify if user exists.");

        String msg=HttpConnection.getMessage(connection);

        return false;
    }

    public static void insertNewUser(String username,String password) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/insertUser?");
        builder.append("email="); builder.append(username);
        builder.append("&password="); builder.append(password);

        System.out.println(builder.toString());

        HttpURLConnection connection=(HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.POST);

        if(HttpConnection.getCode(connection)!=HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to insert new user.");

        String msg=HttpConnection.getMessage(connection);
    }
    public static void insertNewTranslation(String username,String translatedText,String requestId) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/insertUser?");
        builder.append("email="); builder.append(username);
        builder.append("&text="); builder.append(translatedText);
        builder.append("&requestid="); builder.append(requestId);

        System.out.println(builder.toString());

        HttpURLConnection connection=(HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.POST);

        if(HttpConnection.getCode(connection)!=HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to insert new user translation.");

        String msg=HttpConnection.getMessage(connection);
    }

    public static void insertNewRequest(String username,String from,String to,String text) throws IOException, ServerRequestException {
        StringBuilder builder = new StringBuilder();

        builder.append("http://wetranslate.ddns.net:7000/insertRequest?");
        builder.append("from="); builder.append(from);
        builder.append("&to="); builder.append(to);

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpURLConnection connection = (HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.POST);

        if (HttpConnection.getCode(connection) != HttpURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to get requests");

        String msg = HttpConnection.getMessage(connection);
    }
}
