package org.prestech.kejomlexicon;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/************************************************************
 * Created by asohm on 10/29/2017.
 */

/**
 * TODO: Update the Favorite UI as soon as favorite lexicon is removed (Using Simple UIAdapter animation)
 *
 */

public class FavoriteViewAdapter extends RecycleViewAdapter {


    private static LinkedList<Lexicon>  mList;
    private static FavoriteViewAdapter favoriteViewAdapter;
    private Context mContext;
    /************************************************
     *
     * @param list
     */
    private FavoriteViewAdapter(Context context, LinkedList<Lexicon> list) {
        super(context);
        this.mList = list;
        this.mContext = context;

    }//FavoriteViewAdapter(ArrayList, String) Ends


    public  static  FavoriteViewAdapter getInstance( Context mContext, LinkedList list){
        if(favoriteViewAdapter != null){
            return favoriteViewAdapter;
        }// if Ends
        favoriteViewAdapter = new FavoriteViewAdapter(mContext, list);
        return favoriteViewAdapter;

    }//FavoriteViewAdapter() Ends

    public  static  FavoriteViewAdapter getInstance(){
        return favoriteViewAdapter;

    }//FavoriteViewAdapter() Ends



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }//

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        TextView kejomTv = holder.kejomTv;
        TextView enTextView = holder.englishTv;

        ImageView starView = holder.starView;
        starView.setImageResource(android.R.drawable.btn_star_big_on);

        // TODO:Ensure that the right lexicon is removed when un_stared[DONE]
        if(mList.get((position)) != null){

            //set on click listener to remove favorite lexicon from
            //the favorite database and updata the UI when un-starred
            starView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //remove data from data base
                    DataSource.getInstance(mContext).removeFavorite(mList.get(position).getLexiconId());

                    //update mList
                    updateResource();

                    //chnage color to gray; turn of star
                    ((ImageView)v).setImageResource(android.R.drawable.btn_star_big_off);

                }//onClick() Ends
            });//starView.setOnClickListener() Ends


            kejomTv.setText(mList.get((position)).getKejomWord());
            enTextView.setText(mList.get((position)).getEnglishWord());
        }//if Ends

    }//onBindViewHolder() Ends


    /**********************************************
     *
     */
    public void updateResource(){
        mList.clear();
        mList.addAll(DataSource.getInstance(mContext).getFavLexicon());
        notifyDataSetChanged();
    }//addItem Ends


    /***************************************************************************
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if(mList == null) return 0;

        return mList.size();
    }//getItemCount() Ends

}//FavoriteViewAdapter Class Ends