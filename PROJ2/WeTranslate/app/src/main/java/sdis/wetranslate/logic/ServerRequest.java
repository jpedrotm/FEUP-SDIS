package sdis.wetranslate.logic;


import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import sdis.wetranslate.LoginActivity;
import sdis.wetranslate.exceptions.ServerRequestException;
import sdis.wetranslate.utils.HttpConnection;
import sdis.wetranslate.utils.RequestMethod;

public class ServerRequest {
    private static int REQUEST_TIMEOUT=1000;
    private static boolean redirect=false;

    public static JSONArray getRequests(String source, String target,Context context) throws IOException, ServerRequestException, JSONException {
        StringBuilder builder = new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/getRequests?");
        builder.append("from="); builder.append(source);
        builder.append("&to="); builder.append(target);

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpsURLConnection connection = (HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.GET);
        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if (HttpConnection.getCode(connection) != HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to get requests");

        String msg = HttpConnection.getMessage(connection);

        JSONArray jsonArray = new JSONArray(msg);
        System.out.println(jsonArray);

        return jsonArray;
    }

    public static JSONArray getRequestsByUsername(String username,Context context) throws IOException, ServerRequestException, JSONException {
        StringBuilder builder = new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/getRequestsByUsername?");
        builder.append("username="); builder.append(username);

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpsURLConnection connection = (HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.GET);
        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if (HttpConnection.getCode(connection) != HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to get requests.");

        String msg = HttpConnection.getMessage(connection);

        JSONArray jsonArray = new JSONArray(msg);
        System.out.println(jsonArray);

        return jsonArray;
    }

    public static JSONArray getTranslations(String requestId,Context context) throws IOException, ServerRequestException, JSONException {
        StringBuilder builder = new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https" +
                "" +
                "" +
                "://wetranslate.ddns.net:7000/getTranslations?");
        builder.append("requestid="); builder.append(requestId);

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpsURLConnection connection = (HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.GET);

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if (HttpConnection.getCode(connection) != HttpsURLConnection.HTTP_OK)
            return null;

        String msg = HttpConnection.getMessage(connection);

        JSONArray jsonArray = new JSONArray(msg);
        System.out.println(jsonArray);

        return jsonArray;
    }

    public static String loginUser(String username,String password,Context context) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        /* Connect to Load Balancer */
        builder.append("https://wetranslate.ddns.net:7000/login?");
        builder.append("username="); builder.append(username);
        builder.append("&password="); builder.append(password);

        System.out.println(builder.toString());
        
        HttpsURLConnection connection=(HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.POST);

        connection.setConnectTimeout(REQUEST_TIMEOUT); //necessário uma vez que o bloqueava sem o timeout

        System.out.println("Code: "+HttpConnection.getCode(connection));

        if(HttpConnection.getCode(connection)!=HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to login user.");

        String msg=HttpConnection.getMessage(connection);

        return msg;
    }

    public static boolean verifyUsernameAlreadyExists(String username,Context context) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/api/userExists?");
        builder.append("username="); builder.append(username);

        System.out.println(builder.toString());

        HttpsURLConnection connection=(HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.GET);

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        return HttpConnection.getCode(connection)==HttpsURLConnection.HTTP_OK;
    }

    public static void insertNewUser(String username,String password,Context context) throws IOException, ServerRequestException {
        StringBuilder builder=new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/insertUser?");
        builder.append("username="); builder.append(username);
        builder.append("&password="); builder.append(password);

        System.out.println(builder.toString());

        HttpsURLConnection connection=(HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.POST);

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if(HttpConnection.getCode(connection)!=HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to insert new user.");
    }

    public static void insertNewTranslation(String username,String translatedText,String requestId,Context context) throws IOException, ServerRequestException {
        System.out.println("Vou enviar a nova tradução.");
        StringBuilder builder=new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/insertTranslation?");
        builder.append("username="); builder.append(username);
        builder.append("&text="); builder.append(URLEncoder.encode(translatedText,"UTF-8"));
        builder.append("&requestid="); builder.append(requestId);

        System.out.println(builder.toString());

        HttpsURLConnection connection=(HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.POST);

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if(HttpConnection.getCode(connection)!=HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to insert new user translation.");
    }

    public static void insertNewRequest(String username,String from,String to,String text,Context context) throws IOException, ServerRequestException {
        StringBuilder builder = new StringBuilder();
        SSLContext sslContext=initializeSSlContext(context);

        builder.append("https://wetranslate.ddns.net:7000/insertRequest?");
        builder.append("username="); builder.append(username);
        builder.append("&from="); builder.append("pt");
        builder.append("&to="); builder.append("en");
        builder.append("&text="); builder.append(URLEncoder.encode(text,"UTF-8"));

        System.out.println(builder.toString());

        // Connect to Load Balancer
        HttpsURLConnection connection = (HttpsURLConnection) new URL(builder.toString()).openConnection();
        connection.setSSLSocketFactory(sslContext.getSocketFactory());
        trustEveryone(connection);
        connection.setRequestMethod(RequestMethod.POST);

        connection.setConnectTimeout(REQUEST_TIMEOUT);

        if (HttpConnection.getCode(connection) != HttpsURLConnection.HTTP_OK)
            throw new ServerRequestException("Failed to insert requests");
    }

    private static SSLContext initializeSSlContext(Context context){
        SSLContext sslContext=null;
        try {
            char[] password = "123456".toCharArray();
            InputStream fileDescriptor;
            InputStream fKeys = context.getAssets().open("client.bks");
            InputStream fStore= context.getAssets().open("truststore");
            System.out.println("key: "+fKeys);
            System.out.println("store: "+fStore);
            KeyStore keystore = KeyStore.getInstance("BKS");
            KeyStore truststore= KeyStore.getInstance("BKS");
            keystore.load(fKeys, password);
            truststore.load(fStore,password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(truststore);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            kmf.init(keystore, password);


            sslContext=SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }

    private static void trustEveryone(HttpsURLConnection connection) {
        try {
            connection.setHostnameVerifier(new HostnameVerifier(){
                public boolean verify(String hostname, SSLSession session) {
                    System.out.println("HOSTNAME: "+hostname);
                   if(redirect){
                       redirect=false;
                       return true;
                   }
                   else{
                       if(hostname.equals("wetranslate.ddns.net")){
                           redirect=true;
                           return true;
                       }
                       else
                           return false;
                   }
                }});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
