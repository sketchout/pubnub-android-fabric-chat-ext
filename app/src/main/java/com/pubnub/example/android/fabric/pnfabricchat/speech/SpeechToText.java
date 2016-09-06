package com.pubnub.example.android.fabric.pnfabricchat.speech;

import android.app.Activity;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.RecognitionType;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.pubnub.example.android.fabric.pnfabricchat.R;

public class SpeechToText {
    public static final String VIA_DICTATION_DISCLAIMER = " (via dictation)";

    /* State Logic: IDLE -> LISTENING -> PROCESSING -> repeat */
    public enum State {
        IDLE,
        LISTENING,
        PROCESSING
    }

    private Session speechSession;
    private Transaction recoTransaction;
    private State state = State.IDLE;
    private TextHandler textHandler;
    private Activity parent;

    public SpeechToText(Activity parent, TextHandler textHandler) {
        Uri nuanceUri = Uri.parse(parent.getResources().getString(R.string.com_nuance_url));
        String nuanceAppKey = parent.getResources().getString(R.string.com_nuance_appKey);

        this.parent = parent;
        this.speechSession = Session.Factory.session(parent, nuanceUri, nuanceAppKey);
        this.textHandler = textHandler;
    }

    public void toggleReco(View button) {
        switch (state) {
            case IDLE:
                recognize();
                break;
            case LISTENING:
                stopRecording();
                break;
            case PROCESSING:
                cancel();
                break;
        }
    }
    public void recognize() {
        Transaction.Options options = new Transaction.Options();
        options.setRecognitionType(RecognitionType.DICTATION);
        options.setDetection(DetectionType.Long);
        options.setLanguage(new Language("eng-USA"));

        recoTransaction = speechSession.recognize(options, recoListener);
    }

    public void stopRecording() {
        recoTransaction.stopRecording();
    }

    public void cancel() {
        recoTransaction.cancel();
    }

    private Transaction.Listener recoListener = new Transaction.Listener() {
        @Override
        public void onStartedRecording(Transaction transaction) {
            Log.d("SpeechKit", "onStartedRecording");
            state = State.LISTENING;
            startAudioLevelPoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            Log.d("SpeechKit", "onFinishedRecording");
            state = State.PROCESSING;
            stopAudioLevelPoll();
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            String theText = recognition.getText();
            Log.d("SpeechKit", "onRecognition: " + theText);
            state = State.IDLE;

            Toast.makeText(parent.getApplicationContext(), "Recognized: \""
                    + theText + "\"", Toast.LENGTH_LONG).show();

            SpeechToText.this.textHandler.onText(theText + VIA_DICTATION_DISCLAIMER);
        }

        @Override
        public void onSuccess(Transaction transaction, String s) {
            Log.d("SpeechKit", "onSuccess");
        }

        @Override
        public void onError(Transaction transaction, String s, TransactionException e) {
            Log.e("SpeechKit", "onError: " + e.getMessage() + ". " + s);
            state = State.IDLE;
        }
    };

    private Handler handler = new Handler();

    private Runnable audioPoller = new Runnable() {
        @Override
        public void run() {
            float level = recoTransaction.getAudioLevel();
            Log.d("SpeechKit", "audioLevel: " + (int) level);
            handler.postDelayed(audioPoller, 50);
        }
    };

    private void startAudioLevelPoll() {
        audioPoller.run();
    }

    private void stopAudioLevelPoll() {
        handler.removeCallbacks(audioPoller);
    }

}
