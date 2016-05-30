package cu.uci.coj.Application.Mail;

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

import cu.uci.coj.R;

/**
 * Created by osvel on 5/7/16.
 */
public class EmailList extends RecyclerView.Adapter<EmailList.ViewHolder> implements Serializable{

    private List<Email> emails = new ArrayList<>();

    public EmailList(List<Email> emails) {
        this.emails = emails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.to.setText(emails.get(position).getStringTo());
        holder.from.setText(emails.get(position).getFrom());
        holder.subject.setText(emails.get(position).getSubject());
        holder.date.setText(emails.get(position).getStringDate());

        switch (emails.get(position).getFolder()){
            case INBOX: {
                holder.to.setVisibility(View.GONE);
                break;
            }
            case OUTBOX: {
                holder.from.setVisibility(View.GONE);
                break;
            }
            case DRAFT: {
                holder.from.setVisibility(View.GONE);
                holder.to.setVisibility(View.GONE);
                break;
            }
        }

        if (!emails.get(position).isRead()){
            holder.mItemView.setBackgroundResource(R.color.no_accept_background);
        }
        else
            holder.mItemView.setBackgroundResource(R.color.white);

        final FragmentManager fm = ((FragmentActivity)holder.mItemView.getContext()).getSupportFragmentManager();
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (emails.get(0).getFolder() != MailFolder.DRAFT) {
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.container, EmailDetailedFragment.newInstance(emails.get(position)))
                            .addToBackStack(null)
                            .commit();
                } else {
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right)
                            .replace(R.id.container, ComposeMessage.newInstance(emails.get(position).getIdEmail(),
                                    emails.get(position).getSubject(), emails.get(position).getContent()))
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements Serializable{

        protected View mItemView;
        protected TextView to;
        protected TextView from;
        protected TextView subject;
        protected TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mItemView = itemView;
            to = (TextView) mItemView.findViewById(R.id.to);
            from = (TextView) mItemView.findViewById(R.id.from);
            subject = (TextView) mItemView.findViewById(R.id.subject);
            date = (TextView) mItemView.findViewById(R.id.date);
        }
    }
}
