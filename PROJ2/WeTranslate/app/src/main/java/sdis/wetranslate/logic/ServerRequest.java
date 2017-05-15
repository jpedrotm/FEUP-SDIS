package sdis.wetranslate.logic;


import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import sdis.wetranslate.utils.HttpConnection;
import sdis.wetranslate.utils.RequestMethod;

public class ServerRequest {
    public static String getRequests(String source, String target) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append("http://localhost:7000/getRequests?");
        builder.append("from="); builder.append(source);
        builder.append("&to="); builder.append(target);

        /* Connect to Load Balancer */
        HttpURLConnection connection = (HttpURLConnection) new URL(builder.toString()).openConnection();
        connection.setRequestMethod(RequestMethod.POST);

        String msg = HttpConnection.getMessage(connection);

        System.out.println(msg);

        return msg;
    }

    public static boolean verifyUserIsValid(String username,String password){
        return true;
    }

    public static boolean verifyUsernameAlreadyExists(String username){
        return false;
    }
}
