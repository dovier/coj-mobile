package cu.uci.coj.Application;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.R;

public class RecoverPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final EditText emailEditText = (EditText) findViewById(R.id.email);
        Button recover = (Button) findViewById(R.id.recover);
        Button reset = (Button) findViewById(R.id.reset);

        final Activity activity = this;

        recover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                if (email.length() != 0 && email.contains("@"))
                    new mAsyncTask(activity).execute(email);
                else
                    Toast.makeText(activity, R.string.invalid_email, Toast.LENGTH_LONG).show();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditText.setText("");
            }
        });

    }

    public static class mAsyncTask extends AsyncTask<String, Void, String>{

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
        protected String doInBackground(String... emails) {

            String message;
            try {
                message = Conexion.getInstance(reference.get()).forgotPassword(emails[0]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                message = e.getMessage();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {

            progressDialog.dismiss();
            new ScreenOrientationLocker(reference.get()).unlock();

            if (message == null)
                Toast.makeText(reference.get(), R.string.wait_few_seconds, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(reference.get(), reference.get().getResources().getString(R.string.error_occurred) +": "+message, Toast.LENGTH_LONG).show();

        }

    }

}
