package cu.uci.coj;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Application.Contests.Contest;
import cu.uci.coj.Application.Extras.EntriesItem;
import cu.uci.coj.Application.Extras.FaqItem;
import cu.uci.coj.Application.Judgments.Judgment;
import cu.uci.coj.Application.Mail.Email;
import cu.uci.coj.Application.Mail.MailFolder;
import cu.uci.coj.Application.Problems.Problem;
import cu.uci.coj.Application.Problems.ProblemItem;
import cu.uci.coj.Application.Profiles.UserProfile;
import cu.uci.coj.Application.Standings.CountryRank;
import cu.uci.coj.Application.Standings.InstitutionRank;
import cu.uci.coj.Application.Standings.UserRank;
import cu.uci.coj.Application.dao.DBComingContest;
import cu.uci.coj.Application.dao.DBComingContestDao;
import cu.uci.coj.Application.dao.DBCountryStanding;
import cu.uci.coj.Application.dao.DBCountryStandingDao;
import cu.uci.coj.Application.dao.DBDraftMessage;
import cu.uci.coj.Application.dao.DBDraftMessageDao;
import cu.uci.coj.Application.dao.DBEntries;
import cu.uci.coj.Application.dao.DBEntriesDao;
import cu.uci.coj.Application.dao.DBFaq;
import cu.uci.coj.Application.dao.DBFaqDao;
import cu.uci.coj.Application.dao.DBInboxMessage;
import cu.uci.coj.Application.dao.DBInboxMessageDao;
import cu.uci.coj.Application.dao.DBInstitutionStanding;
import cu.uci.coj.Application.dao.DBInstitutionStandingDao;
import cu.uci.coj.Application.dao.DBJudgments;
import cu.uci.coj.Application.dao.DBJudgmentsDao;
import cu.uci.coj.Application.dao.DBOutboxMessage;
import cu.uci.coj.Application.dao.DBOutboxMessageDao;
import cu.uci.coj.Application.dao.DBPreviousContest;
import cu.uci.coj.Application.dao.DBPreviousContestDao;
import cu.uci.coj.Application.dao.DBProblem;
import cu.uci.coj.Application.dao.DBProblemDao;
import cu.uci.coj.Application.dao.DBProblemItem;
import cu.uci.coj.Application.dao.DBProblemItemDao;
import cu.uci.coj.Application.dao.DBProfile;
import cu.uci.coj.Application.dao.DBProfileDao;
import cu.uci.coj.Application.dao.DBRunningContest;
import cu.uci.coj.Application.dao.DBRunningContestDao;
import cu.uci.coj.Application.dao.DBUserStanding;
import cu.uci.coj.Application.dao.DBUserStandingDao;
import cu.uci.coj.Application.dao.DaoMaster;
import cu.uci.coj.Application.dao.DaoSession;
import de.greenrobot.dao.async.AsyncOperation;
import de.greenrobot.dao.async.AsyncOperationListener;
import de.greenrobot.dao.async.AsyncSession;
import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by osvel on 4/19/16.
 */
public class DataBaseManager implements AsyncOperationListener {

    /**
     * Class tag. Used for debug.
     */
    private static final String TAG = DataBaseManager.class.getCanonicalName();

    private static DataBaseManager instance;

    private Context context;
    private DaoSession daoSession;

    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;

    public DataBaseManager(final Context context) {
        this.context = context;
    }

    @Override
    public void onAsyncOperationCompleted(AsyncOperation operation) {}

    public static DataBaseManager getInstance(Context context) {

        if (instance == null) {
            instance = new DataBaseManager(context);
        }

        return instance;
    }

    public void openReadableDb() throws SQLiteException {

        helper = new DaoMaster.DevOpenHelper(this.context, "coj-db", null);
        db = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);

    }

    public void openWritableDb() throws SQLiteException {

        helper = new DaoMaster.DevOpenHelper(this.context, "coj-db", null);
        db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        AsyncSession asyncSession = daoSession.startAsyncSession();
        asyncSession.setListener(this);

    }

    public synchronized void insertProblemItem(ProblemItem problemItem){

        JSONObject item = problemItem.getJSONObject();
        long id = problemItem.getLongId();

        try {
            if (item != null) {
                openWritableDb();

                DBProblemItem dbProblemItem = new DBProblemItem(id, item.toString());
                DBProblemItemDao dbProblemItemDao = daoSession.getDBProblemItemDao();
                dbProblemItemDao.insertOrReplace(dbProblemItem);
                Log.d(TAG, "insertProblemItem: " + id);

                daoSession.clear();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    public synchronized void insertProblem(long id, Problem problem){

        String prob = problem.getJsonString();

        try {
            if (prob != null) {
                openWritableDb();

                DBProblem dbProblem = new DBProblem(id, prob);
                DBProblemDao dbProblemDao = daoSession.getDBProblemDao();
                dbProblemDao.insertOrReplace(dbProblem);
                Log.d(TAG, "insertProblem: " + id);

                daoSession.clear();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public synchronized void insertProblem(ProblemItem problemItem, Problem problem){

        JSONObject item = problemItem.getJSONObject();
        String prob = problem.getJsonString();
        long id = problemItem.getLongId();

        try {
            if (prob != null && item != null) {
                openWritableDb();

                DBProblemItem dbProblemItem = new DBProblemItem(id, item.toString());
                DBProblemItemDao dbProblemItemDao = daoSession.getDBProblemItemDao();
                dbProblemItemDao.insertOrReplace(dbProblemItem);
                Log.d(TAG, "insertProblemItem: " + id);


                DBProblem dbProblem = new DBProblem(id, prob);
                DBProblemDao dbProblemDao = daoSession.getDBProblemDao();
                dbProblemDao.insertOrReplace(dbProblem);
                Log.d(TAG, "insertProblem: " + id);

                daoSession.clear();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    public synchronized void insertEntrie(String content){

        try {
            openWritableDb();

            DBEntries dbEntries = new DBEntries(null, content);
            DBEntriesDao dbEntriesDao = daoSession.getDBEntriesDao();
            dbEntriesDao.insert(dbEntries);
            Log.d(TAG, "insertEntrie: " + content);

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertJudgment(Judgment judgment){

        try {
            openWritableDb();

            DBJudgments dbJudgments = new DBJudgments(judgment.getLongId(), judgment.getJSONString());
            DBJudgmentsDao dbJudgmentsDao = daoSession.getDBJudgmentsDao();
            dbJudgmentsDao.insert(dbJudgments);
            Log.d(TAG, "insertJudgment: " + judgment.getId());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertUserRank(UserRank userRank){

        try {
            openWritableDb();

            DBUserStanding dbUserStanding = new DBUserStanding(null, userRank.getIntRank(), userRank.getJSONString());
            DBUserStandingDao dbUserStandingDao = daoSession.getDBUserStandingDao();
            dbUserStandingDao.insert(dbUserStanding);
            Log.d(TAG, "insertUserRank: " + userRank.getRank());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertInstitutionRank(InstitutionRank institutionRank){

        try {
            openWritableDb();

            DBInstitutionStanding dbInstitutionStanding = new DBInstitutionStanding(null, institutionRank.getIntRank(), institutionRank.getJSONString());
            DBInstitutionStandingDao dbInstitutionStandingDao = daoSession.getDBInstitutionStandingDao();
            dbInstitutionStandingDao.insert(dbInstitutionStanding);
            Log.d(TAG, "insertInstitutionRank: " + institutionRank.getRank());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertCountryRank(CountryRank countryRank){

        try {
            openWritableDb();

            DBCountryStanding dbCountryStanding = new DBCountryStanding(null, countryRank.getIntRank(), countryRank.getJSONString());
            DBCountryStandingDao dbCountryStandingDao = daoSession.getDBCountryStandingDao();
            dbCountryStandingDao.insert(dbCountryStanding);
            Log.d(TAG, "insertCountryRank: " + countryRank.getRank());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertComingContest(Contest contest){

        try {
            openWritableDb();

            DBComingContest dbComingContest = new DBComingContest(Long.parseLong(contest.getId()), contest.getDateStart().getTimeInMillis(), contest.getJSONString());
            DBComingContestDao dbComingContestDao = daoSession.getDBComingContestDao();
            dbComingContestDao.insert(dbComingContest);
            Log.d(TAG, "insertComingContest: " + contest.getId());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertRunningContest(Contest contest){

        try {
            openWritableDb();

            DBRunningContest dbRunningContest = new DBRunningContest(Long.parseLong(contest.getId()), contest.getDateStart().getTimeInMillis(), contest.getJSONString());
            DBRunningContestDao dbRunningContestDao = daoSession.getDBRunningContestDao();
            dbRunningContestDao.insert(dbRunningContest);
            Log.d(TAG, "insertRunningContest: " + contest.getId());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertPreviousContest(Contest contest){

        try {
            openWritableDb();

            DBPreviousContest dbPreviousContest = new DBPreviousContest(Long.parseLong(contest.getId()), contest.getDateStart().getTimeInMillis(), contest.getJSONString());
            DBPreviousContestDao dbPreviousContestDao = daoSession.getDBPreviousContestDao();
            dbPreviousContestDao.insert(dbPreviousContest);
            Log.d(TAG, "insertPreviousContest: " + contest.getId());

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertProfile(UserProfile profile){

        try {
            openWritableDb();

            DBProfile dbProfile = new DBProfile(null, profile.getJSONString());
            DBProfileDao dbProfileDao = daoSession.getDBProfileDao();
            dbProfileDao.insert(dbProfile);
            Log.d(TAG, "insertProfile");

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertFAQ(FaqItem faq){

        try {
            openWritableDb();

            DBFaq dbFaq = new DBFaq(null, faq.getJsonString());
            DBFaqDao dbFaqDao = daoSession.getDBFaqDao();
            dbFaqDao.insert(dbFaq);
            Log.d(TAG, "insertFaqh");

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertInboxMessage(Email email){

        try {
            openWritableDb();

            DBInboxMessage dbInboxMessage = new DBInboxMessage((long) email.getIdEmail(), email.getJSONString());
            DBInboxMessageDao dbInboxMessageDao = daoSession.getDBInboxMessageDao();
            dbInboxMessageDao.insert(dbInboxMessage);
            Log.d(TAG, "insertInboxMessage");

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertOutboxMessage(Email email){

        try {
            openWritableDb();

            DBOutboxMessage dbOutboxMessage = new DBOutboxMessage((long) email.getIdEmail(), email.getJSONString());
            DBOutboxMessageDao dbOutboxMessageDao = daoSession.getDBOutboxMessageDao();
            dbOutboxMessageDao.insert(dbOutboxMessage);
            Log.d(TAG, "insertOutboxMessage");

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertDraftMessage(Email email){

        try {
            openWritableDb();

            DBDraftMessage dbDraftMessage = new DBDraftMessage((long) email.getIdEmail(), email.getJSONString());
            DBDraftMessageDao dbDraftMessageDao = daoSession.getDBDraftMessageDao();
            dbDraftMessageDao.insert(dbDraftMessage);
            Log.d(TAG, "insertDraftMessage");

            daoSession.clear();

        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void insertMessage(Email email, MailFolder folder){

        switch (folder){
            case INBOX: {
                insertInboxMessage(email);
                break;
            }
            case OUTBOX: {
                insertOutboxMessage(email);
                break;
            }
            case DRAFT: {
                insertDraftMessage(email);
                break;
            }
        }
    }

    public synchronized UserProfile getUserProfile() throws JSONException {

        try {
            openReadableDb();
            DBProfileDao dbProfileDao = daoSession.getDBProfileDao();
            QueryBuilder qb = dbProfileDao.queryBuilder();
            List<DBProfile> contestQuery = qb.list();
            daoSession.clear();

            if (contestQuery.size() > 0)
                return new UserProfile(new JSONObject(contestQuery.get(0).getUserProfile()));
            return null;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<Contest> getPreviousContest() throws JSONException {

        try {
            openReadableDb();
            DBPreviousContestDao dbPreviousContest = daoSession.getDBPreviousContestDao();
            QueryBuilder qb = dbPreviousContest.queryBuilder();
            qb.orderDesc(DBPreviousContestDao.Properties.StartTime);
            List<DBPreviousContest> contestQuery = qb.list();
            daoSession.clear();

            List<Contest> contestList = new ArrayList<>();
            for (int i = 0; i < contestQuery.size(); i++) {
                contestList.add(new Contest(contestQuery.get(i).getPreviousContest()));
            }

            return contestList;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<Contest> getComingContest() throws JSONException {

        try {
            openReadableDb();
            DBComingContestDao dbComingContestDao = daoSession.getDBComingContestDao();
            QueryBuilder qb = dbComingContestDao.queryBuilder();
            qb.orderDesc(DBComingContestDao.Properties.StartTime);
            List<DBComingContest> contestQuery = qb.list();
            daoSession.clear();

            List<Contest> contestList = new ArrayList<>();
            for (int i = 0; i < contestQuery.size(); i++) {
                contestList.add(new Contest(contestQuery.get(i).getComingContest()));
            }

            return contestList;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<Contest> getRunningContest() throws JSONException {

        try {
            openReadableDb();
            DBRunningContestDao dbRunningContestDao = daoSession.getDBRunningContestDao();
            QueryBuilder qb = dbRunningContestDao.queryBuilder();
            qb.orderDesc(DBRunningContestDao.Properties.StartTime);
            List<DBRunningContest> contestQuery = qb.list();
            daoSession.clear();

            List<Contest> contestList = new ArrayList<>();
            for (int i = 0; i < contestQuery.size(); i++) {
                contestList.add(new Contest(contestQuery.get(i).getRunningContest()));
            }

            return contestList;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<EntriesItem> getEntries() throws JSONException {

        try {
            openReadableDb();
            DBEntriesDao dbEntriesDao = daoSession.getDBEntriesDao();
            QueryBuilder qb = dbEntriesDao.queryBuilder();
            qb.orderAsc(DBEntriesDao.Properties.Id);
            List<DBEntries> entriesQuery = qb.list();
            daoSession.clear();

            List<EntriesItem> entriesItems = new ArrayList<>();
            for (int i = 0; i < entriesQuery.size(); i++) {
                JSONObject jsonObject = new JSONObject(entriesQuery.get(i).getContent());
                entriesItems.add(new EntriesItem(jsonObject));
            }

            return entriesItems;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

  public synchronized List<FaqItem> getFAQs() throws JSONException {

        try {
            openReadableDb();
            DBFaqDao dbFaqDao = daoSession.getDBFaqDao();
            QueryBuilder qb = dbFaqDao.queryBuilder();
            List<DBFaq> faqsQuery = qb.list();
            daoSession.clear();

            List<FaqItem> emails = new ArrayList<>();
            for (int i = 0; i < faqsQuery.size(); i++) {
                emails.add(new FaqItem(faqsQuery.get(i).getFaqs()));
            }

            return emails;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

  public synchronized List<Email> getInboxMessages() throws JSONException {

        try {
            openReadableDb();
            DBInboxMessageDao dbInboxMessageDao = daoSession.getDBInboxMessageDao();
            QueryBuilder qb = dbInboxMessageDao.queryBuilder();
//            qb.orderAsc(DBEntriesDao.Properties.Id);
            List<DBInboxMessage> emailsQuery = qb.list();
            daoSession.clear();

            List<Email> emails = new ArrayList<>();
            for (int i = 0; i < emailsQuery.size(); i++) {
                emails.add(new Email(emailsQuery.get(i).getMessage(), MailFolder.INBOX));
            }

            return emails;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

  public synchronized List<Email> getOutboxMessages() throws JSONException {

        try {
            openReadableDb();
            DBOutboxMessageDao dbOutboxMessageDao = daoSession.getDBOutboxMessageDao();
            QueryBuilder qb = dbOutboxMessageDao.queryBuilder();
//            qb.orderAsc(DBEntriesDao.Properties.Id);
            List<DBOutboxMessage> emailsQuery = qb.list();
            daoSession.clear();

            List<Email> emails = new ArrayList<>();
            for (int i = 0; i < emailsQuery.size(); i++) {
                emails.add(new Email(emailsQuery.get(i).getMessage(), MailFolder.OUTBOX));
            }

            return emails;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

  public synchronized List<Email> getDraftMessages() throws JSONException {

        try {
            openReadableDb();
            DBDraftMessageDao dbDraftMessageDao = daoSession.getDBDraftMessageDao();
            QueryBuilder qb = dbDraftMessageDao.queryBuilder();
//            qb.orderAsc(DBEntriesDao.Properties.Id);
            List<DBOutboxMessage> emailsQuery = qb.list();
            daoSession.clear();

            List<Email> emails = new ArrayList<>();
            for (int i = 0; i < emailsQuery.size(); i++) {
                emails.add(new Email(emailsQuery.get(i).getMessage(), MailFolder.DRAFT));
            }

            return emails;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<Email> getEmails(MailFolder folder) throws JSONException {

        switch (folder){
            case INBOX: {
                return getInboxMessages();
            }
            case OUTBOX: {
                return getOutboxMessages();
            }
            case DRAFT: {
                return getDraftMessages();
            }
            default:
                return null;
        }

    }

    public synchronized List<Judgment> getJudgments() throws JSONException {

        try {
            openReadableDb();
            DBJudgmentsDao dbJudgmentsDao = daoSession.getDBJudgmentsDao();
            QueryBuilder qb = dbJudgmentsDao.queryBuilder();
            qb.orderDesc(DBJudgmentsDao.Properties.Id);
            List<DBJudgments> dbJudgmentsList = qb.list();
            daoSession.clear();

            List<Judgment> judgmentList = new ArrayList<>();
            for (int i = 0; i < dbJudgmentsList.size(); i++) {
                JSONObject jsonObject = new JSONObject(dbJudgmentsList.get(i).getJudgment());
                judgmentList.add(new Judgment(jsonObject));
            }

            return judgmentList;
        }
        catch (SQLiteException e){
            e.printStackTrace();
        }

        return null;

    }

    public synchronized List<ProblemItem> getProblemsItem() throws JSONException {

        try{
            openReadableDb();
            DBProblemItemDao dbProblemItemDao = daoSession.getDBProblemItemDao();
            QueryBuilder qb = dbProblemItemDao.queryBuilder();
            qb.orderAsc(DBProblemItemDao.Properties.Id);
            List<DBProblemItem> problemItems = qb.list();
            daoSession.clear();

            List<ProblemItem> problemItemList = new ArrayList<>();
            for (int i = 0; i < problemItems.size(); i++) {
                problemItemList.add(new ProblemItem(new JSONObject(problemItems.get(i).getItem())));
            }

            return problemItemList;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<UserRank> getUserStandings() throws JSONException {

        try{
            openReadableDb();
            DBUserStandingDao dbUserStandingDao = daoSession.getDBUserStandingDao();
            QueryBuilder qb = dbUserStandingDao.queryBuilder();
            qb.orderAsc(DBUserStandingDao.Properties.Rank);
            List<DBUserStanding> userStandings = qb.list();
            daoSession.clear();

            List<UserRank> userRanks = new ArrayList<>();
            for (int i = 0; i < userStandings.size(); i++) {
                userRanks.add(new UserRank(new JSONObject(userStandings.get(i).getUserStanding())));
            }

            return userRanks;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<InstitutionRank> getInstitutionStandings() throws JSONException {

        try{
            openReadableDb();
            DBInstitutionStandingDao dbInstitutionStandingDao = daoSession.getDBInstitutionStandingDao();
            QueryBuilder qb = dbInstitutionStandingDao.queryBuilder();
            qb.orderAsc(DBInstitutionStandingDao.Properties.Rank);
            List<DBInstitutionStanding> institutionStandings = qb.list();
            daoSession.clear();

            List<InstitutionRank> institutionRanks = new ArrayList<>();
            for (int i = 0; i < institutionStandings.size(); i++) {
                institutionRanks.add(new InstitutionRank(new JSONObject(institutionStandings.get(i).getInstitutionStanding())));
            }

            return institutionRanks;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized List<CountryRank> getCountryStandings() throws JSONException {

        try{
            openReadableDb();
            DBCountryStandingDao dbCountryStandingDao = daoSession.getDBCountryStandingDao();
            QueryBuilder qb = dbCountryStandingDao.queryBuilder();
            qb.orderAsc(DBCountryStandingDao.Properties.Rank);
            List<DBCountryStanding> countryStandings = qb.list();
            daoSession.clear();

            List<CountryRank> countryRanks = new ArrayList<>();
            for (int i = 0; i < countryStandings.size(); i++) {
                countryRanks.add(new CountryRank(new JSONObject(countryStandings.get(i).getCountryStanding())));
            }

            return countryRanks;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized Problem getProblemByID(int id) throws JSONException, IndexOutOfBoundsException {

        try {
            openReadableDb();
            DBProblemDao dbProblemDao = daoSession.getDBProblemDao();
            QueryBuilder qb = dbProblemDao.queryBuilder();
            qb.where(DBProblemDao.Properties.Id.eq(id));
            List<DBProblem> problem = qb.list();
            daoSession.clear();
            return new Problem(new JSONObject(problem.get(0).getDescription()));
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public synchronized void deleteAllData(){

        deleteAllFAQs();
        deleteAllInboxMessages();
        deleteAllProfiles();
        deleteAllOutboxMessages();
        deleteAllDraftsMessages();
        deleteAllPreviousContest();
        deleteAllComingContest();
        deleteAllRunningContest();
        deleteAllEntries();
        deleteAllJudgment();
        deleteAllUserStandings();
        deleteAllInstitutionStandings();
        deleteAllCountryStandings();
        deleteAllProblems();

    }

    public synchronized void deleteAllFAQs(){

        try {
            openWritableDb();

            DBFaqDao dbFaqDao = daoSession.getDBFaqDao();
            dbFaqDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllProfiles(){

        try {
            openWritableDb();

            DBProfileDao dbProfileDao = daoSession.getDBProfileDao();
            dbProfileDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllInboxMessages(){

        try {
            openWritableDb();

            DBInboxMessageDao dbInboxMessageDao = daoSession.getDBInboxMessageDao();
            dbInboxMessageDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllOutboxMessages(){

        try {
            openWritableDb();

            DBOutboxMessageDao dbOutboxMessageDao = daoSession.getDBOutboxMessageDao();
            dbOutboxMessageDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllDraftsMessages(){

        try {
            openWritableDb();

            DBDraftMessageDao dbDraftMessageDao = daoSession.getDBDraftMessageDao();
            dbDraftMessageDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllEmails(MailFolder folder){

        switch (folder){
            case INBOX: {
                deleteAllInboxMessages();
                break;
            }
            case OUTBOX: {
                deleteAllOutboxMessages();
                break;
            }
            case DRAFT: {
                deleteAllDraftsMessages();
                break;
            }
        }

    }

    public synchronized void deleteAllPreviousContest(){

        try {
            openWritableDb();

            DBPreviousContestDao dbContest = daoSession.getDBPreviousContestDao();
            dbContest.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllRunningContest(){

        try {
            openWritableDb();

            DBRunningContestDao dbContest = daoSession.getDBRunningContestDao();
            dbContest.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllComingContest(){

        try {
            openWritableDb();

            DBComingContestDao dbContest = daoSession.getDBComingContestDao();
            dbContest.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllEntries(){

        try {
            openWritableDb();

            DBEntriesDao dbEntriesDao = daoSession.getDBEntriesDao();
            dbEntriesDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllJudgment(){

        try {
            openWritableDb();

            DBJudgmentsDao dbJudgmentsDao = daoSession.getDBJudgmentsDao();
            dbJudgmentsDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllUserStandings(){

        try {
            openWritableDb();

            DBUserStandingDao userStandingDao = daoSession.getDBUserStandingDao();
            userStandingDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllInstitutionStandings(){

        try {
            openWritableDb();

            DBInstitutionStandingDao institutionStandingDao = daoSession.getDBInstitutionStandingDao();
            institutionStandingDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllCountryStandings(){

        try {
            openWritableDb();

            DBCountryStandingDao countryStandingDao = daoSession.getDBCountryStandingDao();
            countryStandingDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteAllProblems(){

        try {
            openWritableDb();

            DBProblemItemDao dbProblemItemDao = daoSession.getDBProblemItemDao();
            dbProblemItemDao.deleteAll();

            DBProblemDao dbProblemDao = daoSession.getDBProblemDao();
            dbProblemDao.deleteAll();

            daoSession.clear();
        } catch (SQLiteException e){
            e.printStackTrace();
        }

    }

    public synchronized void deleteProblem(int id) {

        try {
            openWritableDb();

            DBProblemItemDao dbProblemItemDao = daoSession.getDBProblemItemDao();
            dbProblemItemDao.deleteByKey((long) id);

            DBProblemDao dbProblemDao = daoSession.getDBProblemDao();
            dbProblemDao.deleteByKey((long) id);

            daoSession.clear();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }

    }

    public void closeDbConnections() {
        if (daoSession != null) {
            daoSession.clear();
            daoSession = null;
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        if (helper != null) {
            helper.close();
            helper = null;
        }
        if (instance != null) {
            instance = null;
        }
    }
}
