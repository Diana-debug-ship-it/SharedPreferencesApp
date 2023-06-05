package com.example.sharedpereferencesapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import java.util.Locale;

public class MainViewModel extends AndroidViewModel {

    private final int MIN = 5;
    private final int MAX = 30;

    private MutableLiveData<Long> timeLeft = new MutableLiveData<>();
    private MutableLiveData<Boolean> isFinished = new MutableLiveData<>();

    private int score = 0;
    private int rightAnswersScore = 0;

    private String generatedQuestion;
    private int rightAnswer;

    private int rightAnswerPosition;

    public LiveData<Long> getTimeLeft() {
        return timeLeft;
    }

    public LiveData<Boolean> getIsFinished() {
        return isFinished;
    }

    public String getGeneratedQuestion() {
        return generatedQuestion;
    }

    public int getRightAnswer() {
        return rightAnswer;
    }

    public int getRightAnswerPosition() {
        return rightAnswerPosition;
    }

    public int getScore() {
        return score;
    }

    public int getRightAnswersScore() {
        return rightAnswersScore;
    }

    public MainViewModel(@NonNull Application application) {
        super(application);

        isFinished.setValue(false);
    }

    public void startTimer() {
        CountDownTimer timer = new CountDownTimer(10_000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Long seconds = millisUntilFinished/1000;
                seconds++;
                timeLeft.setValue(seconds);
            }
            @Override
            public void onFinish() {
                isFinished.setValue(true);
                SharedPreferences preferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplication().getApplicationContext());
                int max = preferences.getInt("max", 0);
                if (rightAnswersScore >= max) {
                    preferences.edit().putInt("max", rightAnswersScore).apply();
                }
            }
        };
        timer.start();
    }

    public void generateQuestion() {
        int a = (int) (Math.random() * (MAX-MIN+1) + MIN);
        int b = (int) (Math.random() * (MAX-MIN+1) + MIN);
        int c = (int) (Math.random() * 2);

        if (c == 0) {
            generatedQuestion = String.format(Locale.getDefault(),"%d + %d", a, b);
            rightAnswer = a + b;
        } else {
            generatedQuestion = String.format(Locale.getDefault(),"%d - %d", a, b);
            rightAnswer = a - b;
        }

        rightAnswerPosition = (int)(Math.random()*4);
    }

    public int generateIncorrectAnswer() {
        int result;
        do {
            result = (int) (Math.random() * MAX*2+1) - (MAX-MIN);
        } while (result==rightAnswer);
        return result;
    }

    public void checkAnswer(int pos) {
        if (pos==rightAnswerPosition) rightAnswersScore++;
        score++;
    }

    public void startNewGame(){
        isFinished.setValue(false);
        startTimer();
        score = 0;
        rightAnswersScore = 0;
    }
}


