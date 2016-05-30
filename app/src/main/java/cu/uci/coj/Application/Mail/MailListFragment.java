package cu.uci.coj.Application.Mail;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;

/**
 * Created by osvel on 5/7/16.
 */
public class MailListFragment extends Fragment {

    private static String ARGS_ADAPTER = "adapter";
    private static String ARGS_FOLDER = "folder";
    private static String ARGS_ERROR = "error";

    private static boolean connectionError = false;
    private static MailFolder folder;
    private static EmailList adapter;

    public MailListFragment() {
    }

    public static MailListFragment newInstance(MailFolder folder){
        MailListFragment fragment = new MailListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_FOLDER, folder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_FOLDER, folder);
        outState.putSerializable(ARGS_ADAPTER, adapter);
        outState.putBoolean(ARGS_ERROR, connectionError);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            folder = (MailFolder) savedInstanceState.getSerializable(ARGS_FOLDER);
            adapter = (EmailList) savedInstanceState.getSerializable(ARGS_ADAPTER);
            connectionError = savedInstanceState.getBoolean(ARGS_ERROR);

            if (connectionError){
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.email_list).setVisibility(View.GONE);
            }
            else {
                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_email_list);
                recyclerView.setAdapter(adapter);

                getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                getActivity().findViewById(R.id.email_list).setVisibility(View.VISIBLE);
            }
        }
        else {
            new mAsyncTask(getActivity()).execute(folder);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        folder = (MailFolder) getArguments().getSerializable(ARGS_FOLDER);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootVew = inflater.inflate(R.layout.fragment_mail_list, container, false);

        switch (folder){
            case INBOX: {
                rootVew.findViewById(R.id.to).setVisibility(View.GONE);
                break;
            }
            case OUTBOX: {
                rootVew.findViewById(R.id.from).setVisibility(View.GONE);
                break;
            }
            case DRAFT: {
                rootVew.findViewById(R.id.to).setVisibility(View.GONE);
                rootVew.findViewById(R.id.from).setVisibility(View.GONE);
                break;
            }
        }

        rootVew.findViewById(R.id.connection_error).setVisibility(View.GONE);
        rootVew.findViewById(R.id.email_list).setVisibility(View.GONE);

        return rootVew;
    }

    public static class mAsyncTask extends AsyncTask<MailFolder, Void, List<Email>>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(FragmentActivity activity) {
            this.fragment_reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            final FragmentActivity activity = fragment_reference.get();

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
        protected List<Email> doInBackground(MailFolder... folders) {

            List<Email> emails = null;

            MailFolder folder = folders[0];

            try {
                emails = Conexion.getInstance(fragment_reference.get()).getEmails(fragment_reference.get(), folder);
                connectionError = false;
            } catch (NoLoginFileException | UnauthorizedException e) {
                e.printStackTrace();
            } catch (IOException | JSONException e){

                connectionError = true;
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get());
                try {
                    emails = dataBaseManager.getEmails(folder);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }

            }

            if (emails == null)
                cancel(true);

            return emails;
        }

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
            final LinearLayout errorView = (LinearLayout) fragment_reference.get().findViewById(R.id.connection_error);
            final LinearLayout mail_list = (LinearLayout) fragment_reference.get().findViewById(R.id.email_list);
            fragment_reference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    errorView.setVisibility(View.VISIBLE);
                    mail_list.setVisibility(View.GONE);
                }
            });
            connectionError = true;
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<Email> emails) {

            if (!connectionError){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get());
                dataBaseManager.deleteAllEmails(folder);
                for (int i = 0; i < emails.size(); i++) {
                    dataBaseManager.insertMessage(emails.get(i), folder);
                }
                dataBaseManager.closeDbConnections();
            }

            if (emails.size() != 0 || !connectionError) {
                adapter = new EmailList(emails);
                RecyclerView recyclerView = (RecyclerView) fragment_reference.get().findViewById(R.id.recycler_email_list);
                recyclerView.setAdapter(adapter);

                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.GONE);
                fragment_reference.get().findViewById(R.id.email_list).setVisibility(View.VISIBLE);
            }
            else if (connectionError){
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                fragment_reference.get().findViewById(R.id.email_list).setVisibility(View.GONE);
            }

            if (emails.size() != 0)
                connectionError = false;

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }
}
