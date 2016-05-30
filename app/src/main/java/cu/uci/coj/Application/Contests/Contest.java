package cu.uci.coj.Application.Contests;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Osvel Alvarez on 2/25/16.
 *
 */
public class Contest implements Serializable {

    private int id;
    private boolean open;
    private String contest_name;
    private String start_date;
    private String end_date;
    private String JSONString;

    public Contest(String JSONString) throws JSONException {
        this.JSONString = JSONString;

        JSONObject JsonContest = new JSONObject(JSONString);

        open = JsonContest.getString("access").equals("open");
        id = JsonContest.getInt("id");
        contest_name = JsonContest.getString("name");
        start_date = JsonContest.getString("start");
        end_date = JsonContest.getString("end");

    }

    public Calendar getDateStart(){

        return toCalendar(start_date);

    }

    private Calendar toCalendar(String date_string){

        Calendar newCalendar = Calendar.getInstance();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
            Date parsed = df.parse(date_string);
            newCalendar.setTime(parsed);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return newCalendar;
    }

    public String getId() {
        return Integer.toString(id);
    }

    public boolean isOpen() {
        return open;
    }

    public String getContest_name() {
        return contest_name;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getJSONString() {
        return JSONString;
    }
}

