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
    private int selectedAnswer = 0;
    private int correctAnswer = 0;
    private int maxQuestions = 3;
    private int currentScore = 0;
    private int chooseQuestion = 0;
    private int numberOfQuestionTypes = 2;
    private int infoboxSize = 0;
    private boolean usedHint = false;
    private String SUBMIT_STRING = "Submit Answer";
    private String NEXT_STRING = "Next Question";

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
       // infoboxesList = new ArrayList<>();

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
                usedHint = true;
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

        questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);

        infoboxesList = questionViewModel.getAllInfoBoxes();
        infoboxSize = infoboxesList.size();

        gotoNextQuestion();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        TODO: This part isn't working to fetch the database
        infoboxesList = new ArrayList<>();

        /*questionViewModel = ViewModelProviders.of(getActivity()).get(QuestionViewModel.class);

        infoboxesList = questionViewModel.getAllInfoBoxes();
        infoboxSize = infoboxesList.size();*/
//        questionViewModel
//                .getAllInfoBoxes()
//                .observe(getViewLifecycleOwner(), new Observer<List<Infobox>>() {
//                    @Override
//                    public void onChanged(@Nullable final List<Infobox> infoboxes) {
//                        Log.e("This part with database", "error");
//                        infoboxesList.addAll(infoboxes);
//                        infoboxSize = infoboxesList.size();
//                        gotoNextQuestion(); // TODO tempo
//                    }
//                });
//
//        // gotoNextQuestion();
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
//        while(infoboxesList.isEmpty()) {
//
//        }
        enableAnswerButtons();
        usedHint = false;
        hintButton.setEnabled(true);
        skipButton.setEnabled(true);
        if (questionCount < maxQuestions) {
            float percent = (float) questionCount / (float) maxQuestions;
            ((ConstraintLayout.LayoutParams)progressBar.getLayoutParams()).matchConstraintPercentWidth = percent;
            progressBar.requestLayout();
            for (int i = 0; i < answerButtons.size(); i++) {
                answerButtons.get(i).getBackground().clearColorFilter();
            }
            QuestionContent questionContent = getQuestionContent();
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
        int chooseQuestion = new Random().nextInt(numberOfQuestionTypes);
        chooseQuestion = 1;
        if (infoboxSize != 0) {
            if (chooseQuestion == 0) {
                int questionInfoboxIndex = new Random().nextInt(infoboxSize);
                Infobox questionInfobox = infoboxesList.get(questionInfoboxIndex);
                qc.imagePath = "";
                qc.questionString = "What is this person's name?";
                qc.answers = new ArrayList<String>(Collections.nCopies(4, ""));
                qc.correctAnswer = new Random().nextInt(4);
                qc.answers.set(qc.correctAnswer, questionInfobox.getName());
                ArrayList<Integer> otherAnswers = new ArrayList<>();
                otherAnswers.add(questionInfoboxIndex);
                for (int i = 0; i < 4; i++) {
                    if (i == qc.correctAnswer) {
                        continue;
                    }
                    while (qc.answers.get(i).equals("")) {
                        int otherAnswerIndex = new Random().nextInt(infoboxSize);
                        if (otherAnswers.contains(otherAnswerIndex)) {
                            continue;
                        }
                        otherAnswers.add(otherAnswerIndex);
                        qc.answers.set(i, infoboxesList.get(otherAnswerIndex).getName());
                    }
                }
            } else {
                int questionInfoboxIndex = new Random().nextInt(infoboxSize);
                Infobox questionInfobox = infoboxesList.get(questionInfoboxIndex);
                qc.imagePath = "";
                qc.questionString = "What year was this person born?";
                qc.answers = new ArrayList<String>(Collections.nCopies(4, ""));
                qc.correctAnswer = new Random().nextInt(4);
                qc.answers.set(qc.correctAnswer, Integer.toString(questionInfobox.getBirthYear()));
                ArrayList<String> otherAnswers = new ArrayList<>();
                otherAnswers.add(Integer.toString(questionInfobox.getBirthYear()));
                for (int i = 0; i < 4; i++) {
                    if (i == qc.correctAnswer) {
                        continue;
                    }
                    while (qc.answers.get(i).equals("")) {
                        int otherAnswerIndex = new Random().nextInt(infoboxSize);
                        Infobox otherInfobox = infoboxesList.get(otherAnswerIndex);
                        if (otherAnswers.contains(Integer.toString(otherInfobox.getBirthYear()))) {
                            continue;
                        }
                        otherAnswers.add(Integer.toString(otherInfobox.getBirthYear()));
                        qc.answers.set(i, Integer.toString(otherInfobox.getBirthYear()));
                    }
                }
            }
        }
        return qc;
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
//        }
//        return qc;
    }

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
        if (answers != null ) {
            for (int i = 0; i < answers.size(); i++) {
                answerButtons.get(i).setText(answers.get(i));
            }
        }
    }


}
