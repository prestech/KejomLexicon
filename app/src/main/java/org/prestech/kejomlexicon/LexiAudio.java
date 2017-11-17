package org.prestech.kejomlexicon;

import android.media.MediaPlayer;

/************************************************************************
 * This class will implement the Singleton design pattern, which ensures that
 * only one instance of this class is created at any given time
 *
 * This class is a template for creating an audio resource object
 *
 * Created by asohm on 4/23/2017.
 */
public class LexiAudio extends MediaPlayer {

    LexiAudio lexiAudio;

    private String audioSourcePath;

    /*************************************
     *
     *TODO: Add available audio the lexicons lexicon
     */
    private LexiAudio(){

    }//LexiAudio() Ends

    /********************************
     *
     */
    public LexiAudio getInstance(){

        if(lexiAudio != null){return lexiAudio; }
        lexiAudio = new LexiAudio();

        return  lexiAudio;
    }//getInstance() Ends


    /********************************************
     *
     * @param audioSourcePath
     */
    public void setAudioSourcePath(String audioSourcePath) {
        this.audioSourcePath = audioSourcePath;
    }
}//LexiAudio Class Ends
