package cu.uci.coj.Application.Problems;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.Filters.Filter;
import cu.uci.coj.Application.Judgments.JudgmentsFragment;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;

/**
 * Created by osvel on 5/17/16.
 */
public class SubmitFragment extends Fragment {

    private final static int RESULT = 1;

    private static String ARGS_ERROR = "error";
    private static String ARGS_LANGUAGES = "filter";
    private static String ARGS_SOURCE_CODE = "source_code";
    private static String ARGS_SOURCE_PATH = "source_path";
    private static String ARGS_PROBLEM_ID = "problem_id";

    private static Filter<String> language;
    private static String source_code;
    private static String source_path;
    private static int problem_id;
    private static boolean error = false;

    public SubmitFragment() {
    }

    public static SubmitFragment newInstance(int id){
        SubmitFragment fragment = new SubmitFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_PROBLEM_ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null){
            problem_id = getArguments().getInt(ARGS_PROBLEM_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ARGS_ERROR, error);
        outState.putSerializable(ARGS_LANGUAGES, language);
        outState.putString(ARGS_SOURCE_CODE, source_code);
        outState.putString(ARGS_SOURCE_PATH, source_path);
        outState.putInt(ARGS_PROBLEM_ID, problem_id);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            error = savedInstanceState.getBoolean(ARGS_ERROR);
            source_code = savedInstanceState.getString(ARGS_SOURCE_CODE);
            source_path = savedInstanceState.getString(ARGS_SOURCE_PATH);
            language = (Filter<String>) savedInstanceState.getSerializable(ARGS_LANGUAGES);
            problem_id = savedInstanceState.getInt(ARGS_PROBLEM_ID);

            if (!error){
                EditText editText = (EditText) getActivity().findViewById(R.id.problem_id);
                editText.setText(problem_id == 0 ? "" : ""+problem_id);

                editText = (EditText) getActivity().findViewById(R.id.file_path);
                editText.setText(source_path == null ? "" : source_path);

                editText = (EditText) getActivity().findViewById(R.id.source_code_text);
                editText.setText(source_code == null ? "" : source_code);

                Spinner spinner = (Spinner) getActivity().findViewById(R.id.language_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item,
                        language.getFilterArray());
                spinner.setAdapter(adapter);

                getActivity().findViewById(R.id.submit_nested_scroll).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
            }
            else {
                getActivity().findViewById(R.id.submit_nested_scroll).setVisibility(View.GONE);
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
            }

        }
        else {
            new Languages(getActivity()).execute();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_submit, container, false);

        Button upload = (Button) rootView.findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, RESULT);

            }
        });

        final EditText id = (EditText) rootView.findViewById(R.id.problem_id);
        final EditText source = (EditText) rootView.findViewById(R.id.source_code_text);
        final EditText file = (EditText) rootView.findViewById(R.id.file_path);
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.language_spinner);

        Button reset = (Button) rootView.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                id.setText("");
                source.setText("");
                file.setText("");

            }
        });

        Button submit = (Button) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id_text = id.getText().toString();
                String source_text = source.getText().toString();
                int selected = spinner.getSelectedItemPosition();

                if (id_text.length() <= 0 || source_text.length() <= 0 || selected <= 0){
                    Toast.makeText(getActivity(), "Invalid values", Toast.LENGTH_LONG).show();
                }
                else {
                    String langKey = language.getFilterValue(selected);
                    new mAsyncTask(getActivity()).execute(id_text, langKey, source_text);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case RESULT: {
                if (resultCode == -1){

                    String url = data.getData().getPath();
                    File file = new File(url);

                    if (file.length()/1024 > 100){
                        Toast.makeText(getActivity(), R.string.file_long, Toast.LENGTH_LONG).show();
                        break;
                    }

                    //Read text from file
                    StringBuilder text = new StringBuilder();

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(file));
                        String line;

                        while ((line = br.readLine()) != null) {
                            text.append(line);
                            text.append('\n');
                        }
                        br.close();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), R.string.file_load_error, Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        break;
                    }

                    source_code = text.toString();
                    source_path = url;

                    EditText editText = (EditText) getActivity().findViewById(R.id.file_path);
                    editText.setText(source_path == null ? "" : source_path);

                    editText = (EditText) getActivity().findViewById(R.id.source_code_text);
                    editText.setText(source_code == null ? "" : source_code);

                }
                break;
            }
        }
    }

    public static class mAsyncTask extends AsyncTask<String, Void, String> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(FragmentActivity activity) {
            fragment_reference = new WeakReference<>(activity);
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
        protected String doInBackground(String... strings) {

            String id = strings[0];
            String language = strings[1];
            String source = strings[2];

            String message;
            try {
                message = Conexion.getInstance(fragment_reference.get()).submitSolution(fragment_reference.get(), id, language, source);
            } catch (NoLoginFileException | JSONException | UnauthorizedException | IOException e) {
                e.printStackTrace();
                message = e.getMessage();
            }

            return message;
        }

        @Override
        protected void onPostExecute(String message) {

            Activity activity = fragment_reference.get();
            if (message != null){
                Toast.makeText(activity, activity.getResources().getString(R.string.submit_error)+": "+message, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(activity, activity.getResources().getString(R.string.submit_successful), Toast.LENGTH_LONG).show();
                fragment_reference.get().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, JudgmentsFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();

        }
    }

    public static class Languages extends AsyncTask<Void, Void, Void> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public Languages(FragmentActivity activity) {
            fragment_reference = new WeakReference<>(activity);
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
        protected Void doInBackground(Void... voids) {

            try {
                language = Conexion.getInstance(fragment_reference.get()).getLanguageFilters(fragment_reference.get().getResources().getString(R.string.select_programming_language));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {

            error = true;
            FragmentActivity activity = fragment_reference.get();
            final NestedScrollView nestedScroll = (NestedScrollView) activity.findViewById(R.id.submit_nested_scroll);
            final LinearLayout error = (LinearLayout) activity.findViewById(R.id.connection_error);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    error.setVisibility(View.VISIBLE);
                    nestedScroll.setVisibility(View.GONE);
                }
            });

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            error = false;
            final FragmentActivity activity = fragment_reference.get();

            EditText editText = (EditText) activity.findViewById(R.id.problem_id);
            editText.setText(problem_id == 0 ? "" : ""+problem_id);

            Spinner spinner = (Spinner) activity.findViewById(R.id.language_spinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                    language.getFilterArray());
            spinner.setAdapter(adapter);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.submit_nested_scroll).setVisibility(View.VISIBLE);
                    activity.findViewById(R.id.connection_error).setVisibility(View.GONE);
                }
            });

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }
    }
}
