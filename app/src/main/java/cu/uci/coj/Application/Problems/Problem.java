package cu.uci.coj.Application.Problems;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by osvel on 2/26/16.
 */
public class Problem implements Serializable{

    private String createdby;
    private String addedby;
    private String dateOfCreation;
    private int[] totaltime;
    private int[] testtime;
    private String[] memory;
    private String output;
    private String[] size;
    private String[] enabledlanguages;
    private String description;
    private String inputSpecifications;
    private String outputSpecification;
    private String sampleInput;
    private String sampleOutput;
    private String hints;
    private int[] recommendation;
    private String jsonString;

    public Problem(JSONObject jsonObject) {

        try {

            this.jsonString = jsonObject.toString();
            createdby = jsonObject.getString("createdby");
            addedby = jsonObject.getString("addedby");
            dateOfCreation = jsonObject.getString("dateOfCreation");

            JSONArray jsonArray = jsonObject.getJSONArray("enabledlanguages");
            enabledlanguages = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                enabledlanguages[i] = jsonArray.get(i).toString();
            }

            jsonArray = jsonObject.getJSONArray("totaltime");
            totaltime = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                totaltime[i] = Integer.parseInt(jsonArray.get(i).toString());
            }

            jsonArray = jsonObject.getJSONArray("testtime");
            testtime = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                testtime[i] = Integer.parseInt(jsonArray.get(i).toString());
            }

            jsonArray = jsonObject.getJSONArray("memory");
            memory = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                memory[i] = jsonArray.get(i).toString();
            }

            output = jsonObject.getString("outputMB");

            jsonArray = jsonObject.getJSONArray("size");
            size = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                size[i] = jsonArray.get(i).toString();
            }

            description = jsonObject.getString("description");
            inputSpecifications = jsonObject.getString("inputSpecification");
            outputSpecification = jsonObject.getString("outputSpecification");
            sampleInput = jsonObject.getString("sampleInput");
            sampleOutput = jsonObject.getString("sampleOutput");
            hints = jsonObject.getString("hints");
            JSONArray JSONRecomendation = jsonObject.getJSONArray("recommendation");
            recommendation = new int[JSONRecomendation.length()];
            for (int i = 0; i < JSONRecomendation.length(); i++) {
                recommendation[i] = Integer.parseInt(JSONRecomendation.get(i).toString());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCreatedby() {
        return createdby;
    }

    public String getAddedby() {
        return addedby;
    }

    public String getDateOfCreation() {
        return dateOfCreation;
    }

    public int[] getTotaltime() {
        return totaltime;
    }

    public int[] getTesttime() {
        return testtime;
    }

    public String[] getMemory() {
        return memory;
    }

    public String getOutput() {
        return output;
    }

    public String[] getSize() {
        return size;
    }

    public String[] getEnabledlanguages() {
        return enabledlanguages;
    }

    public String getDescription() {
        return description;
    }

    public String getInputSpecifications() {
        return inputSpecifications;
    }

    public String getOutputSpecification() {
        return outputSpecification;
    }

    public String getSampleInput() {
        return sampleInput;
    }

    public String getSampleOutput() {
        return sampleOutput;
    }

    public String getHints() {
        return hints;
    }

    public String getRecommendation() {
        String output = "";
        for (int i = 0; i < recommendation.length; i++) {
            output += recommendation[i];
            if (i < recommendation.length-1)
                output += " | ";
        }
        return output;
    }

    public String getJsonString() {
        return jsonString;
    }
}
