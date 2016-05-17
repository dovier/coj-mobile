package cu.uci.coj.Extras;

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

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.R;
import cu.uci.coj.ScreenOrientationLocker;

/**
 * Created by osvel on 4/11/16.
 */
public class FaqFragment extends Fragment {

    private final String ARG_ADAPTER = "adapter";

    private static FaqList adapter;

    public FaqFragment() {}

    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARG_ADAPTER, adapter);
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
            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.faq_item_list);
            recyclerView.setAdapter(adapter);
        }
        else {

            adapter = new FaqList();
            new mAsyncTask(getActivity()).execute(Conexion.URL_FAQ);

        }

    }

    public static class mAsyncTask extends AsyncTask<String, Void, List<FaqItem>>{

        protected WeakReference<FragmentActivity> weakReference;
        protected ProgressDialog progressDialog;
        protected String url;

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
        protected List<FaqItem> doInBackground(String... url) {

            this.url = url[0];
            List<FaqItem> faqItemList = new ArrayList<>();

            try {
                faqItemList = Conexion.getFaq(this.url);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                cancel(true);
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
                    new mAsyncTask(weakReference.get()).execute(url);
                }
            });
            snackbar.show();
            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();
        }

        @Override
        protected void onPostExecute(List<FaqItem> faqItems) {

            RecyclerView recyclerView = (RecyclerView) weakReference.get().findViewById(R.id.faq_item_list);
            if (faqItems.size() == 0)
                Snackbar.make(weakReference.get().findViewById(R.id.faq_coordinator), R.string.no_faq_error, Snackbar.LENGTH_LONG).show();
            else {
                adapter = new FaqList(faqItems);
                recyclerView.setAdapter(adapter);
            }

            progressDialog.dismiss();
            new ScreenOrientationLocker(weakReference.get()).unlock();

        }
    }
}
