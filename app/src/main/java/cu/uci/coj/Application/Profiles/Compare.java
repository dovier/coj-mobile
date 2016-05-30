package cu.uci.coj.Application.Profiles;

import java.io.Serializable;

/**
 * Created by osvel on 2/29/16.
 */
public class Compare implements Serializable{

    private int[] solved_user_1;
    private int[] solved_both;
    private int[] solved_user_2;
    private int[] tried_user_1;
    private int[] tried_both;
    private int[] tried_user_2;

    public Compare(int[] solved_user_1, int[] solved_both, int[] solved_user_2, int[] tried_user_1, int[] tried_both, int[] tried_user_2) {
        this.solved_user_1 = solved_user_1;
        this.solved_both = solved_both;
        this.solved_user_2 = solved_user_2;
        this.tried_user_1 = tried_user_1;
        this.tried_both = tried_both;
        this.tried_user_2 = tried_user_2;
    }

    public int sizeSolved_user_1() {
        return solved_user_1.length;
    }

    public int sizeSolved_both() {
        return solved_both.length;
    }

    public int sizeSolved_user_2() {
        return solved_user_2.length;
    }

    public int sizeTried_user_1() {
        return tried_user_1.length;
    }

    public int sizeTried_both() {
        return tried_both.length;
    }

    public int sizeTried_user_2() {
        return tried_user_2.length;
    }

    public String getSolved_user_1(int position) {
        return Integer.toString(solved_user_1[position]);
    }

    public String getSolved_both(int position) {
        return Integer.toString(solved_both[position]);
    }

    public String getSolved_user_2(int position) {
        return Integer.toString(solved_user_2[position]);
    }

    public String getTried_user_1(int position) {
        return Integer.toString(tried_user_1[position]);
    }

    public String getTried_both(int position) {
        return Integer.toString(tried_both[position]);
    }

    public String getTried_user_2(int position) {
        return Integer.toString(tried_user_2[position]);
    }
}
