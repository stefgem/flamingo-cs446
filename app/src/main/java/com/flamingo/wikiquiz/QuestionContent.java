package com.flamingo.wikiquiz;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class
QuestionContent {
    public byte[] imageBlob;
    public String questionString;
    public ArrayList<String> answers;
    public int correctAnswer;
    public ArrayList<Infobox> infoboxes;

    public QuestionContent() {
        // Empty constructor
    }
    public QuestionContent(ArrayList<ArrayList<byte[]>> questionContentArrayList) {
        // Inner array only has one element so second get with index 0
        imageBlob = questionContentArrayList.get(0).get(0);

        // Inner array only has one element so second get with index 0
        questionString = Arrays.toString(questionContentArrayList.get(1).get(0));

        answers = new ArrayList<String>();
        for (byte[] answer : questionContentArrayList.get(2)) {
            answers.add(Arrays.toString(answer));
        }

        // Inner array only has one element so second get with index 0
        correctAnswer = ByteBuffer.wrap(questionContentArrayList.get(3).get(0)).getInt();
    }

    public ArrayList<ArrayList<byte[]>> getContentByteArray() {
        // Create and ArrayList of ArrayList of byte[] in order to return an array of all matching
        //  types. This was needed because answers is already an ArrayList
        ArrayList<ArrayList<byte[]>> response = new ArrayList<ArrayList<byte[]>>();

        ArrayList<byte[]> imageBlobArray = new ArrayList<byte[]>();
        imageBlobArray.add(imageBlob);
        response.add(imageBlobArray);

        ArrayList<byte[]> questionStringByteArray = new ArrayList<byte[]>();
        questionStringByteArray.add(questionString.getBytes());
        response.add(questionStringByteArray);

        ArrayList<byte[]> answersByteArray = new ArrayList<byte[]>();
        for (String answer : answers) {
            answersByteArray.add(answer.getBytes());
        }
        response.add(answersByteArray);

        ArrayList<byte[]> correctAnswerByteArray = new ArrayList<byte[]>();
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(correctAnswer);
        correctAnswerByteArray.add(bb.array());
        response.add(correctAnswerByteArray);

        return response;
    }

    public void setImageBlob(byte[] imageBytes) {
        imageBlob = imageBytes;
    }

    public void setQuestionString(byte[] questionBytes, int length) {
        questionString = new String(questionBytes, 0, length);
    }

    public void setQuestionString(int i) {
        if (i == 0) {
            questionString = "What is this person's name?";
        }
        else {
            questionString = "In what year was this person born?";
        }
    }

    public void setCorrectAnswer(byte[] correctAnswerBytes) {
        correctAnswer = ByteBuffer.wrap(correctAnswerBytes).getInt();
    }

    public void setCorrectAnswer(int i) {
        correctAnswer = i;
    }
}
