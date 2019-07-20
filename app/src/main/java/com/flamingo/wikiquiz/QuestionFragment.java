package com.flamingo.wikiquiz;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.ColorFilter;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class QuestionFragment extends Fragment {


    private int questionCount = 0;
    private int selectedAnswer = -1;
    private int correctAnswer = 0;
    private int maxQuestions = 3;
    private int currentScore = 0;
    private boolean usedHint = false;
    private String SUBMIT_STRING = "Submit Answer";
    private String NEXT_STRING = "Next Question";
    private String LAST_STRING = "Done";

    private ImageView personImageView;
    private Button submitButton, hintButton, skipButton;
    private HashMap<Integer, Button> answerButtons;
    private TextView questionTextView;
    private Toast toast;
    private View progressBar;

    private QuestionViewModel questionViewModel;
    private List<Infobox> infoboxesList;


    public QuestionFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static QuestionFragment newInstance() {
        QuestionFragment fragment = new QuestionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("ShowToast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
                int total = 0;
                int firstRandom = -1;
                while (total != 2) {
                    int random = new Random().nextInt(4);
                    if (random == correctAnswer || random == firstRandom) {
                        continue;
                    }
                    firstRandom = random;
                    answerButtons.get(random).setEnabled(false);
                    total++;
                }
                hintButton.setEnabled(false);
                usedHint = true;
            }
        });

        submitButton = view.findViewById(R.id.submitButton);
        setupAnswerButtons();

        submitButton.setEnabled(false);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button) v).getText().equals(SUBMIT_STRING) && questionCount == maxQuestions - 1) {
                    submitAnswer();
                    submitButton.setText(NEXT_STRING);
                } else if (((Button) v).getText().equals(SUBMIT_STRING) && questionCount == maxQuestions) {
                    submitAnswer();
                    submitButton.setText(LAST_STRING);
                } else {
                    gotoNextQuestion();
                    submitButton.setText(SUBMIT_STRING);
                    submitButton.setEnabled(false);
                }
            }
        });
        questionTextView = view.findViewById(R.id.QuizQuestionTextView);

        questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);

        infoboxesList = questionViewModel.getAllInfoBoxes();

        gotoNextQuestion();

        return view;
    }

    public void enableAnswerButtons() {
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setEnabled(true);
        }
    }

    public void disableAnswerButtons() {
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setEnabled(false);
        }
    }

    public void submitAnswer() {
        String toastString;

        if (selectedAnswer == correctAnswer) {
            if (usedHint) {
                currentScore += 1;
            } else {
                currentScore += 2;
            }
            toastString = "Correct!";

        } else {
            toastString = "Incorrect.";
        }

        toast.setText(toastString);
        toast.show();

        disableAnswerButtons();
        hintButton.setEnabled(false);
        skipButton.setEnabled(false);
        for (Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            if (button.getKey() == correctAnswer) {
                button.getValue().getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
            } else {
                button.getValue().getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
            }
        }
    }

    public void gotoNextQuestion() {

        enableAnswerButtons();
        usedHint = false;
        hintButton.setEnabled(true);
        skipButton.setEnabled(true);
        if (questionCount < maxQuestions) {
            float percent = (float) questionCount / (float) maxQuestions;
            ((ConstraintLayout.LayoutParams) progressBar.getLayoutParams()).matchConstraintPercentWidth = percent;
            progressBar.requestLayout();
            for (int i = 0; i < answerButtons.size(); i++) {
                answerButtons.get(i).getBackground().clearColorFilter();
            }
            QuestionContent questionContent = questionViewModel.getQuestionContent();
            populateQuestion(questionContent);
            questionCount++;
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt("score", currentScore);
            NavHostFragment.findNavController(this).navigate(R.id.action_questionFragment_to_endQuizFragment, bundle);
        }
    }

    public void setupAnswerButtons() {
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

//        if (questionCount%2 == 0) {
//            qc.imagePath = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/N.Tesla.JPG/800px-N.Tesla.JPG";
//            qc.questionString = "What is this person's name?";
//            qc.answers = new ArrayList<>();
//            qc.answers.add("Harry Houdini");
//            qc.answers.add("Salvador Dali");
//            qc.answers.add("Charlie Chaplin");
//            qc.answers.add("Nikola Tesla");
//            qc.correctAnswer = 3;
//        }
//        else {
//            qc.imagePath = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Queen_Elizabeth_II_in_March_2015.jpg/800px-Queen_Elizabeth_II_in_March_2015.jpg";
//            qc.questionString = "What is this person's Date?";
//            qc.answers = new ArrayList<>();
//            qc.answers.add("Alice");
//            qc.answers.add("Bob");
//            qc.answers.add("Eve");
//            qc.answers.add("Mellisa");
//            qc.correctAnswer = 2;
//        } return qc;
//    }

    public void populateQuestion(QuestionContent questionContent) {
        Log.e("Question: ", "" + questionCount);

        Picasso.get()
                //.load(questionContent.imagePath)
                .load("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Queen_Elizabeth_II_in_March_2015.jpg/800px-Queen_Elizabeth_II_in_March_2015.jpg")
                .resize(200, 200)
                .centerCrop()
                .into(personImageView);

        questionTextView.setText(questionContent.questionString);
        correctAnswer = questionContent.correctAnswer;

        ArrayList<String> answers = questionContent.answers;
        if (answers != null) {
            for (int i = 0; i < answers.size(); i++) {
                answerButtons.get(i).setText(answers.get(i));
            }
        }
    }

}
