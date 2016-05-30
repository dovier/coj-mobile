package cu.uci.coj.Application.Extras;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 4/11/16.
 */
public class FaqItem implements Serializable{

    private String question;
    private String answer;
    private String jsonString;

    public FaqItem(String jsonString) throws JSONException {
        this.jsonString = jsonString;
        JSONObject jsonObject = new JSONObject(jsonString);
        question = jsonObject.getString("question");
        answer = jsonObject.getString("answer");
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getJsonString() {
        return jsonString;
    }
}
