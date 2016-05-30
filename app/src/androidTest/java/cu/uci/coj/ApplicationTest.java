package cu.uci.coj;

import android.support.v4.app.Fragment;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.Extras.EntriesItem;
import cu.uci.coj.Application.Extras.StartFragment;
import cu.uci.coj.Application.Judgments.Judgment;
import cu.uci.coj.Application.LoginData;
import cu.uci.coj.Application.MainActivity;
import cu.uci.coj.Application.Problems.Problem;
import cu.uci.coj.Application.Problems.ProblemFragment;
import cu.uci.coj.Application.Problems.ProblemItem;
import cu.uci.coj.Application.Problems.ProblemsFragment;
import cu.uci.coj.Application.Profiles.Compare;
import cu.uci.coj.Application.Profiles.UserProfile;
import cu.uci.coj.Application.Standings.CountryRank;
import cu.uci.coj.Application.Standings.InstitutionRank;
import cu.uci.coj.Application.Standings.UserRank;

/**
 * Created by osvel on 5/21/16.
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity activity;

    public ApplicationTest(){
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        activity = getActivity();

    }

    private Fragment startFragment(Fragment fragment) {
        activity.getSupportFragmentManager().beginTransaction()
                .add(R.id.container, fragment, "tag")
                .commit();

        getInstrumentation().waitForIdleSync();

        return activity.getSupportFragmentManager().findFragmentByTag("tag");
    }

    public void testGetEntry(){

        List<EntriesItem> entries = null;
        Conexion conexion = Conexion.getInstance(activity);

        try {
            entries = conexion.getEntries(2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertFalse(entries == null);
        assertFalse(entries.size() == 0);

    }

    public void testGetProblem(){

        Conexion conexion = Conexion.getInstance(activity);

        Problem problem = null;
        try {
            problem = conexion.getProblem("1000");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(problem != null);
        assertEquals(problem.getEnabledlanguages().length, problem.getMemory().length);
        assertEquals(problem.getEnabledlanguages().length, problem.getTesttime().length);
        assertEquals(problem.getEnabledlanguages().length, problem.getTotaltime().length);

        String message = "";
        try {
            conexion.getProblem("-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertEquals("Unexpected error: bad pid", message);

    }

    public void testGetUserRank(){

        List<UserRank> userRanks = null;

        Conexion conexion = Conexion.getInstance(activity);

        try {
            userRanks = conexion.getUserRank(2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(userRanks != null);
        assertTrue(userRanks.size() == 30);
        assertEquals(userRanks.get(0).getRank(), "31");
        assertEquals(userRanks.get(29).getRank(), "60");

        try {
            userRanks = conexion.getUserRank(-1);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertEquals(userRanks.size(), 0);

    }

    public void testGetInstitutionRank(){

        List<InstitutionRank> institutionRanks = null;

        Conexion conexion = Conexion.getInstance(activity);

        try {
            institutionRanks = conexion.getInstitutionRank(2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(institutionRanks != null);
        assertTrue(institutionRanks.size() == 30);
        assertEquals(institutionRanks.get(0).getRank(), "31");
        assertEquals(institutionRanks.get(29).getRank(), "60");

        try {
            institutionRanks = conexion.getInstitutionRank(-1);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertEquals(institutionRanks.size(), 0);

    }

    public void testGetCountryRank(){

        List<CountryRank> countryRanks = null;

        Conexion conexion = Conexion.getInstance(activity);

        try {
            countryRanks = conexion.getCountryRank(2);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(countryRanks != null);
        assertTrue(countryRanks.size() == 30);
        assertEquals(countryRanks.get(0).getRank(), "31");
        assertEquals(countryRanks.get(29).getRank(), "60");

        countryRanks = null;
        try {
            countryRanks = conexion.getCountryRank(-1);
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

        Conexion conexion = Conexion.getInstance(activity);

        try {
            compare = conexion.getCompareUsers("Jaco", "Jaco");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(compare != null);
        assertEquals(compare.sizeSolved_user_1(), 0);
        assertEquals(compare.sizeSolved_user_2(), 0);
        assertEquals(compare.sizeTried_both(), 12);
        assertEquals(compare.getTried_both(3), "1238");
        assertEquals(compare.getSolved_both(4), "1028");

        String message = "";
        try {
            conexion.getCompareUsers("Jaco", "Jaco");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertEquals(message, "");
    }

    public void testGetJudgments(){

        List<Judgment> judgments = null;

        Conexion conexion = Conexion.getInstance(activity);

        try {
            judgments = conexion.getJudgmentsItem(conexion.getURL_JUDGMENT_PAGE() + "2");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertTrue(judgments != null);
        assertEquals(judgments.size(), 20);

        String message = "";
        judgments = null;

        try {
            judgments = conexion.getJudgmentsItem(conexion.getURL_JUDGMENT_PAGE() + "-1");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertTrue(judgments == null);
        assertFalse(message.length() == 0);

    }

    public void testLogin(){

        Conexion conexion = Conexion.getInstance(activity);

        String token = null;
        try {
            token = conexion.login(activity, "Jaco", "jacomino123");
        } catch (IOException | JSONException | UnauthorizedException e) {
            e.printStackTrace();
        }

        assertFalse(token == null);

        LoginData login = null;
        try {
            login = LoginData.read(activity);
        } catch (NoLoginFileException e) {
            e.printStackTrace();
        }

        assertFalse(login == null);
        assertEquals(login.getUser(), "Jaco");
        assertEquals(login.decrypt(activity), "jacomino123");

        LoginData.delete(activity);

        login = null;
        String message = null;
        try {
            login = LoginData.read(activity);
        } catch (NoLoginFileException e) {
            e.printStackTrace();
            message = e.getMessage();
        }

        assertEquals(message, new NoLoginFileException().getMessage());
        assertTrue(login == null);

        try {
            token = conexion.login(activity, "Jaco", "contrasena invalida");
        } catch (IOException | JSONException | UnauthorizedException e) {
            e.printStackTrace();
            message = e.getMessage();
            token = null;
        }

        assertEquals(token, null);
        assertEquals(message, new UnauthorizedException().getMessage());
    }

    public void testToggleFavorite(){

        Conexion conexion = Conexion.getInstance(activity);

        String token = null;
        try {
            token = conexion.login(activity, "Jaco", "jacomino123");
        } catch (IOException | UnauthorizedException | JSONException e) {
            e.printStackTrace();
        }

        assertFalse(token == null);

        List<ProblemItem> problems = null;
        try {
            problems = conexion.getProblemsItem(activity, conexion.getURL_PROBLEM_PAGE() + 1, true);
        } catch (IOException | JSONException | UnauthorizedException | NoLoginFileException e) {
            e.printStackTrace();
        }

        assertFalse(problems == null);

        boolean favorite = problems.get(0).isFav();

        try {
            assertTrue(conexion.toggleFavorite(activity, 1000));
        } catch (IOException | JSONException | UnauthorizedException | NoLoginFileException e) {
            e.printStackTrace();
        }

        try {
            problems = conexion.getProblemsItem(activity, conexion.getURL_PROBLEM_PAGE() + 1, true);
        } catch (IOException | JSONException | UnauthorizedException | NoLoginFileException e) {
            e.printStackTrace();
            problems = null;
        }

        assertFalse(problems == null);

        assertFalse(favorite == problems.get(0).isFav());

    }

    public void testUpdateUser(){

        Conexion conexion = Conexion.getInstance(activity);

        String name = "Nuevo Nombre";

        try {
            assertTrue(conexion.login(activity, "Jaco", "jacomino123").length() != 0);
        } catch (IOException | JSONException | UnauthorizedException e) {
            e.printStackTrace();
        }

        try {
            conexion.updateUserProfile(activity, "", name, name, "", -1, -1, -1, -1);
        } catch (NoLoginFileException | IOException | UnauthorizedException | JSONException e) {
            e.printStackTrace();
        }

        UserProfile user = null;
        try {
            user = conexion.getUserProfile("Jaco");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        assertFalse(user == null);
        assertEquals(user.getName(), name);
        assertEquals(user.getLastName(), name);

        assertTrue(LoginData.delete(activity));

        String message = "";
        try {
            conexion.updateUserProfile(activity, "", name, name, "", -1, -1, -1, -1);
        } catch (NoLoginFileException | IOException | UnauthorizedException | JSONException e) {
            message = e.getMessage();
            e.printStackTrace();
        }

        assertEquals(message, new NoLoginFileException().getMessage());

    }

    public void testHomeView(){

        Fragment fragment = startFragment(StartFragment.newInstance(false));
        LinearLayout input_layout = (LinearLayout) fragment.getActivity().findViewById(R.id.input_layout);
        assertEquals(input_layout.getVisibility(), View.GONE);

    }

    public void testProblemsViewLogin(){

        Fragment fragment = startFragment(ProblemsFragment.newInstance(true));
        LinearLayout fav_layout = (LinearLayout) fragment.getActivity().findViewById(R.id.topic_fav_layout);
        assertEquals(fav_layout.getVisibility(), View.VISIBLE);

    }

    public void testProblemsViewNoLogin(){

        Fragment fragment = startFragment(ProblemsFragment.newInstance(false));
        LinearLayout fav_layout = (LinearLayout) fragment.getActivity().findViewById(R.id.topic_fav_layout);
        assertEquals(fav_layout.getVisibility(), View.GONE);

    }

    public void testProblemViewLogin(){

        Conexion conexion = Conexion.getInstance(activity);

        List<ProblemItem> problemsItem = null;
        try {
            conexion.login(activity, "Jaco", "jacomino123");
            problemsItem = conexion.getProblemsItem(activity, conexion.getURL_PROBLEM_PAGE() + 1, true);
            LoginData.delete(activity);
        } catch (IOException | JSONException | NoLoginFileException | UnauthorizedException e) {
            e.printStackTrace();
        }

        assertTrue(problemsItem != null);

        Fragment fragment = startFragment(ProblemFragment.newInstance(problemsItem.get(0), true));
        TextView submit = (TextView)fragment.getActivity().findViewById(R.id.submit);
        assertEquals(submit.getVisibility(), View.VISIBLE);

    }

    public void testProblemViewNoLogin(){

        Conexion conexion = Conexion.getInstance(activity);

        List<ProblemItem> problemsItem = null;
        try {
            problemsItem = conexion.getProblemsItem(activity, conexion.getURL_PROBLEM_PAGE() + 1, false);
        } catch (IOException | JSONException | NoLoginFileException | UnauthorizedException e) {
            e.printStackTrace();
        }

        assertTrue(problemsItem != null);

        Fragment fragment = startFragment(ProblemFragment.newInstance(problemsItem.get(0), false));
        TextView submit = (TextView)fragment.getActivity().findViewById(R.id.submit);
        assertEquals(submit.getVisibility(), View.GONE);

    }

    public void testReadLoginFile(){

        String message = "";
        LoginData login;
        try {
            login = LoginData.read(activity);
        } catch (NoLoginFileException e) {
            e.printStackTrace();
            message = e.getMessage();
            login = null;
        }

        if (login != null){
            assertEquals(message, "");
        }
        else {
            assertEquals(message, new NoLoginFileException().getMessage());
        }

    }
}
