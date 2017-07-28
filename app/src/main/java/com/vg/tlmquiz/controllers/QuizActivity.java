package com.vg.tlmquiz.controllers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.vg.tlmquiz.R;
import com.vg.tlmquiz.models.Question;

/**
 * Main controller for quiz
 */
public class QuizActivity extends AppCompatActivity {

    /** Object for accepting true as answer on question */
    private Button mTrueButton;

    /** Object for accepting true as answer on question */
    private Button mFalseButton;

    /** Object for showing activity with answer on question */
    private Button mCheatButton;

    /** Object to go to the previous question */
    private ImageButton mPreviousButton;

    /** Object to go to the next question */
    private ImageButton mNextButton;

    /** Object for representing text of question */
    private TextView mQuestionText;

    /** For logging */
    private final static String TAG = "QuizActivity";

    /** Key of the current index of question from array for bundle */
    private final static String KEY_CURRENT_INDEX = "current_index";

    /** Key of the boolean array with info about cheating on the questions */
    private final static String KEY_QUESTIONS_SHOWN = "questions_shown";

    /** Requst id of daughter activity
     * @see CheatActivity */
    private final static int REQUEST_CODE_CHEAT = 0;

    /** For checking cheating on the part of the user */
    private boolean mIsCheater;

    /** Current index of qustion from array */
    private int mCurrentQuestionIndex = 0;

    // Initialization of questions
    private Question[] mQuestions = new Question[] {
            new Question(R.string.question_id_1, true),
            new Question(R.string.question_id_2, true),
            new Question(R.string.question_id_3, true),
            new Question(R.string.question_id_4, false)
    };

    private boolean[] mQuestionsShown = new boolean[] {
            false, false, false, false
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if(savedInstanceState != null) {
            mCurrentQuestionIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX, 0);
            mQuestionsShown = savedInstanceState.getBooleanArray(KEY_QUESTIONS_SHOWN);
            mIsCheater = mQuestionsShown[mCurrentQuestionIndex];
        }

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean answerIsTrue = mQuestions[mCurrentQuestionIndex].isAnswerTrue();
                Intent i = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
                startActivityForResult(i, REQUEST_CODE_CHEAT);
            }
        });

        mPreviousButton = (ImageButton) findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setPreviousQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                setNextQuestion();
            }
        });

        mQuestionText = (TextView) findViewById(R.id.question_text);
        mQuestionText.setText(mQuestions[mCurrentQuestionIndex].getTextResId());
        mQuestionText.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                setNextQuestion();
            }
        });
    }

    /**
     * Is called every time after calling onPause() method
     * @param savedInstanceState - bundle for saving state of activity
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState() called");
        savedInstanceState.putInt(KEY_CURRENT_INDEX, mCurrentQuestionIndex);
        savedInstanceState.putBooleanArray(KEY_QUESTIONS_SHOWN, mQuestionsShown);
    }

    /**
     * Is called every time after clicking BACK (OS Andorid) button from
     * daughter activity
     * @param requestCode - request id of daughter activity (to avoid conflicts
     *                      when activity has multiple daughter activities)
     * @param resultCode - result code from daughter activity
     * @param data - intent with extra data from daughter activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
            mQuestionsShown[mCurrentQuestionIndex] = mIsCheater;
        }
    }

    /**
     * Checks answer on current question
     * @param answer - answer pressed by user
     */
    private void checkAnswer(boolean answer){
        boolean answerIsTrue = mQuestions[mCurrentQuestionIndex].isAnswerTrue();
        int messageResId;
        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (answer == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }

    /**
     * Sets next question from array according index of current question
     */
    private void setNextQuestion(){
        mQuestionText.setText(mQuestions[++mCurrentQuestionIndex % mQuestions.length]
                .getTextResId());
        if(mCurrentQuestionIndex >= mQuestions.length){
            mCurrentQuestionIndex -= mQuestions.length;
        }
        mIsCheater = mQuestionsShown[mCurrentQuestionIndex];
    }

    /**
     * Sets previous question from array according index of current question
     */
    private void setPreviousQuestion(){
        if(mCurrentQuestionIndex == 0){
            mCurrentQuestionIndex = mQuestions.length;
        }
        mQuestionText.setText(mQuestions[--mCurrentQuestionIndex].getTextResId());
        mIsCheater = mQuestionsShown[mCurrentQuestionIndex];
    }

    // Methods of activity lifecycle

    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }
}
