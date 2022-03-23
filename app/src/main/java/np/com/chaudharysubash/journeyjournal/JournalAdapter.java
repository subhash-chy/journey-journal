package np.com.chaudharysubash.journeyjournal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.util.ArrayList;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.ViewHolder> {
//    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private ArrayList<JournalRVModal> journalRVModalArrayList;
    private Context context;
    int lastPosition = -1;

    private JournalClickInterface journalClickInterface;

//    Creating constructor
    public JournalAdapter(ArrayList<JournalRVModal> journalRVModalArrayList, Context context, JournalClickInterface journalClickInterface) {
        this.journalRVModalArrayList = journalRVModalArrayList;
        this.context = context;
        this.journalClickInterface = journalClickInterface;
    }

    @NonNull
    @Override
    public JournalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.journal_item, parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        JournalRVModal journalRVModal = journalRVModalArrayList.get(position);
        holder.titleTV.setText(journalRVModal.getTitle());
//        holder.descriptionTV.setText(journalRVModal.getDescription());
        holder.locationTV.setText(journalRVModal.getLocation());

//        declaring image url initially to null
        String imageUrl  = null;
        imageUrl = journalRVModal.getImageURL();
        Picasso.get().load(imageUrl).fit().into(holder.imageView);
        holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        Setting date
        holder.dateTV.setText(journalRVModal.getDate());


        setAnimation(holder.itemView,position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journalClickInterface.onJournalClick(position);
            }
        });

//        holder.share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_TEXT, "Let's go for a trip to "
//                        + journals.getTitle() + "\n" + journals.getDescription() + "\n" + journals.getImage());
//                intent.setType("text/plain");
//                holder.share.getContext().startActivity(Intent.createChooser(intent, "Send to"));
//            }
//        });
//    }
    }
    private void setAnimation(View itemsView, int position){
        if(position>lastPosition){
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            itemsView.setAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return journalRVModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
//        descriptionTV
        private TextView titleTV, locationTV, dateTV;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(R.id.journalTitle);
            locationTV = itemView.findViewById(R.id.journalLocation);
            imageView = itemView.findViewById(R.id.ivImage);
            dateTV = itemView.findViewById(R.id.journalDate);
        }
    }

//    Creating interface
    public interface JournalClickInterface {
        void onJournalClick(int position);
}
}
