package com.pubnub.example.android.fabric.pnfabricchat.speech;


import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.nuance.speechkit.Audio;
import com.nuance.speechkit.AudioPlayer;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.pubnub.example.android.fabric.pnfabricchat.R;

public class TextToSpeech implements TextHandler {
    private boolean ttsEnabled = false;
    private Activity parent;
    private Session speechSession;
    private Transaction ttsTransaction;

    public TextToSpeech(Activity parent) {
        this.parent = parent;
    }

    public boolean isTtsEnabled() {
        return ttsEnabled;
    }

    public void toggleEnabled() {
        if (this.ttsEnabled) {
            stop();
        }

        this.ttsEnabled = !this.ttsEnabled;
    }

    @Override
    public void onText(String text) {
        if (!ttsEnabled) {
            return;
        }

        Uri nuanceUri = Uri.parse(parent.getResources().getString(R.string.com_nuance_url));
        String nuanceAppKey = parent.getResources().getString(R.string.com_nuance_appKey);

        speechSession = Session.Factory.session(parent, nuanceUri, nuanceAppKey);

        speechSession.getAudioPlayer().setListener(new AudioPlayer.Listener() {
            @Override
            public void onBeginPlaying(AudioPlayer audioPlayer, Audio audio) {
                Log.d("SpeechKit", "onBeginPlaying");
            }

            @Override
            public void onFinishedPlaying(AudioPlayer audioPlayer, Audio audio) {
                Log.d("SpeechKit", "onFinishedPlaying");
            }
        });

        synthesize(text);
    }

    private void synthesize(String text) {
        Transaction.Options options = new Transaction.Options();
        options.setLanguage(new Language("eng-USA"));
        speechSession.getAudioPlayer().play();

        Toast.makeText(parent.getApplicationContext(), "Now Speaking: \""
                + text + "\"", Toast.LENGTH_LONG).show();

        ttsTransaction = speechSession.speakString(text, options, new Transaction.Listener() {
            @Override
            public void onAudio(Transaction transaction, Audio audio) {
                Log.d("SpeechKit", "onAudio");
                ttsTransaction = null;
            }

            @Override
            public void onSuccess(Transaction transaction, String s) {
                Log.d("SpeechKit", "onSuccess");
            }

            @Override
            public void onError(Transaction transaction, String suggestion, TransactionException e) {
                Log.d("SpeechKit", "onError: " + e.getMessage() + ". " + suggestion);
                ttsTransaction = null;
            }
        });
    }

    private void stop() {
        if (ttsTransaction != null) {
            ttsTransaction.cancel();
        }
    }

}
