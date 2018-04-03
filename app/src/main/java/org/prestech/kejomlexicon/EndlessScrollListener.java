package org.prestech.kejomlexicon;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;

/**
 * Created by asohm on 3/23/2018.
 */

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {


    private  int prevTotal = 0;
    private  int prevFirstVisibleItem = 0;
    private  int prevLastVisibleItem = 0;
    int lastVisibleItem = 0;
    int firstVisibleItem = 0;
    int totalItemCount = 0;
    Context mContext = null;

    DataSource dataSource = null;

    public  EndlessScrollListener(Context context){
        mContext = context;
        dataSource = DataSource.getInstance(mContext.getApplicationContext());
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

         totalItemCount = recyclerView.getLayoutManager().getItemCount();
         firstVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
         lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();

        int visibleThreshold = 10;

        if(( totalItemCount - lastVisibleItem) < visibleThreshold){
            Log.i("SCROLL EVENT", " Scrolling up ");
            dataSource.getLexicons().clear();
            loadMoreData(true, visibleThreshold);

        }//if Ends

        if( ( totalItemCount - lastVisibleItem) > visibleThreshold*5 ){

        }//if Ends

    }//onScrolled() Ends

    //implement this method to load more data
    public  abstract void loadMoreData(boolean qDownward, int nELement);

}//EndlessScrollListener
