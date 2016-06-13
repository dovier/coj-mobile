package cu.uci.coj.Application.Judgments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Filters.Filter;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link JudgmentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JudgmentsFragment extends Fragment {

    private static final String ARGS_LAST_PAGE = "last_page";
    private static final String ARGS_PAGE = "page";
    private static final String ARGS_ADAPTER = "adapter";
    private static final String ARGS_FILTER = "filter";
    private static final String ARGS_CONSULT_DB = "database";
    private static final String ARGS_ERROR = "error";

    private static View rootView;
    private static JudgmentList adapter;
    private static Filter<String> languageFilter;
    private static boolean last_page;
    private static boolean consult_db;
    private static int page;
    private static boolean error = false;
    private static boolean filter = false;

    public JudgmentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static JudgmentsFragment newInstance() {
        JudgmentsFragment fragment = new JudgmentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_ADAPTER, adapter);
        outState.putInt(ARGS_PAGE, page);
        outState.putBoolean(ARGS_FILTER, filter);
        outState.putBoolean(ARGS_LAST_PAGE, last_page);
        outState.putBoolean(ARGS_CONSULT_DB, consult_db);
        outState.putBoolean(ARGS_ERROR, error);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (JudgmentList) savedInstanceState.getSerializable(ARGS_ADAPTER);
            page = savedInstanceState.getInt(ARGS_PAGE);
            filter = savedInstanceState.getBoolean(ARGS_FILTER);
            last_page = savedInstanceState.getBoolean(ARGS_LAST_PAGE);
            consult_db = savedInstanceState.getBoolean(ARGS_CONSULT_DB);
            error = savedInstanceState.getBoolean(ARGS_ERROR);

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.judgments_items_list);

            if (error){
                recyclerView.setVisibility(View.GONE);
                getActivity().findViewById(R.id.judgments_list).setVisibility(View.GONE);
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.judgments_fab);
                fab.setImageResource(R.drawable.sync);
            }
            else
                recyclerView.setAdapter(adapter);

        }
        else {

            adapter = new JudgmentList(new ArrayList<Judgment>());
            new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_JUDGMENT_PAGE() + page++);
            new languagesFilterAsyncTask(getActivity()).execute();

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        last_page = false;
        page = 1;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_judgments, container, false);
        //modificar fragment como plazca

        //obtener el recyclerview y darle inicialmente un adapter vacio
        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.judgments_items_list);

        //onScrollListener para saber cuando llego al final de la lista
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //como saber que llego al final de la lista
                if (!last_page && recyclerView.getAdapter().getItemCount() != 0) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {

                        new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_JUDGMENT_PAGE() + page++);

                    }
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.judgments_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (consult_db){
                    page = 1;
                    last_page = false;
                    filter = false;
                    recyclerView.setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.judgments_list).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                    new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_JUDGMENT_PAGE() + page++);
                }
                else
                    buildFilterMessage();
            }
        });

        return rootView;
    }

    private void buildFilterMessage(){

        AlertDialog.Builder filterMessage = new AlertDialog.Builder(getActivity());
        final View filterRootView = getActivity().getLayoutInflater().inflate(R.layout.judgment_filter_dialog, null);
        filterMessage.setView(filterRootView);

        final Filter judgmentFilter = Conexion.getJudgmentFilter(getResources().getString(R.string.judgment));

        final Spinner spinner_judgment = (Spinner)filterRootView.findViewById(R.id.spinner_judgment);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                judgmentFilter.getFilterArray());
        spinner_judgment.setAdapter(adapter);

        final Spinner spinner_language = (Spinner)filterRootView.findViewById(R.id.spinner_language);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                languageFilter.getFilterArray());
        spinner_language.setAdapter(adapter);

        filterMessage.setNegativeButton(R.string.cancel, null);
        filterMessage.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //?user=User&prob=1000&status=ac&lang=java

                //tomar lo que hay en el campo texto
                EditText filter_edit = (EditText) filterRootView.findViewById(R.id.filter_user);
                String url = filter_edit.getText().length() == 0 ? "" : "username=" + filter_edit.getText().toString() + "&";

                filter_edit = (EditText) filterRootView.findViewById(R.id.filter_problem);
                url += filter_edit.getText().length() == 0 ? "" : "pid=" + filter_edit.getText().toString() + "&";

                int selected_judgment = spinner_judgment.getSelectedItemPosition();
                url += selected_judgment == 0 ? "" : "status="+ judgmentFilter.getFilterValue(selected_judgment)+"&";

                int selected_language = spinner_language.getSelectedItemPosition();
                url += selected_language == 0 ? "" : "lang=" + languageFilter.getFilterValue(selected_language);

                filter = true;
                if (url.length() != 0)
                    new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_JUDGMENT_FILTER() + url);
            }
        });

        filterMessage.setTitle(getResources().getString(R.string.judgment_filter));
        filterMessage.create().show();

    }

    public static class languagesFilterAsyncTask extends AsyncTask<Void, Void, Void>{

        protected WeakReference<FragmentActivity> weakReference;

        public languagesFilterAsyncTask(FragmentActivity fragmentActivity) {
            this.weakReference = new WeakReference<>(fragmentActivity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String firstElement = weakReference.get().getResources().getString(R.string.languages);
            try {
                languageFilter = Conexion.getInstance(weakReference.get()).getLanguageFilters(firstElement);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                languageFilter = new Filter<>(firstElement);
            }

            return null;
        }

    }
    
    public static class mAsyncTask extends AsyncTask<String, Void, List<Judgment>>{

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected String url;

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
        protected List<Judgment> doInBackground(String... urls) {

            url = urls[0];
            List<Judgment> list = new ArrayList<>();

            consult_db = false;

            final FragmentActivity activity = fragment_reference.get();
            final FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.judgments_fab);

            Conexion conexion = Conexion.getInstance(fragment_reference.get());

            try {
                list = conexion.getJudgmentsItem(url);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.filter);
                    }
                });
                error = false;
            } catch (IOException | JSONException e) {

                error = true;

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fab.setImageResource(R.drawable.sync);
                    }
                });

                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                try {
                    if (page == 2) {
                        list = dataBaseManager.getJudgments();

                        if (list != null && list.size() != 0) {
                            consult_db = true;
                            last_page = true;
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

                if (list == null || list.size() == 0) {
//                    final LinearLayout layout_error = (LinearLayout) fragment_reference.get().findViewById(R.id.connection_error);
//                    final LinearLayout layout_list = (LinearLayout) fragment_reference.get().findViewById(R.id.judgments_list);
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            consult_db = true;
//                            layout_error.setVisibility(View.VISIBLE);
//                            layout_list.setVisibility(View.GONE);
//                        }
//                    });
                    cancel(true);
                }

                e.printStackTrace();
            }

            return list;


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.judgment_coordinator), R.string.judgment_list_error, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.reload, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    last_page = false;
                    new mAsyncTask(fragment_reference.get()).execute(url);
                }
            });
            snackbar.show();
            last_page = true;
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<Judgment> judgments) {

            final LinearLayout layout_error = (LinearLayout) fragment_reference.get().findViewById(R.id.connection_error);
            final LinearLayout layout_list = (LinearLayout) fragment_reference.get().findViewById(R.id.judgments_list);
            final FragmentActivity activity = fragment_reference.get();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout_error.setVisibility(View.GONE);
                    layout_list.setVisibility(View.VISIBLE);
                }
            });

            final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.judgments_items_list);
            if (!filter && judgments.size() == 0 && adapter.getItemCount() == 0 && error){
                consult_db = true;
                recyclerView.setVisibility(View.GONE);
                fragment_reference.get().findViewById(R.id.judgments_list).setVisibility(View.GONE);
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
            }
            else if (!filter && judgments.size() == 0 && !error){
                last_page = true;
                Snackbar.make(fragment_reference.get().findViewById(R.id.judgment_coordinator), R.string.no_more_judgments, Snackbar.LENGTH_LONG).show();
            }
            if (!filter && adapter.addAll(judgments)){
                recyclerView.swapAdapter(adapter, false);
            }
            else if (filter){
                if (judgments.size() == 0)
                    Snackbar.make(fragment_reference.get().findViewById(R.id.judgment_coordinator), R.string.no_matches, Snackbar.LENGTH_LONG).show();
                last_page = true;
                JudgmentList new_adapter = new JudgmentList(judgments);
                recyclerView.setAdapter(new_adapter);
            }

            if (judgments.size() != 0)
                error = false;

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();

            if (page == 2 && !error && !filter){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                dataBaseManager.deleteAllJudgment();
                for (int i = 0; i < judgments.size(); i++) {
                    dataBaseManager.insertJudgment(judgments.get(i));
                }
                dataBaseManager.closeDbConnections();
            }

        }
    }
}
