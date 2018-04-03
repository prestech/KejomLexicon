package org.prestech.kejomlexicon;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/******************************************************************************
 * This class is an implementation  of the SQLITE database resources through
 * which other parts of this activity will use to access the database
 *
 * This class is a Singleton class, which means it is implemented to ensure that
 * only one instance of it can be instantiated at any time in the life cycle of the
 * application.
 *
 * Created by asohm on 4/23/2017.
 * 47439
 */
public class DataSource extends SQLiteOpenHelper {

    // DataSource instance
    private  static DataSource dataSource;
    private  Context context;

    //DBVersion
    private final static int DBVersion = 1;
    public  final static  String DB_NAME = "kejomDB";

    private static int queryIndex = 0;

    // TODO:make this resource entry points trade safe
    private static ArrayDeque<Lexicon> lexiconHashset = null;
    private static  ArrayDeque<Lexicon> favLexiconHashset = null;

    //Statement to create lexicon table
    public final static String SQL_CREATE_LEXICON_TABLE = "CREATE TABLE IF NOT EXISTS "+ LexiconTable.TABLE_NAME + " ("
            + LexiconTable.PRIMARY_KEY + " INTEGER PRIMARY KEY , "
            + LexiconTable.ENGLISH_WORD + " TEXT ,"
            + LexiconTable.KEJOM_WORD + " TEXT ,"
            + LexiconTable.PART_OF_SPEECH + " TEXT ,"
            + LexiconTable.PRONUNCIATION + " TEXT ,"
            + LexiconTable.VARIANT + " TEXT ,"
            + LexiconTable.PLURAL + " TEXT );";

    public final static String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE  IF NOT EXISTS "+ FavoriteTable.TABLE_NAME + "( "
            + FavoriteTable.PRIMARY_KEY + " INTEGER PRIMARY KEY,"+
            FavoriteTable.FORIEGN_KEY + " INTEGER NOT NULL, FOREIGN KEY (lexiconId) REFERENCES "+ LexiconTable.TABLE_NAME  + "("+ LexiconTable.PRIMARY_KEY +") )";



    /**************************************************************************
     * This constructor is made private to ensure that it is not created outside
     * the class using the NEW operator; since this is a singleton class it will
     * be accessed with the getInstance(Context) function
     */
    private  DataSource(Context context){
        super(context, DB_NAME, null, DBVersion);
        this.context = context;
        lexiconHashset = new ArrayDeque<>(5);

    }//Database() Ends


    /***********************************************************************************
     *
     * @return
     */
    public  ArrayDeque<Lexicon>  getLexicons() {
        return lexiconHashset;
    }//getLexiconArrayList()


    /***********************************************************************************
     * Return a list of favorite lexicons from the
     * Favorite table. Only Query favorite lexicon when
     * change occur in the databases'  favorite table
     * @return
     */
    public  ArrayDeque<Lexicon> getFavLexicon() {

        if(favLexiconHashset == null){
            favLexiconHashset = this.queryFavorites();
        }//if Ends

        return favLexiconHashset;

    }//getLexiconArrayList()



    /************************************************************************************
     * This function returns an instance of the data base
     * it create a new Datasource object if it thus not
     * exit. Else it reuses the already created instance
     *
     * @param context
     * @return
     */
    public static DataSource getInstance(Context context) {
        // re-use data instance if it exist
        if(dataSource != null){return dataSource; }

        //create a new instance if it thus not
        //exist
        dataSource = new DataSource(context);

        return dataSource;
    }//getInstance() Ends

    /***********************************************************************************
     * @param sqLiteDatabase
     * The Helper (super class) calls this method each time the database is accessed,
     * to create one, if the data base does not exits
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //create the database(s) and the tables
        sqLiteDatabase.execSQL(SQL_CREATE_LEXICON_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);

    }//onCreate() Ends


    /*******************************************************************************
     * This function inserts rows of lexicon data into the database
     * @param lexicon
     */
    public  void insert(Lexicon lexicon) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LexiconTable.ENGLISH_WORD, lexicon.getEnglishWord());
        values.put(LexiconTable.KEJOM_WORD, lexicon.getKejomWord());
        values.put(LexiconTable.PART_OF_SPEECH, lexicon.getPartOfSpeech());
        values.put(LexiconTable.PRONUNCIATION, lexicon.getPronunciation());
        values.put(LexiconTable.PLURAL, lexicon.getPluralForm());
        //values.put(LexiconTable.EXAMPLE, lexicon.getExamplePhrase());
        values.put(LexiconTable.VARIANT, lexicon.getVariant());

        //if database object is not NULL
        if(database != null){
            database.insert(LexiconTable.TABLE_NAME, null, values);
        }//if ends
        database.close();
    }//onUpgrade() Ends


    /**********************************************************************************
     * resetQueryIndex()
     */
     private void resetQueryIndex(){
         queryIndex = 0;
     }//if Ends


    /*******************************************************************************
     * This function inserts rows of lexicon data into the database
     */
    public  ArrayDeque<Lexicon>  queryAll(boolean qDownWards, int nIncrement ) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Lexicon lexicon = null;
        Log.i("QUERRY_INDEX", queryIndex+"");
        SQLiteDatabase database = this.getReadableDatabase();
        String select_stmt = "";
        //Query Statement

         if(qDownWards == false){
            select_stmt = "SELECT * FROM "+LexiconTable.TABLE_NAME+ " WHERE "+LexiconTable.PRIMARY_KEY+" >= "+ (queryIndex-nIncrement) +" AND "+ LexiconTable.PRIMARY_KEY+" < "+ (queryIndex);
            queryIndex-=nIncrement;
        }else if(qDownWards == true){
            select_stmt = "SELECT * FROM "+LexiconTable.TABLE_NAME+ " WHERE "+LexiconTable.PRIMARY_KEY+" >= "+ queryIndex +" AND "+ LexiconTable.PRIMARY_KEY+" < "+ (queryIndex+nIncrement);
            queryIndex+=nIncrement;

        }else {
            return null;
         }



        Cursor cursor = database.rawQuery(select_stmt, null);

        if (cursor.moveToFirst()){

            do{

                lexicon = new Lexicon();

                lexicon.setKejomWord(cursor.getString(cursor.getColumnIndex(LexiconTable.KEJOM_WORD)));
                lexicon.setEnglishWord(cursor.getString(cursor.getColumnIndex(LexiconTable.ENGLISH_WORD)));
                lexicon.setPartOfSpeech(cursor.getString(cursor.getColumnIndex(LexiconTable.PART_OF_SPEECH)));
                lexicon.setPluralForm(cursor.getString(cursor.getColumnIndex(LexiconTable.PLURAL)));
                lexicon.setPronunciation(cursor.getString(cursor.getColumnIndex(LexiconTable.PRONUNCIATION)));
                // lexicon.setVariant(cursor.getString(cursor.getColumnIndex(LexiconTable.VARIANT)));
                lexicon.setLexiconId(cursor.getInt(cursor.getColumnIndex(LexiconTable.PRIMARY_KEY)));

                lexiconHashset.addLast(lexicon);
            }while (cursor.moveToNext());
            database.close();

            return lexiconHashset;
        }

        return  null;

    }//onUpgrade() Ends



    /*******************************************************************************
     * This function inserts rows of lexicon data into the database
     */
    public  ArrayDeque<Lexicon> queryAll(int from ,int to) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        Lexicon lexicon = null;

        SQLiteDatabase database = this.getReadableDatabase();


        String select_stmt = "SELECT * FROM "+LexiconTable.TABLE_NAME+ " WHERE "+LexiconTable.PRIMARY_KEY+" >= "+ from +" AND "+ LexiconTable.PRIMARY_KEY+" <= "+ to;

        //update query index
        DataSource.queryIndex = to;

        Cursor cursor = database.rawQuery(select_stmt, null);

        if (cursor.moveToFirst()){

            do{

                lexicon = new Lexicon();

                lexicon.setKejomWord(cursor.getString(cursor.getColumnIndex(LexiconTable.KEJOM_WORD)));
                lexicon.setEnglishWord(cursor.getString(cursor.getColumnIndex(LexiconTable.ENGLISH_WORD)));
                lexicon.setPartOfSpeech(cursor.getString(cursor.getColumnIndex(LexiconTable.PART_OF_SPEECH)));
                lexicon.setPluralForm(cursor.getString(cursor.getColumnIndex(LexiconTable.PLURAL)));
                lexicon.setPronunciation(cursor.getString(cursor.getColumnIndex(LexiconTable.PRONUNCIATION)));
                // lexicon.setVariant(cursor.getString(cursor.getColumnIndex(LexiconTable.VARIANT)));
                lexicon.setLexiconId(cursor.getInt(cursor.getColumnIndex(LexiconTable.PRIMARY_KEY)));

                lexiconHashset.addLast(lexicon);
            }while (cursor.moveToNext());
            database.close();

            return lexiconHashset;
        }

        return  null;

    }//onUpgrade() Ends



    /*********************************************************************************
     * Function to clear all elements from the lexicon
     */
      public void clearDataSource(){
          lexiconHashset.clear();
          resetQueryIndex();
      }//clear ends

    /*******************************************************************************
     * queryAll(String, boolean) an overloaded function
     * This function inserts rows of lexicon data into the database
     */
    public   LinkedList<Lexicon>  queryAll(String keyword ) {

        keyword = keyword.toLowerCase();

        Toast.makeText(context, "Searching: "+keyword, Toast.LENGTH_SHORT).show();
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        LinkedList<Lexicon>  lexiconHashset =  new LinkedList<Lexicon>();
        Lexicon lexicon = null;

        SQLiteDatabase database = this.getReadableDatabase();

        //Query Statement
        String select_stmt = "SELECT * FROM "+LexiconTable.TABLE_NAME +" WHERE " + LexiconTable.ENGLISH_WORD+ "  LIKE '%"+ keyword +"%'";

        Cursor cursor = database.rawQuery(select_stmt, null);

        if (cursor.moveToFirst()){

            do{
                lexicon = new Lexicon();

                lexicon.setKejomWord(cursor.getString(cursor.getColumnIndex(LexiconTable.KEJOM_WORD)));
                lexicon.setEnglishWord(cursor.getString(cursor.getColumnIndex(LexiconTable.ENGLISH_WORD)));
                lexicon.setPartOfSpeech(cursor.getString(cursor.getColumnIndex(LexiconTable.PART_OF_SPEECH)));
                lexicon.setPluralForm(cursor.getString(cursor.getColumnIndex(LexiconTable.PLURAL)));
                lexicon.setPronunciation(cursor.getString(cursor.getColumnIndex(LexiconTable.PRONUNCIATION)));
                // lexicon.setVariant(cursor.getString(cursor.getColumnIndex(LexiconTable.VARIANT)));
                lexicon.setLexiconId(cursor.getInt(cursor.getColumnIndex(LexiconTable.PRIMARY_KEY)));

                lexiconHashset.add(lexicon );

                Log.i("SEARCH_RESULT:", "{"+lexicon.getLexiconId() + "}: "+ lexicon.getEnglishWord() +" ["+lexicon.getKejomWord()+"]" );

            }while (cursor.moveToNext());

            Log.i("SEARCH_SIZE"," ["+lexiconHashset.size()+"]" );
            cursor.close();
            database.close();

            return lexiconHashset;
        }
        return  null;

    }//onUpgrade() Ends


    /***********************************************************************
     * TODO: Makes sure favorite Lexicon does not already exist in the data base
     */
    public ArrayDeque<Lexicon> insertFavorite(int lexiconId){

        SQLiteDatabase db = this.getWritableDatabase();

        String insert_stmt = "INSERT INTO "+ FavoriteTable.TABLE_NAME +
                " ( "+FavoriteTable.FORIEGN_KEY+ ")  VALUES "+
                "( "+lexiconId + " )";
        Log.i("FAVORITE_INSERT", ""+lexiconId);
        db.execSQL(insert_stmt);

        //Do a fresh query
        favLexiconHashset = this.queryFavorites();

        Log.i("FAVORITE EVENT", "Inserting int favorite"+ favLexiconHashset);
        db.close();

        return favLexiconHashset;

    }//insert() Ends

    /**********************************************************************************
     *
     */
    public ArrayDeque removeFavorite(int lexiconId){

        //open database connection
        SQLiteDatabase db = this.getWritableDatabase();

        String insert_stmt = "DELETE FROM "+ FavoriteTable.TABLE_NAME +
                " WHERE "+FavoriteTable.FORIEGN_KEY + " = "+lexiconId;
        Log.i("FAVORITE_REMOVE", ""+lexiconId);
        //execute sql statement
        db.execSQL(insert_stmt);

        //Refresh data in favLexiconHashset
        favLexiconHashset = this.queryFavorites();

        //close database connection
        db.close();

        return  favLexiconHashset;

    }//removeFavorite() Ends

    /**********************************************************************************
     * This function inserts rows of lexicon data into the database
     */
    public  ArrayDeque<Lexicon> queryFavorites() {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over

        ArrayDeque<Lexicon> favLeixons = new  ArrayDeque<Lexicon>();
        Lexicon lexicon = null;

        SQLiteDatabase database = this.getReadableDatabase();

        //Query Statement
        String select_stmt = "SELECT * FROM "+ FavoriteTable.TABLE_NAME;

        Cursor cursor = database.rawQuery(select_stmt, null);

        try {

           if (cursor.moveToFirst()) {

               do {

                   for (Lexicon lexi : lexiconHashset) {
                       if (lexi.getLexiconId() == cursor.getInt(cursor.getColumnIndex(FavoriteTable.FORIEGN_KEY))) {
                           if (!favLeixons.contains(lexi)) {
                               favLeixons.addLast(lexi);
                           }
                       }//if Ends
                   }

                   if(lexicon != null){
                       Log.i("FAVORITE_QUERY", lexicon.getEnglishWord());
                   }//if Ends

               } while (cursor.moveToNext());
               cursor.close();
               return favLeixons;
           }//if Ends
       /*}catch (Exception ex){*/

       }finally {
           cursor.close();
       }//try-catch finally ends

        return  null;

    }//onUpgrade() Ends


    /***********************************************************************************************
     * TODO: Better still perform a direct query from the database
     */
    public  boolean isFavorite(Lexicon lexicon){

        //do a fresh query to update favLexiconHashset
        favLexiconHashset = this.queryFavorites();

        if(favLexiconHashset == null || lexicon == null) return  false;

        for (Lexicon favLexicon : favLexiconHashset ){

            if(lexicon.getLexiconId() == favLexicon.getLexiconId()){
                return  true;
            }//if Ends
        }//for Ends
        return false;
    }//isFavorite() Ends



    /*************************************************************************
     *
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        //delete the data base and create a new one by calling the database's onCreate()

    }//onUpgrade() Ends


    /******************************************************************************
     * This function extracts the components of a lexicon and create a lexicon object
     * from it. it returns the lexicon object
     * @param lineOfData
     * @return
     */
    private static Lexicon extractLexiconTokens(String lineOfData) {

        int index = 0;
        char charAtIndex = lineOfData.charAt(index);
        String variantPluralPartOfSpeech[] = new String[6];

        variantPluralPartOfSpeech[0] = "";
        while(charAtIndex != '['){
            variantPluralPartOfSpeech[0] = variantPluralPartOfSpeech[0] + charAtIndex;
            index++;
            charAtIndex = lineOfData.charAt(index);
        }//while Ends
        index++;
        charAtIndex = lineOfData.charAt(index);

        //System.out.println(charAtIndex+ " kejom:"+ variantPluralPartOfSpeech[0]);

        variantPluralPartOfSpeech[1] = "";
        while(charAtIndex != ']'){
            variantPluralPartOfSpeech[1] = variantPluralPartOfSpeech[1] + charAtIndex;
            index++;
            charAtIndex = lineOfData.charAt(index);
        }//while Ends
        index++;
        charAtIndex = lineOfData.charAt(index);

        //System.out.println(charAtIndex + " Phonetics:"+ variantPluralPartOfSpeech[1]);
        String lexiconData ="";
        boolean hasPlural = false;
        boolean hasVariant = false;


        while(charAtIndex != ':' && charAtIndex != '.' && charAtIndex != '?'){

            lexiconData = lexiconData + charAtIndex;
            index++;
            charAtIndex = lineOfData.charAt(index);

        }//while Ends
        index++;
        charAtIndex = lineOfData.charAt(index);

        /********************************************************
         *
         */
        if(lexiconData.equalsIgnoreCase("variant:")
                || lexiconData.toLowerCase().contains("variant")){
            hasPlural = true;
            variantPluralPartOfSpeech[2] = "";
            while(charAtIndex != '.'){
                variantPluralPartOfSpeech[2] = variantPluralPartOfSpeech[2] + charAtIndex;
                index++;
                charAtIndex = lineOfData.charAt(index);
            }//while Ends
            index++;
            charAtIndex = lineOfData.charAt(index);
            // System.out.println(charAtIndex + " Variant:"+ variantPluralPartOfSpeech[2]);

        }//if Ends

        else if(lexiconData.equalsIgnoreCase("pl:") ||
                lexiconData.toLowerCase().contains("pl")){
            hasVariant = true;
            variantPluralPartOfSpeech[3] = "";
            while(charAtIndex != '.'){
                variantPluralPartOfSpeech[3] = variantPluralPartOfSpeech[3] + charAtIndex;
                index++;
                charAtIndex = lineOfData.charAt(index);
            }//while Ends
            index++;
            charAtIndex = lineOfData.charAt(index);
            // System.out.println(charAtIndex + " Plural:"+ variantPluralPartOfSpeech[3]);
        }//else if Ends


        if(hasVariant==true || hasPlural == true){
            variantPluralPartOfSpeech[4] = "";
            while(charAtIndex != '.' && charAtIndex != '?'){
                variantPluralPartOfSpeech[4] = variantPluralPartOfSpeech[4] + charAtIndex;
                index++;
                charAtIndex = lineOfData.charAt(index);
            }//while Ends
            index++;

        }//if Ends
        else{
            variantPluralPartOfSpeech[4] = lexiconData;
        }//else ends
        //System.out.println(charAtIndex + " Part of Speech:"+ variantPluralPartOfSpeech[4]);

        variantPluralPartOfSpeech[5] = "";
        while(index < lineOfData.length()-1){
            variantPluralPartOfSpeech[5] = variantPluralPartOfSpeech[5] + charAtIndex;
            index++;
            charAtIndex = lineOfData.charAt(index);
        }//while Ends

        // System.out.println(""+variantPluralPartOfSpeech[5]);
        if(variantPluralPartOfSpeech[5].length() < 1){
            System.out.println(charAtIndex + " Part of Speech:"+ variantPluralPartOfSpeech[4]);
            System.err.println("Exception: No english word Captured for "+variantPluralPartOfSpeech[0]);
            System.out.println("******************************");
        }
        index = 0;

        Lexicon lexicon = new Lexicon(variantPluralPartOfSpeech);

        return lexicon;
    }//extractLexiconTokens() Ends



    /************************************************************
     * This function reads the lexicon data from the text file, located in the raw folder,
     * and rights it to the SQLITE data base.
     *
     * It calls the extractLexiconTokens() function to created Lexicon objects from the data
     */
    public  void transferFileDataToSqlite (){

        //Querry to check if data is in database; if there is no data inser data into database table
        if(context.getApplicationContext().getDatabasePath(DB_NAME).exists() == true){
            Log.i("SQLITE:","Alrefady Exist");

            if(queryAll(0, 20) != null){
                return;
            }
        }
            Toast.makeText(context, "INSERTING INTO DATA BASE ", Toast.LENGTH_SHORT);
            InputStream inputStream = null;

            String path = context.getPackageResourcePath() + "/raw/kejom_english.txt";

            // inputStream = this.getApplicationContext().getAssets().open("../res/raw/kejom_english.txt");
            inputStream = context.getResources().openRawResource(R.raw.kejom_english);

           /* try {
                //inputStream = context.getApplicationContext().getAssets().open("../res/raw");
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            Scanner scanner = null;
            try {
                scanner = new Scanner(inputStream);
                String lexiconId = "";

                while (scanner.hasNextLine()) {
                    //Log.i("INSERTER: ", "Inserting lexicon");
                    Lexicon lexicon = extractLexiconTokens(scanner.nextLine());

                    if(lexicon.getEnglishWord().charAt(0) == '.'){
                        lexicon.setEnglishWord( lexicon.getEnglishWord().substring(1));
                    }//if Ends

                    insert(lexicon);
                }//while() Ends

            } //readData(String)
            finally {
                try {
                    inputStream.close();
                    scanner.close();
                } catch (IOException ex) {
                }//catch() Ends
            }//finally Ends

    }//readData(String Filename) Ends



    /**************************************************************************************
     *
     */
    public static class LexiconTable{

        public static String TABLE_NAME = "lexicon";
        public static String KEJOM_WORD = "kejomWord";
        public static String ENGLISH_WORD = "englishWord";
        public static String PART_OF_SPEECH = "partOfSpeech";
        public static String PLURAL = "pluralForm";
        public static String VARIANT = "variant";
        public static String EXAMPLE = "examplePhrase";
        public static String PRONUNCIATION = "pronunciation";
        public static String PRIMARY_KEY =  "lexiconID";

    }//LexiconTable



    /**************************************************************************************
     *
     */
    public static class FavoriteTable{

        public static String TABLE_NAME = "favorite_lexicon";
        public static String FORIEGN_KEY = LexiconTable.PRIMARY_KEY;
        public static String PRIMARY_KEY = "favID";


    }//LexiconTable


}//Database Class