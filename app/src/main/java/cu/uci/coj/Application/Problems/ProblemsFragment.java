package cu.uci.coj.Application.Problems;

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
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.Application.Filters.Filter;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ProblemsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProblemsFragment extends Fragment{

    private static final String ARGS_LOGIN = "login";
    private static final String ARGS_LAST_PAGE = "last_page";
    private static final String ARGS_PAGE = "page";
    private static final String ARGS_ADAPTER = "adapter";
    private static final String ARGS_FILTER = "filter";
    private static final String ARGS_DATABASE = "data_base";
    private static final String ARGS_CLASSIFICATION_FILTER = "problem_filter";
    private static final String ARGS_ERROR = "error";

    private static View rootView;
    private static ProblemList adapter;
    private static boolean last_page;
    private static int page;
    private static boolean consultDB;
    private static boolean filter = false;
    private static boolean login = false;
    private static boolean error = false;
    private static Filter<Integer> classificationFilter;

//    private static View progressRootView;

    public ProblemsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static ProblemsFragment newInstance(boolean login) {
        ProblemsFragment fragment = new ProblemsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARGS_LOGIN, login);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_ADAPTER, adapter);
        outState.putSerializable(ARGS_CLASSIFICATION_FILTER, classificationFilter);
        outState.putInt(ARGS_PAGE, page);
        outState.putBoolean(ARGS_FILTER, filter);
        outState.putBoolean(ARGS_LAST_PAGE, last_page);
        outState.putBoolean(ARGS_DATABASE, consultDB);
        outState.putBoolean(ARGS_ERROR, error);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (ProblemList) savedInstanceState.getSerializable(ARGS_ADAPTER);
            classificationFilter = (Filter) savedInstanceState.getSerializable(ARGS_CLASSIFICATION_FILTER);
            page = savedInstanceState.getInt(ARGS_PAGE);
            filter = savedInstanceState.getBoolean(ARGS_FILTER);
            last_page = savedInstanceState.getBoolean(ARGS_LAST_PAGE);
            consultDB = savedInstanceState.getBoolean(ARGS_DATABASE);
            error = savedInstanceState.getBoolean(ARGS_ERROR);

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.problem_item_list);
            if (!error){
                recyclerView.setAdapter(adapter);
            }
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.table).setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            }

        }
        else {

            consultDB = true;
            adapter = new ProblemList(new ArrayList<ProblemItem>(), login);
            new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_PROBLEM_PAGE() + page++);
            new classificationFilterAsyncTask(getActivity()).execute();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            login = getArguments().getBoolean(ARGS_LOGIN);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        filter = false;
        last_page = false;
        page = 1;

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_problems, container, false);
        //modificar fragment como plazca

        //ocultar o mostrar lo que sea necesario
        LinearLayout fav_layout = (LinearLayout) rootView.findViewById(R.id.topic_fav_layout);
        if (login){
            fav_layout.setVisibility(View.VISIBLE);
        }
        else {
            fav_layout.setVisibility(View.GONE);
        }

        final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.problem_item_list);
        //onScrollListener para saber cuando llego al final de la lista
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                //como saber que llego al final de la lista
                if (!last_page && recyclerView.getAdapter().getItemCount() != 0) {

                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1 && dy > 10) {

                        new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_PROBLEM_PAGE() + page++);

                    }
                }

            }
        });

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.problems_fab);

        if (consultDB)
            fab.setImageResource(R.drawable.sync);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!consultDB){
                        buildFilterMessage();
                }
                else {
                    page = 1;
                    last_page = false;
                    adapter = new ProblemList(new ArrayList<ProblemItem>(), login);
                    getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.table).setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_PROBLEM_PAGE() + page++);
                    new classificationFilterAsyncTask(getActivity()).execute();
                }
            }
        });

        return rootView;
    }

    private void buildFilterMessage(){

        AlertDialog.Builder filterMessage = new AlertDialog.Builder(getActivity());
        final View filterRootView = getActivity().getLayoutInflater().inflate(R.layout.problems_filter_dialog, null);
        filterMessage.setView(filterRootView);

        final Spinner spinner_category = (Spinner)filterRootView.findViewById(R.id.spinner_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                classificationFilter.getFilterArray());
        spinner_category.setAdapter(adapter);

        final Spinner spinner_difficult = (Spinner)filterRootView.findViewById(R.id.spinner_difficult);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.difficult_array));
        spinner_difficult.setAdapter(adapter);

        filterMessage.setNegativeButton(R.string.cancel, null);
        filterMessage.setPositiveButton(R.string.filter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //?pattern=1001&classification=-1&complexity=-1

                //tomar lo que hay en el campo texto
                EditText filter_title = (EditText) filterRootView.findViewById(R.id.filter_title);
                String url = filter_title.getText().length() == 0 ? "" : "pattern=" + filter_title.getText().toString() + "&";

                int selected_category = spinner_category.getSelectedItemPosition();
                url += selected_category == 0 ? "" : "classification="+ classificationFilter.getFilterValue(selected_category)+"&";

                //tomar la opcion seleccionada del spinner difficult
                int selected_difficult = spinner_difficult.getSelectedItemPosition();

                //segun la opcion seleccionada rellenar la url
                url += selected_difficult == 0 ? "" : "complexity=" + selected_difficult;

                filter = true;
                if (url.length() != 0)
                    new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_PROBLEM_FILTER() + url);
            }
        });

        filterMessage.setTitle(getResources().getString(R.string.problem_filter));
        filterMessage.create().show();

    }

    public static class classificationFilterAsyncTask extends AsyncTask<Void, Void, Void>{

        protected WeakReference<FragmentActivity> weakReference;

        public classificationFilterAsyncTask(FragmentActivity fragmentActivity) {
            this.weakReference = new WeakReference<>(fragmentActivity);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String firstElement = weakReference.get().getResources().getString(R.string.filter_classification);
            try {
                classificationFilter = Conexion.getInstance(weakReference.get()).getClassificationFilters(firstElement);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                classificationFilter = new Filter<>(firstElement);
            }

            return null;
        }

    }

    public static class mAsyncTask extends AsyncTask<String, Void, List<ProblemItem>> {

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
        protected List<ProblemItem> doInBackground(String... urls) {

            url = urls[0];

            List<ProblemItem> list = new ArrayList<>();
            Context context = fragment_reference.get().getApplicationContext();

            final FragmentActivity activity = fragment_reference.get();
            final FloatingActionButton fab = (FloatingActionButton)fragment_reference.get().findViewById(R.id.problems_fab);

            try {

                try {
                    list = Conexion.getInstance(fragment_reference.get()).getProblemsItem(fragment_reference.get(), urls[0], login);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            fab.setImageResource(R.drawable.filter);
                        }
                    });
                    consultDB = false;
                } catch (UnauthorizedException | NoLoginFileException e) {
                    e.printStackTrace();
                }

            } catch (IOException | JSONException e) {
                if (consultDB){

                    error = true;
                    DataBaseManager dataBaseManager = DataBaseManager.getInstance(context);

                    try {
                        if (page == 2) {

                            list = dataBaseManager.getProblemsItem();

                            final List<ProblemItem> finalList = list;
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (finalList != null && finalList.size() != 0)
                                        Toast.makeText(fragment_reference.get().getApplicationContext(), R.string.off_line_mode, Toast.LENGTH_SHORT).show();
                                    fab.setImageResource(R.drawable.sync);
                                }
                            });

                            consultDB = true;
                            last_page = true;
                        }
                    } catch (Exception e1) {
                        //si la base de datos tambien falla cancelar la operacion
                        e1.printStackTrace();
                        consultDB = false;
                        last_page = false;
                        cancel(true);
                    }

                    dataBaseManager.closeDbConnections();
                }
                else {
                    e.printStackTrace();
                    cancel(true);
                }

                if (list == null)
                    cancel(true);

            }

            return list;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.problems_coordinator), R.string.problem_list_error, Snackbar.LENGTH_INDEFINITE);
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
        protected void onPostExecute(List<ProblemItem> problemItems) {

            adapter.setLogin(!consultDB && login);

            LinearLayout topic = (LinearLayout) fragment_reference.get().findViewById(R.id.topic_fav_layout);

            if (!consultDB && login)
                topic.setVisibility(View.VISIBLE);
            else
                topic.setVisibility(View.GONE);


            final RecyclerView recyclerView = (RecyclerView) fragment_reference.get().findViewById(R.id.problem_item_list);
            //no es un filtro, la lista esta vacia y hubo error conexion a la base de datos entonces mostrar error de conexion
            if (!filter && problemItems.size() == 0 && adapter.getItemCount() == 0 && error){
                fragment_reference.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                        fragment_reference.get().findViewById(R.id.table).setVisibility(View.GONE);
                        recyclerView.setVisibility(View.GONE);
                        final FloatingActionButton fab = (FloatingActionButton) fragment_reference.get().findViewById(R.id.problems_fab);
                        fab.setImageResource(R.drawable.sync);
                    }
                });
            }
            //no es un filtro, la lista obtenida esta vacia y no hubo error entonces no mas problemas
            else if (!filter && problemItems.size() == 0 && !error){
                last_page = true;
                Snackbar.make(fragment_reference.get().findViewById(R.id.problems_coordinator), R.string.no_more_problems, Snackbar.LENGTH_LONG).show();
            }
            if (!filter && adapter.addAll(problemItems)){
                recyclerView.swapAdapter(adapter, false);
            }
            else if (filter){
                if (problemItems.size() == 0)
                    Snackbar.make(fragment_reference.get().findViewById(R.id.problems_coordinator), R.string.no_matches, Snackbar.LENGTH_LONG).show();
                last_page = true;
                ProblemList new_adapter = adapter;
                new_adapter.setProblemItemList(problemItems);
                recyclerView.swapAdapter(new_adapter, true);
            }

            if (error && problemItems.size() != 0)
                error = false;

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

}
