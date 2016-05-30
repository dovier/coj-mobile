package cu.uci.coj.Application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Conexion;
import cu.uci.coj.R;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private TextView userView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageView coj_logo = (ImageView) findViewById(R.id.coj_logo);

        TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_SELF, 0);
        translate.setStartOffset(400);
        translate.setDuration(700);

        coj_logo.startAnimation(translate);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setStartOffset(1000);
        alpha.setDuration(300);

        LinearLayout login = (LinearLayout) findViewById(R.id.email_login_form);
        login.startAnimation(alpha);
        login.setVisibility(View.VISIBLE);

        // Set up the login form.
        userView = (TextView) findViewById(R.id.coj_user);

        TextView register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(Conexion.getInstance(getApplicationContext()).getURL_CREATE_ACCOUNT())));
            }
        });

        TextView forgot = (TextView) findViewById(R.id.forgot_password);
        forgot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RecoverPasswordActivity.class);
                startActivity(intent);

            }
        });

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        String user = userView.getText().toString();
        String pswd = mPasswordView.getText().toString();

        if (user.length() == 0){
            Toast.makeText(this, R.string.empty_username, Toast.LENGTH_LONG).show();
        }
        else if (user.length() < 3){
            Toast.makeText(this, R.string.short_username, Toast.LENGTH_LONG).show();
        }
        else if (pswd.length() == 0){
            Toast.makeText(this, R.string.empty_password, Toast.LENGTH_LONG).show();
        }
        else if (pswd.length() < 8){
            Toast.makeText(this, R.string.short_password, Toast.LENGTH_LONG).show();
        }
        else
            new UserLoginTask(this, user, pswd).execute();

    }

    public class ForgotPassword extends AsyncTask<String, Void, String>{

        protected WeakReference<Activity> weakReference;
        protected ProgressDialog progressDialog;

        public ForgotPassword(Activity activity){
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {

            final Activity activity = weakReference.get();
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
        protected String doInBackground(String... strings) {

            try {
                Conexion.getInstance(weakReference.get()).forgotPassword(strings[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {

            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();
            final Activity activity = weakReference.get();
            new ScreenOrientationLocker(activity).lock();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(weakReference.get(), R.string.error_occurred, Toast.LENGTH_LONG).show();
                }
            });
        }

        @Override
        protected void onPostExecute(final String message) {

            final Activity activity = weakReference.get();
            new ScreenOrientationLocker(activity).lock();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (message != null)
                        Toast.makeText(weakReference.get(), message, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(weakReference.get(), R.string.wait_few_seconds, Toast.LENGTH_LONG).show();
                }
            });

            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the institution.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        WeakReference<Activity> weakReference;
        private final String user;
        private final String pswd;

        UserLoginTask(Context context, String user, String pswd) {
            weakReference = new WeakReference<>((Activity)context);
            this.user = user;
            this.pswd = pswd;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                Conexion.getInstance(weakReference.get()).login(getApplicationContext(), user, pswd);
            } catch (IOException e) {
                System.out.println("Check network");
                e.printStackTrace();
                return false;
            } catch (JSONException e) {
                System.out.println("JSONException");
                e.printStackTrace();
                return false;
            } catch (UnauthorizedException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            final Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            if (success){
                Activity activity = weakReference.get();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (vibrator.hasVibrator()) {
                            long array[] = {0, 100, 100, 100};
                            vibrator.vibrate(array, -1);
                        }
                    }
                });

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else {
                if (vibrator.hasVibrator()) {
                    long array[] = {0, 100};
                    vibrator.vibrate(array, -1);
                }
                Toast.makeText(weakReference.get(), R.string.login_failed, Toast.LENGTH_LONG).show();
            }

        }

    }
}

