package cu.uci.coj.Application.Extras;

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
 * Created by osvel on 4/11/16.
 */
public class FaqFragment extends Fragment {

    private final String ARG_ADAPTER = "adapter";
    private final String ARG_CONNECTION_ERROR = "connection_error";

    private static boolean connectionError;
    private static FaqList adapter;

    public FaqFragment() {}

    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARG_ADAPTER, adapter);
        outState.putBoolean(ARG_CONNECTION_ERROR, connectionError);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){

            adapter = (FaqList) savedInstanceState.getSerializable(ARG_ADAPTER);
            connectionError = savedInstanceState.getBoolean(ARG_CONNECTION_ERROR);
            if (!connectionError){
                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.faq_item_list);
                recyclerView.setAdapter(adapter);
            }
            else {
                getActivity().findViewById(R.id.faq_item_list).setVisibility(View.GONE);
                getActivity().findViewById(R.id.faq_title).setVisibility(View.GONE);
                getActivity().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
            }
        }
        else {

            adapter = new FaqList();
            new mAsyncTask(getActivity()).execute();

        }

    }

    public static class mAsyncTask extends AsyncTask<Void, Void, List<FaqItem>>{

        protected WeakReference<FragmentActivity> weakReference;
        protected ProgressDialog progressDialog;

        public mAsyncTask(FragmentActivity fragmentActivity) {
            this.weakReference = new WeakReference<>(fragmentActivity);
        }

        @Override
        protected void onPreExecute() {
            final FragmentActivity activity = weakReference.get();

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
        protected List<FaqItem> doInBackground(Void... voids) {

            List<FaqItem> faqItemList = new ArrayList<>();

            try {
                faqItemList = Conexion.getInstance(weakReference.get()).getFaq();
                connectionError = false;
            } catch (IOException | JSONException e) {
                connectionError = true;
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(weakReference.get());
                try {
                    faqItemList = dataBaseManager.getFAQs();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }

            return faqItemList;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Snackbar snackbar = Snackbar.make(weakReference.get().findViewById(R.id.faq_coordinator), R.string.faq_error, Snackbar.LENGTH_INDEFINITE);
            snackbar.setAction(R.string.reload, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new mAsyncTask(weakReference.get()).execute();
                }
            });
            snackbar.show();
            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<FaqItem> faqItems) {

            if (!connectionError && faqItems != null && faqItems.size() != 0){
                //consulta a la red satisfactoria
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(weakReference.get());
                dataBaseManager.deleteAllFAQs();
                for (int i = 0; i < faqItems.size(); i++) {
                    dataBaseManager.insertFAQ(faqItems.get(i));
                }
                dataBaseManager.closeDbConnections();
            }
            else if (!connectionError && faqItems != null && faqItems.size() == 0){
                //no faqs
                Toast.makeText(weakReference.get(), R.string.no_faq_error, Toast.LENGTH_LONG).show();
            }
            else if (connectionError && faqItems != null && faqItems.size() == 0){
                //no faqs en base de datos = connection error
                weakReference.get().findViewById(R.id.connection_error).setVisibility(View.VISIBLE);
                weakReference.get().findViewById(R.id.faq_title).setVisibility(View.GONE);
                weakReference.get().findViewById(R.id.faq_item_list).setVisibility(View.GONE);
            }

            connectionError = faqItems == null || faqItems.size() == 0;

            if (!connectionError){
                RecyclerView recyclerView = (RecyclerView) weakReference.get().findViewById(R.id.faq_item_list);
                adapter = new FaqList(faqItems);
                recyclerView.setAdapter(adapter);
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();

        }
    }
}
