package cu.uci.coj.Application.Standings;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
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
 * Activities that contain this fragment must implement the
 * Use the {@link UserStandingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserStandingFragment extends Fragment {

    private static final String ARGS_LAST_PAGE = "last_page";
    private static final String ARGS_PAGE = "page";
    private static final String ARGS_ADAPTER = "adapter";
    private static final String ARGS_ERROR = "error";

    private static View rootView;
    private static boolean last_page;
    private static int page;
    private static boolean error;
    private static UserStandingItem adapter;

    public UserStandingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static UserStandingFragment newInstance() {
        UserStandingFragment fragment = new UserStandingFragment();
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
        outState.putBoolean(ARGS_LAST_PAGE, last_page);
        outState.putBoolean(ARGS_ERROR, error);
        outState.putInt(ARGS_PAGE, page);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (UserStandingItem) savedInstanceState.getSerializable(ARGS_ADAPTER);
            page = savedInstanceState.getInt(ARGS_PAGE);
            error = savedInstanceState.getBoolean(ARGS_ERROR);
            last_page = savedInstanceState.getBoolean(ARGS_LAST_PAGE);

            if (error){
                getActivity().findViewById(R.id.table).setVisibility(View.GONE);
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
            }
            else{
                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.standing_item_list);
                recyclerView.setAdapter(adapter);
            }

        }
        else {

            adapter = new UserStandingItem(new ArrayList<UserRank>());
            new mAsyncTask(getActivity()).execute(page++);

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
        rootView = inflater.inflate(R.layout.fragment_standing, container, false);
        //modificar fragment como plazca

        TextView textView = (TextView) rootView.findViewById(R.id.rank_name);
        textView.setText(R.string.user);

        textView = (TextView) rootView.findViewById(R.id.cant_1);
        textView.setText(R.string.sub);

        textView = (TextView) rootView.findViewById(R.id.cant_2);
        textView.setText(R.string.ac);

        textView = (TextView) rootView.findViewById(R.id.cant_3);
        textView.setText(R.string.ac_percent);

        textView = (TextView) rootView.findViewById(R.id.cant_4);
        textView.setText(R.string.score);

        //crear la lista inicialmente vacia
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.standing_item_list);
        assert recyclerView != null;
        recyclerView.setAdapter(new UserStandingItem(new ArrayList<UserRank>()));

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //como saber que llego al final de la lista
                if (!last_page && recyclerView.getAdapter().getItemCount() != 0) {
                    int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1) {

                        new mAsyncTask(getActivity()).execute(page++);

                    }
                }

            }
        });

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.standing_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animateFab();

            }
        });

        final FragmentManager fm =  getActivity()
                .getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right);

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_1);
        fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.popBackStack();
                ft.replace(R.id.container, CountryStandingFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });

        fab = (FloatingActionButton) rootView.findViewById(R.id.fab_3);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.popBackStack();
                ft.replace(R.id.container, InstitutionsStandingFragment.newInstance())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    private float calculatePxTranslation(){

        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        float density =  metrics.densityDpi / (float)DisplayMetrics.DENSITY_DEFAULT;
        return 40 * density;

    }

    private void translateAndRotate(View view, float factor, int time, boolean isRotate){

        TranslateAnimation translate = new TranslateAnimation(0, 0, calculatePxTranslation() * factor, 0);
        translate.setDuration(time);

        RotateAnimation rotate = new RotateAnimation(360, 0, view.getWidth() * 0.5f, (calculatePxTranslation() * -factor) + (view.getHeight() * 0.5f));
        rotate.setDuration(250);
        rotate.setStartOffset(time);

        AnimationSet animationSet = new AnimationSet(false);

        if(isRotate)
            animationSet.addAnimation(rotate);

        animationSet.addAnimation(translate);

        view.startAnimation(animationSet);

    }

    private void animateFab() {

        final FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.standing_fab);
        FloatingActionButton fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_3);

        boolean hiden = fab_mini.getTranslationY() > -1 && fab_mini.getTranslationY() < 1;
        if (hiden) {

            fab.animate().translationY(calculatePxTranslation() * -4.5f).setDuration(500).start();

            //animate fab_1
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_1);
            translateAndRotate(fab_mini, 0, 0, true);

            fab_mini.setTranslationY(0);
            fab_mini.setVisibility(View.VISIBLE);
            fab_mini.setClickable(true);
            //animate fab_1

            //animate fab_2
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_2);
            translateAndRotate(fab_mini, 1.5f, 200, true);

            fab_mini.setTranslationY(calculatePxTranslation() * -1.5f);
            fab_mini.setVisibility(View.VISIBLE);
            fab_mini.setClickable(true);
            //animate fab_2

            //animate fab_3
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_3);
            translateAndRotate(fab_mini, 3.0f, 400, true);

            fab_mini.setTranslationY(calculatePxTranslation() * -3.0f);
            fab_mini.setVisibility(View.VISIBLE);
            fab_mini.setClickable(true);
            //animate fab_3

        }
        else {

            fab.animate().translationY((float) (0)).setDuration(500).start();

            //animate fab_1
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_1);
            fab_mini.setClickable(false);
            fab_mini.setTranslationY(0);
            translateAndRotate(fab_mini, 0, 350, false);
            fab_mini.setVisibility(View.GONE);
            //animate fab_1

            //animate fab_2
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_2);
            fab_mini.setClickable(false);
            fab_mini.setTranslationY(0);
            translateAndRotate(fab_mini, -1.5f, 400, false);
            fab_mini.setVisibility(View.GONE);
            //animate fab_2

            //animate fab_3
            fab_mini = (FloatingActionButton) getActivity().findViewById(R.id.fab_3);
            fab_mini.setClickable(false);
            fab_mini.setTranslationY(0);
            translateAndRotate(fab_mini, -3.0f, 450, false);
            fab_mini.setVisibility(View.GONE);
            //animate fab_3
        }
    }


    static class mAsyncTask extends AsyncTask<Integer, Void, List<UserRank>> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected int page;

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
        protected List<UserRank> doInBackground(Integer... pages) {

            List<UserRank> list = null;

            try {
                page = pages[0];
                list = Conexion.getInstance(fragment_reference.get()).getUserRank(page);
                error = false;
            } catch (IOException | JSONException e) {

                error = true;
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                try {
                    if (page == 1) {
                        list = dataBaseManager.getUserStandings();

                        if (list != null)
                            last_page = true;
                        if (list != null && list.size() != 0){
                            final FragmentActivity activity = fragment_reference.get();
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                Toast.makeText(fragment_reference.get().getApplicationContext(), R.string.off_line_mode, Toast.LENGTH_SHORT).show();
                                }
                            });
                           page = 1;
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
//                cancel(true);

            }

            return list;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar snackbar = Snackbar.make(fragment_reference.get().findViewById(R.id.standing_coordinator), R.string.standing_list_error, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.reload, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    last_page = false;
                    new mAsyncTask(fragment_reference.get()).execute(page);
                }
            });
            snackbar.show();
            progressDialog.dismiss();
            last_page = true;
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<UserRank> userRanks) {

            if (page == 1 && !error){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get().getApplicationContext());
                dataBaseManager.deleteAllUserStandings();
                for (int i = 0; i < userRanks.size(); i++) {
                    dataBaseManager.insertUserRank(userRanks.get(i));
                }
                dataBaseManager.closeDbConnections();
            }

            if (userRanks.size() == 0 && !error){
                last_page = true;
                Snackbar.make(fragment_reference.get().findViewById(R.id.standing_coordinator), R.string.no_more_users, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (userRanks.size() == 0 && adapter.getItemCount() == 0 && error){
                fragment_reference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                fragment_reference.get().findViewById(R.id.table).setVisibility(View.GONE);
            }
            else {

                final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.standing_item_list);
                adapter.addAll(userRanks);
                recyclerView.swapAdapter(adapter, false);

            }

            if (userRanks.size() != 0)
                error = false;

            if (userRanks.size() == 0 && adapter.getItemCount() == 0)
                error = true;

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }

}
