package cu.uci.coj.Application.Judgments;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 3/8/16.
 */
public class Judgment implements Serializable{

    private int id;
    private String date;
    private String user;
    private int problem_id;
    private String judgment;
    private int test_case;
    private int time;
    private String memory;
    private String size;
    private String language;
    private String JSONString;

    public Judgment(JSONObject jsonObject) throws JSONException {
        JSONString = jsonObject.toString();

        id = jsonObject.getInt("id");
        date = jsonObject.getString("date");
        user = jsonObject.getString("user");
        problem_id = jsonObject.getInt("prob");
        judgment = jsonObject.getString("judgment");
        test_case = jsonObject.getInt("errortestcase");
        time = jsonObject.getInt("time");
        memory = jsonObject.getString("memory");
        size = jsonObject.getString("tam");
        language = jsonObject.getString("lang");

    }

    public Judgment(int id, String date, String user, int problem_id, String judgment,
                    int test_case, int time, String memory, String size, String language) {
        this.id = id;
        this.date = date;
        this.user = user;
        this.problem_id = problem_id;
        this.judgment = judgment;
        this.test_case = test_case;
        this.time = time;
        this.memory = memory;
        this.size = size;
        this.language = language;
    }

    public String getId() {
        return Integer.toString(id);
    }

    public String getDate() {
        String aux = date.replace(".", "@");
        return aux.split("@")[0];
    }

    public String getUser() {
        return user;
    }

    public String getProblem_id() {
        return Integer.toString(problem_id);
    }

    public String getJudgment() {
        return judgment;
    }

    public String getTest_case() {
        return Integer.toString(test_case);
    }

    public String getTime() {
        return Integer.toString(time);
    }

    public String getMemory() {
        return memory;
    }

    public String getSize() {
        return size;
    }

    public String getLanguage() {
        return language;
    }

    public long getLongId(){
        return (long)id;
    }

    public String getJSONString() {
        return JSONString;
    }

    public int getIntTest_case(){
        return test_case;
    }
}
