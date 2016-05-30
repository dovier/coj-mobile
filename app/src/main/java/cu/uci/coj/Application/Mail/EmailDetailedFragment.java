package cu.uci.coj.Application.Mail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spanned;
import android.text.SpannedString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.LoginData;
import cu.uci.coj.R;

/**
 * Created by osvel on 5/8/16.
 */
public class EmailDetailedFragment extends Fragment {

    private static String ARGS_EMAIL = "email";

    private static Email email;

    public EmailDetailedFragment() {
    }

    public static EmailDetailedFragment newInstance(Email email){
        EmailDetailedFragment fragment = new EmailDetailedFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null){
            email = (Email)getArguments().getSerializable(ARGS_EMAIL);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_EMAIL, email);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            email = (Email) savedInstanceState.getSerializable(ARGS_EMAIL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_view_message, container, false);

        String user;
        try {
            user = LoginData.read(getContext()).getUser();
        } catch (NoLoginFileException e) {
            user = "";
            e.printStackTrace();
        }

        if (!email.isRead()){
            new ToggleStatus(getActivity()).execute(email.getIdEmail());
        }

        TextView textView = (TextView)rootView.findViewById(R.id.to);
        textView.setText(email.getStringTo() == null ? user : email.getStringTo());
        textView = (TextView)rootView.findViewById(R.id.from);
        textView.setText(email.getFrom() == null ? user : email.getFrom());
        textView = (TextView)rootView.findViewById(R.id.subject);
        textView.setText(email.getSubject());
        textView = (TextView)rootView.findViewById(R.id.date);
        textView.setText(email.getStringDate());
        System.out.println("lol " + email.getContent());
        Spanned spanned = new SpannedString(email.getContent());
        WebView webView = (WebView)rootView.findViewById(R.id.content);
//        webView.loadData(Html.toHtml(spanned), "text/html; charset=utf-8", null);
        webView.loadData(email.getContent(), "text/html; charset=utf-8", null);

        ImageView imageView = (ImageView) rootView.findViewById(R.id.delete);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new mAsyncTask(getActivity()).execute();
            }
        });

        imageView = (ImageView) rootView.findViewById(R.id.reply);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String to = email.getFrom() == null ? email.getStringTo() : email.getFrom();
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, ComposeMessage.newInstance(to))
                        .addToBackStack(null)
                        .commit();
            }
        });

        imageView = (ImageView) rootView.findViewById(R.id.forward);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, ComposeMessage.newInstance(0, "Fwd: " + email.getSubject(), email.getContent()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    public static class ToggleStatus extends AsyncTask<Integer, Void, Void>{

        protected WeakReference<FragmentActivity> fragment_reference;

        public ToggleStatus(FragmentActivity activity) {
            this.fragment_reference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            try {
                Conexion.getInstance(fragment_reference.get()).mailToggleStatus(fragment_reference.get(), "" + integers[0]);
            } catch (NoLoginFileException | JSONException | IOException | UnauthorizedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private static class mAsyncTask extends AsyncTask<Void, Void, Void> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(FragmentActivity activity) {
            fragment_reference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Activity activity = fragment_reference.get();
            try {
                Conexion.getInstance(fragment_reference.get()).deleteEmail(activity, email.getFolder(), email.getIdEmail());

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment_reference.get(), R.string.delete_ok, Toast.LENGTH_LONG).show();
                        fragment_reference.get().getSupportFragmentManager().popBackStack();
                    }
                });
            } catch (JSONException | UnauthorizedException | NoLoginFileException | IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment_reference.get(), R.string.delete_error, Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            return null;
        }
    }

}
