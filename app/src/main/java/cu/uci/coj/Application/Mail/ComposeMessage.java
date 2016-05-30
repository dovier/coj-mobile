package cu.uci.coj.Application.Mail;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.ScreenOrientationLocker;
import cu.uci.coj.Conexion;
import cu.uci.coj.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ComposeMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeMessage extends Fragment {

    private static final String ARGS_TO = "Send_to";
    private static final String ARGS_CONTENT = "content";
    private static final String ARGS_SUBJECT = "subject";
    private static final String ARGS_ID = "id";

    private static int id = 0;
    private String send_to = null;
    private String content = null;
    private String subject = null;

    public ComposeMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ComposeMessage.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeMessage newInstance() {
        ComposeMessage fragment = new ComposeMessage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ComposeMessage.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeMessage newInstance(String send_to) {
        ComposeMessage fragment = new ComposeMessage();
        Bundle args = new Bundle();
        args.putString(ARGS_TO, send_to);
        fragment.setArguments(args);
        return fragment;
    }

    public static ComposeMessage newInstance(int idEmail, String subject, String content) {
        ComposeMessage fragment = new ComposeMessage();
        Bundle args = new Bundle();
        args.putString(ARGS_SUBJECT, subject);
        args.putString(ARGS_CONTENT, content);
        args.putInt(ARGS_ID, idEmail);
        fragment.setArguments(args);
        return fragment;
    }

    public static ComposeMessage newInstance(int idEmail, String send_to, String subject, String content) {
        ComposeMessage fragment = new ComposeMessage();
        Bundle args = new Bundle();
        args.putString(ARGS_TO, send_to);
        args.putString(ARGS_SUBJECT, subject);
        args.putString(ARGS_CONTENT, content);
        args.putInt(ARGS_ID, idEmail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            send_to = getArguments().getString(ARGS_TO);
            subject = getArguments().getString(ARGS_SUBJECT);
            content = getArguments().getString(ARGS_CONTENT);
            id = getArguments().getInt(ARGS_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_compose_mensage, container, false);

        if (send_to != null){
            ((TextView) rootView.findViewById(R.id.to_editText)).setText(send_to);
        }
        if (subject != null){
            ((TextView) rootView.findViewById(R.id.subject_editText)).setText(subject);
        }
        if (content != null){
            ((TextView) rootView.findViewById(R.id.message_editText)).setText(Html.fromHtml(content));
        }

        ImageView send = (ImageView) rootView.findViewById(R.id.send_mail);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String to = ((TextView)rootView.findViewById(R.id.to_editText)).getText().toString();
                final String subject = ((TextView)rootView.findViewById(R.id.subject_editText)).getText().toString();
                Spanned spanned = ((TextView)rootView.findViewById(R.id.message_editText)).getEditableText();
                final String content = Html.toHtml(spanned);

                if (to.length() == 0){
                    Toast.makeText(getContext(), getResources().getString(R.string.empty_recipient), Toast.LENGTH_LONG).show();
                }
                else {
                    if (subject.length() == 0 && content.length() == 0){
                        Toast.makeText(getContext(), getResources().getString(R.string.empty_fields_error), Toast.LENGTH_LONG).show();
                    }
                    else if (subject.length() == 0 || content.length() == 0){
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(R.string.empty_fields_short);
                        alertDialog.setMessage(R.string.empty_fields);
                        alertDialog.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new mAsyncTask(getActivity()).execute(to, subject, content);
                            }
                        });
                        alertDialog.show();
                    }
                    else {
                        new mAsyncTask(getActivity()).execute(to, subject, content);
                    }
                }
            }
        });

        ImageView cancel = (ImageView) rootView.findViewById(R.id.discard_mail);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id != 0)
                    new Delete(getActivity()).execute(id);
                getFragmentManager().popBackStack();
            }
        });

        return rootView;
    }

    private static class Delete extends AsyncTask<Integer, Void, Void> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public Delete(FragmentActivity activity) {
            fragment_reference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Integer... ids) {

            Activity activity = fragment_reference.get();
            try {
                Conexion.getInstance(fragment_reference.get()).deleteEmail(activity, MailFolder.DRAFT, ids[0]);

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment_reference.get(), R.string.delete_ok, Toast.LENGTH_LONG).show();
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

    private static class mAsyncTask extends AsyncTask<String, Void, Void>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(FragmentActivity activity) {
            fragment_reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

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
        protected Void doInBackground(String... strings) {

            try {
                Conexion.getInstance(fragment_reference.get()).sendEmail(fragment_reference.get(), strings[0], strings[1], strings[2]);
                if (id != 0){
                    try {
                        Conexion.getInstance(fragment_reference.get()).deleteEmail(fragment_reference.get(), MailFolder.DRAFT, id);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                final FragmentActivity activity = fragment_reference.get();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment_reference.get(), R.string.send_ok, Toast.LENGTH_LONG).show();
                        fragment_reference.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fragment_reference.get().getSupportFragmentManager().popBackStack();
                            }
                        });
                    }
                });

            } catch (final NoLoginFileException | IOException | UnauthorizedException | JSONException e) {
                fragment_reference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(fragment_reference.get(), R.string.send_error + ": " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }
}
