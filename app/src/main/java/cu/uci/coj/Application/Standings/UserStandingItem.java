package cu.uci.coj.Application.Standings;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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

import cu.uci.coj.Application.Profiles.ProfileFragment;
import cu.uci.coj.R;

/**
 * Created by osvel on 2/28/16.
 */
public class UserStandingItem extends RecyclerView.Adapter<UserStandingItem.ViewHolder> implements Serializable{

    private List<UserRank> userRankList = new ArrayList<>();

    public UserStandingItem(List<UserRank> userRankList) {
        this.userRankList = userRankList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.problem_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = userRankList.get(position);
        holder.rank.setText(holder.mItem.getRank());

        try {
            InputStream ims = holder.itemView.getContext().getAssets().open(userRankList.get(position).getCountry()+".png");
            Drawable d = Drawable.createFromStream(ims, null);
            holder.country.setVisibility(View.VISIBLE);
            holder.country.setImageDrawable(d);
        } catch (IOException e) {
            holder.country.setVisibility(View.INVISIBLE);
            e.printStackTrace();
        }

        holder.user.setText(holder.mItem.getUser());
        holder.sub.setText(holder.mItem.getSub());
        holder.ac.setText(holder.mItem.getAc());
        holder.ac_percent.setText(holder.mItem.getAc_percent());
        holder.score.setText(holder.mItem.getScore());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((FragmentActivity)holder.itemView.getContext()).getSupportFragmentManager();
                fm.beginTransaction()
                    .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                    .replace(R.id.container, ProfileFragment.newInstance(holder.user.getText().toString()))
                    .addToBackStack(null)
                    .commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userRankList.size();
    }

    public boolean addAll(List<UserRank> ranks){
        return this.userRankList.addAll(ranks);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        protected UserRank mItem;
        protected View itemView;
        protected TextView rank;
        protected ImageView country;
        protected TextView user;
        protected TextView sub;
        protected TextView ac;
        protected TextView ac_percent;
        protected TextView score;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.rank = (TextView) itemView.findViewById(R.id.id);
            this.country = (ImageView) itemView.findViewById(R.id.fav_buttom);
            this.user = (TextView) itemView.findViewById(R.id.problem_name);
            this.sub = (TextView) itemView.findViewById(R.id.item_cant_1);
            this.ac = (TextView) itemView.findViewById(R.id.item_cant_2);
            this.ac_percent = (TextView) itemView.findViewById(R.id.item_cant_3);
            this.score = (TextView) itemView.findViewById(R.id.item_cant_4);
        }
    }
}