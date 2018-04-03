package org.prestech.kejomlexicon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

/*****************************************************************************
 * Created by asohm on 3/23/2018.
 *
 * This is a singleton class that represents the audio resource. It will controll the
 * loading, playing and stopping of the audio files
 */
public class AudioResource {

    private static AudioResource mAudioResource = null;

    private String audioFile = "";
    private String audioFolder = "";
    private Context context = null;
    private static  MediaPlayer mediaPlayer  = new MediaPlayer();

    /**************************************************************
     * This function will return an instance of this class
     * @param mContext
     * @return
     */
    public static AudioResource getInstance(Context mContext) {
        if(mAudioResource == null){
            mAudioResource = new AudioResource(mContext);
        }

        return mAudioResource;
    }//AudioResource() Ends

    /***************************
     * No-arg constructor
     */
    private AudioResource( Context mContext ) {
        context = mContext;
    }//AudioResource() Ends



    public MediaPlayer playAudio(String audioFile){

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
        }
        try {
            AssetFileDescriptor asd = context.getAssets().openFd("Kejom_Audio/"+audioFile.trim()+".mp3");

            mediaPlayer.setOnPreparedListener( new MediaService());
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(asd.getFileDescriptor(), asd.getStartOffset(), asd.getLength());
            mediaPlayer.prepareAsync();

            return mediaPlayer;
        } catch (IOException e ) {
            e.printStackTrace();
        }
        return null;

    }//playAudio() Ends

    public void releaseAudioRes(){
        mediaPlayer.release();
        mediaPlayer = null;
    }//releaseAudioRes() Ends

    private class MediaService implements MediaPlayer.OnPreparedListener{

        @Override
        public void onPrepared(MediaPlayer mp) {
            mp.start();
        }//onPrepared() Ends

    }
}//AudioResource Class Ends
