package sdis.wetranslate.logic;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PagerFeeder {
    private static JSONArray array = null;

    public static void feed(JSONArray jsonArray) {
        array = jsonArray;
    }

    public static int count() {
        return array == null ? 1 : array.length();
    }

    public static JSONObject get(int index) {
        if (array == null)
            return null;

        try {
            return array.getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }
}