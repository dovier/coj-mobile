package cu.uci.coj.Application.Mail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by osvel on 5/7/16.
 */
public class Email implements Serializable{

    private int idEmail;
    private String subject;
    private String content;
    private String from;
    private String[] to;
    private long date;
    private boolean isRead;
    private String JSONString;
    private MailFolder folder;

    public Email(String JSONString, MailFolder folder) throws JSONException {
        this.JSONString = JSONString;
        this.folder = folder;

        JSONObject jsonObject = new JSONObject(JSONString);

        idEmail = jsonObject.getInt("idmail");
        subject = jsonObject.getString("title");
        content = jsonObject.getString("content");
        from = jsonObject.getString("id_from");
        if (from.equals("null"))
            from = null;

        try {
            JSONArray JsonTo = jsonObject.getJSONArray("to");
            to = new String[JsonTo.length()];
            for (int i = 0; i < JsonTo.length(); i++) {
                to[i] = JsonTo.get(i).toString();
            }
        }
        catch (JSONException e){
            to = null;
        }

        date = jsonObject.getLong("date");
        isRead = folder != MailFolder.INBOX || jsonObject.getBoolean("isread");

    }

    public int getIdEmail() {
        return idEmail;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public String[] getTo() {
        return to;
    }

    public String getStringTo() {
        if (to == null)
            return null;

        String to_string = "";
        for (int i = 0; i < to.length; i++) {
            to_string += to[i]+";";
        }
        return to_string;
    }

    public long getDate() {
        return date;
    }

    public String getStringDate() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return df.format(new Date(date));

    }

    public MailFolder getFolder() {
        return folder;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getJSONString() {
        return JSONString;
    }
}
