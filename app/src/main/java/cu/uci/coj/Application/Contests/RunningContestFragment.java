package cu.uci.coj.Application.Contests;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RunningContestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RunningContestFragment extends Fragment {

    private final String ARGS_ADAPTER = "adapter";
    private final String ARGS_NETWORK = "network";

    private ContestListItem adapter;
    private RecyclerView recyclerView;
    private boolean network;

    public RunningContestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ComingContestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RunningContestFragment newInstance() {
        RunningContestFragment fragment = new RunningContestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_ADAPTER, adapter);
        outState.putBoolean(ARGS_NETWORK, network);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            adapter = (ContestListItem) savedInstanceState.getSerializable(ARGS_ADAPTER);
            network = savedInstanceState.getBoolean(ARGS_NETWORK);

            if (network)
                recyclerView.setAdapter(adapter);
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.table).setVisibility(View.GONE);
            }
        }
        else{
            adapter = new ContestListItem(new ArrayList<Contest>());
            recyclerView.setAdapter(adapter);
            new mAsyncTask(getActivity()).execute(Conexion.getInstance(getContext()).getURL_CONTEST_RUNNING());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contest, container, false);

        //crear la lista inicialmente vacia
        recyclerView = (RecyclerView) rootView.findViewById(R.id.contest_item_list);

        return rootView;
    }

    public class mAsyncTask extends AsyncTask<String, Void, List<Contest>> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;

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
        protected void onCancelled() {

//            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.contest_coordinator), R.string.contest_list_error, Snackbar.LENGTH_INDEFINITE);
//            snackbar.setAction(R.string.reload, new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    new mAsyncTask(fragment_reference.get()).execute(Conexion.getInstance(fragment_reference.get()).getURL_CONTEST_RUNNING());
//                }
//            });
//            snackbar.show();
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();

        }

        @Override
        protected List<Contest> doInBackground(String... urls) {

            List<Contest> contests = null;

            try {
                String url = urls[0];
                contests = Conexion.getInstance(fragment_reference.get()).getContests(url);
                network = true;
            } catch (IOException | JSONException e) {

                network = false;

                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                try {
                    contests = dataBaseManager.getRunningContest();

                    if (contests != null && contests.size() != 0){
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

                if (contests == null)
                    cancel(true);

                e.printStackTrace();
            }

            return contests;

        }

        @Override
        protected void onPostExecute(List<Contest> contests) {

            if (network){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                dataBaseManager.deleteAllRunningContest();
                for (int i = 0; i < contests.size(); i++) {
                    dataBaseManager.insertRunningContest(contests.get(i));
                }
                dataBaseManager.closeDbConnections();
            }
            if (contests.size() == 0 && network){
                Snackbar.make(fragment_reference.get().findViewById(R.id.contest_coordinator), R.string.no_contest_error, Snackbar.LENGTH_LONG).show();
            }
            else if (contests.size() == 0 && adapter.getItemCount() == 0 && !network){
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                fragment_reference.get().findViewById(R.id.table).setVisibility(View.GONE);
            }
            else {

                adapter = (ContestListItem) recyclerView.getAdapter();
                adapter.addAll(contests);
                recyclerView.swapAdapter(adapter, false);

            }

            if (contests.size() != 0)
                network = false;

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

}
