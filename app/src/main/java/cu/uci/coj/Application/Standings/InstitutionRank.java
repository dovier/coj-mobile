package cu.uci.coj.Application.Standings;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 2/28/16.
 */
public class InstitutionRank implements Serializable{

    private int rank;
    private int id;
    private String country;
    private String institution;
    private int users;
    private int ac;
    private double score;
    private String JSONString;

    public InstitutionRank(JSONObject jsonObject) throws JSONException {
        this.JSONString = jsonObject.toString();

        id = jsonObject.getInt("inst_id");
        rank = jsonObject.getInt("rank");
        country = jsonObject.getString("country_code");
        institution = jsonObject.getString("name");
        users = jsonObject.getInt("users");
        ac = jsonObject.getInt("acc");
        score = jsonObject.getDouble("points");
    }

    public String getRank() {
        return Integer.toString(rank);
    }

    public String getCountry() {
        return country;
    }

    public String getInstitution() {
        return institution;
    }

    public String getUsers() {
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
