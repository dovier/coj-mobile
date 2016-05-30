package cu.uci.coj.Application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import cu.uci.coj.Application.Behaviors.AppBarLayoutBehavior;
import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Contests.ComingContestFragment;
import cu.uci.coj.Application.Contests.PreviousContestFragment;
import cu.uci.coj.Application.Contests.RunningContestFragment;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Extras.FaqFragment;
import cu.uci.coj.Application.Extras.StartFragment;
import cu.uci.coj.Application.Judgments.JudgmentsFragment;
import cu.uci.coj.Application.Mail.MailFolder;
import cu.uci.coj.Application.Mail.MailListFragment;
import cu.uci.coj.Application.Problems.ProblemsFragment;
import cu.uci.coj.Application.Profiles.CompareFragment;
import cu.uci.coj.Application.Profiles.EditFragment;
import cu.uci.coj.Application.Profiles.ProfileFragment;
import cu.uci.coj.R;
import cu.uci.coj.Application.Standings.UserStandingFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String FRAGMENT_TAG = "start_fragment";
    private static boolean login = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //verificar el login from file
        LoginData loginData = null;
        try {
            loginData = LoginData.read(this);
            login = loginData != null;
        } catch (NoLoginFileException e) {
            login = false;
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        //definir el toolbar como actionbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //crear el drawer menu y asignarlo al toolbar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //collapsar el app_bar y asignarle un comportamiento para que no se expanda
        AppBarLayout app_bar = (AppBarLayout) findViewById(R.id.app_bar);
        app_bar.setExpanded(false, false);
        AppBarLayoutBehavior appBarBehavior = new AppBarLayoutBehavior(false);
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) app_bar.getLayoutParams();
        appBarLayoutParams.setBehavior(appBarBehavior);
        app_bar.setLayoutParams(appBarLayoutParams);

        invalidateOptionsMenu();
        updateMenu();

        //iniciar el primer fragment de la aplicación
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
            ft.replace(R.id.container, StartFragment.newInstance(login), FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void updateMenu(){
        //mostrar/ocultar el menu perfil segun el estado de la variable login
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.menu_profile).setVisible(login);
        menu.findItem(R.id.menu_mail).setVisible(login);
        navigationView.invalidate();

        invalidateOptionsMenu();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            switch (getSupportFragmentManager().getBackStackEntryCount()){
                case 1: {
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
                    if (fragment != null && fragment.isVisible()) {

                        AlertDialog.Builder close = new AlertDialog.Builder(this);
                        close.setNegativeButton(R.string.cancel, null);
                        close.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                        close.setTitle(R.string.close);
                        close.setMessage(R.string.close_question);
                        close.create().show();

                    }
                    else {
                        getSupportFragmentManager().popBackStack();

                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                                .replace(R.id.container, StartFragment.newInstance(login), FRAGMENT_TAG)
                                .addToBackStack(null)
                                .commit();

                        cleanDrawerMenu();
                    }

                    break;
                }
                default: {
                    super.onBackPressed();
                }

            }
        }
    }

    public void cleanDrawerMenu(){

        //desmarcar las opciones del menu
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            for (int j = 0; j < menu.getItem(i).getSubMenu().size(); j++) {
                menu.getItem(i).getSubMenu().getItem(j).setChecked(false);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.getItem(1).setVisible(!login);
        menu.getItem(2).setVisible(login);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_login: {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                cleanDrawerMenu();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
                return true;
            }
            case R.id.action_faq: {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                cleanDrawerMenu();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, FaqFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            case R.id.action_logout: {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                login = !LoginData.delete(this);

                DataBaseManager db = DataBaseManager.getInstance(getApplicationContext());
                db.deleteAllProfiles();
                db.deleteAllEmails(MailFolder.INBOX);
                db.deleteAllEmails(MailFolder.OUTBOX);
                db.deleteAllEmails(MailFolder.DRAFT);
                db.deleteAllProblems();
                db.closeDbConnections();

                updateMenu();
                cleanDrawerMenu();

                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, StartFragment.newInstance(login), FRAGMENT_TAG)
                        .addToBackStack(null)
                        .commit();
                return true;
            }
            case  R.id.server_settings: {

                AlertDialog.Builder preferenceMessage = new AlertDialog.Builder(this);
                final View preferenceView = getLayoutInflater().inflate(R.layout.preference_dialog, null);
                preferenceMessage.setView(preferenceView);

                final EditText serverEdit = (EditText) preferenceView.findViewById(R.id.coj_server);

                final String preference_name = getResources().getString(R.string.preference_name);

                SharedPreferences prefs = getSharedPreferences(preference_name, Context.MODE_PRIVATE);
                String server = prefs.getString(preference_name, Conexion.DEFAULT_COJ_URL);

                serverEdit.setText(server);

                preferenceMessage.setTitle(R.string.server_preference);

                final SharedPreferences.Editor editor = getSharedPreferences(preference_name, MODE_PRIVATE).edit();

                preferenceMessage.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String new_server = serverEdit.getText().toString();

                        if (Patterns.WEB_URL.matcher(new_server).matches()){
                            editor.putString(preference_name, new_server);
                            editor.apply();

                            //clean database
                            DataBaseManager db = DataBaseManager.getInstance(getApplicationContext());
                            db.deleteAllData();
                            db.closeDbConnections();

                            //clean login data
                            LoginData.delete(getApplicationContext());

                            Toast.makeText(getApplicationContext(), R.string.restart_application, Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), R.string.invalid_server, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                preferenceMessage.setNegativeButton(R.string.cancel, null);
                preferenceMessage.show();

                break;
            }
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right);

        switch (id){
            case R.id.view_profile:{
                try {
                    LoginData loginData = LoginData.read(this);
                    ft.replace(R.id.container, ProfileFragment.newInstance(loginData.getUser(), true))
                            .addToBackStack(null)
                            .commit();
                } catch (NoLoginFileException e) {
                    e.printStackTrace();
                    ft.commit();
                }
                break;
            }
            case R.id.edit_account:{
                ft.replace(R.id.container, EditFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.problems:{
                ft.replace(R.id.container, ProblemsFragment.newInstance(login), "problems_fragment")
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.judgments:{
                ft.replace(R.id.container, JudgmentsFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.standings:{
                ft.replace(R.id.container, UserStandingFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.inbox:{
                ft.replace(R.id.container, MailListFragment.newInstance(MailFolder.INBOX))
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.outbox:{
                ft.replace(R.id.container, MailListFragment.newInstance(MailFolder.OUTBOX))
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.draft:{
                ft.replace(R.id.container, MailListFragment.newInstance(MailFolder.DRAFT))
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.compare:{

                ft.replace(R.id.container, CompareFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
                break;
            }
            case R.id.coming:{

                ft.replace(R.id.container, ComingContestFragment.newInstance())
                        .addToBackStack(null)
                        .commit();

                break;
            }
            case R.id.previous:{

                ft.replace(R.id.container, PreviousContestFragment.newInstance())
                        .addToBackStack(null)
                        .commit();

                break;
            }
            case R.id.running:{

                ft.replace(R.id.container, RunningContestFragment.newInstance())
                        .addToBackStack(null)
                        .commit();

                break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    static class mAsyncTask extends AsyncTask<Void, Void, Void>{

        protected WeakReference<Activity> reference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(Activity reference) {
            this.reference = new WeakReference<>(reference);
        }

        @Override
        protected void onPreExecute() {
            final Activity activity = reference.get();

            new ScreenOrientationLocker(activity).lock();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(activity, "",
                            activity.getString(R.string.loading), true);
                }
            });
        }

        @Override
        protected Void doInBackground(Void... voids) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            new ScreenOrientationLocker(reference.get()).unlock();
        }
    }
}




