package cu.uci.coj.Application.Judgments;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Application.Profiles.ProfileFragment;
import cu.uci.coj.R;

/**
 * Created by osvel on 3/8/16.
 */
public class JudgmentList extends RecyclerView.Adapter<JudgmentList.ViewHolder> implements Serializable {

    private List<Judgment> judgmentsItemList = new ArrayList<>();

    public JudgmentList(List<Judgment> judgmentsItemList) {
        this.judgmentsItemList = judgmentsItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.judgment_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = judgmentsItemList.get(position);
        holder.id.setText(holder.mItem.getId());
        holder.date.setText(holder.mItem.getDate());
        holder.user.setText(holder.mItem.getUser());
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, ProfileFragment.newInstance(holder.mItem.getUser()))
                        .addToBackStack(null)
                        .commit();
            }
        });

        holder.problem_id.setText(holder.mItem.getProblem_id());
        int color = holder.itemView.getResources().getColor(R.color.no_accept);
        if (holder.mItem.getJudgment().equals("Accepted")){
            color = holder.itemView.getResources().getColor(R.color.accept);
        }
        else if (holder.mItem.getJudgment().equals("Judging")){
            color = holder.itemView.getResources().getColor(R.color.colorAccent);
        }
        if (holder.mItem.getIntTest_case() == 0){
            holder.test_case.setVisibility(View.GONE);
        }
        else {
            holder.test_case.setVisibility(View.VISIBLE);
            holder.test_case.setText(holder.itemView.getContext().getResources().getString(R.string.test));
            holder.test_case.append(holder.mItem.getTest_case());
        }
        holder.judgment.setText(holder.mItem.getJudgment());
        holder.judgment.setTextColor(color);
        holder.time.setText(holder.mItem.getTime());
        holder.memory.setText(holder.mItem.getMemory());
        holder.size.setText(holder.mItem.getSize());
        holder.language.setText(holder.mItem.getLanguage());

    }

    @Override
    public int getItemCount() {
        return judgmentsItemList.size();
    }

    public boolean addAll(List<Judgment> judgments) {
        return judgmentsItemList.addAll(judgments);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public View itemView;
        public TextView id;
        public TextView date;
        public TextView user;
        public TextView problem_id;
        public TextView judgment;
        public TextView test_case;
        public TextView time;
        public TextView memory;
        public TextView size;
        public TextView language;
        public Judgment mItem;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            id = (TextView) itemView.findViewById(R.id.id);
            date = (TextView) itemView.findViewById(R.id.submit_date);
            user = (TextView) itemView.findViewById(R.id.from);
            problem_id = (TextView) itemView.findViewById(R.id.problem_id);
            judgment = (TextView) itemView.findViewById(R.id.judgment);
            test_case = (TextView) itemView.findViewById(R.id.test_case);
            time = (TextView) itemView.findViewById(R.id.time);
            memory = (TextView) itemView.findViewById(R.id.memory);
            size = (TextView) itemView.findViewById(R.id.size);
            language = (TextView) itemView.findViewById(R.id.language);
        }
    }
}
