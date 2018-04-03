package org.prestech.kejomlexicon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

/***********************************************************
 * Created by asohm on 10/29/2017.
 */

public class LexiconViewAdapter extends RecycleViewAdapter {


    private static Activity mContext = null;
    private  LinkedList<Lexicon>  mList;
    private static   LexiconViewAdapter lexiconViewAdapter;
    MediaPlayer mp = null;
    private AudioResource audioResource = null;

    /************************************************
     *
     * @param list
     */
    private LexiconViewAdapter(Activity context,  LinkedList<Lexicon>  list) {
        super(context);
        this.mList = list;
        this.mContext = context;
        audioResource =  AudioResource.getInstance(mContext);
    }//LexiconViewAdapter(ArrayList, String)


    /*****************************************************************************
     *
     * @param mContext
     * @param list
     * @return
     */
    public  static  LexiconViewAdapter getInstance( Activity mContext, LinkedList<Lexicon>  list){
        if(lexiconViewAdapter != null){
            return lexiconViewAdapter;
        }// if Ends
        lexiconViewAdapter = new LexiconViewAdapter(mContext, list);

        return lexiconViewAdapter;
    }//getInstance() Ends


    /*****************************************************************
     *
     * @return
     */
    public static LexiconViewAdapter getInstance() {
        return  lexiconViewAdapter;
    }//LexiconViewAdapter() Ends

    /****************************************************************
     *
     */
    public void updateResource(LinkedList<Lexicon> list) {
        mList = list;
        if(mList != null){notifyDataSetChanged();}
    }//updateResource() Ends


    /****************************************************************************************
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        //holder.setIsRecyclable(false);
        ImageView spaekerView = holder.spaekerView;

        TextView kejomTv = holder.kejomTv;
        TextView enTextView = holder.englishTv;
        ImageView starView = holder.starView;

        if(mList == null){return;}

        //make star yellow if lexicon is a favorite lexicon
        if(DataSource.getInstance(mContext).isFavorite(mList.get((position)))) {
            starView.setImageResource(android.R.drawable.btn_star_big_on);
        }//if Ends


        kejomTv.setText(mList.get((position)).getKejomWord());
        enTextView.setText(mList.get((position)).getEnglishWord());

        // This toast text for newly binded lexicons
        // Toast.makeText(mContext, holder.kejomTv.getText(), Toast.LENGTH_SHORT).show();

        //TODO: Check if lexicon at this position is a Favorite and add the ImageResource accordingly[DONE]
        //check if the lexicon to be binded exist in the Favorite Table of the data base

        //TODO: DO we need to create a new instance of ViewClickListener Envery time
        //set on click listener
        starView.setOnClickListener( new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //TODO: Check if the the lexicon exist in the favorite list: if it does remove it [DONE]
                Lexicon targetLexicon = mList.get((position));

                int lexiconId = targetLexicon.getLexiconId();
                Log.i("FAVORITE EVENT",DataSource.getInstance(mContext).isFavorite(targetLexicon)+"  " );
                //if lexicon already exist as favorite remove it
                if(DataSource.getInstance(mContext).isFavorite(targetLexicon)){
                    //remove lexicon from favorite table database
                    DataSource.getInstance(mContext).removeFavorite(lexiconId);

                    //changes star's color to grey
                    ((ImageView)v).setImageResource(android.R.drawable.btn_star_big_off);

                }else{
                    DataSource.getInstance( mContext ).insertFavorite(lexiconId);
                    //changes star's color to yellow
                    ((ImageView)v).setImageResource(android.R.drawable.btn_star_big_on);

                }//if Else ends

                //update FavoriteViewAdapter
                try {
                    FavoriteViewAdapter.getInstance().updateResource();

                }catch (NullPointerException ex){}

                //TODO: Refresh the content of the lay out to display the recent update[DONE]
            }//onClick() Ends
        });

        spaekerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(v.getContext(), mList.get(position).getEnglishWord(), Toast.LENGTH_SHORT).show();

                if(mp != null){
                   audioResource.releaseAudioRes();
                }//if Ends

                 mp = audioResource.playAudio( mList.get(position).getKejomWord());

            }//onClick() Ends
        });

        getLayoutView().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LexiconDetail.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity( intent );
            }//onClick Ends

        });//setOnClickListener() Ends

    }//onBindViewHolder() Ends




    /****************************************
     *
     * @param holder
     */
    @Override
    public void onViewRecycled(MyViewHolder holder) {
        super.onViewRecycled(holder);

        holder.starView.setImageResource(android.R.drawable.btn_star_big_off);

        //TODO: Retrieve kedjom and English text from Text views and Check if there are favorite lexicon. Use the yellow star if they are[DONE].

    }//onViewRecycled()


    /***************************************************************************
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if(mList != null)
            return mList.size();

        return  0;
    }//getItemCount() Ends

}//LexiconViewAdapter Class