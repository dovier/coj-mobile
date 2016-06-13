package cu.uci.coj.Application.Problems;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Exceptions.NoLoginFileException;
import cu.uci.coj.Application.Exceptions.UnauthorizedException;
import cu.uci.coj.R;
import cu.uci.coj.Application.Status;

/**
 * Created by osvel on 2/21/16.
 */
public class ProblemList extends RecyclerView.Adapter<ProblemList.ViewHolder> implements Serializable{

    private List<ProblemItem> problemItemList = new ArrayList<>();
    private boolean login;

    public ProblemList(List<ProblemItem> problemItemList, boolean login) {
        this.problemItemList = problemItemList;
        this.login = login;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.mItem = problemItemList.get(position);
        holder.id.setText(problemItemList.get(position).getID());
        holder.problem_name.setText(problemItemList.get(position).getProblem_name());
        holder.submit.setText(problemItemList.get(position).getSubmit());
        holder.accept.setText(problemItemList.get(position).getAccept());
        holder.accept_percent.setText(problemItemList.get(position).getAccept_percent());
        holder.score.setText(problemItemList.get(position).getScore());
        int color = holder.itemView.getResources().getColor(android.R.color.black);

        final FragmentActivity activity = (FragmentActivity)holder.itemView.getContext();

        //si el usuario esta logueado
        if (login) {

            holder.fav.setVisibility(View.VISIBLE);
            if (problemItemList.get(position).isFav()) {
                holder.fav.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                holder.fav.setImageResource(android.R.drawable.btn_star_big_off);
            }

            if (problemItemList.get(position).getStatus() == Status.AC) {
                color = holder.itemView.getResources().getColor(R.color.accept);
            } else if (problemItemList.get(position).getStatus() == Status.Unsolved) {
                color = holder.itemView.getResources().getColor(R.color.no_accept);
            }

            //establecer el onclick del layout
            holder.itemView.findViewById(R.id.layout_fav_buttom).setVisibility(View.VISIBLE);
            holder.itemView.findViewById(R.id.layout_fav_buttom).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //onclick del favorito

                    if (problemItemList.get(position).isFav()){
                        new mAsyncTask(activity, (int)holder.mItem.getLongId(), false).execute();
                        holder.fav.setImageResource(android.R.drawable.btn_star_big_off);
                        problemItemList.get(position).setFav();
                    }
                    else {
                        new mAsyncTask(activity, (int)holder.mItem.getLongId(), true).execute();
                        holder.fav.setImageResource(android.R.drawable.btn_star_big_on);
                        problemItemList.get(position).setFav();
                    }

                }
            });

        }
        else {
            holder.fav.setVisibility(View.GONE);
            holder.itemView.findViewById(R.id.layout_fav_buttom).setVisibility(View.GONE);
        }

        holder.id.setTextColor(color);
        holder.problem_name.setTextColor(color);
        holder.submit.setTextColor(color);
        holder.accept.setTextColor(color);
        holder.accept_percent.setTextColor(color);
        holder.score.setTextColor(color);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //acceder a la vista del problema

                FragmentManager fm = activity.getSupportFragmentManager();
                fm.beginTransaction()
                        .replace(R.id.container, ProblemFragment.newInstance(holder.mItem, login))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    public boolean addAll(List<ProblemItem> problemItems){

        return this.problemItemList.addAll(problemItems);

    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public void setProblemItemList(List<ProblemItem> problemItemList) {
        this.problemItemList = problemItemList;
    }

    @Override
    public int getItemCount() {
        return problemItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Serializable{

        public View itemView;
        public TextView id;
        public ImageView fav;
        public TextView problem_name;
        public TextView submit;
        public TextView accept;
        public TextView accept_percent;
        public TextView score;
        public ProblemItem mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.id = (TextView) itemView.findViewById(R.id.id);
            this.fav = (ImageView) itemView.findViewById(R.id.fav_buttom);
            this.problem_name = (TextView) itemView.findViewById(R.id.problem_name);
            this.submit = (TextView) itemView.findViewById(R.id.item_cant_1);
            this.accept = (TextView) itemView.findViewById(R.id.item_cant_2);
            this.accept_percent = (TextView) itemView.findViewById(R.id.item_cant_3);
            this.score = (TextView) itemView.findViewById(R.id.item_cant_4);
        }
    }

    public static class mAsyncTask extends AsyncTask<Void, Void, Void>{

        protected int id;
        protected boolean favorite;
        protected WeakReference<FragmentActivity> fragment_reference;

        public mAsyncTask(FragmentActivity activity, int id, boolean favorite) {
            this.fragment_reference = new WeakReference<>(activity);
            this.id = id;
            this.favorite = favorite;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Conexion.getInstance(fragment_reference.get()).toggleFavorite(fragment_reference.get(), id);
            } catch (IOException | JSONException | NoLoginFileException | UnauthorizedException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}

