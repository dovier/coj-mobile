package cu.uci.coj.Application.Problems;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Application.Image;
import cu.uci.coj.Application.Status;

/**
 * Created by osvel on 2/21/16.
 */

public class ProblemItem implements Serializable{

    private int ID;
    private boolean fav;
    private Status status;
    private String problem_name;
    private int submit;
    private int accept;
    private double accept_percent;
    private double score;
    private List<Image> images;
    private String JSONString;

    public ProblemItem(org.json.JSONObject JSONObject) throws JSONException {
        this.JSONString = JSONObject.toString();
        ID = JSONObject.getInt("pid");

        try {
            fav = JSONObject.getBoolean("favorite");
            String stat = JSONObject.getString("status");
            switch (stat){
                case "unsolved" :{
                    status = Status.Unsolved;
                    break;
                }
                case "solved": {
                    status = Status.AC;
                    break;
                }
                default: {
                    status = Status.NoSend;
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }


        problem_name = JSONObject.getString("title");
        submit = JSONObject.getInt("sub");
        accept = JSONObject.getInt("ac");
        accept_percent = JSONObject.getDouble("acporciento");
        score = JSONObject.getDouble("score");
        images = new ArrayList<>();
    }

    public ProblemItem(int ID, boolean fav, Status status, String title, int submit, int accept, double accept_percent, double score, JSONObject JSONObject) {
        this.ID = ID;
        this.fav = fav;
        this.status = status;
        this.problem_name = title;
        this.submit = submit;
        this.accept = accept;
        this.accept_percent = accept_percent;
        this.score = score;
        this.JSONString = JSONObject.toString();
    }

    public void setFav() {
        fav = !fav;
    }

    public String getID() {
        return Integer.toString(ID);
    }

    public boolean isFav() {
        return fav;
    }

    public Status getStatus() {
        return status;
    }

    public String getProblem_name() {
        return problem_name;
    }

    public String getSubmit() {
        return Integer.toString(submit);
    }

    public String getAccept() {
        return Integer.toString(accept);
    }

    public String getAccept_percent() {
        return String.format("%.2f", accept_percent);
    }

    public String getScore() {
        return String.format("%.2f", score);
    }

    public long getLongId(){
        return (long)ID;
    }

    public JSONObject getJSONObject() {

        try {
            return new JSONObject(JSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public void setNullBitmap(){
        for (int i = 0; i < images.size(); i++) {
            images.get(i).setImage(null);
        }
    }

    public List<Image> getImages() {
        return images;
    }

    public boolean addImage(String name, Bitmap image) {
        return images.add(new Image(name, image));
    }
}