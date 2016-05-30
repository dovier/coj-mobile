package cu.uci.coj.Application.Profiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;

import cu.uci.coj.Application.Behaviors.AppBarLayoutBehavior;
import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Extras.EntriesList;
import cu.uci.coj.Application.LoginData;
import cu.uci.coj.Application.Mail.ComposeMessage;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    private final static String ARGS_USER = "user";
    private final static String ARGS_SAVE = "saved";
    private final static String ARGS_USER_PROFILE = "user_profile";

    private static UserProfile userProfile;
    private static boolean save;
    private String user;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ProfileFragment newInstance(String user, boolean save) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_USER, user);
        args.putBoolean(ARGS_SAVE, save);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ProfileFragment newInstance(String user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_USER, user);
        args.putBoolean(ARGS_SAVE, false);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (getArguments() != null) {
            user = getArguments().getString(ARGS_USER);
            save = getArguments().getBoolean(ARGS_SAVE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARGS_USER_PROFILE, userProfile);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            userProfile = (UserProfile) savedInstanceState.getSerializable(ARGS_USER_PROFILE);
            createView(getContext(), userProfile);
        }
        else {
            new mAsyncTask((FragmentActivity)getContext()).execute(user);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if (userProfile != null && user.equals(userProfile.getUserName())){
            AppBarLayout app_bar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
            app_bar.setExpanded(true, true);
            AppBarLayoutBehavior appBarBehavior = new AppBarLayoutBehavior(true);
            CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) app_bar.getLayoutParams();
            appBarLayoutParams.setBehavior(appBarBehavior);
            app_bar.setLayoutParams(appBarLayoutParams);
            CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) getActivity().findViewById(R.id.collapsing_toolbar_layout);
            toolbar.setTitle(userProfile.getUserName());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        NestedScrollView nested = (NestedScrollView) rootView.findViewById(R.id.nested_scroll_view);
        nested.setVisibility(View.GONE);

        LinearLayout error = (LinearLayout) rootView.findViewById(R.id.connection_error);
        error.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.profile_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    LoginData.read(getContext());
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.container, ComposeMessage.newInstance(userProfile.getUserName()))
                            .addToBackStack(null)
                            .commit();
                    //el usuario esta loggeado
                } catch (NoLoginFileException e) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.must_logged_in), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();

        AppBarLayout app_bar = (AppBarLayout) getActivity().findViewById(R.id.app_bar);
        app_bar.setExpanded(false, false);
        AppBarLayoutBehavior appBarBehavior = new AppBarLayoutBehavior(false);
        CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) app_bar.getLayoutParams();
        appBarLayoutParams.setBehavior(appBarBehavior);
        app_bar.setLayoutParams(appBarLayoutParams);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) app_bar.findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbarLayout.setTitle(getActivity().getResources().getString(R.string.app_name));

    }

    private static void createView(final Context context, UserProfile userProfile){

        final FragmentActivity view = (FragmentActivity)context;

        if (userProfile != null){

            AppBarLayout app_bar = (AppBarLayout) view.findViewById(R.id.app_bar);
            app_bar.setExpanded(true, true);
            AppBarLayoutBehavior appBarBehavior = new AppBarLayoutBehavior(true);
            CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) app_bar.getLayoutParams();
            appBarLayoutParams.setBehavior(appBarBehavior);
            app_bar.setLayoutParams(appBarLayoutParams);

            String name = userProfile.getName();
            if (name.charAt(name.length()-1) != ' ')
                name += " ";
            name += userProfile.getLastName();

            CollapsingToolbarLayout toolbar = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar_layout);
            final ImageView avatar = (ImageView) toolbar.findViewById(R.id.avatar);
            TextView nameTextView = (TextView) toolbar.findViewById(R.id.user_name);
            nameTextView.setText(name);
            toolbar.setTitle(userProfile.getUserName());

            final Transformation transformation = new RoundedTransformationBuilder()
                    .borderColor(view.getResources().getColor(R.color.colorPrimaryLight))
                    .borderWidthDp(2)
                    .cornerRadiusDp(90)
                    .oval(false)
                    .build();

            final int dim = (int)view.getResources().getDimension(R.dimen.avatar_size);

            String image = userProfile.getAvatar();
            image = image.replace("http://coj.uci.cu", Conexion.getInstance(context).getIMAGE_URL());

            Picasso.with(view)
                    .load(image)
                    .resize(dim, dim)
                    .transform(transformation)
                    .into(avatar, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(view)
                                    .load(R.drawable.default_avatar)
                                    .resize(dim, dim)
                                    .transform(transformation)
                                    .into(avatar);
                        }
                    });

            TextView textView = (TextView) view.findViewById(R.id.institution);
            textView.setText(userProfile.getInstitution());

            textView = (TextView) view.findViewById(R.id.favLanguage);
            textView.setText(userProfile.getFavLanguage());

            textView = (TextView) view.findViewById(R.id.registrationDate);
            textView.setText(userProfile.getRegistrationDate());

            textView = (TextView) view.findViewById(R.id.lastSubmissionDate);
            textView.setText(userProfile.getLastSubmission());

            textView = (TextView) view.findViewById(R.id.lastAcceptedDate);
            textView.setText(userProfile.getLastAccepted());

            textView = (TextView) view.findViewById(R.id.score);
            textView.setText(userProfile.getScore());

            textView = (TextView) view.findViewById(R.id.rankByUser);
            textView.setText(userProfile.getRankByUser());

            textView = (TextView) view.findViewById(R.id.rankByInstitution);
            textView.setText(userProfile.getRankByInstitution());

            textView = (TextView) view.findViewById(R.id.rankByCountry);
            textView.setText(userProfile.getRankByCountry());

            textView = (TextView) view.findViewById(R.id.followers);
            textView.setText(userProfile.getFollowers());

            textView = (TextView) view.findViewById(R.id.following);
            textView.setText(userProfile.getFollowing());

            textView = (TextView) view.findViewById(R.id.last_entry);
            textView.setText("");
            EntriesList.write(textView, userProfile.getLastEntry());

            NestedScrollView nested = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
            nested.setVisibility(View.VISIBLE);
            LinearLayout error = (LinearLayout) view.findViewById(R.id.connection_error);
            error.setVisibility(View.GONE);

        }
        else {

            AppBarLayout app_bar = (AppBarLayout) view.findViewById(R.id.app_bar);
            app_bar.setExpanded(false, false);
            AppBarLayoutBehavior appBarBehavior = new AppBarLayoutBehavior(false);
            CoordinatorLayout.LayoutParams appBarLayoutParams = (CoordinatorLayout.LayoutParams) app_bar.getLayoutParams();
            appBarLayoutParams.setBehavior(appBarBehavior);
            app_bar.setLayoutParams(appBarLayoutParams);

            NestedScrollView nested = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
            nested.setVisibility(View.GONE);
            LinearLayout error = (LinearLayout) view.findViewById(R.id.connection_error);
            error.setVisibility(View.VISIBLE);
        }
    }

    private static class mAsyncTask extends AsyncTask<String, Void, UserProfile> {

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
        protected UserProfile doInBackground(String... user) {

            UserProfile userProfile = null;
            Activity activity = fragment_reference.get();
            final LinearLayout error = (LinearLayout) activity.findViewById(R.id.connection_error);
            final NestedScrollView nestedScroll = (NestedScrollView) activity.findViewById(R.id.nested_scroll_view);
            try {
                userProfile = Conexion.getInstance(fragment_reference.get()).getUserProfile(user[0]);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        error.setVisibility(View.GONE);
                        nestedScroll.setVisibility(View.VISIBLE);
                    }
                });
            } catch (IOException | JSONException e) {
                e.printStackTrace();

                if (save){
                    DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get());
                    try {
                        userProfile = dataBaseManager.getUserProfile();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                    dataBaseManager.closeDbConnections();
                }
                if (userProfile == null){
                    cancel(true);
                }
            }

            return userProfile;
        }

        @Override
        protected void onCancelled() {

            userProfile = null;
            createView(fragment_reference.get(), null);
            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }

        @Override
        protected void onPostExecute(UserProfile profile) {

            if (save){
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(fragment_reference.get());
                dataBaseManager.deleteAllProfiles();
                dataBaseManager.insertProfile(profile);
                dataBaseManager.closeDbConnections();
            }

            userProfile = profile;
            createView(fragment_reference.get(), profile);

            progressDialog.dismiss();
            new ScreenOrientationLocker(fragment_reference.get()).unlock();

        }
    }
}
