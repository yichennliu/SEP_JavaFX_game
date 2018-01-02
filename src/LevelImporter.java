import org.json.*;
import java.io.*;
import java.util.*;

public class LevelImporter {
    public static Level createLevel(String jsonPath) {
        /* load file with relative path*/
        InputStream is = ClassLoader.getSystemResourceAsStream(jsonPath);
        if(is==null) {
            /*if file not found try to load file with absolute path*/
            try {
                is = new FileInputStream(jsonPath);
            }
            catch(FileNotFoundException e) {
                throw new JSONException("File not found");
            }
        }
        JSONObject json = new JSONObject(new JSONTokener(is));

        // name
        String name = json.getString("name");

        // import gems
        JSONArray jsonGems = json.getJSONArray("gems");
        int[] gems = new int[jsonGems.length()];
        for (int i = 0; i < jsonGems.length(); i++) {
            gems[i] = jsonGems.getInt(i);
        }

        // import ticks
        JSONArray jsonTicks = json.getJSONArray("ticks");
        int[] ticks = new int[jsonTicks.length()];
        for (int i = 0; i < jsonTicks.length(); i++) {
            ticks[i] = jsonTicks.getInt(i);
        }

        // import maxslime
        Integer maxslime = json.has("maxslime") ? json.getInt("maxslime") : null;

        // import map
        int width = json.getInt("width");
        int height = json.getInt("height");
        JSONArray rows = json.getJSONArray("map");
        Feld[][] map = new Feld[height][width];
        for (int rowNum = 0; rowNum < rows.length(); rowNum++) {
            JSONArray row = rows.getJSONArray(rowNum);
            for (int colNum = 0; colNum < row.length(); colNum++) {
                Object jsonFeld = row.get(colNum);
                // import field
                Feld feld = null;
                if (jsonFeld instanceof String) {
                    // only token
                    feld = new Feld(Token.valueOf(((String) jsonFeld).toUpperCase()));
                } else {
                    // token + properties
                    Token token = Token.valueOf(((JSONObject) jsonFeld).getString("token").toUpperCase());
                    Map<Property, Integer> values = new HashMap<>();
                    JSONObject jsonValues = ((JSONObject) jsonFeld).getJSONObject("values");
                    for (Iterator<String> i = jsonValues.keys(); i.hasNext();) {
                        String key = i.next();
                        int keyVal = jsonValues.getInt(key);
                        values.put(Property.valueOf(key.toUpperCase()), keyVal);
                    }
                    feld = new Feld(token, values);
                }
                map[rowNum][colNum] = feld;
            }
        }

        // TODO: add import for pre / post rules
        List<Rule> pre = new ArrayList<>();
        List<Rule> post = new ArrayList<>();

        // create level
        return new Level(name, map, gems, ticks, pre, post, maxslime);
    }
}
