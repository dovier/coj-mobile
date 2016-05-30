package cu.uci.coj.Application.Standings;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.R;

/**
 * Created by osvel on 2/28/16.
 */
public class InstitutionStandingItem extends RecyclerView.Adapter<InstitutionStandingItem.ViewHolder> implements Serializable{

    private List<InstitutionRank> institutionRanks = new ArrayList<>();

    public InstitutionStandingItem(List<InstitutionRank> institutionRank) {
        this.institutionRanks = institutionRank;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = institutionRanks.get(position);
        holder.rank.setText(holder.mItem.getRank());

        try {
            InputStream ims = holder.itemView.getContext().getAssets().open(institutionRanks.get(position).getCountry()+".png");
            Drawable d = Drawable.createFromStream(ims, null);
            holder.country.setVisibility(View.VISIBLE);
            holder.country.setImageDrawable(d);
        } catch (IOException e) {
            holder.country.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        holder.institution.setText(holder.mItem.getInstitution());
        holder.users.setText(holder.mItem.getUsers());
        holder.ac.setText(holder.mItem.getAc());
        holder.cant_gone.setVisibility(View.GONE);
        holder.score.setText(holder.mItem.getScore());
    }

    @Override
    public int getItemCount() {
        return institutionRanks.size();
    }

    public boolean addAll(List<InstitutionRank> ranks){
        return this.institutionRanks.addAll(ranks);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        protected InstitutionRank mItem;
        protected View itemView;
        protected TextView rank;
        protected ImageView country;
        protected TextView institution;
        protected TextView users;
        protected TextView ac;
        protected TextView cant_gone;
        protected TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.rank = (TextView) itemView.findViewById(R.id.id);
            this.country = (ImageView) itemView.findViewById(R.id.fav_buttom);
            this.institution = (TextView) itemView.findViewById(R.id.problem_name);
            this.users = (TextView) itemView.findViewById(R.id.item_cant_1);
            this.ac = (TextView) itemView.findViewById(R.id.item_cant_2);
            this.cant_gone = (TextView) itemView.findViewById(R.id.item_cant_3);
            this.score = (TextView) itemView.findViewById(R.id.item_cant_4);
        }
    }
}