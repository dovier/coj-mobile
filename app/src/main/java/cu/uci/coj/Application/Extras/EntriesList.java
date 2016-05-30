package cu.uci.coj.Application.Extras;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cu.uci.coj.Conexion;
import cu.uci.coj.Application.Profiles.ProfileFragment;
import cu.uci.coj.R;

/**
 * Created by osvel on 4/8/16.
 */
public class EntriesList extends RecyclerView.Adapter<EntriesList.ViewHolder> implements Serializable {

    private List<EntriesItem> entriesItemList = new ArrayList<>();
    private boolean login;

    public EntriesList(List<EntriesItem> entriesItemList, boolean login) {
        this.entriesItemList = entriesItemList;
        this.login = login;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(holder.itemView.getResources().getColor(R.color.colorPrimaryLight))
                .borderWidthDp(2)
                .cornerRadiusDp(90)
                .oval(false)
                .build();

        /**
         * modificar la url de la imagen para usar el servidor local
         * TODO: quitar cuando se use con la url real del COJ
         */
        String image_url = entriesItemList.get(position).getAvatar();
        image_url = image_url.replace("http://coj.uci.cu", Conexion.getInstance(holder.itemView.getContext()).getIMAGE_URL());

        final int dim = (int)holder.itemView.getContext().getResources().getDimension(R.dimen.avatar_size);

        Picasso.with(holder.itemView.getContext())
                .load(image_url)
                .resize(dim, dim)
                .transform(transformation)
                .into(holder.avatar, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Picasso.with(holder.itemView.getContext())
                                .load(R.drawable.default_avatar)
                                .resize(dim, dim)
                                .transform(transformation)
                                .into(holder.avatar);
                    }
                });

        holder.user_name.setText(entriesItemList.get(position).getUser_name());
        holder.submit_date.setText(entriesItemList.get(position).getSubmit_date());
        holder.content.setText("");
        write(holder.content, entriesItemList.get(position).getContent());
        holder.content.setMovementMethod(LinkMovementMethod.getInstance());
        holder.rate.setText(entriesItemList.get(position).getRate());

        holder.user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentManager fm = ((FragmentActivity) holder.itemView.getContext()).getSupportFragmentManager();
                fm.beginTransaction()
                        .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                        .replace(R.id.container, ProfileFragment.newInstance(entriesItemList.get(position).getUser_name()))
                        .addToBackStack(null)
                        .commit();
            }
        });

    }

    public static String[] getUserLink(String content){

        String before, user = null, after = null;

        //buscar >@ que es donde empieza el nombre del usuario
        String[] splited = content.split(">@");
        //existe al menos un usuario
        if (splited.length > 1){
            user = splited[1].split("</a>")[0];
        }

        //eliminar las etiquetas anteriores al usuario
        String full = "";
        before = splited[0];
        int i = before.length()-1;
        char charAt;
        if (i > 0) {
            do {
                charAt = before.charAt(i);
                before = before.substring(0, i);
                full = charAt + full;
                i--;
            } while (charAt != '<' && i >= 0);
        }

//        borrar el contenido antes y de la etiqueta para quedar solo con lo que esta despues
        if (user != null){
            full += ">@" + user + "</a>";
            after = content.replace(before, "").replace(full, "");
        }
        else
            before = content;

        String[] code = new String[3];
        code[0] = before;
        code[1] = user;
        code[2] = after;

        return code;

    }

    public static void write(TextView textView, String content){

        final String[] split_text = getUserLink(content);

        //si hay texto antes del user? escribo el textView
        if (split_text[0] != null){
           textView.append(Html.fromHtml(split_text[0]));
        }
        //si hay user

        if (split_text[1]  != null){

            final String profileUser = split_text[1];

            String user = "@"+split_text[1];

            if(split_text[0] != null)
                user = " "+user;
            if(split_text[2] != null)
                user += " ";

            SpannableString spannableString = new SpannableString(user);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    FragmentManager fm = ((FragmentActivity) textView.getContext()).getSupportFragmentManager();
                    fm.beginTransaction()
                            .setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_left, R.anim.exit_to_right)
                            .replace(R.id.container, ProfileFragment.newInstance(profileUser))
                            .addToBackStack(null)
                            .commit();
                }
                @Override
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                }
            };
            spannableString.setSpan(clickableSpan, 1, user.length()-1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            textView.append(spannableString);
        }
        if (split_text[2] != null){
            write(textView, split_text[2]);
        }

    }

    @Override
    public int getItemCount() {
        return entriesItemList.size();
    }

    public boolean addAll(List<EntriesItem> entriesItems) {
        return entriesItemList.addAll(entriesItems);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        protected View itemView;
        protected ImageView avatar;
        protected TextView user_name;
        protected TextView submit_date;
        protected TextView content;
        protected TextView rate;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
            user_name = (TextView) itemView.findViewById(R.id.user_name);
            submit_date = (TextView) itemView.findViewById(R.id.submit_date);
            content = (TextView) itemView.findViewById(R.id.content);
            rate = (TextView) itemView.findViewById(R.id.rate);
        }
    }
}
