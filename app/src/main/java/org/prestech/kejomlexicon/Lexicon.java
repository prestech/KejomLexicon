package org.prestech.kejomlexicon;

import java.util.ArrayList;

/**************************************************************************
 * Created by asohm on 4/23/2017.
 */
public class Lexicon {

    private String kejomWord;
    private String englishWord;
    private String partOfSpeech;
    private String pluralForm;
    private String variant;
    private String examplePhrase;
    private String pronunciation;
    private int lexiconId;

    public  Lexicon(){

    }
    /*******************************************************************
     *
     * @param lexiconToken
     */
   public Lexicon(String lexiconToken[] ){

       this.kejomWord = lexiconToken[0];
       this.pronunciation = lexiconToken[1];
       this.pluralForm = lexiconToken[2];
       this.variant = lexiconToken [3];
       this.partOfSpeech = lexiconToken[4];
       this.englishWord = lexiconToken[5];
      // this.examplePhrase = lexiconToken[6];
   }//Lexicon

    /**********************************************
     *
     * @param lexiconId
     */
    public void setLexiconId(int lexiconId) {
        this.lexiconId = lexiconId;
    }//setLexiconId() Ends

    /**********************************************
     *
     * @return
     */
    public int getLexiconId() {
        return lexiconId;
    }//getLexiconId() Ends


    /************************************
     *
     * @return
     */
    public String getEnglishWord() {
        return englishWord;
    }//getEnglishWord() Ends

    /**********************************
     *
     * @return
     */
    public String getKejomWord() {
        return kejomWord;
    }//getKejomWord() Ends

    /**
     *
     * @return
     */
    public String getExamplePhrase() {
        return examplePhrase;
    }//getExamplePhrase() Ends

    /**
     *
     * @param kejomWord
     */
    public void setKejomWord(String kejomWord) {
        this.kejomWord = kejomWord;
    }//setKejomWord() Ends

    /**
     *
     * @param englishWord
     */
    public void setEnglishWord(String englishWord) {
        this.englishWord = englishWord;
    }//englishWord() Ends


    /**
     *
     * @return
     */
    public String getPartOfSpeech() {
        return partOfSpeech;
    }//getPartOfSpeech() Ends

    /**
     *
     * @param partOfSpeech
     */
    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }//partOfSpeech() Ends

    /**
     *
     * @return
     */
    public String getPluralForm() {
        return pluralForm;
    }//getPluralForm() Ends

    /**
     *
     * @param pluralForm
     */
    public void setPluralForm(String pluralForm) {
        this.pluralForm = pluralForm;
    }//setPluralForm() Ends

    /**
     *
     * @return
     */
    public String getVariant() {
        return variant;
    }//getVariant() Ends

    /**
     *
     * @param variant
     */
    public void setVariant(String variant) {
        this.variant = variant;
    }//setVariant() Ends

    /**
     *
     * @param examplePhrase
     */
    public void setExamplePhrase(String examplePhrase) {
        this.examplePhrase = examplePhrase;
    }//setExamplePhrase() Ends

    /**
     *
     * @return
     */
    public String getPronunciation() {
        return pronunciation;
    }//getPronunciation() Ends

    /**
     *
     * @param pronunciation
     */
    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }//setPronunciation() Ends


    /******************************************
     *
     * @param
     * @return
     */
    @Override
    public boolean equals(Object object) {

        //down cast
        Lexicon lexicon =  (Lexicon) object;

        if(this.getLexiconId()==lexicon.getLexiconId()){
            return true;
        }else {
            return false;
        }
    }//equals() Ends

}//Lexicon Class Ends
