package org.prestech.kejomlexicon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Created by asohm on 10/25/2017.
 */

public abstract class RecycleViewAdapter extends RecyclerView.Adapter < RecycleViewAdapter.MyViewHolder> {

    private MyViewHolder mViewHolder;
    private  Context mContext;
    private  View layoutView = null;
    /************************************************
     *
     */
    public RecycleViewAdapter(Context context){

        this.mContext = context;

    }//RecycleViewAdapter() Ends


    /******************************************************************************
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecycleViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //inflate layout from parent
        LayoutInflater inflater = LayoutInflater.from( parent.getContext());

        //get view
        layoutView = inflater.inflate( R.layout.list_layout, parent, false);

        MyViewHolder myViewHolder = new MyViewHolder(layoutView);

        return myViewHolder;
    }//onCreateViewHolder() Ends

    public View getLayoutView() {
        return layoutView;
    }

    /************************************************************************
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecycleViewAdapter.MyViewHolder holder, int position) {


    }//onBindViewHolder() Ends


    /***************************************************************************
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return 0;
    }//getItemCount() Ends


    /***************************************************************************
     *
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView kejomTv = null;
        TextView englishTv = null;
        ImageView starView = null;
        ImageView spaekerView = null;

        //MyViewHolder
        public MyViewHolder(View itemView) {

            super(itemView);

            kejomTv = (TextView) itemView.findViewById(R.id.kejom_textview);
            englishTv = (TextView) itemView.findViewById(R.id.english_txview);
            starView = (ImageView) itemView.findViewById(R.id.lexicon_star);
            spaekerView = (ImageView) itemView.findViewById(R.id.lexicon_speak);

        }//MyViewHolder() Ends

    }//MyViewHolder() Ends

}//RecycleViewAdapter Class Ends
