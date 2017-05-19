package sdis.wetranslate.logic;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class Translation {
    public static final String Portugues="Português";
    public static final String Ingles="Inglês";
    public static final String Alemao="Alemão";
    public static final String Frances="Francês";
    private static final ArrayList<String> languagesList;

    static {
        languagesList = new ArrayList<>();languagesList.add(Portugues);
        languagesList.add(Ingles);
        languagesList.add(Alemao);
        languagesList.add(Frances);
    }

    private int id;
    private String translatedText;
    private String requestId;
    private String username;

    public Translation(int id, String translatedText, String requestId, String username) {
        this.id = id;
        this.translatedText = translatedText;
        this.requestId = requestId;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getUsername() {
        return username;
    }

    public static String getLanguage(String language){
        String lang=null;
        switch(language){
            case Portugues:
                lang="pt";
                break;
            case Ingles:
                lang="en";
                break;
            case Alemao:
                lang="gr";
                break;
            case Frances:
                lang="fr";
                break;
            default:
                break;
        }

        return lang;
    }

    public static ArrayList<String> getLanguagesList(){
        return languagesList;
    }
}
