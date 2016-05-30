package cu.uci.coj.Application.Contests;

import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.R;

/**
 * Created by osvel on 2/21/16.
 */
public class ContestListItem extends RecyclerView.Adapter<ContestListItem.ViewHolder> implements Serializable{

    List<Contest> contestList = new ArrayList<>();

    public ContestListItem(List<Contest> contestList) {
        this.contestList = contestList;
    }

    public boolean addAll(List<Contest> contestList){
        return this.contestList.addAll(contestList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contest_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        //rellenar todos los elementos con lo que lleva
        holder.mItem = contestList.get(position);
        holder.id.setText(holder.mItem.getId());
        Drawable acces;
        if (holder.mItem.isOpen()){
            //es de acceso abierto, establecer icono
            acces = holder.itemView.getContext().getResources().getDrawable(R.drawable.open_acces);
        }
        else{
            //no es de acceso abierto, establecer icono
            acces = holder.itemView.getContext().getResources().getDrawable(R.drawable.private_acces);
        }
        holder.acces.setImageDrawable(acces);
        holder.contest_name.setText(holder.mItem.getContest_name());
        holder.start_date.setText(holder.mItem.getStart_date());
        holder.end_date.setText(holder.mItem.getEnd_date());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager();
                int id = Integer.parseInt(holder.id.getText().toString());
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                        .replace(R.id.container, ContestDetailFragment.newInstance(id))
                        .addToBackStack(null)
                        .commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return contestList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public View itemView;
        public TextView id;
        public ImageView acces;
        public TextView contest_name;
        public TextView start_date;
        public TextView end_date;
        public Contest mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.id = (TextView) itemView.findViewById(R.id.id);
            this.acces = (ImageView) itemView.findViewById(R.id.acces_indicator);
            this.contest_name = (TextView) itemView.findViewById(R.id.contest_name);
            this.start_date = (TextView) itemView.findViewById(R.id.start);
            this.end_date = (TextView) itemView.findViewById(R.id.end);
        }
    }
}

