package cu.uci.coj.Application.Problems;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import cu.uci.coj.Application.Behaviors.FloatingActionButtonBehavior;
import cu.uci.coj.Conexion;
import cu.uci.coj.DataBaseManager;
import cu.uci.coj.Application.Image;
import cu.uci.coj.Application.Judgments.BestSolutionsFragment;
import cu.uci.coj.Application.Profiles.ProfileFragment;
import cu.uci.coj.R;
import cu.uci.coj.Application.ScreenOrientationLocker;

/**
 * Created by osvel on 2/26/16.
 */
public class ProblemFragment extends Fragment {

    private static View rootView;

    private final static String ARGS_PROBLEM_DESCRIPTION = "problem_description";
    private final static String ARGS_PROBLEM_ITEM = "problem_item";
    private final static String ARGS_DATABASE = "database_load";
    private final static String ARGS_CONNECTION_ERROR = "connection_error";
    private final static String ARGS_LOGIN = "login";

    private static ProblemItem problemItem;
    private static Problem problemDescription;
    private static boolean saved = false;
    private static boolean connectionError = false;
    private static boolean login = false;
    private static FragmentManager fm = null;

    public ProblemFragment() {}

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment PoblemFragment.
     */
    public static ProblemFragment newInstance(ProblemItem problemItem, boolean login){
        ProblemFragment fragment = new ProblemFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGS_PROBLEM_ITEM, problemItem);
        args.putBoolean(ARGS_LOGIN, login);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        problemItem.setNullBitmap();
        outState.putSerializable(ARGS_PROBLEM_DESCRIPTION, problemDescription);
        outState.putBoolean(ARGS_DATABASE, saved);
        outState.putBoolean(ARGS_CONNECTION_ERROR, connectionError);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        problemItem = (ProblemItem) getArguments().getSerializable(ARGS_PROBLEM_ITEM);
        login = getArguments().getBoolean(ARGS_LOGIN);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            problemDescription = (Problem) savedInstanceState.getSerializable(ARGS_PROBLEM_DESCRIPTION);
            saved = savedInstanceState.getBoolean(ARGS_DATABASE);
            connectionError = savedInstanceState.getBoolean(ARGS_CONNECTION_ERROR);

            if (!connectionError)
                createView(getContext(), problemDescription);
            else{
                rootView.findViewById(R.id.problem_description_scroll).setVisibility(View.GONE);
                rootView.findViewById(R.id.connection_error).setVisibility(View.VISIBLE);

                FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.problem_description_fab);
                CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                FloatingActionButtonBehavior behavior = new FloatingActionButtonBehavior(false);
                fabLayoutParams.setBehavior(behavior);
                fab.setLayoutParams(fabLayoutParams);
                fab.setVisibility(View.GONE);
            }
        }
        else {
            new mAsyncTask(getActivity()).execute(problemItem.getID());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_problem, container, false);

        fm = getActivity().getSupportFragmentManager();

        if (login){
            rootView.findViewById(R.id.submit).setVisibility(View.VISIBLE);
        }
        else {
            rootView.findViewById(R.id.submit).setVisibility(View.GONE);
        }

        TextView submit = (TextView) rootView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, SubmitFragment.newInstance((int) problemItem.getLongId()))
                        .addToBackStack(null)
                        .commit();
            }
        });


        TextView best_solutions = (TextView) rootView.findViewById(R.id.best_solutions);
        best_solutions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, BestSolutionsFragment.newInstance((int) problemItem.getLongId()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
//        dataBaseManager.closeDbConnections();
    }

    /**
     * @TODO: Elimina algunas etiquetas inecesarias
     *
     * @param html Texto en formato HTML para limpiar de etiquetas inecesarias
     * @return Texto sin etiquetas inecesarias
     */
    private static String cleanHtml(Context context, String html){

        html = html.replace("\n", "<br/>");

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (size.x < 1000){
            html = html.replace("  ", " ");
        }
        if (size.x < 500){
            html = html.replace("  ", " ");
        }
        html = "<div style=\"text-align: justify;\">" + html + "</div>";

        return html;
    }

    /**
     * TODO: clean html tags
     *
     * @param cadena
     * @return
     */
    private static String cleanString(String cadena){

        String new_line = "My_NeW_lin3_seCuENC3";
        cadena = cadena.replace("\n", new_line);
        cadena = Html.fromHtml(cadena).toString();
        return cadena.replace(new_line, "\n");

    }

    /**
     * TODO: return string array with:
     *      array[0] = string before img
     *      array[1] = img URL
     *      array[2] = string after img
     *
     * @param text with problem description
     *
     * @return array with images urls
     */
    public static String[] getImgURL(String text){

        String[] code = new String[3];

        //limpiar el texto de caracteres no deseados
//        String only_text = text.replace(" ", "");
//        only_text = only_text.replace("\n", "");
//        only_text = only_text.replace("\t", "");
//        only_text = only_text.toLowerCase();

        String lower_case_text = text.toLowerCase();

        //buscar la etiqueta img
        String[] split_aux = lower_case_text.split("img");
        String url = null;

        String[] new_split;
        int url_index, url_end_index;
        //si se encontro alguna etiqueta y en la cadena despues de ella existe alguna etiqueta src se toma la url
        for (int i = 1; i < split_aux.length; i++) {
            if (split_aux[i].contains("src")){
                split_aux = split_aux[i].split("src");

                try {
                    int j = 0;
                    while (split_aux[1].charAt(j++) != '\"') ;
                    url_index = j++;
                    while (split_aux[1].charAt(j++) != '\"') ;
                    url_end_index = --j;
                }
                catch (StringIndexOutOfBoundsException e){
                    e.printStackTrace();
                    continue;
                }

                url = split_aux[1].substring(url_index, url_end_index);
                url_index = lower_case_text.indexOf(url);
                url = text.substring(url_index, url_index + url.length());
                break;
            }
        }

        String before, after = null, full_hastag;
        if (url != null){
            try {
                //tomo lo que esta antes de la url
                before = text.split(url)[0];
                after = text.split(url)[1];
                full_hastag = "<" + before.split("<")[before.split("<").length - 1] + url + after.split(">")[0] + ">";
                new_split = text.split(full_hastag);
                if (new_split.length > 0)
                    before = text.split(full_hastag)[0];
                else
                    before = null;
                if (new_split.length > 1)
                    after = text.split(full_hastag)[1];
                else
                    after = null;

                while (url.charAt(0) == '.') {
                    url = (String) url.subSequence(1, url.length());
                }
            }
            catch (Exception e){
                code[0] = text;
                code[1] = null;
                code[2] = null;
                return code;
            }
        }
        else
            before = text;

        code[0] = before;
        code[1] = url;
        code[2] = after;

        return code;

    }

    /**
     * TODO: create view resources for layout_descripption
     * Es necesario usar webviews para que reconozca las etiquetas como tablas y otras.
     *
     * @param context contexto de la aplicacion para escribir en la interfaz grafica
     * @param layout layout al que se le incorporara la descripcion
     * @param text descripcion a incorporar en el layout
     *
     */
    public static void write(final Context context, final LinearLayout layout, String text){

        final String[] split_text = getImgURL(text);

        //si hay texto antes de la imagen? escribo el webView
        if (split_text[0] != null){
            WebView webView = new WebView(context);
            webView.setLayerType(View.LAYER_TYPE_NONE, null);
            webView.setLayoutParams(new ActionBar.LayoutParams(StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT));
            webView.loadData(cleanHtml(context, split_text[0]), "text/html; charset=utf-8", null);

            webView.setWebViewClient(new WebViewClient(){
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null){
                        if (url.startsWith("/")){
                            view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(Conexion.getInstance(context).getCOJ_URL() + url)));
                            return true;
                        }
                        else {
                            view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                            return true;
                        }
                    }
                    return false;
                }
            });

            webView.getRootView();
            layout.addView(webView);
            webView.setVisibility(View.VISIBLE);
        }
        if (split_text[1]  != null){
            final ImageView imageView = new ImageView(context);
            layout.addView(imageView);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            String url = Conexion.getInstance(context).getIMAGE_URL()+split_text[1];
            url = url.replace(" ", "%20");

            Picasso.with(context)
                    .load(url)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {

                            /**
                             * almacenar los bitmaps con los nombres para escribirlos en disco si es necesario
                             */
                            String splitted[] = split_text[1].split("/");
                            String name = splitted[splitted.length - 1];

                            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            problemItem.addImage(name, bmp);

                        }

                        @Override
                        public void onError() {

                            String splitted[] = split_text[1].split("/");
                            final String name = splitted[splitted.length - 1];
                            String dir = context.getFilesDir().getAbsolutePath();
//                            String dir = rootView.getContext().getFilesDir().getAbsolutePath();

                            //file:///mnt/sdcard/image.png

                            Picasso.with(context)
//                            Picasso.with(rootView.getContext())
                                    .load("file://" + dir + "/" + name)
                                    .into(imageView, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                            /**
                                             * almacenar los bitmaps con los nombres para escribirlos en disco si es necesario
                                             */

                                            Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                            problemItem.addImage(name, bmp);
                                        }

                                        @Override
                                        public void onError() {
                                            Toast.makeText(context.getApplicationContext(), R.string.image_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

        }
        if (split_text[2] != null){
            write(context, layout, split_text[2]);
        }
    }

    private static void createView(final Context context, final Problem problem){

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout_description);
        write(context, layout, problem.getDescription());

        layout = (LinearLayout) rootView.findViewById(R.id.layout_input_specification);
        write(context, layout, problem.getInputSpecifications());

        layout = (LinearLayout) rootView.findViewById(R.id.layout_output_specification);
        write(context, layout, problem.getOutputSpecification());

        layout = (LinearLayout) rootView.findViewById(R.id.layout_hints);
        write(context, layout, problem.getHints());

        WebView webView = (WebView) rootView.findViewById(R.id.recommendation_text);
        webView.loadData(rootView.getResources().getString(R.string.recommendation_large) +
                cleanHtml(context, problem.getRecommendation()), "text/html; charset=utf-8", null);

        //trabajando con textview
        TextView textView = (TextView) rootView.findViewById(R.id.sample_input);
        textView.setText(cleanString(problem.getSampleInput()));

        textView = (TextView) rootView.findViewById(R.id.sample_output);
        textView.setText(cleanString(problem.getSampleOutput()));

        textView = (TextView) rootView.findViewById(R.id.title_tex_view);
        textView.setText((problemItem.getID() + " - " + problemItem.getProblem_name()).toUpperCase());

        textView = (TextView) rootView.findViewById(R.id.created_by);
        textView.setText(problem.getCreatedby());

        textView = (TextView) rootView.findViewById(R.id.added_by);
        textView.setText(problem.getAddedby());

        textView = (TextView) rootView.findViewById(R.id.statistics);
        textView.setText("");
        textView.append(rootView.getResources().getString(R.string.sub) + ": " + problemItem.getSubmit()
                + " | " + rootView.getResources().getString(R.string.ac) + ": " + problemItem.getAccept()
                + " | " + rootView.getResources().getString(R.string.ac_percent) + ": " + problemItem.getAccept_percent()
                + " | " + rootView.getResources().getString(R.string.score) + ": " + problemItem.getScore());

        textView = (TextView) rootView.findViewById(R.id.date_creation);
        textView.setText("");
        textView.append(" (" + problem.getDateOfCreation() + ")");

        Spinner spinner = (Spinner) rootView.findViewById(R.id.language);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, problem.getEnabledlanguages());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                TextView limits_TextView = (TextView) rootView.findViewById(R.id.limits);
                limits_TextView.setText("");
                limits_TextView.append(rootView.getResources().getString(R.string.total_time)
                        + problem.getTotaltime()[i] + " | " + rootView.getResources().getString(R.string.test_time) + problem.getTesttime()[i]
                        + " | " + rootView.getResources().getString(R.string.memory) + problem.getMemory()[i] + " | "
                        + rootView.getResources().getString(R.string.output) + problem.getOutput() + " | "
                        + rootView.getResources().getString(R.string.size_) + problem.getSize()[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        final DataBaseManager dataBaseManager = DataBaseManager.getInstance(context);

        final FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.problem_description_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!saved) {
                    /**
                     * insertar el problema en la base de datos
                     */
                    dataBaseManager.insertProblem(problemItem, problemDescription);

                    /**
                     * guardar las imagenes en disco
                     *
                     * A continuación, crearemos un flujo de salida de byte[] (ByteArrayOutputStream) y
                     * lo usaremos para cargar en este el Bitmap comprimido y después obtener el byte[].
                     */

                    List<Image> images = problemItem.getImages();
                    for (int i = 0; i < images.size(); i++) {
                        Bitmap bmp = images.get(i).getImage();
                        if (bmp != null) {
                            String name = images.get(i).getName();

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            try {
                                FileOutputStream outputStream = rootView.getContext().openFileOutput(name, Context.MODE_PRIVATE);
                                outputStream.write(byteArray);
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    fab.setImageResource(R.drawable.delete);
                    saved = true;
                    Toast.makeText(context, R.string.saved, Toast.LENGTH_SHORT).show();
                } else {

                    /**
                     * eliminar problema de la base de datos
                     */

                    dataBaseManager.deleteProblem(Integer.parseInt(problemItem.getID()));
                    dataBaseManager.closeDbConnections();

                    /**
                     * eliminar las imagenes del disco
                     */
                    List<Image> images = problemItem.getImages();
                    for (int i = 0; i < images.size(); i++) {
                        String name = images.get(i).getName();

                        try {
                            // delete the original file
                            String dir = rootView.getContext().getFilesDir().getAbsolutePath();
                            new File(dir, name).delete();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }

                    fab.setImageResource(R.drawable.download);
                    saved = false;
                    Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (saved){
            fab.setImageResource(R.drawable.delete);
        }
        else {
            fab.setImageResource(R.drawable.download);
        }

        rootView.findViewById(R.id.added_by).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
                ft.replace(R.id.container, ProfileFragment.newInstance(((TextView) rootView.findViewById(R.id.added_by)).getText().toString()));
                ft.addToBackStack(null);
                ft.commit();
            }
        });

    }

    static class mAsyncTask extends AsyncTask<String, Void, Problem> {

//        private boolean imageError = false;
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
        protected Problem doInBackground(String... id) {

            Problem problem = null;
            Context context = fragment_reference.get().getApplicationContext();

            String mId = id[0];
            saved = false;

            try {
                //intetar conectarse
                problem = Conexion.getInstance(fragment_reference.get()).getProblem(mId);

            } catch (IOException | JSONException e) {

                //en caso que la coneccion falle intentar buscar en la base de datos
                DataBaseManager dataBaseManager = DataBaseManager.getInstance(context);

                try {
                    problem = dataBaseManager.getProblemByID(Integer.parseInt(mId));
                    saved = true;
                } catch (Exception e1) {
                    //si la base de datos tambien falla cancelar la operacion
                    e1.printStackTrace();
                    cancel(true);
                }

                if (problem == null)
                    cancel(true);

                dataBaseManager.closeDbConnections();
            }

            return problem;
        }

        @Override
        protected void onCancelled() {
            //invocar el callback para salir del fragment que dio error

            final FragmentActivity activity = fragment_reference.get();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.problem_description_scroll).setVisibility(View.GONE);
                    activity.findViewById(R.id.connection_error).setVisibility(View.VISIBLE);

                    FloatingActionButton fab = (FloatingActionButton) activity.findViewById(R.id.problem_description_fab);
                    CoordinatorLayout.LayoutParams fabLayoutParams = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
                    FloatingActionButtonBehavior behavior = new FloatingActionButtonBehavior(false);
                    fabLayoutParams.setBehavior(behavior);
                    fab.setLayoutParams(fabLayoutParams);
                    fab.setVisibility(View.GONE);
                }
            });

            progressDialog.dismiss();
            connectionError = true;

            new ScreenOrientationLocker(fragment_reference.get()).unlock();


        }


        /**
         * @TODO: Create problem description View
         *
         * @param problem
         */
        @Override
        protected void onPostExecute(final Problem problem) {

            connectionError = false;
            problemDescription = problem;

            createView(fragment_reference.get(), problemDescription);

            progressDialog.dismiss();

            new ScreenOrientationLocker(fragment_reference.get()).unlock();
        }
    }
}
