package com.flamingo.wikiquiz;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class QuestionFragment extends Fragment {


    private static final int CORRECT_ANSWER_POINTS_VALUE = 100;
    private static final String SUBMIT_STRING = "Submit Answer";
    private static final String NEXT_STRING = "Next Question";
    private static final String LAST_STRING = "Done";

    private static int NUM_TOTAL_QUESTIONS = 7;
    private static boolean IS_BLUETOOTH_SESSION;
    private static boolean IS_CLIENT;

    private int currentQuestionCount = 0;
    private int selectedAnswer = -1;
    private int correctAnswer = 0;
    private int currentScore = 0;
    private boolean usedHint = false;

    private ImageView personImageView;
    private Button submitButton, hintButton, skipButton;
    private HashMap<Integer, Button> answerButtons;
    private TextView questionTextView, questionCountView, scoreCountView;
    private View progressBar;

    private Toast toast;

    private QuestionViewModel questionViewModel;
    private List<Infobox> infoboxesList;

    private List<Boolean> bluetoothCorrectLog;
    private List<Timestamp> bluetoothTimestampLog;
    private List<Boolean> bluetoothHintLog;


    public QuestionFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ShowToast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        IS_BLUETOOTH_SESSION = getArguments().getBoolean("isBluetooth");
        IS_CLIENT = false;
        if (IS_BLUETOOTH_SESSION) {
            IS_CLIENT = getArguments().getBoolean("isClient");
        }

        questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);

        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        personImageView = view.findViewById(R.id.personImageView);
        progressBar = view.findViewById(R.id.quizProgressBar);

        answerButtons = new HashMap<Integer, Button>();
        answerButtons.put(0, (Button) view.findViewById(R.id.answerButton1));
        answerButtons.put(1, (Button) view.findViewById(R.id.answerButton2));
        answerButtons.put(2, (Button) view.findViewById(R.id.answerButton3));
        answerButtons.put(3, (Button) view.findViewById(R.id.answerButton4));

        skipButton = view.findViewById(R.id.skipButton);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoNextQuestion();
            }
        });

        hintButton = view.findViewById(R.id.hintButton);
        hintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyHint();
            }
        });

        submitButton = view.findViewById(R.id.submitButton);
        setupAnswerButtons();

        submitButton.setEnabled(false); // disabled before an answer has been selected
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button) v).getText().equals(SUBMIT_STRING)
                        && currentQuestionCount < NUM_TOTAL_QUESTIONS) {
                    submitAnswer();
                    submitButton.setText(NEXT_STRING);
                } else if (((Button) v).getText().equals(SUBMIT_STRING)
                        && currentQuestionCount == NUM_TOTAL_QUESTIONS) {
                    submitAnswer();
                    submitButton.setText(LAST_STRING);
                } else {
                    gotoNextQuestion();
                    submitButton.setText(SUBMIT_STRING);
                }
            }
        });
        questionTextView = view.findViewById(R.id.QuizQuestionTextView);
        questionCountView = view.findViewById(R.id.questionCountView);
        scoreCountView = view.findViewById(R.id.scoreCountView);

        infoboxesList = questionViewModel.getAllInfoBoxes();

        if (IS_CLIENT) {
            List<QuestionContent> hostQCs = new ArrayList<>();
            // TODO hostQCs assign here
            questionViewModel.setAllPreloadedQCs(hostQCs);
        } else {
            questionViewModel.generatePreloadedQCs(NUM_TOTAL_QUESTIONS);

        }

        bluetoothCorrectLog = new ArrayList<>();
        bluetoothTimestampLog = new ArrayList<>();
        bluetoothHintLog = new ArrayList<>();

        gotoNextQuestion();

        return view;
    }

    private void enableAnswerButtons() {
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setEnabled(true);
        }
    }

    private void disableAnswerButtons() {
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setEnabled(false);
        }
    }

    private void submitAnswer() {
        String toastString;
        Timestamp timestamp = makeTimestamp();
        bluetoothTimestampLog.add(timestamp);
        if (selectedAnswer == correctAnswer) {
            if (usedHint) {
                currentScore += CORRECT_ANSWER_POINTS_VALUE / 2;
                bluetoothHintLog.add(true);
            } else {
                currentScore += CORRECT_ANSWER_POINTS_VALUE;
                bluetoothHintLog.add(false);
            }
            scoreCountView.setText("Score: " + currentScore);
            toastString = "Correct!";
            bluetoothCorrectLog.add(true);
        } else {
            toastString = "Incorrect.";
            bluetoothCorrectLog.add(false);
        }
        toast.setText(toastString);
        toast.show();

        disableAnswerButtons();
        hintButton.setEnabled(false);
        skipButton.setEnabled(false);
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            if (button.getKey() == correctAnswer) {
                button.getValue().getBackground()
                        .setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            } else {
                button.getValue().getBackground()
                        .setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    private void clearSelection() {
        selectedAnswer = -1;
        submitButton.setEnabled(false);
        for (int i = 0; i < answerButtons.size(); i++) {
            answerButtons.get(i).getBackground().clearColorFilter();
        }
    }

    private void gotoNextQuestion() {
        enableAnswerButtons();
        usedHint = false;
        hintButton.setEnabled(true);
        skipButton.setEnabled(true);
        if (currentQuestionCount < NUM_TOTAL_QUESTIONS) {

            advanceProgressBar();

            clearSelection();
            //QuestionContent questionContent = questionViewModel.generateQuestionContent();
            QuestionContent questionContent =
                    questionViewModel.getPreloadedAtIndex(currentQuestionCount);
            populateQuestion(questionContent);
            currentQuestionCount++;
        } else {
            handleEndOfQuiz();
        }
    }

    private void advanceProgressBar() {
        float percent = (float) (currentQuestionCount) / ((float) NUM_TOTAL_QUESTIONS);
        if (percent == 1.0) {
            ((ConstraintLayout.LayoutParams) progressBar.
                    getLayoutParams()).matchConstraintPercentWidth = (float) (0.9999999);
        } else {
            ((ConstraintLayout.LayoutParams) progressBar
                    .getLayoutParams()).matchConstraintPercentWidth = percent;
        }
        progressBar.requestLayout();
    }

    private void applyHint() {
        int total = 0;
        int firstRandom = -1;
        boolean clear = false;
        while (total != 2) {
            int random = new Random().nextInt(4);
            if (random == correctAnswer || random == firstRandom) {
                continue;
            }
            firstRandom = random;
            if (random == selectedAnswer) {
                clear = true;
            }
            answerButtons.get(random).setEnabled(false);
            total++;
        }
        if (clear) {
            clearSelection();
        }
        hintButton.setEnabled(false);
        usedHint = true;
    }

    private void setupAnswerButtons() {
        for (final Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    submitButton.setEnabled(true);
                    for (final Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
                        button.getValue().getBackground().clearColorFilter();
                    }
                    v.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                    selectedAnswer = button.getKey();
                }
            });
        }
    }

    private void populateQuestion(QuestionContent questionContent) {
        Log.e("Question: ", "" + currentQuestionCount);

        if (questionContent != null && questionContent.imageBlob != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(questionContent.imageBlob);
            Bitmap bp = BitmapFactory.decodeStream(bis); //decode stream to a bitmap image
            personImageView.setImageBitmap(bp);

            questionTextView.setText(questionContent.questionString);
            questionCountView.setText("Question " + (currentQuestionCount + 1) + " of " + NUM_TOTAL_QUESTIONS + "  |");
            correctAnswer = questionContent.correctAnswer;

            ArrayList<String> answers = questionContent.answers;
            if (answers != null) {
                for (int i = 0; i < answers.size(); i++) {
                    answerButtons.get(i).setText(answers.get(i));
                }
            }
        }
    }

    private void handleEndOfQuiz() {
        if (!IS_BLUETOOTH_SESSION) {
            moveToEndQuizFragment();
        } else if (IS_CLIENT) {
            // TODO
        } else {
            // TODO
        }
    }

    private void moveToEndQuizFragment() {
        Bundle bundle = new Bundle();
        bundle.putInt("score", currentScore);
        bundle.putInt("numQuestions", NUM_TOTAL_QUESTIONS);
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_questionFragment_to_endQuizFragment, bundle);
    }

    private void moveToMultiplayerDoneFragment() {

    }

    private Timestamp makeTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


}
