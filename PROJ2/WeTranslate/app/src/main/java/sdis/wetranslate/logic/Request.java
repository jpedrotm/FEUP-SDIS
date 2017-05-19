package sdis.wetranslate.logic;

public class Request {
    private int id;
    private String content;
    private String source;
    private String target;
    private String username;

    public Request(int id,String content,String source,String target,String username){
        this.id=id;
        this.source=source;
        this.target=target;
        this.username=username;
    }

    public String getContent() {
        return content;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public String getUsername() {
        return username;
    }

    public int getId(){
        return id;
    }
}
