package cu.uci.coj.Application.Standings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 2/28/16.
 */
public class UserRank implements Serializable{

    private int rank;
    private String country;
    private String user;
    private int sub;
    private int ac;
    private double ac_percent;
    private double score;
    private String JSONString;

    public UserRank(JSONObject jsonObject) throws JSONException {
        this.JSONString = jsonObject.toString();

        rank = jsonObject.getInt("rank");
        country = jsonObject.getString("country_code");
        user = jsonObject.getString("user");
        sub = jsonObject.getInt("sub");
        ac = jsonObject.getInt("ac");
        ac_percent = jsonObject.getDouble("acporciento");
        score = jsonObject.getDouble("score");
    }

    public UserRank(int rank, String country, String user, int sub, int ac, double ac_percent, double score) {
        this.rank = rank;
        this.country = country;
        this.user = user;
        this.sub = sub;
        this.ac = ac;
        this.ac_percent = ac_percent;
        this.score = score;
    }

    public String getRank() {
        return Integer.toString(rank);
    }

    public String getCountry() {
        return country;
    }

    public String getUser() {
        return user;
    }

    public String getSub() {
        return Integer.toString(sub);
    }

    public String getAc() {
        return Integer.toString(ac);
    }

    public String getAc_percent() {
        return String.format("%.2f", ac_percent);
    }

    public String getScore() {
        return String.format("%.2f", score);
    }

    public int getIntRank(){
        return rank;
    }

    public String getJSONString() {
        return JSONString;
    }
}
