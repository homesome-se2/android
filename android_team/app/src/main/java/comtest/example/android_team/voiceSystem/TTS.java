package comtest.example.android_team.voiceSystem;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class TTS {

    private TextToSpeech mtts;
    private Context activityContext;
    private static final String TAG = "Info";

    public TTS(Context context) {
        this.activityContext = context;

    }

    public void initTTS(){
        mtts = new TextToSpeech(activityContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = mtts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e(TAG, "onInit: TTS Language not supported" );
                    }
                }else{
                    Log.e(TAG, "onInit: TTS Initialization Failed" );
                }
            }
        });
    }

    public void textToSpeak(String textSpeak){
        Bundle b = new Bundle();
        b.putFloat("KEY_PARAM_VOLUME",1f); // Don't do shit
        mtts.speak(textSpeak,TextToSpeech.QUEUE_ADD,b,"1");
    }

    public void stopTTS(){
        if (mtts !=null){
            mtts.stop();
            mtts.shutdown();
        }
    }
}
