package org.prestech.kejomlexicon;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/***
 * TODO: ADD "CATEGORIES" COLUMN TO VIEW
 * TODO: DESIGN AND IMPLEMENT THE HOME LAYOUT
 * TODO: DESIGN AND IMPLEMENT THE KEYBOARD
 *
 * TODO: IMPLEMENT A SEARCH VIEW, SEEACH BY ENGLISH WORD
 * TODO: DESIGN AND IMPLEMENT THE AUDIO CLASS TO PLAY AUDIO
 * TODO: IMPLEMENT A SEARCH VIEW, SEARCH BY KEJOM WORD [CREATE A KEYBOARD FROM FRAGMENT UI]
 */
public class LexiconActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager = null;

    private  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // set up layout view for the first tab
       // if(){
        setContentView(R.layout.activity_lexicon);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        //get Database instance
        DataSource.getInstance(this).transferFileDataToSqlite();

        //set up the activity toolbar
        toolbar = (Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);


        /*********************************************************************************
         *
         */
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }//onPageScrolled() Ends

            @Override
            public void onPageSelected(int position) {

                Fragment currentFragment = ((SectionsPagerAdapter)mViewPager.getAdapter()).getFragment(position);

                currentFragment.onResume();

            }//onPageSelected() Ends

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });//addOnPageChangeListener() Ends


    }//Oncreate() Ends




    /****************************************************
     * This is a call back function to create this activity's
     * menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lexicon, menu);

        //Set up searchable configuration
        SearchManager searchManager =  (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                LexiconViewAdapter lexiconViewAdapter = LexiconViewAdapter.getInstance();
                if(lexiconViewAdapter != null){
                    lexiconViewAdapter.updateResource(DataSource.getInstance(getApplicationContext()).queryAll(query));
                }//if Ends

                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LexiconViewAdapter lexiconViewAdapter = LexiconViewAdapter.getInstance();
                if(lexiconViewAdapter != null){
                    lexiconViewAdapter.updateResource(DataSource.getInstance(getApplicationContext()).getLexicons());
                }//if Ends
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;

    }//onCreateOptionsMenu() Ends

    /***********************************************************
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }//if Ends


        return super.onOptionsItemSelected(item);

    }//onOptionsItemSelected() Ends


    /********************************************************************************************************************
    /********************************************************************************************************************
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static ArrayList<Lexicon> lexiconList = null;
        private static ArrayList<Lexicon> favLexiconList = null;
        private  RecycleViewAdapter mFavoriteViewAdapter = null;
        private LexiconViewAdapter mLexiconViewAdapter = null;

        RecyclerView favRecycleview;
        RecyclerView lexRecycleview;
        View rootView;

        public PlaceholderFragment() {

        }//PlaceholderFragment() Ends

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();

            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);

            return fragment;
        }//newInstance() Ends 


        /************************************************************************
         *
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            int placeHolerId  = getArguments().getInt(ARG_SECTION_NUMBER);
            lexiconList = DataSource.getInstance(getActivity()).getLexicons();

            //set up layout view for the second tab: Lexicon
             rootView = inflater.inflate(R.layout.fragment_main, container, false);


            //set up layout view for the first tab: Lexicon
           if(placeHolerId == 1){
               //HOME FRAGMENT
            }//if Ends

            //setup layout view for the third tab: Favorite tab
            else if(placeHolerId == 2){

               // reference to root  fragment_lexicon view
               rootView = inflater.inflate(R.layout.fragment_lexicon, container, false);

               //reference recycle view
                 lexRecycleview = (RecyclerView)rootView.findViewById(R.id.lex_recycle_view);

               // setup recycle view adapter and layout manager
                if( lexRecycleview != null && lexiconList != null ){
                    mLexiconViewAdapter = LexiconViewAdapter.getInstance(getActivity(), lexiconList);
                    lexRecycleview.setAdapter(mLexiconViewAdapter);
                    lexRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));

                }//if Ends

            }//if Ends


            //this is the default view
            else if(placeHolerId == 3){

               // reference to root  fragment_favorite view
               rootView = inflater.inflate(R.layout.fragment_favorite, container, false);;

                // reference recycle view object
                 favRecycleview = (RecyclerView)rootView.findViewById(R.id.fav_recycle_view);

                favLexiconList = DataSource.getInstance(getActivity()).getFavLexicon();

               if(favLexiconList != null)
                    Log.v("FAV_RECYCLER", "Favsize:"+favLexiconList.size() );

                //favLexiconList
                // setup recycle view adapter and layout manager
                if( favRecycleview != null && favLexiconList != null) {

                     mFavoriteViewAdapter =  FavoriteViewAdapter.getInstance(getActivity(), favLexiconList);
                    favRecycleview.setAdapter(mFavoriteViewAdapter);
                    favRecycleview.setLayoutManager(new LinearLayoutManager(getActivity()));

                }//if Ends

            }//if Ends


            return rootView;

        }//onCreateView() ends


        /**************************
         *
         */
        @Override
        public void onResume() {
            super.onResume();

            int placeholder_id = getArguments().getInt(ARG_SECTION_NUMBER);

            if(placeholder_id == 3){

                if(favRecycleview == null) return;
                if(rootView == null) return;

                favLexiconList = DataSource.getInstance(getActivity()).getFavLexicon();

                favRecycleview.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
                favRecycleview.setAdapter( FavoriteViewAdapter.getInstance(getActivity(),favLexiconList ));
            }//if Ends

            if(placeholder_id == 2){

                if(lexRecycleview == null) return;
                if(rootView == null) return;

                lexRecycleview.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

                if(lexiconList != null)
                    lexRecycleview.setAdapter(LexiconViewAdapter.getInstance(getActivity(),lexiconList ));

            }//if Ends
        }
    }//PlaceholderFragment() Ends


    /**************************************************************************
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private HashMap<Integer, String> fragmentTags;
        private FragmentManager mFragmentManager;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragmentTags = new HashMap<>();
            mFragmentManager = fm;


        }//SectionsPagerAdapter() Ends

        /**********************************************
         * getItem(): returns a fragment's position
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }//getItem() Ends


        /**
         *
         * @param container
         * @param position
         * @return
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position){

            Object object = super.instantiateItem(container, position);

            if(object instanceof  Fragment){
                String framentTag = ((Fragment)object).getTag();
                fragmentTags.put(position, framentTag);
            }//if Ends

            return object;
        }//instantiateItem() Ends


            /***
             *
             * @param position
             * @return
             */
        public  Fragment getFragment(int position){
            String fragmentTag = fragmentTags.get(position);

            if(fragmentTag == null) return null;

            return mFragmentManager.findFragmentByTag(fragmentTag);
        }//getFragment() Ends

        /***************************
         *
         * @return
         */
        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }//getCount() Ends

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "LEXICON";
                case 2:
                    return "FAVORITE";
            }//switch() Ends

            return null;
        }//CharSequence() Ends
    }//SectionsPagerAdapter Ends
}//LexiconActivity Class Ends
