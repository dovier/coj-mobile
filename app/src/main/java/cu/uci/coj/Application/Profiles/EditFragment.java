package cu.uci.coj.Application.Profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.Filters.Filter;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link EditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditFragment extends Fragment {

    private static String ARGS_ERROR = "ERROR";

    private static Filter<Integer> country;
    private static Filter<Integer> institutions;
    private static Filter<Integer> language;
    private static boolean error;

    public EditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static EditFragment newInstance() {
        EditFragment fragment = new EditFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(ARGS_ERROR, error);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            error = savedInstanceState.getBoolean(ARGS_ERROR);
            if (!error){
                createView(getActivity());
                getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                getActivity().findViewById(R.id.edit_nested_scroll).setVisibility(View.VISIBLE);
            }
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.edit_nested_scroll).setVisibility(View.GONE);
            }
        }
        else {
            new mAsyncTask(getActivity()).execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_edit, container, false);

        final EditText nick_name = (EditText) rootView.findViewById(R.id.nick_name);
        final EditText name = (EditText) rootView.findViewById(R.id.first_name);
        final EditText last_name = (EditText) rootView.findViewById(R.id.last_name);
        final EditText email = (EditText) rootView.findViewById(R.id.email);
        final Spinner gender = (Spinner) rootView.findViewById(R.id.gender);
        final Spinner prog_language = (Spinner) rootView.findViewById(R.id.favorite_language);
        final Spinner institutionSpinner = (Spinner) rootView.findViewById(R.id.institution);
        final Spinner countrySpinner = (Spinner) rootView.findViewById(R.id.country);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i != 0){
                    int id = country.getFilterValue(i);
                    new Institutions(getActivity()).execute(id);
                }
                else {
                    institutionSpinner.setVisibility(View.GONE);
                    institutionSpinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Button edit = (Button) rootView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nickName = nick_name.getText().toString();
                String firstName = name.getText().toString();
                String lastName = last_name.getText().toString();
                String email_string = email.getText().toString();
                Object obj = country.getFilterValue(countrySpinner.getSelectedItemPosition());
                int country_code = obj != null ? (Integer)obj : -1;
                if (institutions != null) {
                    obj = institutions.getFilterValue(institutionSpinner.getSelectedItemPosition());
                }
                else {
                    obj = null;
                }
                int institution_code = obj != null ? (Integer)obj : -1;
                institution_code = institutionSpinner.getVisibility() == View.VISIBLE ? institution_code : -1;
                obj = country.getFilterValue(prog_language.getSelectedItemPosition());
                int language_code = obj != null ? (Integer)obj : -1;
                int gender_code = gender.getSelectedItemPosition() == 0 ? -1 : gender.getSelectedItemPosition();

                new Update(getActivity(), firstName, lastName, email_string, nickName)
                        .execute(institution_code, country_code, language_code, gender_code);
             }
        });

        Button reset = (Button) rootView.findViewById(R.id.reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nick_name.setText("");
                name.setText("");
                last_name.setText("");
                email.setText("");
                institutionSpinner.setVisibility(View.GONE);
                institutionSpinner.setSelection(0);
                countrySpinner.setSelection(0);
                prog_language.setSelection(0);
                gender.setSelection(0);
            }
        });

        return rootView;
    }

    private static void createView(Activity activity){

        Spinner spinner = (Spinner) activity.findViewById(R.id.country);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                country.getFilterArray());
        spinner.setAdapter(adapter);

        spinner = (Spinner) activity.findViewById(R.id.favorite_language);
        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                language.getFilterArray());
        spinner.setAdapter(adapter);

        spinner = (Spinner) activity.findViewById(R.id.gender);
        adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                activity.getResources().getStringArray(R.array.gender));
        spinner.setAdapter(adapter);

    }

    public static class Update extends AsyncTask<Integer, Void, String>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected String name;
        protected String last_name;
        protected String email;
        protected String nick;

        public Update(FragmentActivity activity, String name, String last_name, String email, String nick) {
            fragment_reference = new WeakReference<>(activity);
            this.name = name;
            this.last_name = last_name;
            this.email = email;
            this.nick = nick;
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
        protected String doInBackground(Integer... integers) {

            try {
                return Conexion.getInstance(fragment_reference.get()).updateUserProfile(fragment_reference.get(), nick, name, last_name, email,
                        integers[0], integers[1], integers[2], integers[3]);
            } catch (NoLoginFileException | IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
            } catch (UnauthorizedException e) {
                e.printStackTrace();
                //desloguear usuario
            }

            return null;
        }

        @Override
        protected void onCancelled() {

            FragmentActivity activity = fragment_reference.get();

            Toast.makeText(activity, activity.getResources().getString(R.string.update_error), Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }

        @Override
        protected void onPostExecute(String message) {

            final FragmentActivity activity = fragment_reference.get();

            if (message != null)
                Toast.makeText(activity, activity.getResources().getString(R.string.update_error)+": "+message, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(activity, activity.getResources().getString(R.string.update_successful), Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }
    }

    public static class Institutions extends AsyncTask<Integer, Void, Filter<Integer>>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public Institutions(FragmentActivity activity) {
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
        protected Filter<Integer> doInBackground(Integer... integers) {

            try {
                return Conexion.getInstance(fragment_reference.get()).getInstitutionFilter(integers[0], fragment_reference.get().getResources().getString(R.string.institution));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {

            FragmentActivity activity = fragment_reference.get();
            final Spinner spinner = (Spinner) activity.findViewById(R.id.institution);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.GONE);
                }
            });

            Toast.makeText(activity, activity.getResources().getString(R.string.error_institutions), Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }

        @Override
        protected void onPostExecute(Filter<Integer> filter) {

            final FragmentActivity activity = fragment_reference.get();

            final Spinner spinner = (Spinner) activity.findViewById(R.id.institution);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item,
                    filter.getFilterArray());
            spinner.setAdapter(adapter);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    spinner.setVisibility(View.VISIBLE);
                }
            });

            institutions = filter;
            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }
    }

    public static class mAsyncTask extends AsyncTask<Void, Void, Void>{

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
        protected Void doInBackground(Void... voids) {

            try {
                country = Conexion.getInstance(fragment_reference.get()).getCountryFilter(fragment_reference.get().getResources().getString(R.string.country));
                language = Conexion.getInstance(fragment_reference.get()).getIDLanguageFilters(fragment_reference.get().getResources().getString(R.string.default_programming_language));
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
            }

            return null;
        }

        @Override
        protected void onCancelled() {

            FragmentActivity activity = fragment_reference.get();
            final NestedScrollView nestedScroll = (NestedScrollView) activity.findViewById(R.id.edit_nested_scroll);
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

            FragmentActivity activity = fragment_reference.get();

            createView(activity);

            final Spinner institution = (Spinner) activity.findViewById(R.id.institution);

            final NestedScrollView nestedScroll = (NestedScrollView) activity.findViewById(R.id.edit_nested_scroll);
            final LinearLayout error = (LinearLayout) activity.findViewById(R.id.connection_error);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    institution.setVisibility(View.GONE);
                    error.setVisibility(View.GONE);
                    nestedScroll.setVisibility(View.VISIBLE);
                }
            });

            progressDialog.dismiss();
            new ScreenOrientationLocker(activity).unlock();
        }
    }

}
