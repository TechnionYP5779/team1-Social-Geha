package com.example.ofir.social_geha.Activities_and_Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ofir.social_geha.Person;
import com.example.ofir.social_geha.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;


public class MatchesListAdapter extends ArrayAdapter<Person> {
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    private static class ViewHolder{
        TextView name;
        TextView description;
        ImageView image;
    }

    public MatchesListAdapter(Context ctxt, int resource, ArrayList<Person> objects){
        super(ctxt, resource, objects);
        mContext = ctxt;
        mResource = resource;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        setupImageLoader();

        String name = getItem(position).getAnonymousName();
        String description = getItem(position).getDescription();
        String imageUrl = getItem(position).getAnonymousImageURL();

        // Create the view result for showing the animation
        final View result;
        ViewHolder holder;

        if( convertView == null ){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate( mResource, parent, false);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.row_name);
            holder.description = (TextView) convertView.findViewById(R.id.row_description);
            holder.image = (ImageView) convertView.findViewById(R.id.row_image);

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
        image_loader.displayImage(imageUrl, holder.image, options);

        holder.name.setText(name);
        holder.description.setText(description);

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
