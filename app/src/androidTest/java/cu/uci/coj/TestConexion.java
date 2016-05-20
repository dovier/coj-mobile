package cu.uci.coj;

import junit.framework.TestCase;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import cu.uci.coj.Extras.EntriesItem;
import cu.uci.coj.Judgments.Judgment;
import cu.uci.coj.Problems.Problem;
import cu.uci.coj.Profiles.Compare;
import cu.uci.coj.Standings.CountryRank;
import cu.uci.coj.Standings.InstitutionRank;
import cu.uci.coj.Standings.UserRank;

/**
 * Created by osvel on 5/19/16.
 */
public class TestConexion extends TestCase {

    public void testGetEntry(){

        List<EntriesItem> entries = null;
        try {
            entries = Conexion.getEntries(Conexion.URL_WELCOME_PAGE + 2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertFalse(entries == null);
        assertFalse(entries.size() == 0);

    }

    public void testProblem(){

        Problem problem = null;
        try {
            problem = Conexion.getProblem("1000");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(problem != null);
//        assertEquals(problem.getRecommendation(), "1960 | 3314 | 1389 | 1244 | 1001 | 1007");
        assertEquals(problem.getEnabledlanguages().length, problem.getMemory().length);
        assertEquals(problem.getEnabledlanguages().length, problem.getTesttime().length);
        assertEquals(problem.getEnabledlanguages().length, problem.getTotaltime().length);

        String message = "";
        try {
            Conexion.getProblem("-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertEquals("Unexpected error: bad pid", message);

    }

    public void testGetUserRank(){

        List<UserRank> userRanks = null;

        try {
            userRanks = Conexion.getUserRank(Conexion.URL_RANKING_BY_USER + "2");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(userRanks != null);
        assertTrue(userRanks.size() == 30);
        assertEquals(userRanks.get(0).getRank(), "31");
        assertEquals(userRanks.get(29).getRank(), "60");

        String message = "";

        try {
            Conexion.getUserRank(Conexion.URL_RANKING_BY_USER + "-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertTrue(message.contains("Unexpected code"));

    }

    public void testGetInstitutionRank(){

        List<InstitutionRank> institutionRanks = null;

        try {
            institutionRanks = Conexion.getInstitutionRank(Conexion.URL_RANKING_BY_INSTITUTION + "2");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(institutionRanks != null);
        assertTrue(institutionRanks.size() == 30);
        assertEquals(institutionRanks.get(0).getRank(), "31");
        assertEquals(institutionRanks.get(29).getRank(), "60");

        String message = "";

        try {
            Conexion.getInstitutionRank(Conexion.URL_RANKING_BY_INSTITUTION + "-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertTrue(message.contains("Unexpected code"));

    }

    public void testGetCountryRank(){

        List<CountryRank> countryRanks = null;

        try {
            countryRanks = Conexion.getCountryRank(2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(countryRanks != null);
        assertTrue(countryRanks.size() == 30);
        assertEquals(countryRanks.get(0).getRank(), "31");
        assertEquals(countryRanks.get(29).getRank(), "60");

        countryRanks = null;
        try {
            countryRanks = Conexion.getCountryRank(-1);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(countryRanks != null);
        assertTrue(countryRanks.size() == 174);
        assertEquals(countryRanks.get(0).getRank(), "1");
        assertEquals(countryRanks.get(29).getRank(), "30");

    }

    public void testCompareUsers(){

        Compare compare = null;

        try {
            compare = Conexion.getCompareUsers(Conexion.URL_COMPARE_USER + "Jaco/Jaco");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(compare != null);
        assertEquals(compare.sizeSolved_user_1(), 0);
        assertEquals(compare.sizeSolved_user_2(), 0);
        assertEquals(compare.sizeTried_both(), 13);
        assertEquals(compare.getTried_both(3), "1079");
        assertEquals(compare.getSolved_both(4), "1028");

        String message = "";
        try {
            Conexion.getCompareUsers(Conexion.URL_COMPARE_USER);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertFalse(message.length() == 0);
    }

    public void testGetJudgments(){

        List<Judgment> judgments = null;

        try {
            judgments = Conexion.getJudgmentsItem(Conexion.URL_JUDGMENT_PAGE + "2");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(judgments != null);
        assertEquals(judgments.size(), 20);

        String message = "";
        judgments = null;

        try {
            judgments = Conexion.getJudgmentsItem(Conexion.URL_JUDGMENT_PAGE+ "-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertTrue(judgments == null);
        assertFalse(message.length() == 0);

    }

}
