package com.combrainiton.models;

import java.util.ArrayList;

public class ObjectQuizResult implements java.io.Serializable {

    private int questionNo;
    private String questionText;
    private String correctOptionText;
    private String userOptionText;
    private boolean isUserAnswerCorrect;
    private ArrayList<ObjectQuizResult> allData;//todo ADD ADDER FOR LIST

    public ObjectQuizResult(){
        allData = new ArrayList<ObjectQuizResult>();
    }

    public int getQuestionNo() {
        return questionNo;
    }

    public void setQuestionNo(int questionNo) {
        this.questionNo = questionNo;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getCorrectOptionText() {
        return correctOptionText;
    }

    public void setCorrectOptionText(String correctOptionText) {
        this.correctOptionText = correctOptionText;
    }

    public String getUserOptionText() {
        return userOptionText;
    }

    public void setUserOptionText(String userOptionText) {
        this.userOptionText = userOptionText;
    }

    public boolean isUserAnswerCorrect() {
        return isUserAnswerCorrect;
    }

    public void setUserAnswerCorrect(boolean userAnswerCorrect) {
        isUserAnswerCorrect = userAnswerCorrect;
    }

    public void addData(ObjectQuizResult objectQuizResult){
        this.allData.add(objectQuizResult);
    }

    public ArrayList<ObjectQuizResult> getData(){
        return this.allData;
    }

}
