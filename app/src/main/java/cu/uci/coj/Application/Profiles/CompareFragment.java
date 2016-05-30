package cu.uci.coj.Application.Profiles;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Conexion;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link CompareFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompareFragment extends Fragment {

    private static View rootView;
    private static Compare compare = null;

    private static String SAVED_INSTANCE_ARGS_COMPARE = "compare";
    private static String SAVED_INSTANCE_ARGS_isSAVED = "saved";

    private static compareCallBack mListener = new compareCallBack() {
        @Override
        public void compareFragmentCallBack(String message) {}
    };

    public CompareFragment() {
        // Required empty public constructor
    }

    public interface compareCallBack {
        void compareFragmentCallBack(String message);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    // TODO: Rename and change types and number of parameters
    public static CompareFragment newInstance() {
        CompareFragment fragment = new CompareFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            if(savedInstanceState.getBoolean(SAVED_INSTANCE_ARGS_isSAVED)){
                compare = (Compare)savedInstanceState.getSerializable(SAVED_INSTANCE_ARGS_COMPARE);
                rootView.findViewById(R.id.compare_layout).setVisibility(View.VISIBLE);
                createView();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (compare != null){
            outState.putSerializable(SAVED_INSTANCE_ARGS_COMPARE, compare);
            outState.putBoolean(SAVED_INSTANCE_ARGS_isSAVED, rootView.findViewById(R.id.compare_layout).getVisibility() == View.VISIBLE);
        }
        else
            outState.putBoolean(SAVED_INSTANCE_ARGS_isSAVED, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_compare, container, false);
        //modificar fragment como plazca

//        ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.compare_user_scroll);
//        scrollView.setNestedScrollingEnabled(false);


        final EditText user_1 = (EditText) rootView.findViewById(R.id.compare_user_1);
        final EditText user_2 = (EditText) rootView.findViewById(R.id.compare_user_2);

        LinearLayout buton = (LinearLayout) rootView.findViewById(R.id.button_compare);
        buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearLayout compare_layout = (LinearLayout) rootView.findViewById(R.id.compare_layout);
                compare_layout.setVisibility(View.GONE);
                compare_layout.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fade_out));
                if (user_1.getText().length() != 0 && user_2.getText().length() != 0)
                    new mAsyncTask(getActivity(), CompareFragment.this).execute(user_1.getText().toString(), user_2.getText().toString());
                else
                    mListener.compareFragmentCallBack(getResources().getString(R.string.user_invalid));

                TextView text = (TextView) rootView.findViewById(R.id.solved_user_1);
                text.setText(user_1.getText());
                text = (TextView) rootView.findViewById(R.id.solved_user_2);
                text.setText(user_2.getText());
                text = (TextView) rootView.findViewById(R.id.tried_user_1);
                text.setText(user_1.getText());
                text = (TextView) rootView.findViewById(R.id.tried_user_2);
                text.setText(user_2.getText());
            }
        });
        return rootView;
    }

    public void createView(){

        //saber el tamaño de la pantalla en dp
        DisplayMetrics displayMetrics = rootView.getResources().getDisplayMetrics();

        int textView_width = rootView.getResources().getDimensionPixelOffset(R.dimen.circular_problem_width);
        int padding = rootView.getResources().getDimensionPixelOffset(R.dimen.padding_half);

        //cantidad de columnas = ancho de la pantalla / (ancho de un elemento + margin del elemento)
        int columnCount = (int)(displayMetrics.widthPixels - rootView.getResources().getDimension(R.dimen.activity_horizontal_margin)*2)
                / (textView_width + padding*2);

        //cambiar el margen de los elementos dentro del GridLayout
        GridLayout.LayoutParams param;
        TextView problem_id;

        //obtener el gridLayout
        GridLayout gridLayout = (GridLayout)rootView.findViewById(R.id.grid_solved_user_1);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas resuletos por el usuario 1
        for (int i = 0; i < compare.sizeSolved_user_1(); i++) {

            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(rootView.getContext());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //colores para el TextView problemas aceptados
            problem_id.setBackgroundResource(R.drawable.solved);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.accept));

            problem_id.setText(compare.getSolved_user_1(i));
            gridLayout.addView(problem_id);
        }

        //obtener el gridLayout
        gridLayout = (GridLayout)rootView.findViewById(R.id.grid_solved_user_2);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas resuletos por el usuario 2
        for (int i = 0; i < compare.sizeSolved_user_2(); i++) {
            //elementos para el usuario 2
            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(rootView.getContext());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //colores para el TextView problemas aceptados
            problem_id.setBackgroundResource(R.drawable.solved);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.accept));

            problem_id.setText(compare.getSolved_user_2(i));
            gridLayout.addView(problem_id);
        }

        gridLayout = (GridLayout)rootView.findViewById(R.id.grid_solved_both);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas resuletos por ambos
        for (int i = 0; i < compare.sizeSolved_both(); i++) {

            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(rootView.getContext());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //nuevo color del textView para los aceptados por ambos
            problem_id.setBackgroundResource(R.drawable.solved_both);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.accept_both));

            problem_id.setText(compare.getSolved_both(i));
            gridLayout.addView(problem_id);
        }

        gridLayout = (GridLayout)rootView.findViewById(R.id.grid_tried_user_1);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas intentado por el usuario 1
        for (int i = 0; i < compare.sizeTried_user_1(); i++) {

            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(getActivity());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //nuevo color del textView para los intentados
            problem_id.setBackgroundResource(R.drawable.tried);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.tried));

            problem_id.setText(compare.getTried_user_1(i));
            gridLayout.addView(problem_id);
        }

        gridLayout = (GridLayout)rootView.findViewById(R.id.grid_tried_user_2);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas intentado por el usuario 2
        for (int i = 0; i < compare.sizeTried_user_2(); i++) {

            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(rootView.getContext());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //nuevo color del textView para los intentados
            problem_id.setBackgroundResource(R.drawable.tried);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.tried));

            problem_id.setText(compare.getTried_user_2(i));
            gridLayout.addView(problem_id);
        }

        gridLayout = (GridLayout)rootView.findViewById(R.id.grid_tried_both);
        gridLayout.setColumnCount(columnCount);
        gridLayout.removeAllViews();

        //problemas intentado por los dos
        for (int i = 0; i < compare.sizeTried_both(); i++) {

            param = new GridLayout.LayoutParams();
            param.rightMargin = padding;
            param.leftMargin = padding;
            param.topMargin = padding;
            param.bottomMargin = padding;
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = textView_width;
            param.setGravity(Gravity.CENTER);

            problem_id = new TextView(rootView.getContext());
            problem_id.setPadding(padding, padding, padding, padding);
            problem_id.setGravity(Gravity.CENTER);
            problem_id.setLayoutParams(param);

            //nuevo color del textView para los intentado por los dos
            problem_id.setBackgroundResource(R.drawable.tried_both);
            problem_id.setTextColor(rootView.getResources().getColor(R.color.no_accept));

            problem_id.setText(compare.getTried_both(i));
            gridLayout.addView(problem_id);
        }
    }

    static class mAsyncTask extends AsyncTask<String, Void, Compare> {

        protected WeakReference<FragmentActivity> fragment_reference;
        protected ProgressDialog progressDialog;
        protected LinearLayout compare_layout;
        protected CompareFragment compareFragment;

        public mAsyncTask(FragmentActivity fragment_reference, CompareFragment compareFragment) {
            this.fragment_reference = new WeakReference<>(fragment_reference);
            this.compareFragment = compareFragment;
        }

        @Override
        protected void onPreExecute() {
            final FragmentActivity activity = fragment_reference.get();

            new ScreenOrientationLocker(activity).lock();
            compare_layout = (LinearLayout)rootView.findViewById(R.id.compare_layout);

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog = ProgressDialog.show(activity, "",
                            activity.getString(R.string.loading), true);
                }
            });
        }

        @Override
        protected Compare doInBackground(String... url) {

            try {
                compare = Conexion.getInstance(fragment_reference.get()).getCompareUsers(url[0], url[1]);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                compare = null;
                cancel(true);
            }

            return compare;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mListener.compareFragmentCallBack(rootView.getResources().getString(R.string.compare_error));
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(Compare onPost) {

            //inflar los elementos del compare institution

            compare = onPost;
            compareFragment.createView();
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
            compare_layout.setVisibility(View.VISIBLE);
            compare_layout.startAnimation(AnimationUtils.loadAnimation(fragment_reference.get().getBaseContext(), R.anim.fade_in));
        }
    }
}
