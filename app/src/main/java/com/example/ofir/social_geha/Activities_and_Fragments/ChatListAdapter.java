package com.example.ofir.social_geha.Activities_and_Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ofir.social_geha.Firebase.Message;
import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatListAdapter extends ArrayAdapter<ChatEntry> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder{
        TextView name;
        TextView description;
        CircleImageView image;
        TextView unread_msg_count;
        TextView msg_time;
        RelativeLayout chat_message_layout;
        String imgURL;
        String imgColor;
    }

    public ChatListAdapter(Context ctxt, int resource, ArrayList<ChatEntry> objects){
        super(ctxt, resource, objects);
        this.mContext = ctxt;
        this.mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        setupImageLoader();

        String name = getItem(position).getPerson().getAnonymousIdentity().getName();
        String description = getItem(position).getPerson().getDescription();
        String imageUrl = getItem(position).getPerson().getAnonymousIdentity().getImageName();
        String imageColor = getItem(position).getPerson().getAnonymousIdentity().getImageColor();
        Message lastMessage = getItem(position).getMessage();
        Log.d("imageBug1", imageUrl);
        int image_id = mContext.getResources().getIdentifier("@drawable/" + imageUrl, null, mContext.getPackageName() );
        imageUrl = "drawable://" + image_id;
        Log.d("imageBug", imageUrl);

        // Create the view result for showing the animation
        final View result;
        ViewHolder holder;

        if( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate( mResource, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.row_name);
            holder.description = convertView.findViewById(R.id.row_description);
            holder.image = convertView.findViewById(R.id.row_image);
            holder.imgURL = imageUrl;
            holder.imgColor = imageColor;
            setUpOnClick(holder);
            holder.msg_time = convertView.findViewById(R.id.message_time);
            holder.unread_msg_count = convertView.findViewById(R.id.unread_messages_icon);
            holder.chat_message_layout = convertView.findViewById(R.id.chat_message_layout);

            result = convertView;
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


//        Animation animation = AnimationUtils.loadAnimation(mContext,
//                (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
//        result.startAnimation(animation);
        lastPosition = position;

        int default_image = mContext.getResources().getIdentifier("@drawable/image_fail", null, mContext.getPackageName() );
        ImageLoader image_loader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true)
                .showImageForEmptyUri(default_image)
                .showImageOnFail(default_image)
                .showImageOnLoading(default_image).build();

        //download and display image from url
        image_loader.displayImage(imageUrl, holder.image, options); //display no_bg image
        holder.image.setCircleBackgroundColor(Color.parseColor(holder.imgColor));

        holder.name.setText(name);
        holder.description.setText(description);
        holder.chat_message_layout.setVisibility(View.VISIBLE);
        holder.msg_time.setText(getHourStringFromDate(lastMessage.getMessageDate()));
        holder.unread_msg_count.setText("0");
        holder.description.setText(lastMessage.getMessage());
        return convertView;
    }

    private void setUpOnClick(final ViewHolder holder) {
//        final ImageView img = holder.image;
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ordinary Intent for launching a new activity
                Intent intent = new Intent(mContext, ZoomedPictureActivity.class);

                // Get the transition name from the string
                String transitionName = mContext.getString(R.string.transition_string);

                // Define the view that the animation will start from
                View viewStart = holder.image;

                ActivityOptionsCompat options =

                        ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext,
                                viewStart,   // Starting view
                                transitionName    // The String
                        );

                //put extra
                intent.putExtra("EXTRA_IMAGE_URL", holder.imgURL);
                intent.putExtra("EXTRA_IMAGE_COLOR", holder.imgColor);

                //Start the Intent
                ActivityCompat.startActivity(mContext, intent, options.toBundle());
            }
        });

    }

    private static String getHourStringFromDate(Date date){
        if(date == null)
            return "NULL DATE";
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        StringBuilder sb = new StringBuilder();
        if(hour < 10)
            sb.append(0);
        sb.append(hour);
        sb.append(":");
        if(minute < 10)
            sb.append(0);
        sb.append(minute);
        return sb.toString();
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }
}
