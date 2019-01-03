package com.example.toshiba.instagramclone.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.toshiba.instagramclone.Models.Comment;
import com.example.toshiba.instagramclone.Models.UserAccountSettings;
import com.example.toshiba.instagramclone.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentListAdapter extends ArrayAdapter<Comment> {

    private static final String TAG = "CommentListAdapter";

    private LayoutInflater mInflator;
    private int layoutResource;
    private Context mContext;

    public CommentListAdapter(@NonNull Context context, int resource,
                              @NonNull List<Comment> objects) {
        super(context, resource, objects);

        mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder{
        TextView comment,username,timestamp,reply,likes;
        CircleImageView profileImage;
        ImageView like;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflator.inflate(layoutResource,parent,false);
            holder = new ViewHolder();

            holder.comment = (TextView) convertView.findViewById(R.id.comment);
            holder.username = (TextView) convertView.findViewById(R.id.comment_username);
            holder.timestamp = (TextView) convertView.findViewById(R.id.comment_time_posted);
            holder.reply = (TextView) convertView.findViewById(R.id.comment_reply);
            holder.like = (ImageView) convertView.findViewById(R.id.comment_like);
            holder.likes = (TextView) convertView.findViewById(R.id.comment_likes);
            holder.profileImage = (CircleImageView) convertView.findViewById(R.id.comment_profile_image);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder)  convertView.getTag();
        }
        //set the comment
        holder.comment.setText(getItem(position).getComment());


        //set the the timestamp difference
        String timestampDifference = getTimeStampDifference(getItem(position));
        if(!timestampDifference.equals("0")){
            holder.timestamp.setText(timestampDifference + "d");
        }
        else{
            holder.timestamp.setText("today");
        }
        //set the username and profile image
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(mContext.getString(R.string.dbname_user_account_settings))
                .orderByChild(mContext.getString(R.string.field_user_id))
                .equalTo(getItem(position).getUser_id());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapShot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: setting up the username and display image. " + singleSnapShot.getValue(UserAccountSettings.class).getUsername());
                    holder.username.setText(singleSnapShot.getValue(UserAccountSettings.class).getUsername());

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(singleSnapShot.getValue(UserAccountSettings.class).getProfile_photo(),holder.profileImage);

                }
                Log.d(TAG, "onDataChange: outside of the loop");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }
        });
        try{
            if(position == 0 )
            {
                holder.like.setVisibility(View.GONE);
                holder.likes.setVisibility(View.GONE);
                holder.reply.setVisibility(View.GONE);
            }
        }catch (NullPointerException e){
            Log.d(TAG, "getView: NullPointerException: " + e.getMessage());
        }
        return convertView;
    }
    /**
     * returns a string representing the number of days ago the post was made
     * @return
     */
    private String getTimeStampDifference(Comment comment){
        Log.d(TAG, "getTimeStampDifference: getting timestamp difference. ");

        String differnce = "";
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kuwait"));  //google 'android list of timezones'
        Date today = c.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimestamp = comment.getDate_created();
        try{
            timestamp = sdf.parse(photoTimestamp);
            differnce = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) /1000 /60 /60 /24 )));
        }catch(ParseException e){
            Log.e(TAG, "getTimeStampDofference: ParseExcption: " + e.getMessage() );
            differnce = "0";
        }
        return differnce;
    }
}
