package org.prestech.kejomlexicon;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by asohm on 12/17/2017.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;

    //constructor
    public GridViewAdapter(Context context){
        mContext = context;
    }//constructor ends

    public  int getCount(){
        return mThumIds.length;
    }

    public Object getItem(int position){
        return  null;
    }

    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView;
        if(convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams( new GridView.LayoutParams(85,85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            imageView = (ImageView) convertView;
        }//else ends

        imageView.setImageResource(mThumIds[position]);
        return imageView;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

    public  Integer[] mThumIds = {
            R.drawable.ic_action_search,
            R.mipmap.ic_launcher_round
    };

}
