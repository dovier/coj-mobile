package cu.uci.coj.Application.Extras;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Application.Problems.ProblemFragment;
import cu.uci.coj.R;

/**
 * Created by osvel on 4/11/16.
 */
public class FaqList extends RecyclerView.Adapter<FaqList.ViewHolder> implements Serializable{

    List<FaqItem> faqItemList;

    public FaqList() {
        faqItemList = new ArrayList<>();
    }

    public FaqList(List<FaqItem> faqItemList) {
        this.faqItemList = faqItemList;
    }

    @Override
    public FaqList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.faq_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.question.setText(faqItemList.get(position).getQuestion());
        ProblemFragment.write(holder.answer.getContext(), holder.answer, faqItemList.get(position).getAnswer());

//        holder.question.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (holder.answer.getVisibility() == View.VISIBLE)
//                    holder.answer.setVisibility(View.GONE);
//                else
//                    holder.answer.setVisibility(View.VISIBLE);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return faqItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        protected TextView question;
        protected LinearLayout answer;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            question = (TextView) itemView.findViewById(R.id.question);
            answer = (LinearLayout) itemView.findViewById(R.id.answer);
        }
    }

}
