package com.vg.tlmquiz.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import com.vg.tlmquiz.R;

/**
 * Controller for showing answer on current question from
 * @see QuizActivity
 */
public class CheatActivity extends AppCompatActivity {

    /** Object for accepting showing of answer */
    private Button mShowAnswerButton;

    /** Object for representing answer on question */
    private TextView mAnswerTextView;

    /** Object for representing version of Android SDK */
    private TextView mAndroidSdkVersionTextView;

    /** Variable for storing answer on question */
    private boolean mIsAnswerTrue;

    /** Variable with info about if answer on the question was shown */
    private boolean mIsAnswerShown;

    /** For logging */
    private final static String TAG = "CheatActivity";

    /** Key of the property mIsAnswerShown for bundle*/
    private final static String KEY_IS_ANSWER_SHOWN = "answer_is_shown";

    /** Key for extracting answer on question from
     * @see QuizActivity using
     * @see Intent */
    private final static String EXTRA_ANSWER_IS_TRUE =
            "com.vg.tlmquiz.controllers.quizactivity.answer_is_true";

    /** Key for sharing answer on question with
     * @see QuizActivity using
     * @see Intent */
    private final static String EXTRA_ANSWER_IS_SHOWN =
            "com.vg.tlmquiz.controllers.cheatactivity.answer_is_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_cheat);

        mIsAnswerTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        if(savedInstanceState != null){
            mIsAnswerShown = savedInstanceState.getBoolean(KEY_IS_ANSWER_SHOWN, false);
            setAnswerIsShownResult(mIsAnswerShown);
        }

        mAnswerTextView = (TextView) findViewById(R.id.answer_text);

        mShowAnswerButton = (Button) findViewById(R.id.show_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mIsAnswerTrue){
                    mAnswerTextView.setText(R.string.true_button);
                } else {
                    mAnswerTextView.setText(R.string.false_button);
                }
                setAnswerIsShownResult(true);

                // Executing of this code is available only for API 21 and higher
                // Animation of hiding mShowAnswerButton
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth();
                    Animator anim = ViewAnimationUtils
                            .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mAnswerTextView.setVisibility(View.VISIBLE);
                            mShowAnswerButton.setVisibility(View.INVISIBLE);
                        }
                    });
                    anim.start();
                } else {
                    mAnswerTextView.setVisibility(View.VISIBLE);
                    mShowAnswerButton.setVisibility(View.INVISIBLE);
                }
            }
        });

        // Showing Android SDK version
        mAndroidSdkVersionTextView = (TextView) findViewById(R.id.android_sdk_version);
        mAndroidSdkVersionTextView.append(" " + Build.VERSION.SDK_INT);
    }

    /**
     * Is called every time after calling onPause() method
     * @param savedInstanceState - bundle for saving state of activity
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putBoolean(KEY_IS_ANSWER_SHOWN, mIsAnswerShown);
    }

    /**
     * Encapsulates sharing extra data with contexts (activities, components etc)
     * @param packageContext - context for sharing
     * @param answerIsTrue - value for sharing
     * @return Intent with sharing data
     */
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent i = new Intent(packageContext, CheatActivity.class);
        i.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return i;
    }

    /**
     * Sets result of showing answer on question using setResult() method
     * @param isAnswerShown
     */
    private void setAnswerIsShownResult(boolean isAnswerShown) {
        mIsAnswerShown = isAnswerShown;
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_IS_SHOWN, isAnswerShown);
        setResult(RESULT_OK, intent);
    }

    /** Returns info about cheating on the side of the user */
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_IS_SHOWN, false);
    }
}
