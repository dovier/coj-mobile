package cu.uci.coj.Application.Profiles;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 4/24/16.
 */
public class UserProfile implements Serializable{

    private String avatar;
    private String name;
    private String lastName;
    private String userName;
//    private String sex;
//    private String country;
    private String institution;
    private String favLanguage;
    private String registrationDate;
    private String lastSubmission;
    private String lastAccepted;
    private double score;
    private int rankByUser;
    private int rankByInstitution;
    private int rankByCountry;
    private String lastEntry;
    private String lastEntryDate;
    private int followers;
    private int following;
    private String JSONString;

    public UserProfile(JSONObject jsonObject) throws JSONException {
        JSONString = jsonObject.toString();

        avatar = jsonObject.getString("avatar");
        name = jsonObject.getString("firstname");
        lastName = jsonObject.getString("lastname");
        userName = jsonObject.getString("username");
//        sex = jsonObject.getString("gender");
//        country = jsonObject.getString("country");
        institution = jsonObject.getString("institution");
        favLanguage = jsonObject.getString("favLanguage");
        registrationDate = jsonObject.getString("registration");
        lastSubmission = jsonObject.getString("lastSubmission");
        lastAccepted = jsonObject.getString("lastAccepted");
        score = jsonObject.getDouble("score");
        rankByUser = jsonObject.getInt("rankByUser");
        rankByInstitution = jsonObject.getInt("rankByInstotution");
        rankByCountry = jsonObject.getInt("rankByCountry");
        lastEntry = jsonObject.getString("lastEntry");
        lastEntryDate = jsonObject.getString("lastEntryDate");
        followers = jsonObject.getInt("followers");
        following = jsonObject.getInt("following");
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getInstitution() {
        return institution;
    }

    public String getFavLanguage() {
        return favLanguage;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public String getLastSubmission() {
        return lastSubmission;
    }

    public String getLastAccepted() {
        return lastAccepted;
    }

    public String getScore() {
        return String.format("%.2f", score);
    }

    public String getRankByUser() {
        return Integer.toString(rankByUser);
    }

    public String getRankByInstitution() {
        return Integer.toString(rankByInstitution);
    }

    public String getRankByCountry() {
        return Integer.toString(rankByCountry);
    }

    public String getLastEntry() {
        return lastEntry;
    }

    public String getLastEntryDate() {
        return lastEntryDate;
    }

    public String getFollowers() {
        return Integer.toString(followers);
    }

    public String getFollowing() {
        return Integer.toString(following);
    }

    public String getJSONString() {
        return JSONString;
    }
}
