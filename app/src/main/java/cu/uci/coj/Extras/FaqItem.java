package cu.uci.coj.Extras;

import java.io.Serializable;

/**
 * Created by osvel on 4/11/16.
 */
public class FaqItem implements Serializable{

    private String question;
    private String answer;

    public FaqItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }
}
