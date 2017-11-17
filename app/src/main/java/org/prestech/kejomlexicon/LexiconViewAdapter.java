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
import java.util.Objects;

/***********************************************************
 * Created by asohm on 10/29/2017.
 */

public class LexiconViewAdapter extends RecycleViewAdapter {


    private Context mContext = null;
    private  ArrayList<Lexicon>  mList;
    private static   LexiconViewAdapter lexiconViewAdapter;
    /************************************************
     *
     * @param list
     */
    private LexiconViewAdapter(Context context,  ArrayList<Lexicon>  list) {
        super(context);
        this.mList = list;
        this.mContext = context;

    }//LexiconViewAdapter(ArrayList, String)


    /*****************************************************************************
     *
     * @param mContext
     * @param list
     * @return
     */
    public  static  LexiconViewAdapter getInstance( Context mContext, ArrayList<Lexicon>  list){
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
    public void updateResource(ArrayList<Lexicon>  list) {
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
        ImageView startView = holder.starView;

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
        startView.setOnClickListener( new ViewClickListener(position));

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


    /*********************************************************
     *
     */
    class ViewClickListener implements View.OnClickListener {

            private int position = 0;

            ViewClickListener(int lexPosition){
                this.position = lexPosition;
            }
            @Override
            public void onClick(View v) {

                //TODO: Check if the the lexicon exist in the favorite list: if it does remove it [DONE]
                Lexicon targetLexicon = DataSource.getInstance(mContext).getLexicons().get((position));

                int lexiconId = targetLexicon.getLexiconId();

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

            }//onClick() Listener

        }

}//LexiconViewAdapter Class
