package com.flamingo.wikiquiz;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;


public class QuestionFragment extends Fragment {


    int questionCount = 0;
    int selectedAnswer = 0;

    ImageView personImageView;
    Button answerButton1, answerButton2, answerButton3, answerButton4, submitButton, hintButton;
    TextView questionTextView;
    Toast toast;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        toast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        personImageView = view.findViewById(R.id.personImageView);

        answerButton1 = view.findViewById(R.id.answerButton1);
        answerButton2 = view.findViewById(R.id.answerButton2);
        answerButton3 = view.findViewById(R.id.answerButton3);
        answerButton4 = view.findViewById(R.id.answerButton4);

        hintButton = view.findViewById(R.id.hintButton);

        submitButton = view.findViewById(R.id.submitButton);
        questionTextView = view.findViewById(R.id.QuizQuestionTextView);

        answerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerButton2.getBackground().clearColorFilter();
                answerButton3.getBackground().clearColorFilter();
                answerButton4.getBackground().clearColorFilter();

                answerButton1.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                selectedAnswer = 1;
            }
        });

        answerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerButton1.getBackground().clearColorFilter();
                answerButton3.getBackground().clearColorFilter();
                answerButton4.getBackground().clearColorFilter();

                answerButton2.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                selectedAnswer = 2;
            }
        });

        answerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerButton1.getBackground().clearColorFilter();
                answerButton2.getBackground().clearColorFilter();
                answerButton4.getBackground().clearColorFilter();

                answerButton3.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                selectedAnswer = 3;
            }
        });

        answerButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                answerButton1.getBackground().clearColorFilter();
                answerButton2.getBackground().clearColorFilter();
                answerButton3.getBackground().clearColorFilter();

                answerButton4.getBackground().setColorFilter(Color.MAGENTA, PorterDuff.Mode.MULTIPLY);
                selectedAnswer = 4;
            }
        });


        nextQuestion();

        return view;
    }

    public void nextQuestion() {

        questionCount++;

        if (questionCount == 1) {
            Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/d/d4/N.Tesla.JPG/800px-N.Tesla.JPG")
                    .resize(200, 200)
                    .centerCrop()
                    .into(personImageView);

            questionTextView.setText("What is this person's name?");

            answerButton1.setText("Harry Houdini");
            answerButton2.setText("Salvador Dali");
            answerButton3.setText("Charlie Chaplin");
            answerButton4.setText("Nikola Tesla");

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedAnswer > 0) {

                        answerButton1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton2.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton3.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton4.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);

                        String toastString;

                        if (selectedAnswer == 4) {
                            toastString = "Correct!";

                        } else {
                            toastString = "Incorrect.";
                        }

                        toast.setText(toastString);
                        toast.show();

                        selectedAnswer = -1;
                        submitButton.setText("Next Question");

                    } else if (selectedAnswer == -1) {

                        toast.cancel();
                        answerButton1.getBackground().clearColorFilter();
                        answerButton2.getBackground().clearColorFilter();
                        answerButton3.getBackground().clearColorFilter();
                        answerButton4.getBackground().clearColorFilter();

                        selectedAnswer = 0;
                        submitButton.setText("Submit Answer");
                        nextQuestion();
                    }
                }
            });
        } else if (questionCount == 2) {

            Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/b/b6/Queen_Elizabeth_II_in_March_2015.jpg/800px-Queen_Elizabeth_II_in_March_2015.jpg")
                    .resize(200, 200)
                    .centerCrop()
                    .into(personImageView);

            questionTextView.setText("In what year was this person born?");

            answerButton1.setText("1933");
            answerButton2.setText("1926");
            answerButton3.setText("1921");
            answerButton4.setText("1918");

            hintButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    answerButton1.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);
                    answerButton3.getBackground().setColorFilter(Color.DKGRAY, PorterDuff.Mode.MULTIPLY);

                }
            });

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedAnswer > 0) {

                        answerButton1.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton2.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        answerButton3.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton4.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

                        String toastString;

                        if (selectedAnswer == 2) {
                            toastString = "Correct!";

                        } else {
                            toastString = "Incorrect.";
                        }

                        toast.setText(toastString);
                        toast.show();

                        selectedAnswer = -1;
                        submitButton.setText("Next Question");

                    } else if (selectedAnswer == -1) {

                        toast.cancel();
                        answerButton1.getBackground().clearColorFilter();
                        answerButton2.getBackground().clearColorFilter();
                        answerButton3.getBackground().clearColorFilter();
                        answerButton4.getBackground().clearColorFilter();

                        selectedAnswer = 0;
                        submitButton.setText("Submit Answer");
                        nextQuestion();
                    }
                }
            });
        } else {

            Picasso.get()
                    .load("https://upload.wikimedia.org/wikipedia/commons/thumb/9/94/Robert_Downey_Jr_2014_Comic_Con_%28cropped%29.jpg/800px-Robert_Downey_Jr_2014_Comic_Con_%28cropped%29.jpg")
                    .resize(200, 200)
                    .centerCrop()
                    .into(personImageView);

            questionTextView.setText("In what city was this person born?");

            answerButton1.setText("New York City");
            answerButton2.setText("Seattle");
            answerButton3.setText("Montreal");
            answerButton4.setText("Boston");

            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (selectedAnswer > 0) {

                        answerButton1.getBackground().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                        answerButton2.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton3.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                        answerButton4.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);

                        String toastString;

                        if (selectedAnswer == 1) {
                            toastString = "Correct!";

                        } else {
                            toastString = "Incorrect.";
                        }

                        toast.setText(toastString);
                        toast.show();

                        selectedAnswer = -1;
                        submitButton.setText("Next Question");

                    } else if (selectedAnswer == -1) {

                        toast.cancel();
                        answerButton1.getBackground().clearColorFilter();
                        answerButton2.getBackground().clearColorFilter();
                        answerButton3.getBackground().clearColorFilter();
                        answerButton4.getBackground().clearColorFilter();

                        selectedAnswer = 0;
                        submitButton.setText("Submit Answer");
                        nextQuestion();
                    }
                }
            });


        }
    }


}
