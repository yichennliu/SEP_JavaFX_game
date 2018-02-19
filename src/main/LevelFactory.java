package main;

import model.enums.*;
import model.enums.Property;
import model.game.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class LevelFactory {

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

        // import global properties (non-standard, for savegames)
        Map<Property, Integer> globalProperties = new HashMap<>();
        if (jsonLevel.has("values")) {
            JSONObject jsonValues = jsonLevel.getJSONObject("values");
            for (Iterator<String> i = jsonValues.keys(); i.hasNext();) {
                String key = i.next();
                int keyVal = jsonValues.getInt(key);
                globalProperties.put(Property.valueOf(key.toUpperCase()), keyVal);
            }
        }

        // preserve original level path if available
        if (jsonLevel.has("jsonPath")) {
            jsonPath = jsonLevel.getString("jsonPath");
        }

        // create level
        return new Level(name, map, gems, ticks, pre, post, maxslime, globalProperties, jsonPath);
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

    /**
     * Exports a boulder dash level
     *
     * @param level
     */
    public static void exportLevel(Level level, String destionationPath) throws IOException {
        JSONObject jsonLevel = new JSONObject();

        // add name
        jsonLevel.put("name", level.getName());

        // add height/width
        jsonLevel.put("height", level.getHeight());
        jsonLevel.put("width", level.getWidth());

        // add gem goals
        jsonLevel.put("gems", level.getGemGoals());

        // add tick goals
        jsonLevel.put("ticks", level.getTickGoals());

        // add max slime
        jsonLevel.putOpt("maxslime", level.getMaxslime());

        // add pre rules
        if (!level.getPre().isEmpty()) {
            jsonLevel.put("pre", LevelFactory.exportLevelRules(level.getPre()));
        }

        // add post rules
        if (!level.getPost().isEmpty()) {
            jsonLevel.put("post", LevelFactory.exportLevelRules(level.getPost()));
        }

        // add map
        JSONArray jsonMap = new JSONArray();
        for (Feld[] row : level.getMap()) {
            for (Feld feld : row) {
                Object jsonFeld = null;
                Token token = feld.getToken();
                Map<Property, Integer> properties = feld.getProperties();

                // Token + properties
                JSONObject values = new JSONObject();
                for (Map.Entry entry : properties.entrySet()) {
                    Property property = (Property) entry.getKey();
                    if (property == Property.DIRECTION || property == Property.A || property == Property.B
                            || property == Property.C || property == Property.D) {
                        values.put(property.toString().toLowerCase(), entry.getValue());
                    }
                }
                if (values.length() > 0) {
                    jsonFeld = new JSONObject();
                    ((JSONObject) jsonFeld).put("values", values);
                    ((JSONObject) jsonFeld).put("token", token.toString().toLowerCase());
                } else {
                    jsonFeld = token.toString().toLowerCase();
                }
                jsonMap.put(jsonFeld);
            }
        }
        jsonLevel.put("map", jsonMap);

        // add global properties in a field "values" (this is necessary for running games but non-standard!)
        JSONObject jsonValues = new JSONObject();
        for (Map.Entry entry : level.getProperties().entrySet()) {
            jsonValues.put(entry.getKey().toString().toLowerCase(), entry.getValue());
        }
        jsonLevel.put("values", jsonValues);

        // save original path
        jsonLevel.put("jsonPath", level.getJsonPath());

        // save level
        PrintWriter out = new PrintWriter(destionationPath);
        out.print(jsonLevel.toString(4));
        out.close();
    }

    /**
     * @param rules level rules (pre or post)
     * @return level rules in JSON format
     */
    private static JSONArray exportLevelRules(List<Rule> rules) {
        JSONArray jsonRules = new JSONArray();
        for (Rule rule : rules) {
            JSONObject jsonRule = new JSONObject();
            jsonRule.put("situation", rule.getSituation().toString().toLowerCase());
            jsonRule.put("direction", rule.getDirection().toString().toLowerCase());
            jsonRule.putOpt("sparsity", rule.getSparsity());

            // matching patterns
            JSONArray jsonOriginal = new JSONArray();
            for (RuleElementOriginal elementOriginal : rule.getOriginal()) {
                JSONObject jsonElementOriginal = new JSONObject();
                TokenMatcher tokenMatcher = elementOriginal.getToken();

                if (tokenMatcher.getTokens() == null || tokenMatcher.getTokens().size() == 0) {
                    jsonElementOriginal.put("token", "*");
                } else if (tokenMatcher.getTokens().size() == 1) {
                    jsonElementOriginal.put("token", tokenMatcher.getTokens().get(0).toString().toLowerCase());
                } else {
                    JSONArray tokens = new JSONArray();
                    for (Token token : tokenMatcher.getTokens()) {
                        tokens.put(token.toString().toLowerCase());
                    }
                    jsonElementOriginal.put("token", tokens);
                }

                if (elementOriginal.getValues() != null && !elementOriginal.getValues().isEmpty()) {
                    jsonElementOriginal.put("values", elementOriginal.getValues());
                }

                jsonOriginal.put(jsonElementOriginal);
            }
            jsonRule.put("original", jsonOriginal);

            // replacing patterns
            JSONArray jsonResult = new JSONArray();
            for (RuleElementResult elementResult : rule.getResult()) {
                JSONObject jsonElementResult = new JSONObject();
                TokenReplacer tokenReplacer = elementResult.getToken();

                if (tokenReplacer.getToken() != null) {
                    jsonElementResult.put("token", tokenReplacer.getToken().toString().toLowerCase());
                } else {
                    jsonElementResult.put("token", tokenReplacer.getIndex().toString());
                }

                if (elementResult.getValues() != null && !elementResult.getValues().isEmpty()) {
                    jsonElementResult.put("values", elementResult.getValues());
                }

                jsonResult.put(jsonElementResult);
            }
            jsonRule.put("result", jsonResult);

            jsonRules.put(jsonRule);
        }

        return jsonRules;
    }

}
