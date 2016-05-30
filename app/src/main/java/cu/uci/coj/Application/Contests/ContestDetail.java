package cu.uci.coj.Application.Contests;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 4/12/16.
 */
public class ContestDetail implements Serializable {

    private String contestType;
    private String contestantType;
    private String accesType;
    private String registrationType;
    private boolean templateVirtual;
    private int penalt;
    private int frozenTime;
    private int deadTime;
    private int levels;
    private int acceptedLimit;
    private int acceptedByLevel;
    private int pointByProblem;
    private String[] programmingLanguage;
    private boolean showProblemsToAll;
    private boolean showJudgmentsToContestants;
    private boolean showJudgmentsToAll;
    private boolean showStandings;
    private boolean showStandingsToAll;
    private boolean showStatisticsToContestants;
    private boolean showStatisticsToAll;
    private int goldMedals;
    private int silverMedals;
    private int bronzeMedals;
    private String overView;
    private String JsonString;

    public ContestDetail(String JsonString) throws JSONException {
        this.JsonString = JsonString;

        JSONObject object = new JSONObject(JsonString);

        JSONArray languagesArray = object.getJSONArray("programmingLanguages");
        programmingLanguage = new String[languagesArray.length()];
        for (int i = 0; i < languagesArray.length(); i++) {
            programmingLanguage[i] = languagesArray.getString(i);
        }

        contestType = object.getString("contestType");
        contestantType = object.getString("contestantType");
        accesType = object.getString("accessType");
        registrationType = object.getString("registrationType");
        templateVirtual = object.getBoolean("templateToVirtualContest");
        penalt = object.getInt("penaltyB0EachRejectedSubmission");
        frozenTime = object.getInt("frozenTime");
        deadTime = object.getInt("deadTime");
        levels = object.getInt("levels");
        acceptedLimit = object.getInt("acceptedLimit");
        acceptedByLevel = object.getInt("acceptedByLevels");
        pointByProblem = object.getInt("pointsByProblem");
        showProblemsToAll = object.getBoolean("showProblemsToAll");
        showJudgmentsToContestants = object.getBoolean("showJudgmentsToTheContestants");
        showJudgmentsToAll = object.getBoolean("showJudgmentsToAll");
        showStandings = object.getBoolean("showStandings");
        showStandingsToAll = object.getBoolean("showStandingstoall");
        showStatisticsToContestants = object.getBoolean("showStatisticsToTheContestants");
        showStatisticsToAll = object.getBoolean("showStatisticsToAll");
        goldMedals = object.getInt("goldMedals");
        silverMedals = object.getInt("silverMedals");
        bronzeMedals = object.getInt("bronzeMedals");
        overView = object.getString("overwiev");

    }

    public String getJsonString() {
        return JsonString;
    }

    public String getContestType() {
        return contestType;
    }

    public String getContestantType() {
        return contestantType;
    }

    public String getAccesType() {
        return accesType;
    }

    public String getRegistrationType() {
        return registrationType;
    }

    public boolean isTemplateVirtual() {
        return templateVirtual;
    }

    public String getPenalt() {
        return Integer.toString(penalt);
    }

    public String getFrozenTime() {
        return Integer.toString(frozenTime);
    }

    public String getDeadTime() {
        return Integer.toString(deadTime);
    }

    public String getLevels() {
        return Integer.toString(levels);
    }

    public String getAcceptedLimit() {
        return Integer.toString(acceptedLimit);
    }

    public String getAcceptedByLevel() {
        return Integer.toString(acceptedByLevel);
    }

    public String getPointByProblem() {
        return Integer.toString(pointByProblem);
    }

    public String getProgrammingLanguage() {
        String cad = programmingLanguage[0];
        for (int i = 1; i < programmingLanguage.length; i++) {
            cad += "\t" + programmingLanguage[i];
        }
        return cad;
    }

    public boolean isShowProblemsToAll() {
        return showProblemsToAll;
    }

    public boolean isShowJudgmentsToContestants() {
        return showJudgmentsToContestants;
    }

    public boolean isShowJudgmentsToAll() {
        return showJudgmentsToAll;
    }

    public boolean isShowStandings() {
        return showStandings;
    }

    public boolean isShowStandingsToAll() {
        return showStandingsToAll;
    }

    public boolean isShowStatisticsToContestants() {
        return showStatisticsToContestants;
    }

    public boolean isShowStatisticsToAll() {
        return showStatisticsToAll;
    }

    public String getGoldMedals() {
        return Integer.toString(goldMedals);
    }

    public String getSilverMedals() {
        return Integer.toString(silverMedals);
    }

    public String getBronzeMedals() {
        return Integer.toString(bronzeMedals);
    }

    public String getOverView() {
        return overView;
    }
}
