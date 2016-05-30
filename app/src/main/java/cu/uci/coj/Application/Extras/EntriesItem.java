package cu.uci.coj.Application.Extras;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 4/8/16.
 */
public class EntriesItem implements Serializable {

    private String avatar;
    private String user_name;
    private String submit_date;
    private String content;
    private int rate;
    private String JSONString;

    public EntriesItem(JSONObject JSONObject) throws JSONException {
        JSONString = JSONObject.toString();
        avatar = JSONObject.getString("avatar");
        user_name = JSONObject.getString("user");
        submit_date = JSONObject.getString("date");
        content = JSONObject.getString("content");
        rate = JSONObject.getInt("rate");
    }

    public EntriesItem(String avatar, String user_name, String submit_date, String content, int rate) {
        this.avatar = avatar;
        this.user_name = user_name;
        this.submit_date = submit_date;
        this.content = content;
        this.rate = rate;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getSubmit_date() {
        return submit_date.substring(0, 16);
    }

    public String getContent() {
        return content;
    }

    public String getRate() {
        return Integer.toString(rate);
    }

    public String getJSONString() {
        return JSONString;
    }

    public JSONObject getJSONEntries(){

        try {
            return new JSONObject(JSONString);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
