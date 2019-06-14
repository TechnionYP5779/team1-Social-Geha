package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
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
import com.github.angads25.toggle.widget.DayNightSwitch;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.lang.reflect.Array;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class MatchesListAdapter extends ArrayAdapter<Person> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder{
        TextView name;
        TextView description;
        CircleImageView image;
        RelativeLayout chat_message_layout;
        ImageView pick_arrow;
    }


    public MatchesListAdapter(Context ctxt, int resource, ArrayList<Person> objects){
        super(ctxt, resource, objects);
        this.mContext = ctxt;
        this.mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        setupImageLoader();

        String name = getItem(position).getAnonymousIdentity().getName();
        String description = getItem(position).getDescription();
        String imageUrl = getItem(position).getAnonymousIdentity().getImageName();
        String imageColor = getItem(position).getAnonymousIdentity().getImageColor();
        Log.d("imageBug1", imageUrl);
        int image_id = mContext.getResources().getIdentifier("@drawable/" + imageUrl, null, mContext.getPackageName() );
        imageUrl = "drawable://" + image_id;
        Log.d("imageBug", imageUrl);

        // Create the view result for showing the animation
        ViewHolder holder;

        if( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate( mResource, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.row_name);
            holder.description = convertView.findViewById(R.id.row_description);
            holder.image = convertView.findViewById(R.id.row_image);
            holder.chat_message_layout = convertView.findViewById(R.id.chat_message_layout);
            holder.pick_arrow = convertView.findViewById(R.id.pick_arrow);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
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
        image_loader.displayImage(imageUrl, holder.image, options); //display no_bg photo
        holder.image.setCircleBackgroundColor(Color.parseColor(imageColor)); //change bg color

        holder.name.setText(name);
        holder.description.setText(description);
        holder.chat_message_layout.setVisibility(View.GONE);
        holder.pick_arrow.setVisibility(View.VISIBLE);
        return convertView;
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
