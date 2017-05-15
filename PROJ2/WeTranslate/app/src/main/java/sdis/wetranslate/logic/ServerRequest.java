package sdis.wetranslate.logic;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
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

    public static boolean verifyUserIsValid(String username,String password){
        return true;
    }

    public static boolean verifyUsernameAlreadyExists(String username){
        return false;
    }
}
