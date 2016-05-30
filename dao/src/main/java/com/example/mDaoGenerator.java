package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class mDaoGenerator {

    private static final String PROJECT_DIR = System.getProperty("user.dir");
    private static final String OUT_DIR = PROJECT_DIR + "/app/src/main/java";

    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(1, "cu.uci.coj.Application.dao");

        addTables(schema);

        new DaoGenerator().generateAll(schema, OUT_DIR);
    }

    private static void addTables(Schema schema) {

        addProblemItem(schema);
        addProblem(schema);
        addEntrie(schema);
        addJudgment(schema);
        addUserStanding(schema);
        addCountryStanding(schema);
        addInstitutionStanding(schema);
        addComingContest(schema);
        addPreviousContest(schema);
        addRunningContest(schema);
        addProfile(schema);
        addInboxMessage(schema);
        addOutboxMessage(schema);
        addDraftMessage(schema);
        addFAQs(schema);

    }

    private static Entity addProblem(Schema schema) {

        Entity problem = schema.addEntity("DBProblem");
        problem.addIdProperty().primaryKey();
        problem.addStringProperty("description").notNull();

        return problem;

    }

    private static Entity addProblemItem(Schema schema) {

        Entity problemItem = schema.addEntity("DBProblemItem");
        problemItem.addIdProperty().primaryKey();
        problemItem.addStringProperty("item").notNull();

        return problemItem;
    }

    private static Entity addEntrie(Schema schema){

        Entity entries = schema.addEntity("DBEntries");
        entries.addIdProperty().primaryKey().autoincrement();
        entries.addStringProperty("content").notNull();

        return entries;
    }

    private static Entity addJudgment(Schema schema){

        Entity judgment = schema.addEntity("DBJudgments");
        judgment.addIdProperty().primaryKey();
        judgment.addStringProperty("judgment").notNull();

        return judgment;

    }

    private static Entity addUserStanding(Schema schema){

        Entity userStanding = schema.addEntity("DBUserStanding");
        userStanding.addIdProperty().primaryKey().autoincrement();
        userStanding.addIntProperty("rank").notNull();
        userStanding.addStringProperty("userStanding").notNull();

        return userStanding;

    }

    private static Entity addInstitutionStanding(Schema schema){

        Entity institutionStanding = schema.addEntity("DBInstitutionStanding");
        institutionStanding.addIdProperty().primaryKey().autoincrement();
        institutionStanding.addIntProperty("rank").notNull();
        institutionStanding.addStringProperty("institutionStanding").notNull();

        return institutionStanding;

    }

    private static Entity addCountryStanding(Schema schema){

        Entity countryStanding = schema.addEntity("DBCountryStanding");
        countryStanding.addIdProperty().primaryKey().autoincrement();
        countryStanding.addIntProperty("rank").notNull();
        countryStanding.addStringProperty("countryStanding").notNull();

        return countryStanding;

    }

    private static Entity addComingContest(Schema schema){

        Entity comingContest = schema.addEntity("DBComingContest");
        comingContest.addIdProperty().primaryKey();
        comingContest.addLongProperty("startTime").notNull();
        comingContest.addStringProperty("comingContest").notNull();

        return comingContest;

    }

    private static Entity addPreviousContest(Schema schema){

        Entity previousContest = schema.addEntity("DBPreviousContest");
        previousContest.addIdProperty().primaryKey();
        previousContest.addLongProperty("startTime").notNull();
        previousContest.addStringProperty("previousContest").notNull();

        return previousContest;

    }

    private static Entity addRunningContest(Schema schema){

        Entity runningContest = schema.addEntity("DBRunningContest");
        runningContest.addIdProperty().primaryKey();
        runningContest.addLongProperty("startTime").notNull();
        runningContest.addStringProperty("runningContest").notNull();

        return runningContest;

    }

    private static Entity addProfile(Schema schema){

        Entity profile = schema.addEntity("DBProfile");
        profile.addIdProperty().primaryKey();
        profile.addStringProperty("userProfile").notNull();

        return profile;

    }

    private static Entity addInboxMessage(Schema schema){

        Entity profile = schema.addEntity("DBInboxMessage");
        profile.addIdProperty().primaryKey();
        profile.addStringProperty("message").notNull();

        return profile;

    }

    private static Entity addOutboxMessage(Schema schema){

        Entity profile = schema.addEntity("DBOutboxMessage");
        profile.addIdProperty().primaryKey();
        profile.addStringProperty("message").notNull();

        return profile;

    }

    private static Entity addDraftMessage(Schema schema){

        Entity profile = schema.addEntity("DBDraftMessage");
        profile.addIdProperty().primaryKey();
        profile.addStringProperty("message").notNull();

        return profile;

    }

    private static Entity addFAQs(Schema schema){

        Entity profile = schema.addEntity("DBFaq");
        profile.addIdProperty().primaryKey().autoincrement();
        profile.addStringProperty("faqs").notNull();

        return profile;

    }
}
