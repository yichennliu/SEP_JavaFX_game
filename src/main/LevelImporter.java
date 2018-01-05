package main;

import model.*;
import model.enums.*;
import model.enums.Property;
import org.json.*;
import java.io.*;
import java.util.*;

public class LevelImporter {

    /**
     * Imports a BoulderDash level
     *
     * @param jsonPath path to json file, relative or absolute
     * @return The imported level
     */
    public static Level importLevel(String jsonPath) {
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
        JSONObject jsonLevel = new JSONObject(new JSONTokener(is));

        // name
        String name = jsonLevel.getString("name");

        // import gems
        JSONArray jsonGems = jsonLevel.getJSONArray("gems");
        int[] gems = new int[jsonGems.length()];
        for (int i = 0; i < jsonGems.length(); i++) {
            gems[i] = jsonGems.getInt(i);
        }

        // import ticks
        JSONArray jsonTicks = jsonLevel.getJSONArray("ticks");
        int[] ticks = new int[jsonTicks.length()];
        for (int i = 0; i < jsonTicks.length(); i++) {
            ticks[i] = jsonTicks.getInt(i);
        }

        // import maxslime
        Integer maxslime = jsonLevel.has("maxslime") ? jsonLevel.getInt("maxslime") : null;

        // import map
        int width = jsonLevel.getInt("width");
        int height = jsonLevel.getInt("height");
        JSONArray rows = jsonLevel.getJSONArray("map");
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

        // import pre / post rules
        List<Rule> pre = jsonLevel.has("pre")
                ? importLevelRules(jsonLevel.getJSONArray("pre"))
                : new ArrayList<>();

        List<Rule> post = jsonLevel.has("post")
                ? importLevelRules(jsonLevel.getJSONArray("post"))
                : new ArrayList<>();

        // create level
        return new Level(name, map, gems, ticks, pre, post, maxslime);
    }

    /**
     * @param preOrPost List of (pre or post) level rules in JSON format
     * @return List of level rules in proper format
     */
    private static List<Rule> importLevelRules(JSONArray preOrPost) {
        List<Rule> levelRules = new ArrayList<>();

        for(int ruleNum = 0; ruleNum < preOrPost.length(); ruleNum++) {
            JSONObject jsonRule = preOrPost.getJSONObject(ruleNum);

            // import situation
            Situation situation = Situation.valueOf(jsonRule.getString("situation").toUpperCase());

            // import direction
            Direction direction = Direction.valueOf(jsonRule.getString("direction").toUpperCase());

            // import original rule elements
            List<RuleElement> original = jsonRule.has("original")
                    ? importRuleElements(jsonRule.getJSONArray("original"))
                    : new ArrayList<>();

            // import original rule elements
            List<RuleElement> result = jsonRule.has("result")
                    ? importRuleElements(jsonRule.getJSONArray("result"))
                    : new ArrayList<>();

            // import sparsity
            Integer sparsity = jsonRule.has("sparsity") ? jsonRule.getInt("sparsity") : null;

            levelRules.add(new Rule(situation, direction, original, result, sparsity));
        }

        return levelRules;
    }

    /**
     * @param originalOrResult List of (original or result) rule elements in json format
     * @return List of rule elements in proper format
     */
    private static List<RuleElement> importRuleElements(JSONArray originalOrResult) {
        List<RuleElement> ruleElements = new ArrayList<>();

        for (int ruleElementNum = 0; ruleElementNum < originalOrResult.length(); ruleElementNum++) {
            JSONObject jsonRuleElement = originalOrResult.getJSONObject(ruleElementNum);

            // import token matchers
            // FIXME: Should there be different TokenMatchers for original and result?

            Object jsonTokenMatcher = jsonRuleElement.get("token");
            TokenMatcher tokenMatcher = null;

            if ("*".equals(jsonTokenMatcher)) {
                // match any token
                tokenMatcher = new TokenMatcher();
            } else if (jsonTokenMatcher instanceof String && ((String) jsonTokenMatcher).matches("-?\\d+")) {
                // index number (as string)
                tokenMatcher = new TokenMatcher(Integer.parseInt((String) jsonTokenMatcher));
            } else if (jsonTokenMatcher instanceof String) {
                // single token
                Token token = Token.valueOf(((String) jsonTokenMatcher).toUpperCase());
                tokenMatcher = new TokenMatcher(Arrays.asList(token));
            } else if (jsonTokenMatcher instanceof JSONArray) {
                // list of tokens
                JSONArray jsonTokenArray = (JSONArray) jsonTokenMatcher;
                List<Token> tokens = new ArrayList<>();
                for (int tokenNum = 0; tokenNum < jsonTokenArray.length(); tokenNum++) {
                    Token token = Token.valueOf(jsonTokenArray.getString(tokenNum).toUpperCase());
                    tokens.add(token);
                }
                tokenMatcher = new TokenMatcher(tokens);
            } else if (jsonTokenMatcher instanceof Integer) {
                // index number (as integer)
                tokenMatcher = new TokenMatcher((Integer) jsonTokenMatcher);
            } else {
                throw new JSONException("Wrong format for token matcher "+tokenMatcher.toString());
            }


            // import values
            Map<Property, Integer> values = new HashMap<>();
            if (jsonRuleElement.has("values")) {
                JSONObject jsonValues = jsonRuleElement.getJSONObject("values");
                for (Iterator<String> i = jsonValues.keys(); i.hasNext(); ) {
                    String key = i.next();
                    int keyVal = jsonValues.getInt(key);
                    values.put(Property.valueOf(key.toUpperCase()), keyVal);
                }
            }

            ruleElements.add(new RuleElement(tokenMatcher, values));
        }

        return ruleElements;
    }
}
