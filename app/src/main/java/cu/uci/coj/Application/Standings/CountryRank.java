package cu.uci.coj.Application.Standings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 2/28/16.
 */
public class CountryRank implements Serializable{

    private int rank;
    private int id;
    private String country_code;
    private String country_name;
    private int institution;
    private int users;
    private int ac;
    private double score;
    private String JSONString;

    public CountryRank(JSONObject jsonObject) throws JSONException {
        this.JSONString = jsonObject.toString();

        rank = jsonObject.getInt("rank");
        id = jsonObject.getInt("country_id");
        country_code = jsonObject.getString("country_code");
        country_name = jsonObject.getString("country_desc");
        institution = jsonObject.getInt("institutions");
        users = jsonObject.getInt("users");
        ac = jsonObject.getInt("acc");
        score = jsonObject.getDouble("points");
    }

    public String getRank() {
        return Integer.toString(rank);
    }

    public String getCountryCode() {
        return country_code;
    }

    public String getCountryName() {
        return country_name;
    }

    public String getInstitution() {
        return Integer.toString(institution);
    }

    public String getUsers(){
        return Integer.toString(users);
    }

    public String getAc() {
        return Integer.toString(ac);
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

    public int getId() {
        return id;
    }
}
