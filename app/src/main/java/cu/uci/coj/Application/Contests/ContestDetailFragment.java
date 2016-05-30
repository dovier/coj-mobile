package cu.uci.coj.Application.Contests;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Problems.ProblemFragment;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;

/**
 * Created by osvel on 5/8/16.
 */
public class ContestDetailFragment extends Fragment {

    private static String ARGS_CONTEST_DETAIL = "contest_detail";
    private static String ARGS_CONTEST_ID = "contest_id";

    private static ContestDetail contestDetail;
    private static int id;

    public ContestDetailFragment() {
    }

    public static ContestDetailFragment newInstance(int id){

        Fragment fragment = new ContestDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_CONTEST_ID, id);
        fragment.setArguments(args);
        return (ContestDetailFragment)fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null){
            id = getArguments().getInt(ARGS_CONTEST_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            contestDetail = (ContestDetail) savedInstanceState.getSerializable(ARGS_CONTEST_DETAIL);
            if (contestDetail != null){
                createView(getActivity());
                getActivity().findViewById(R.id.connection_error).setVisibility(View.GONE);
                getActivity().findViewById(R.id.nested_scroll_view).setVisibility(View.VISIBLE);
            }
            else {
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.nested_scroll_view).setVisibility(View.GONE);
            }

        }
        else {

            new mAsyncTask(getActivity()).execute(id);

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_CONTEST_DETAIL, contestDetail);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_contest_details, container, false);

        rootView.findViewById(R.id.connection_error).setVisibility(View.GONE);
        rootView.findViewById(R.id.nested_scroll_view).setVisibility(View.GONE);

        return rootView;
    }

    public static void createView(Activity activity){

        TextView textView = (TextView) activity.findViewById(R.id.contest_type);
        textView.setText(contestDetail.getContestType());
        textView = (TextView) activity.findViewById(R.id.contestant_type);
        textView.setText(contestDetail.getContestantType());
        textView = (TextView) activity.findViewById(R.id.acces_type);
        textView.setText(contestDetail.getAccesType());
        textView = (TextView) activity.findViewById(R.id.registration_type);
        textView.setText(contestDetail.getRegistrationType());
        CheckBox checkBox = (CheckBox) activity.findViewById(R.id.template_virtual);
        checkBox.setChecked(contestDetail.isTemplateVirtual());
        textView = (TextView) activity.findViewById(R.id.penalty);
        textView.setText(contestDetail.getPenalt());
        textView = (TextView) activity.findViewById(R.id.frozen_time);
        textView.setText(contestDetail.getFrozenTime());
        textView = (TextView) activity.findViewById(R.id.dead_time);
        textView.setText(contestDetail.getDeadTime());
        textView = (TextView) activity.findViewById(R.id.programming_languages);
        textView.setText(contestDetail.getProgrammingLanguage());
        checkBox = (CheckBox) activity.findViewById(R.id.show_problems_all);
        checkBox.setChecked(contestDetail.isShowProblemsToAll());
        checkBox = (CheckBox) activity.findViewById(R.id.show_judgment_contestant);
        checkBox.setChecked(contestDetail.isShowJudgmentsToContestants());
        checkBox = (CheckBox) activity.findViewById(R.id.show_judgment_all);
        checkBox.setChecked(contestDetail.isShowJudgmentsToAll());
        checkBox = (CheckBox) activity.findViewById(R.id.show_standing_contestant);
        checkBox.setChecked(contestDetail.isShowStandings());
        checkBox = (CheckBox) activity.findViewById(R.id.show_standing_all);
        checkBox.setChecked(contestDetail.isShowStandingsToAll());
        checkBox = (CheckBox) activity.findViewById(R.id.show_statistics_contestant);
        checkBox.setChecked(contestDetail.isShowStatisticsToContestants());
        checkBox = (CheckBox) activity.findViewById(R.id.show_statistics_all);
        checkBox.setChecked(contestDetail.isShowStatisticsToAll());
        textView = (TextView) activity.findViewById(R.id.gold_medals);
        textView.setText(contestDetail.getGoldMedals());
        textView = (TextView) activity.findViewById(R.id.silver_medals);
        textView.setText(contestDetail.getSilverMedals());
        textView = (TextView) activity.findViewById(R.id.bronze_medals);
        textView.setText(contestDetail.getBronzeMedals());

        LinearLayout overView = (LinearLayout) activity.findViewById(R.id.over_view);
        overView.removeAllViews();
            ProblemFragment.write(activity, overView, contestDetail.getOverView());

    }

    private static class mAsyncTask extends AsyncTask<Integer, Void, ContestDetail>{

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
        protected ContestDetail doInBackground(Integer... ids) {

            int id = ids[0];

            try {
                return Conexion.getInstance(fragment_reference.get()).getContestDetail(id);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                //leer de la base de datos
            }

            return null;
        }

        @Override
        protected void onPostExecute(ContestDetail contest) {

            Activity activity = fragment_reference.get();
            final LinearLayout error = (LinearLayout) activity.findViewById(R.id.connection_error);
            final NestedScrollView nestedScroll = (NestedScrollView) activity.findViewById(R.id.nested_scroll_view);

            contestDetail = contest;
            if (contest == null){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error.setVisibility(View.VISIBLE);
                        nestedScroll.setVisibility(View.GONE);
                    }
                });
            }
            else {
                createView(activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error.setVisibility(View.GONE);
                        nestedScroll.setVisibility(View.VISIBLE);
                    }
                });
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }
}
