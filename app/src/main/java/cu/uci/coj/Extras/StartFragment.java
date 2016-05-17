package cu.uci.coj.Extras;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.R;
import cu.uci.coj.ScreenOrientationLocker;


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

    private static View rootView;
    private static EntriesList adapter;
    private static boolean last_page;
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
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (EntriesList) savedInstanceState.getSerializable(ARG_ADAPTER);
            page = savedInstanceState.getInt(ARG_PAGE);
            last_page = savedInstanceState.getBoolean(ARG_LAST_PAGE);

            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.entries_item_list);
            recyclerView.setAdapter(adapter);

        }
        else{
            new mAsyncTask(getActivity()).execute(Conexion.URL_WELCOME_PAGE + page++);
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
        if (login)
            input_layout.setVisibility(View.VISIBLE);
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

                        new mAsyncTask(getActivity()).execute(Conexion.URL_WELCOME_PAGE + page++);

                    }
                }

            }
        });

        return rootView;
    }

    public static class mAsyncTask extends AsyncTask<String, Void, List<EntriesItem>> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected String url;
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
        protected List<EntriesItem> doInBackground(String... url) {

            this.url = url[0];
            List<EntriesItem> list = new ArrayList<>();

            try {
                list = Conexion.getEntries(this.url);
                secondPage = true;
            } catch (IOException | JSONException e) {

                secondPage = false;
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                try {
                    list = dataBaseManager.getEntries();

                    if (list != null){
                        last_page = true;
                        final FragmentActivity activity = fragment_reference.get();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(fragment_reference.get().getApplicationContext(), R.string.off_line_mode, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (JSONException e1) {
                    e1.printStackTrace();
                    cancel(true);
                }

                dataBaseManager.closeDbConnections();

                if (list == null)
                    cancel(true);

                e.printStackTrace();
//                cancel(true);
            }

            return list;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.home_coordinator), R.string.entries_error, Snackbar.LENGTH_INDEFINITE);
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
                new mAsyncTask(fragment_reference.get()).execute(Conexion.URL_WELCOME_PAGE + page++);
            }

            RecyclerView recyclerView = (RecyclerView) fragment_reference.get().findViewById(R.id.entries_item_list);
            if (entriesItems.size() == 0){
                Snackbar.make(fragment_reference.get().findViewById(R.id.home_coordinator), R.string.no_more_entries_error, Snackbar.LENGTH_LONG)
                        .setAction(null, null).show();
            }
            else {

                adapter = (EntriesList) recyclerView.getAdapter();
                adapter.addAll(entriesItems);
                recyclerView.swapAdapter(adapter, false);

            }
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

}
