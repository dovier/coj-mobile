package cu.uci.coj.Application.Extras;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link StartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StartFragment extends Fragment {

    private static final String ARG_LOGIN = "login";
    private static final String ARG_PAGE = "page";
    private static final String ARG_LAST_PAGE = "last_page";
    private static final String ARG_ADAPTER = "adapter";
    private static final String ARG_ERROR = "error";

    private static View rootView;
    private static EntriesList adapter;
    private static boolean last_page;
    private static boolean error = false;
    private static int page;
    private static boolean login = false;

    public StartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StartFragment newInstance(boolean login) {
        StartFragment fragment = new StartFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LOGIN, login);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARG_ADAPTER, adapter);
        outState.putInt(ARG_PAGE, page);
        outState.putBoolean(ARG_LAST_PAGE, last_page);
        outState.putBoolean(ARG_ERROR, error);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (EntriesList) savedInstanceState.getSerializable(ARG_ADAPTER);
            page = savedInstanceState.getInt(ARG_PAGE);
            last_page = savedInstanceState.getBoolean(ARG_LAST_PAGE);
            error = savedInstanceState.getBoolean(ARG_ERROR);

            if (!error){
                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.entries_item_list);
                recyclerView.setAdapter(adapter);
            }
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.input_layout).setVisibility(View.GONE);
                getActivity().findViewById(R.id.entries_item_list).setVisibility(View.GONE);
            }

        }
        else{
            new mAsyncTask(getActivity()).execute();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            login = getArguments().getBoolean(ARG_LOGIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        last_page = false;
        page = 1;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_start, container, false);

        LinearLayout input_layout = (LinearLayout) rootView.findViewById(R.id.input_layout);
        if (login){
            input_layout.setVisibility(View.VISIBLE);
            final EditText input_message = (EditText) rootView.findViewById(R.id.input_message);

            Button create = (Button) rootView.findViewById(R.id.create_button);
            create.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String entry = input_message.getText().toString();
                    if (entry.length() != 0)
                        new Entry(getActivity()).execute(entry);
                    else
                        Toast.makeText(getActivity(), R.string.entry_empty, Toast.LENGTH_LONG).show();
                }
            });

            final TextView char_count = (TextView) rootView.findViewById(R.id.char_count);
            TextWatcher watcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    char_count.setText(charSequence.length()+"/255");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            };
            input_message.addTextChangedListener(watcher);

            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        else
            input_layout.setVisibility(View.GONE);

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.entries_item_list);
        adapter = new EntriesList(new ArrayList<EntriesItem>(), login);
        recyclerView.setAdapter(adapter);
        //onScrollListener para saber cuando llego al final de la lista
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //como saber que llego al final de la lista
                if (!last_page && recyclerView.getAdapter().getItemCount() != 0) {

                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 && dy > 10) {

                        new mAsyncTask(getActivity()).execute();

                    }
                }

            }
        });

        return rootView;
    }

    public static class Entry extends AsyncTask<String, Void, String>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

        public Entry(FragmentActivity fragment_reference) {
            this.fragment_reference = new WeakReference<>(fragment_reference);
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
        protected String doInBackground(String... entries) {

            String message;

            try {
                message = Conexion.getInstance(fragment_reference.get()).addEntry(fragment_reference.get(), entries[0]);
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
                Toast.makeText(activity, activity.getResources().getString(R.string.entry_error)+": "+message, Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(activity, activity.getResources().getString(R.string.entry_successful), Toast.LENGTH_LONG).show();
                EditText entry = (EditText) fragment_reference.get().findViewById(R.id.input_message);
                entry.setText("");
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

    public static class mAsyncTask extends AsyncTask<Void, Void, List<EntriesItem>> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected boolean secondPage;

        public mAsyncTask(FragmentActivity fragment_reference) {
            this.fragment_reference = new WeakReference<>(fragment_reference);
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
        protected List<EntriesItem> doInBackground(Void... voids) {

            List<EntriesItem> list = null;

            try {
                list = Conexion.getInstance(fragment_reference.get()).getEntries(page++);
                secondPage = true;
            } catch (IOException | JSONException e) {

                secondPage = false;
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                try {
                    if (page == 2) {
                        list = dataBaseManager.getEntries();

                        if (list != null)
                            last_page = true;
                        if (list != null && list.size() != 0) {
                            final FragmentActivity activity = fragment_reference.get();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(fragment_reference.get().getApplicationContext(), R.string.off_line_mode, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    cancel(true);
                }

                dataBaseManager.closeDbConnections();

                if (list == null)
                    cancel(true);

                e.printStackTrace();
            }

            return list;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
//            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.home_coordinator), R.string.entries_error, Snackbar.LENGTH_INDEFINITE);
//            snackbar.setAction(R.string.reload, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    last_page = false;
//                    new mAsyncTask(fragment_reference.get()).execute();
//                }
//            });
//            snackbar.show();

            Toast.makeText(fragment_reference.get(), R.string.entries_error, Toast.LENGTH_LONG).show();

            last_page = true;
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<EntriesItem> entriesItems) {

            if (page == 2){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                dataBaseManager.deleteAllEntries();
                for (int i = 0; i < entriesItems.size(); i++) {
                    dataBaseManager.insertEntrie(entriesItems.get(i).getJSONString());
                }
                dataBaseManager.closeDbConnections();
            }

            if (page == 2 && secondPage){
                new mAsyncTask(fragment_reference.get()).execute();
            }

            final RecyclerView recyclerView = (RecyclerView) fragment_reference.get().findViewById(R.id.entries_item_list);
            adapter = (EntriesList) recyclerView.getAdapter();

            if (entriesItems.size() == 0 && adapter.getItemCount() == 0){
                error = true;

                final Activity activity = fragment_reference.get();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                        activity.findViewById(R.id.input_layout).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                    }
                });

            }
            else if (entriesItems.size() == 0){
                Snackbar.make(fragment_reference.get().findViewById(R.id.home_coordinator), R.string.no_more_entries_error, Snackbar.LENGTH_LONG)
                        .setAction(null, null).show();
            }
            else {

                adapter.addAll(entriesItems);
                recyclerView.swapAdapter(adapter, false);

            }
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

}
