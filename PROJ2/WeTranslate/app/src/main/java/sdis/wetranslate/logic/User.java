package sdis.wetranslate.logic;

public class User {

    private static User instance=null;

    private String username=null;

    protected User(String username){
        this.username=username;
    }

    public static User getInstance(){
        return instance;
    }

    public static void initSession(String username){
        instance=new User(username);
    }

    public String getUsername(){
        return username;
    }
}