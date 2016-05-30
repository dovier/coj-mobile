package cu.uci.coj.Application.Judgments;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Application.Behaviors.FloatingActionButtonBehavior;
import cu.uci.coj.Conexion;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;

/**
 * Created by osvel on 5/17/16.
 */
public class BestSolutionsFragment extends Fragment {

    private static final String ARGS_ADAPTER = "adapter";
    private static String ARG_PROBLEM_ID = "problem_id";

    private static JudgmentList adapter;
    private int problem_id;

    public static BestSolutionsFragment newInstance(int problem_id) {
        BestSolutionsFragment fragment = new BestSolutionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PROBLEM_ID, problem_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null){

            problem_id = getArguments().getInt(ARG_PROBLEM_ID);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_ADAPTER, adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            adapter = (JudgmentList) savedInstanceState.getSerializable(ARGS_ADAPTER);
            if (adapter != null){
                getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                getActivity().findViewById(R.id.judgments_list).setVisibility(View.VISIBLE);

                RecyclerView recycler = (RecyclerView) getActivity().findViewById(R.id.judgments_items_list);
                recycler.setAdapter(adapter);
            }
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.judgments_list).setVisibility(View.GONE);
            }
        }
        else {

            new mAsyncTask(getActivity()).execute(problem_id);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_judgments, container, false);

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.judgments_fab);
        CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        FloatingActionButtonBehavior behavior = new FloatingActionButtonBehavior(false);
        fabLayoutParams.setBehavior(behavior);
        fab.setLayoutParams(fabLayoutParams);
        fab.setVisibility(View.GONE);

        return rootView;
    }

    public static class mAsyncTask extends AsyncTask<Integer, Void, List<Judgment>> {

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
        protected List<Judgment> doInBackground(Integer... ids) {

            List<Judgment> list = new ArrayList<>();

            Conexion conexion = Conexion.getInstance(fragment_reference.get());

            try {
                list = conexion.getJudgmentsItem(conexion.getURL_JUDGMENT_BEST_SOLUTIONS() + ids[0]);
            } catch (IOException | JSONException e) {
                list = null;
                e.printStackTrace();
            }

            return list;


        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<Judgment> judgments) {

            if (judgments == null){
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                fragment_reference.get().findViewById(R.id.judgments_list).setVisibility(View.GONE);
                adapter = null;
            }
            else {
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.GONE);
                fragment_reference.get().findViewById(R.id.judgments_list).setVisibility(View.VISIBLE);

                RecyclerView recycler = (RecyclerView) fragment_reference.get().findViewById(R.id.judgments_items_list);
                recycler.setAdapter(new JudgmentList(judgments));
                adapter = new JudgmentList(judgments);
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }
}
