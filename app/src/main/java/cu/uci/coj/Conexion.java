package cu.uci.coj;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Patterns;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cu.uci.coj.Application.Contests.Contest;
import cu.uci.coj.Application.Contests.ContestDetail;
import cu.uci.coj.Application.Exceptions.InvalidTokenException;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.Extras.EntriesItem;
import cu.uci.coj.Application.Extras.FaqItem;
import cu.uci.coj.Application.Filters.Filter;
import cu.uci.coj.Application.Judgments.Judgment;
import cu.uci.coj.Application.LoginData;
import cu.uci.coj.Application.Mail.Email;
import cu.uci.coj.Application.Mail.MailFolder;
import cu.uci.coj.Application.Problems.Problem;
import cu.uci.coj.Application.Problems.ProblemItem;
import cu.uci.coj.Application.Profiles.Compare;
import cu.uci.coj.Application.Profiles.UserProfile;
import cu.uci.coj.Application.Standings.CountryRank;
import cu.uci.coj.Application.Standings.InstitutionRank;
import cu.uci.coj.Application.Standings.UserRank;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Conexion {

    public static Conexion conexion = null;

    public final static String DEFAULT_COJ_URL = "http://coj.uci.cu";
    private String COJ_URL;

    private String API_KEY = "nTBitFVbpDrdeBa7Ki8vRjSt9z3XE+hAdRHY7XbY9CNvV9p5G/KnxZoTLGOu/gs5Wp/5ABt5S+AzX8gvtIdz9eNH149cw5lncunoeULJhehGY2v3aC6B7Rp+s6MmK6JCdBP/vjbPfGWuJyGfIw3iPxSeQrW987rKRRYEN//lAkgYBJMCgE/Ei+U0QdHlLqvd";

    private String IMAGE_URL;

    private String URL_CREATE_ACCOUNT;
    private String URL_FORGOT_PASSWORD;

    private String URL_GENERATE_API;
    private String URL_LOGIN;

    private String URL_MAIL_INBOX;
    private String URL_MAIL_OUTBOX;
    private String URL_MAIL_DRAFT;
    private String URL_MAIL_DELETE;
    private String URL_MAIL_SEND;
    private String URL_MAIL_TOGGLE_STATUS;

    private String URL_PROBLEM_PAGE;
    private String URL_PROBLEM;
    private String URL_PROBLEM_FILTER;
    private String URL_TOGGLE_FAVORITE;

    private String URL_RANKING_BY_USER;
    private String URL_RANKING_BY_INSTITUTION;
    private String URL_RANKING_BY_COUNTRY;
    private String URL_RANKING_BY_COUNTRY_PAGE;
    private String URL_RANKING_INSTITUTION_BY_COUNTRY;

    private String URL_CONTEST_NEXT;
    private String URL_CONTEST_RUNNING;
    private String URL_CONTEST_PAST;
    private String URL_CONTEST_DETAIL;

    private String URL_FILTER_CLASSIFICATION;
    private String URL_FILTER_LANGUAGE;

    private String URL_COMPARE_USER;
    private String URL_USER_PROFILE;
    private String URL_USER_PROFILE_UPDATE;

    private String URL_JUDGMENT_PAGE;
    private String URL_JUDGMENT_SUBMIT;
    private String URL_JUDGMENT_BEST_SOLUTIONS;
    private String URL_JUDGMENT_FILTER;

    private String URL_WELCOME_PAGE;
    private String URL_ADD_ENTRY;
    private String URL_FAQ;

    public final static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private void generateURLs(){

        String API_URL = "/api";

        IMAGE_URL = COJ_URL;

        URL_CREATE_ACCOUNT = COJ_URL + "/user/createnewaccount.xhtml";
        URL_FORGOT_PASSWORD = COJ_URL + API_URL + "/private/forgottenpassword";

        URL_GENERATE_API = COJ_URL + API_URL + "/private/generateapi";
        URL_LOGIN = COJ_URL + API_URL + "/private/login";

        String URL_MAIL = COJ_URL + API_URL + "/mail";
        URL_MAIL_INBOX = URL_MAIL + "/inbox";
        URL_MAIL_OUTBOX = URL_MAIL + "/outbox";
        URL_MAIL_DRAFT = URL_MAIL + "/draft";
        URL_MAIL_DELETE = URL_MAIL + "/delete";
        URL_MAIL_SEND = URL_MAIL + "/send";
        URL_MAIL_TOGGLE_STATUS = URL_MAIL + "/toggle/status/";

        URL_PROBLEM_PAGE = COJ_URL + API_URL + "/problem/page/";
        URL_PROBLEM = COJ_URL + API_URL + "/problem/";
        URL_PROBLEM_FILTER = COJ_URL + API_URL + "/problem?";
        URL_TOGGLE_FAVORITE = URL_PROBLEM + "togglefavorite/";

        URL_RANKING_BY_USER = COJ_URL + API_URL + "/ranking/byuser/page/";
        URL_RANKING_BY_INSTITUTION = COJ_URL + API_URL + "/ranking/byinstitution/page/";
        URL_RANKING_BY_COUNTRY = COJ_URL + API_URL + "/ranking/bycountry";
        URL_RANKING_BY_COUNTRY_PAGE = URL_RANKING_BY_COUNTRY + "/page/";
        URL_RANKING_INSTITUTION_BY_COUNTRY = COJ_URL + API_URL + "/ranking/institutionbycountry/";

        URL_CONTEST_NEXT = COJ_URL + API_URL + "/contest/next";
        URL_CONTEST_RUNNING = COJ_URL + API_URL + "/contest/running";
        URL_CONTEST_PAST = COJ_URL + API_URL + "/contest/past";
        URL_CONTEST_DETAIL = COJ_URL + API_URL + "/contest/";

        URL_FILTER_CLASSIFICATION = COJ_URL + API_URL +"/filter/classifications";
        URL_FILTER_LANGUAGE = COJ_URL + API_URL +"/filter/languages";

        URL_COMPARE_USER = COJ_URL + API_URL + "/statistic/compare/";
        URL_USER_PROFILE = COJ_URL + API_URL + "/userprofile/";
        URL_USER_PROFILE_UPDATE = URL_USER_PROFILE + "update";

        URL_JUDGMENT_PAGE = COJ_URL + API_URL + "/judgment/page/";
        URL_JUDGMENT_SUBMIT = COJ_URL + API_URL + "/judgment/submit/";
        URL_JUDGMENT_BEST_SOLUTIONS = COJ_URL + API_URL + "/judgment/best/";
        URL_JUDGMENT_FILTER = COJ_URL + API_URL + "/judgment?";

        URL_WELCOME_PAGE = COJ_URL + API_URL + "/extras/entry/";
        URL_ADD_ENTRY = COJ_URL + API_URL + "/extras/entry";
        URL_FAQ = COJ_URL + API_URL + "/extras/faq";
    }

    public Conexion(String url) {
        if (url != null) {
            COJ_URL = url;
            generateURLs();
        }
        else{
            COJ_URL = DEFAULT_COJ_URL;
            generateURLs();
        }
    }

    public static Conexion getInstance(Context context){

        String preference_name = context.getResources().getString(R.string.preference_name);

        SharedPreferences prefs = context.getSharedPreferences(preference_name, Context.MODE_PRIVATE);
        String server = prefs.getString(preference_name, Conexion.DEFAULT_COJ_URL);

        if (conexion == null){
            if (server != null && Patterns.WEB_URL.matcher(server).matches())
                conexion = new Conexion(server);
            else
                conexion = new Conexion(null);
        }

        return conexion;

    }

    public String getCOJ_URL() {
        return COJ_URL;
    }

    public String getIMAGE_URL() {
        return IMAGE_URL;
    }

    public String getURL_CREATE_ACCOUNT() {
        return URL_CREATE_ACCOUNT;
    }

    public String getURL_PROBLEM_PAGE() {
        return URL_PROBLEM_PAGE;
    }

    public String getURL_PROBLEM_FILTER() {
        return URL_PROBLEM_FILTER;
    }

    public String getURL_CONTEST_NEXT() {
        return URL_CONTEST_NEXT;
    }

    public String getURL_CONTEST_RUNNING() {
        return URL_CONTEST_RUNNING;
    }

    public String getURL_CONTEST_PAST() {
        return URL_CONTEST_PAST;
    }

    public String getURL_JUDGMENT_PAGE() {
        return URL_JUDGMENT_PAGE;
    }

    public String getURL_JUDGMENT_BEST_SOLUTIONS() {
        return URL_JUDGMENT_BEST_SOLUTIONS;
    }

    public String getURL_JUDGMENT_FILTER() {
        return URL_JUDGMENT_FILTER;
    }

    /**
     * Get ApiKey for identify programmer in API services.
     *
     * @return ApiKey
     * @throws IOException
     */
//    public String getApiKey() throws IOException, JSONException {
//
//        String jsonApi =
//                "{" +
//                "\"username\" : \"Jaco\"," +
//                "\"password\" : \"jacomino123\"" +
//                "}";
//
//        OkHttpClient client = new OkHttpClient();
//
//        RequestBody body = RequestBody.create(JSON, jsonApi);
//        Request request = new Request.Builder()
//                .url(URL_GENERATE_API)
//                .post(body)
//                .build();
//        Response response = client.newCall(request).execute();
//
//        if (!response.isSuccessful())
//            throw new IOException("Unexpected code " + response);
//
//        JSONObject jsonObject = new JSONObject(response.body().string());
//
//        return jsonObject.getString("apikey");
//
//    }

    /**
     * Request for new login token when expired
     *
     * @param context Application context for read and write LoginData file
     *
     * @return New token
     * @throws NoLoginFileException
     * @throws UnauthorizedException
     */
    public String getNewToken(Context context) throws NoLoginFileException, UnauthorizedException {

        LoginData loginData = LoginData.read(context);

        String user = loginData.getUser();
        String pswd = loginData.decrypt(context);
        String oldToken = loginData.getToken();

        String token = "";
        try {
            token = login(context, user, pswd);
        } catch (IOException | JSONException e) {
            new LoginData(context, user, pswd, oldToken).save(context);
            e.printStackTrace();
        }

        return token;

    }

    /**
     * Login user in COJ
     *
     * @param context Application context used for create LoginData file
     * @param user Username
     * @param pswd Password unencrypted
     *
     * @return User token
     * @throws IOException
     * @throws JSONException
     */
    public String login(Context context, String user, String pswd) throws IOException, JSONException, UnauthorizedException {

        //eliminar del disco cualquier intento de login anterior excepto en caso de IOException (no hay coneccion)
        LoginData oldLoginData = null;
        try {
            oldLoginData = LoginData.read(context);
        } catch (NoLoginFileException e) {
            e.printStackTrace();
        }
        LoginData.delete(context);

        //crear el String para el JSON
        String jsonLogin =
                "{" +
                "\"apikey\" : \"" +API_KEY+ "\" ," +
                "\"username\" : \"" +user+ "\" ," +
                "\"password\" : \"" +pswd+ "\"" +
                "}";

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, jsonLogin);
        Request request = new Request.Builder()
                .url(URL_LOGIN)
                .post(body)
                .build();
        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            //escribir nuevamente el LoginData si error de coneccion
            if (oldLoginData != null)
                oldLoginData.save(context);
            throw e;
        }

        String token = null;

        //login ok; save on disck
        int code = response.code();
        switch (code){
            case 200: {
                JSONObject jsonObject = new JSONObject(response.body().string());
                token = jsonObject.getString("token");
                LoginData loginData = new LoginData(context, user, pswd, token);
                loginData.save(context);
                break;
            }
            case 401: {
                throw new UnauthorizedException();
            }
        }

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        return token;
    }

    /**
     * Get problem description given a problem id
     *
     * @param id problem id
     * @return Problem object with problem description
     * @throws IOException
     * @throws JSONException
     */
    public Problem getProblem(String id) throws IOException, JSONException {

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_PROBLEM + id)
                        .build())
                    .execute();

        JSONObject mJSONObject = new JSONObject(response.body().string());

        String error = "";
        try {
            error = mJSONObject.getString("error");
        }
        catch (JSONException e){

            return new Problem(mJSONObject);

        }

        throw new IOException("Unexpected error: " + error);

    }

    /**
     * Compare two users and return a Compare object with comparation data
     *
     * @param user1 UserName from first user
     * @param user2 UserName from second user
     *
     * @return Compare object
     * @throws IOException
     * @throws JSONException
     */
    public Compare getCompareUsers(String user1, String user2) throws IOException, JSONException {

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_COMPARE_USER + user1 + "/" + user2)
                        .build())
                .execute();

        if (!response.isSuccessful())
            throw new IOException("Unexpected code: " + response);

        JSONObject JsonCompare = new JSONObject(response.body().string());

        JSONArray array = JsonCompare.getJSONArray("solvedOnlyByUser1");
        int[] solve_user_1 = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            solve_user_1[i] = array.getInt(i);

        array = JsonCompare.getJSONArray("solvedOnlyByBoth");
        int[] solve_both = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            solve_both[i] = array.getInt(i);

        array = JsonCompare.getJSONArray("solvedOnlyByUser2");
        int[] solve_user_2 = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            solve_user_2[i] = array.getInt(i);

        array = JsonCompare.getJSONArray("triedOnlyByUser1");
        int[] tried_user_1 = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            tried_user_1[i] = array.getInt(i);

        array = JsonCompare.getJSONArray("triedOnlyByBoth");
        int[] tried_both = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            tried_both[i] = array.getInt(i);

        array = JsonCompare.getJSONArray("triedOnlyByUser2");
        int[] tried_user_2 = new int[array.length()];
        for (int i = 0; i < array.length(); i++)
            tried_user_2[i] = array.getInt(i);

        return new Compare(solve_user_1, solve_both, solve_user_2, tried_user_1, tried_both, tried_user_2);

    }

    public List<UserRank> getUserRank(int page) throws IOException, JSONException {

        List<UserRank> standing = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_RANKING_BY_USER + page)
                        .build())
                .execute();

        if (response.code() == 400)
            return standing;

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        JSONArray mJsonArray = new JSONArray(response.body().string());

        JSONObject mJObjectSection;

        for (int i = 0; i < mJsonArray.length(); i++) {
            mJObjectSection = mJsonArray.getJSONObject(i);
            standing.add(new UserRank(mJObjectSection));
        }

        return standing;
    }

    public List<InstitutionRank> getInstitutionRank(int page) throws IOException, JSONException {

        List<InstitutionRank> standing = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_RANKING_BY_INSTITUTION + page)
                        .build())
                .execute();

        if (response.code() == 400)
            return standing;

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        JSONArray mJsonArray = new JSONArray(response.body().string());

        JSONObject mJObjectSection;

        for (int i = 0; i < mJsonArray.length(); i++) {
            mJObjectSection = mJsonArray.getJSONObject(i);
            standing.add(new InstitutionRank(mJObjectSection));
        }

        return standing;
    }

    /**
     * Get ranking by institution given an country (needs country ID)
     *
     * @param country_id Integer represents country id
     *
     * @return List of Institutions
     * @throws IOException
     * @throws JSONException
     */
    public List<InstitutionRank> getInstitutionByCountryRank(int country_id) throws IOException, JSONException {

        List<InstitutionRank> institutionRank = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_RANKING_INSTITUTION_BY_COUNTRY + country_id)
                        .build())
                .execute();

        String resp = response.body().string();
        switch (response.code()){
            case 200: {
                JSONArray mJsonArray = new JSONArray(resp);

                for (int i = 0; i < mJsonArray.length(); i++) {

                    JSONObject object = mJsonArray.getJSONObject(i);
                    institutionRank.add(new InstitutionRank(object));

                }

                return institutionRank;
            }
            default: {
                String error = new JSONObject(resp).getString("error");
                throw new IOException("Unexpected code " + error);
            }
        }
    }

    /**
     * Return ranking by country one page at time. If page = -1, then getCountryRank return all country ranking.
     *
     * @param page page value
     * @return list of countries
     * @throws IOException
     * @throws JSONException
     */
    public List<CountryRank> getCountryRank(int page) throws IOException, JSONException {

        List<CountryRank> standing = new ArrayList<>();

        String url;
        if (page == -1){
            url = URL_RANKING_BY_COUNTRY;
        }
        else {
            url = URL_RANKING_BY_COUNTRY_PAGE + page;
        }

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(url)
                        .build())
                .execute();

        if (response.code() == 400)
            return standing;

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        JSONArray mJsonArray = new JSONArray(response.body().string());

        JSONObject mJObjectSection;

        for (int i = 0; i < mJsonArray.length(); i++) {
            mJObjectSection = mJsonArray.getJSONObject(i);
            standing.add(new CountryRank(mJObjectSection));
        }

        return standing;
    }

    /**
     * Get problems list
     *
     * @param url Api URL for get problems list
     * @param login User is logged in or not
     *
     * @return list of problems
     * @throws IOException
     * @throws JSONException
     */
    public List<ProblemItem> getProblemsItem(Context context, String url, Boolean login)
            throws IOException, JSONException, UnauthorizedException, NoLoginFileException {

        List<ProblemItem> result = new ArrayList<>();

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        Request request;

        if(login){

            String token = LoginData.read(context).getToken();

            request = new Request.Builder()
                    .header("apikey", API_KEY)
                    .header("token", token)
                    .url(url)
                    .build();
        }
        else {
            request = new Request.Builder()
                    .url(url)
                    .build();
        }

        Response response = client.newCall(request)
                .execute();

        String resp = response.body().string();
        switch (response.code()){
            case 200: {
                JSONArray mJsonArray = new JSONArray(resp);

                if (login && mJsonArray.length() != 0){
                    try {
                        mJsonArray.getJSONObject(0).getBoolean("favorite");
                    }
                    catch (JSONException e){
                        getNewToken(context);
                        return getProblemsItem(context, url, login);
                    }
                }

                for (int i = 0; i < mJsonArray.length(); i++) {

                    result.add(new ProblemItem(mJsonArray.getJSONObject(i)));

                }

                return result;
            }
            case 400: {
                return result;
            }
            case 401: {
                String error = new JSONObject(resp).getString("error");
                switch (error){
                    case "token expirated": {
                        getNewToken(context);
                        return getProblemsItem(context, url, login);
                    }
                    case "token or apikey incorrect": {
                        getNewToken(context);
                        return getProblemsItem(context, url, login);
                    }
                    default: {
                        throw new IOException("Unexpected code " + error);
                    }
                }
            }
            case 404: {
                return result;
            }
            default: {
                String error = new JSONObject(resp).getString("error");
                throw new IOException("Unexpected code " + error);
            }
        }
    }

    /**
     * Select favorite problem or not favorite.
     *
     * @param context application context
     * @param id problem id
     *
     * @return operation successful or not
     * @throws IOException
     * @throws JSONException
     */
    public boolean toggleFavorite(Context context, int id) throws IOException, JSONException, UnauthorizedException, NoLoginFileException {

        String token = LoginData.read(context).getToken();

        Request request = new Request.Builder()
                .header("apikey", API_KEY)
                .header("token", token)
                .put(new FormBody.Builder().build())
                .url(URL_TOGGLE_FAVORITE + id)
                .build();

        Response response = new OkHttpClient().newCall(request)
                .execute();

        switch (response.code()){
            case 200: {
                return true;
            }
            case 400: {
                return false;
            }
            case 401: {
                String error = new JSONObject(response.body().string()).getString("error");
                switch (error){
                    case "token expirated": {
                        getNewToken(context);
                        throw new InvalidTokenException();
                    }
                    case "token or apikey incorrect":{
                        getNewToken(context);
                        throw new InvalidTokenException();
                    }
                }

                return false;
            }
        }

        return false;

    }

    /**
     * Get list of judgments
     * @param url Api url
     * @return List of Judgments
     * @throws IOException
     * @throws JSONException
     */
    public List<Judgment> getJudgmentsItem(String url) throws IOException, JSONException {

        List<Judgment> judgments = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(url)
                        .build())
                .execute();

        String resp = response.body().string();

        if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

        JSONArray mJsonArray = new JSONArray(resp);

        JSONObject mJObjectSeccion;
        for (int i = 0; i < mJsonArray.length(); i++) {

            mJObjectSeccion = mJsonArray.getJSONObject(i);

            judgments.add(new Judgment(mJObjectSeccion));
        }

        return judgments;
    }

    public List<Contest> getContests(String url) throws IOException, JSONException {

        List<Contest> contestList = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(url)
                        .build())
                .execute();

        String resp = response.body().string();
        switch (response.code()){
            case 400: {
                return contestList;
            }
        }

        JSONArray JsonContestArray = new JSONArray(resp);

        for (int i = 0; i < JsonContestArray.length(); i++) {

            JSONObject JsonContest = JsonContestArray.getJSONObject(i);

            contestList.add(new Contest(JsonContest.toString()));
        }

        return contestList;

    }

    public List<EntriesItem> getEntries(int page) throws IOException, JSONException {
        List<EntriesItem> entriesItemList = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_WELCOME_PAGE + page)
                        .build())
                .execute();

        String resp = response.body().string();

        JSONArray JsonEntriesArray = new JSONArray(resp);

        for (int i = 0; i < JsonEntriesArray.length(); i++) {

            JSONObject JsonEntries = JsonEntriesArray.getJSONObject(i);
            entriesItemList.add(new EntriesItem(JsonEntries));

        }

        return entriesItemList;
    }

    public List<FaqItem> getFaq() throws IOException, JSONException {

        List<FaqItem> faqItemList = new ArrayList<>();

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_FAQ)
                        .build())
                .execute();

        String resp = response.body().string();
        switch (response.code()){
            case 400: {
                return faqItemList;
            }
        }

        JSONArray JsonFaqList = new JSONArray(resp);

        for (int i = 0; i < JsonFaqList.length(); i++) {
            faqItemList.add(new FaqItem(JsonFaqList.getJSONObject(i).toString()));
        }

        return faqItemList;

    }

    /**
     * Given Contest ID get from server a ContestDetail Object
     * @param id Integer with Contest ID
     * @return ContestDetail
     * @throws IOException
     * @throws JSONException
     */
    public ContestDetail getContestDetail(int id) throws IOException, JSONException {

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_CONTEST_DETAIL + id)
                        .build())
                .execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return new ContestDetail(resp);
            }
            default: {
                JSONObject error = new JSONObject(resp);
                throw new IOException("Unexpected code: "+error.getString("error"));
            }
        }
    }

    /**
     * Get UserProfile object given a username
     *
     * @param username to find
     * @return user profile
     * @throws IOException
     * @throws JSONException
     */
    public UserProfile getUserProfile(String username) throws IOException, JSONException {

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(URL_USER_PROFILE + username)
                        .build())
                .execute();

        String resp = response.body().string();

        switch (response.code()){
            case 400: {

            }
        }

        JSONObject jsonObject = new JSONObject(resp);

        return new UserProfile(jsonObject);

    }

    public String updateUserProfile(Context context, String nickName, String firstName, String lastName,
                           String email_string, int institution_code, int country_code,
                           int language_code, int gender_code) throws NoLoginFileException, IOException, JSONException, UnauthorizedException {

        String token = LoginData.read(context).getToken();

        String url = "?apikey=" + URLEncoder.encode(API_KEY, "UTF-8") + "&token=" + URLEncoder.encode(token, "UTF-8");

        boolean someValue = false;

        if (nickName.length() != 0){
            someValue = true;
            url += "&nick=" + URLEncoder.encode(nickName, "UTF-8");
        }

        if (firstName.length() != 0) {
            someValue = true;
            url += "&name=" + URLEncoder.encode(firstName, "UTF-8");
        }

        if (lastName.length() != 0) {
            someValue = true;
            url += "&lastname=" + URLEncoder.encode(lastName, "UTF-8");
        }

        if (email_string.length() != 0) {
            someValue = true;
            url += "&email=" + URLEncoder.encode(email_string, "UTF-8");
        }

        if (institution_code != -1) {
            someValue = true;
            url += "&institution_id=" + URLEncoder.encode(""+institution_code, "UTF-8");
        }

        if (country_code != -1) {
            someValue = true;
            url += "&country_id=" + URLEncoder.encode(""+country_code, "UTF-8");
        }

        if (language_code != -1) {
            someValue = true;
            url += "&lid="+ URLEncoder.encode(""+language_code, "UTF-8");
        }

        if (gender_code != -1) {
            someValue = true;
            url += "&gender=" + URLEncoder.encode(""+gender_code, "UTF-8");
        }

        if (!someValue){
            return "No data to update";
        }

        url = URL_USER_PROFILE_UPDATE + url;

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(url)
                        .put(new FormBody.Builder().build())
                        .build())
                .execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return null;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        return updateUserProfile(context, nickName, firstName, lastName, email_string,
                                institution_code, country_code, language_code, gender_code);
                    }
                    case "token or apikey incorrect": {
                        getNewToken(context);
                        return updateUserProfile(context, nickName, firstName, lastName, email_string,
                                institution_code, country_code, language_code, gender_code);
                    }
                }

            }
            default: {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    return jsonObject.getString("error");
                }
                catch (JSONException e){
                    return response.code()+ resp;
                }
            }
        }


    }

    /**
     * Get valid classifications filters in problems list and theirs equivalents integer
     *
     * @param firstElement String represents default value for spinner
     *
     * @return Filter object. A Filter contains Key and Value pairs. Pair example: <"Ad-Hoc":"25">
     * @throws IOException
     * @throws JSONException
     */
    public Filter<Integer> getClassificationFilters(String firstElement) throws IOException, JSONException {

        Filter<Integer> languageFilter = new Filter<>(firstElement);

        JSONArray jsonArray = getFilter(URL_FILTER_CLASSIFICATION);

        for (int i = 0; i < jsonArray.length(); i++) {
            String classification = jsonArray.getJSONObject(i).getString("name");
            int filter = jsonArray.getJSONObject(i).getInt("idClassification");

            languageFilter.addFilter(classification, filter);
        }

        return languageFilter;

    }

    /**
     * Get valid filter for Institutions by Country.
     *
     * @param firstElement default value user in spinner
     * @param country_id Country ID
     *
     * @return Filter object. A filter contains InstitutionName and ID pairs. Pair example <"UCI":123>
     * @throws IOException
     * @throws JSONException
     */
    public Filter<Integer> getInstitutionFilter(Integer country_id, String firstElement) throws IOException, JSONException {

        Filter<Integer> institutions = new Filter<>(firstElement);

        List<InstitutionRank> institutionRank = getInstitutionByCountryRank(country_id);

        for (int i = 0; i < institutionRank.size(); i++) {
            String name = institutionRank.get(i).getInstitution();
            int id = institutionRank.get(i).getId();

            institutions.addFilter(name, id);
        }

        return institutions;

    }

    /**
     * Get valid filter for countries and theirs equivalents id.
     *
     * @param firstElement default value for use in spinner
     * @return Filter object. A filter contains CountryName and ID pairs. Pair example: <"Cuba":52>
     * @throws IOException
     * @throws JSONException
     */
    public Filter<Integer> getCountryFilter(String firstElement) throws IOException, JSONException {

        Filter<Integer> country = new Filter<>(firstElement);

        List<CountryRank> countryRank = getCountryRank(-1);

        for (int i = 0; i < countryRank.size(); i++) {
            String name = countryRank.get(i).getCountryName();
            int id = countryRank.get(i).getId();

            country.addFilter(name, id);
        }

        return country;

    }

    /**
     * Get valid language filters and theirs equivalents key
     *
     * @param firstElement String with a default value for spinner
     *
     * @return Filter object. A filter contain Language+Description and key pairs. Pair example: <"C++ (g++ 4.8.2)":"cpp">
     * @throws IOException
     * @throws JSONException
     */
    public Filter<String> getLanguageFilters(String firstElement) throws IOException, JSONException {

        Filter<String> languageFilter = new Filter<>(firstElement);

            JSONArray jsonArray = getFilter(URL_FILTER_LANGUAGE);

        for (int i = 0; i < jsonArray.length(); i++) {
            String name = jsonArray.getJSONObject(i).getString("description");
            String key = jsonArray.getJSONObject(i).getString("key");

            languageFilter.addFilter(name, key);
        }

        return languageFilter;

    }

    /**
     * Get valid language filters and theirs equivalents key
     *
     * @param firstElement String with a default value for spinner
     *
     * @return Filter object. A filter contain Language+Description and Integer Key pairs. Pair example: <"C++ (g++ 4.8.2)":"1">
     * @throws IOException
     * @throws JSONException
     */
    public Filter<Integer> getIDLanguageFilters(String firstElement) throws IOException, JSONException {

        Filter<Integer> languageFilter = new Filter<>(firstElement);

        JSONArray jsonArray = getFilter(URL_FILTER_LANGUAGE);

        for (int i = 0; i < jsonArray.length(); i++) {
            String name = jsonArray.getJSONObject(i).getString("description");
            int key = jsonArray.getJSONObject(i).getInt("id");

            languageFilter.addFilter(name, key);
        }

        return languageFilter;

    }

    /**
     * This is a final filter and it will never change value and key
     *
     * @param firstElement default element for filter
     * @return Filter object
     */
    public static Filter<String> getJudgmentFilter(String firstElement){

        Filter<String> judgmentFilter = new Filter<>(firstElement);

        judgmentFilter.addFilter("Accepted", "ac");
        judgmentFilter.addFilter("Internal Error", "sie");
        judgmentFilter.addFilter("Time Limit Exceeded", "tle");
        judgmentFilter.addFilter("Memory Limit Exceeded", "mle");
        judgmentFilter.addFilter("Wrong Answer", "wa");
        judgmentFilter.addFilter("Size Limit Exceeded", "sle");
        judgmentFilter.addFilter("Compilation Error", "ce");
        judgmentFilter.addFilter("Runtime Error", "rte");
        judgmentFilter.addFilter("Output Limit Exceeded", "ole");
        judgmentFilter.addFilter("Presentation Error", "pe");
        judgmentFilter.addFilter("Invalid Function", "ivf");
        judgmentFilter.addFilter("Judging", "jdg");

        return judgmentFilter;

    }

    /**
     * Returns JSONArray with specified filters
     *
     * @param url filter service url
     * @return JSONArray
     * @throws IOException
     * @throws JSONException
     */
    private static JSONArray getFilter(String url) throws IOException, JSONException {

        Response response = new OkHttpClient().newCall(
                new Request.Builder()
                        .url(url)
                        .build())
                .execute();

        if (response.code() != 200){
            throw new IOException("Unexpected code: "+response.code());
        }

        return new JSONArray(response.body().string());

    }

    /**
     * Send new email
     *
     * @param context Application context
     * @param to Send email to. Format: User ";" separated. Example: User1;User2;User3
     * @param subject Email subject
     * @param content Email body
     *
     * @return TRUE if email send successful or FALSE else.
     *
     * @throws NoLoginFileException
     * @throws IOException
     * @throws JSONException
     * @throws UnauthorizedException
     */
    public boolean sendEmail(Context context, String to, String subject, String content)
            throws NoLoginFileException, IOException, JSONException, UnauthorizedException {

        String token = LoginData.read(context).getToken();

        JSONObject json = new JSONObject();

        json.put("content", content);
        json.put("to", to);
        json.put("token", token);
        json.put("apikey", API_KEY);
        json.put("subject", subject);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(URL_MAIL_SEND)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200:{
                return true;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        sendEmail(context, to, subject, content);
                        break;
                    }

                }

            }
            default: {
                String error;
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    error = jsonObject.getString("error");
                }
                catch (JSONException e){
                    error = resp;
                }
                throw new IOException("Unexpected error: "+error);
            }
        }

    }

    /**
     * Delete email by ID
     *
     * @param context Application context
     * @param folder Email folder
     * @param emailID Email ID
     *
     * @return TRUE if consult was successful, else FALSE
     * @throws NoLoginFileException
     * @throws IOException
     * @throws JSONException
     * @throws UnauthorizedException
     */
    public boolean deleteEmail(Context context, MailFolder folder, int emailID)
            throws NoLoginFileException, IOException, JSONException, UnauthorizedException {

        String token = LoginData.read(context).getToken();

        String emailFolder = "";
        switch (folder){

            case INBOX: {
                emailFolder = "inbox";
                break;
            }
            case DRAFT: {
                emailFolder = "draft";
                break;
            }
            case OUTBOX: {
                emailFolder = "outbox";
                break;
            }

        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .delete()
                .header("apikey", API_KEY)
                .header("token", token)
                .url(URL_MAIL_DELETE + "/" + emailFolder + "/" + emailID)
                .build();
        Response response = client.newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200:{
                return true;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        deleteEmail(context, folder, emailID);
                        break;
                    }

                }

            }
            default: {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    throw new IOException("Unexpected error: " + jsonObject.getString("error"));
                }
                catch (JSONException e){
                    throw new IOException("Unexpected error: "+resp);
                }
            }
        }

    }

    /**
     * Get list of emails in in folder.
     *
     * @param context Application context
     * @param folder Folder to get emails
     *
     * @return Email list
     *
     * @throws NoLoginFileException
     * @throws IOException
     * @throws JSONException
     * @throws UnauthorizedException
     */
    public List<Email> getEmails(Context context, MailFolder folder) throws NoLoginFileException, IOException,
            JSONException, UnauthorizedException {

        List<Email> emails = new ArrayList<>();

        String token = LoginData.read(context).getToken();

        OkHttpClient client = new OkHttpClient();

        String url = "";
        switch (folder){
            case INBOX: {
                url = URL_MAIL_INBOX;
                break;
            }
            case OUTBOX: {
                url = URL_MAIL_OUTBOX;
                break;
            }
            case DRAFT: {
                url = URL_MAIL_DRAFT;
                break;
            }

        }

        Request request = new Request.Builder()
                .header("apikey", API_KEY)
                .header("token", token)
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200:{
                JSONArray jsonArray = new JSONArray(resp);
                for (int i = 0; i < jsonArray.length(); i++) {
                    emails.add(new Email(jsonArray.get(i).toString(), folder));
                }
                return emails;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        getEmails(context, folder);
                        break;
                    }

                }

            }
            default: {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    throw new IOException("Unexpected error: " + jsonObject.getString("error"));
                }
                catch (JSONException e){
                    throw new IOException("Unexpected error: "+resp);
                }
            }
        }
    }

    /**
     * Request for new password and send email to User
     *
     * @param email Email address
     * @return Null if was successful or a String with error in ether case
     * @throws IOException
     * @throws JSONException
     */
    public String forgotPassword(String email) throws IOException, JSONException {

        JSONObject json = new JSONObject();
        json.put("apikey", API_KEY);
        json.put("email", email);

        RequestBody body = RequestBody.create(JSON, json.toString());

        Request request = new Request.Builder()
                .url(URL_FORGOT_PASSWORD)
                .post(body)
                .build();

        Response response = new OkHttpClient().newCall(request)
                .execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return null;
            }
            default: {
                JSONObject jsonObject = new JSONObject(resp);
                return jsonObject.getString("error");
            }
        }


    }

    /**
     * Send new entry to COJ Board
     * @param context Application context
     * @param entry Text for new entry
     * @return Null if was successful or error message in other case
     *
     * @throws NoLoginFileException
     * @throws JSONException
     * @throws IOException
     * @throws UnauthorizedException
     */
    public String addEntry(Context context, String entry) throws NoLoginFileException, JSONException, IOException, UnauthorizedException {

        JSONObject json = new JSONObject();

        String token = LoginData.read(context).getToken();

        json.put("apikey", API_KEY);
        json.put("token", token);
        json.put("entryText", entry);

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url(URL_ADD_ENTRY)
                .post(body)
                .build();
        Response response = new OkHttpClient().newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return null;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        return addEntry(context, entry);
                    }
                    case "token or apikey incorrect": {
                        getNewToken(context);
                        return addEntry(context, entry);
                    }
                }
            }
            default: {
                JSONObject jsonObject = new JSONObject(response.body().string());
                return jsonObject.getString("error");
            }
        }
    }

    /**
     * Submit a problem solution
     * @param context Application context
     * @param id Problem id
     * @param keyLanguage Programming language for solution
     * @param source Solution
     * @return Error message or null if there was not error
     *
     * @throws NoLoginFileException
     * @throws JSONException
     * @throws IOException
     * @throws UnauthorizedException
     */
    public String submitSolution(Context context, String id, String keyLanguage, String source) throws NoLoginFileException, JSONException, IOException, UnauthorizedException {

        String token = LoginData.read(context).getToken();

        RequestBody body = RequestBody.create(JSON, source);
        Request request = new Request.Builder()
                .header("apikey", API_KEY)
                .header("token", token)
                .url(URL_JUDGMENT_SUBMIT + keyLanguage + "/"+id)
                .post(body)
                .build();

        Response response = new OkHttpClient().newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return null;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        return submitSolution(context, id, keyLanguage, source);
                    }
                    case "token or apikey incorrect": {
                        getNewToken(context);
                        return submitSolution(context, id, keyLanguage, source);
                    }
                }
            }
            default: {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    return jsonObject.getString("error");
                }
                catch (JSONException e){
                    return resp;
                }
            }
        }
    }

    /**
     * Mark an email read or unread depends on actual status
     *
     * @param context Application context
     * @param id Problem ID for toggle status
     * @return Error message if occurred or Null if was successful
     * @throws NoLoginFileException
     * @throws JSONException
     * @throws IOException
     * @throws UnauthorizedException
     */
    public String mailToggleStatus(Context context, String id) throws NoLoginFileException, JSONException, IOException, UnauthorizedException {

        String token = LoginData.read(context).getToken();

        Request request = new Request.Builder()
                .header("apikey", API_KEY)
                .header("token", token)
                .url(URL_MAIL_TOGGLE_STATUS + id)
                .put(new FormBody.Builder().build())
                .build();
        Response response = new OkHttpClient().newCall(request).execute();

        String resp = response.body().string();

        switch (response.code()){
            case 200: {
                return null;
            }
            case 401: {
                JSONObject jsonObject = new JSONObject(resp);
                String error = jsonObject.getString("error");

                switch (error){

                    case "token expirated": {
                        getNewToken(context);
                        return mailToggleStatus(context, id);
                    }
                    case "token or apikey incorrect": {
                        getNewToken(context);
                        return mailToggleStatus(context, id );
                    }
                }
            }
            default: {
                try {
                    JSONObject jsonObject = new JSONObject(resp);
                    return jsonObject.getString("error");
                }
                catch (JSONException e){
                    return resp;
                }
            }
        }
    }

}