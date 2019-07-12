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

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class QuestionFragment extends Fragment {


    private int questionCount = 0;
    private int selectedAnswer = 0;
    private int correctAnswer = 0;
    private int maxQuestions = 3;
    private String SUBMIT_STRING = "Submit Answer";
    private String NEXT_STRING = "Next Question";

    private ImageView personImageView;
    private Button submitButton, hintButton, skipButton;
    private HashMap<Integer, Button> answerButtons;
    private TextView questionTextView;
    private Toast toast;


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

        answerButtons = new HashMap<Integer, Button>();
        answerButtons.put(0, (Button)view.findViewById(R.id.answerButton1));
        answerButtons.put(1, (Button)view.findViewById(R.id.answerButton2));
        answerButtons.put(2, (Button)view.findViewById(R.id.answerButton3));
        answerButtons.put(3, (Button)view.findViewById(R.id.answerButton4));

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
            }
        });

        submitButton = view.findViewById(R.id.submitButton);
        setupAnswerButtons();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((Button)v).getText().equals(SUBMIT_STRING)) {
                    submitAnswer();
                    submitButton.setText(NEXT_STRING);
                } else {
                    gotoNextQuestion();
                    submitButton.setText(SUBMIT_STRING);
                }
            }
        });
        questionTextView = view.findViewById(R.id.QuizQuestionTextView);
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
        hintButton.setEnabled(true);
        skipButton.setEnabled(true);
        if (questionCount < maxQuestions) {
            for (int i = 0; i < answerButtons.size(); i++) {
                answerButtons.get(i).getBackground().clearColorFilter();
            }
            QuestionContent questionContent = getQuestionContent();
            populateQuestion(questionContent);
            questionCount++;
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.action_questionFragment_to_endQuizFragment);
        }

    }

    public void setupAnswerButtons() {
        for (final Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
            button.getValue().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (final Map.Entry<Integer, Button> button : answerButtons.entrySet()) {
                        button.getValue().getBackground().clearColorFilter();
                    }

                    v.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                    selectedAnswer = button.getKey();
                }
            });
        }
    }

    public QuestionContent getQuestionContent() {
        QuestionContent qc = new QuestionContent();
        if (questionCount%2 == 0) {
            qc.imagePath = "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/N.Tesla.JPG/800px-N.Tesla.JPG";
            qc.questionString = "What is this person's name?";
            qc.answers = new ArrayList<>();
            qc.answers.add("Harry Houdini");
            qc.answers.add("Salvador Dali");
            qc.answers.add("Charlie Chaplin");
            qc.answers.add("Nikola Tesla");
            qc.correctAnswer = 3;
        }
        else {
            qc.imagePath = "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Queen_Elizabeth_II_in_March_2015.jpg/800px-Queen_Elizabeth_II_in_March_2015.jpg";
            qc.questionString = "What is this person's Date?";
            qc.answers = new ArrayList<>();
            qc.answers.add("Alice");
            qc.answers.add("Bob");
            qc.answers.add("Eve");
            qc.answers.add("Mellisa");
            qc.correctAnswer = 2;
        }
        return qc;
    }

    public void populateQuestion(QuestionContent questionContent) {
        Log.e("Question: ", "" + questionCount);

        Picasso.get()
                .load(questionContent.imagePath)
                .resize(200, 200)
                .centerCrop()
                .into(personImageView);

        questionTextView.setText(questionContent.questionString);
        correctAnswer = questionContent.correctAnswer;

        ArrayList<String> answers = questionContent.answers;
        for (int i = 0; i < answers.size(); i++) {
            answerButtons.get(i).setText(answers.get(i));
        }
    }


}
