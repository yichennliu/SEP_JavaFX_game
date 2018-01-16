package main;

import model.enums.*;
import model.enums.Property;
import model.game.*;
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
                    feld = new Feld(Token.valueOf(((String) jsonFeld).toUpperCase()), colNum, rowNum);
                } else {
                    // token + properties
                    Token token = Token.valueOf(((JSONObject) jsonFeld).getString("token").toUpperCase());
                    Map<Property, Integer> values = new HashMap<>();
                    JSONObject jsonValues = ((JSONObject) jsonFeld).getJSONObject("values");
                    for (Iterator<String> i = jsonValues.keys(); i.hasNext();) {
                        String key = i.next();
                        int keyVal = jsonValues.getInt(key);
                        // TODO: check if property is a level-wide property and handle it differently
                        values.put(Property.valueOf(key.toUpperCase()), keyVal);
                    }
                    feld = new Feld(token, values, colNum, rowNum);
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

            // import sparsity
            Integer sparsity = jsonRule.has("sparsity") ? jsonRule.getInt("sparsity") : null;

            // import original rule elements
            List<RuleElementOriginal> original = new ArrayList<>();
            if (jsonRule.has("original")) {
                JSONArray jsonOriginal = jsonRule.getJSONArray("original");

                for (int ruleElementNum = 0; ruleElementNum < jsonOriginal.length(); ruleElementNum++) {
                    JSONObject jsonRuleElement = jsonOriginal.getJSONObject(ruleElementNum);

                    Object jsonTokenMatcher = jsonRuleElement.get("token");
                    TokenMatcher tokenMatcher = null;

                    if ("*".equals(jsonTokenMatcher)) {
                        // match any token
                        tokenMatcher = new TokenMatcher();
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
                    } else {
                        throw new JSONException("Wrong format for token matcher " + tokenMatcher);
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

                    original.add(new RuleElementOriginal(tokenMatcher, values));
                }
            }

            // import result rule elements
            List<RuleElementResult> result = new ArrayList<>();
            if (jsonRule.has("result")) {
                JSONArray jsonResult = jsonRule.getJSONArray("result");

                for (int ruleElementNum = 0; ruleElementNum < jsonResult.length(); ruleElementNum++) {
                    JSONObject jsonRuleElement = jsonResult.getJSONObject(ruleElementNum);

                    Object jsonTokenReplacer = jsonRuleElement.get("token");
                    TokenReplacer tokenReplacer = null;

                    if (jsonTokenReplacer instanceof Integer) {
                        // index number (as integer)
                        tokenReplacer = new TokenReplacer((Integer) jsonTokenReplacer);
                    } else if (jsonTokenReplacer instanceof String && ((String) jsonTokenReplacer).matches("-?\\d+")) {
                        // index number (as string)
                        tokenReplacer = new TokenReplacer(Integer.parseInt((String) jsonTokenReplacer));
                    } else if (jsonTokenReplacer instanceof String) {
                        // single token
                        Token token = Token.valueOf(((String) jsonTokenReplacer).toUpperCase());
                        tokenReplacer = new TokenReplacer(token);
                    } else {
                        throw new JSONException("Wrong format for token replacer " + tokenReplacer);
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

                    result.add(new RuleElementResult(tokenReplacer, values));
                }
            }

            levelRules.add(new Rule(situation, direction, original, result, sparsity));
        }

        return levelRules;
    }

}
