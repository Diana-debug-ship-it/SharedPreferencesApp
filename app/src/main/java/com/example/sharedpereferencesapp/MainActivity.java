package com.example.sharedpereferencesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView textViewTimer;

    private static final String EXTRA_IS_NEW_GAME = "isNewGame";
    private TextView textViewQuestion;
    private TextView textViewScore;

    private TextView textViewOption1;
    private TextView textViewOption2;
    private TextView textViewOption3;
    private TextView textViewOption4;

    private List<TextView> options = new ArrayList<>();

    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        sharedPreferences.edit().putInt("test", 5).apply();
//        int test = sharedPreferences.getInt("test", 0);
//        Toast.makeText(this, "" +test, Toast.LENGTH_SHORT).show();

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        initViews();
        setScore();
        generateProblem();
        viewModel.startTimer();

        Intent intent = getIntent();
        if (intent.getBooleanExtra(EXTRA_IS_NEW_GAME, false)) {
            viewModel.startNewGame();
        }

        viewModel.getTimeLeft().observe(this, new Observer<Long>() {
            @Override
            public void onChanged(Long time) {
                textViewTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", time/60, time%60));
            }
        });

        viewModel.getIsFinished().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isFinished) {
                if (isFinished) {
                    textViewTimer.setText("Время вышло");
                    startActivity(ScoreActivity.newIntent(MainActivity.this, viewModel.getScore(), viewModel.getRightAnswersScore()));
                }
                else {
                    onClickAnswer();
                }
            }
        });

    }

    private void onClickAnswer() {
        for (int i=0; i<options.size(); i++) {
            int f = i;
            options.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (f==viewModel.getRightAnswerPosition()){
                        Toast.makeText(MainActivity.this, "Правильно!", Toast.LENGTH_SHORT).show();
                        viewModel.checkAnswer(f);
                    } else {
                        Toast.makeText(MainActivity.this, "Неправильно!", Toast.LENGTH_SHORT).show();
                        viewModel.checkAnswer(f);
                    }
                    setScore();
                    generateProblem();
                }
            });
        }
    }

    private void initViews(){
        textViewOption1 = findViewById(R.id.textViewAnswer1);
        textViewOption2 = findViewById(R.id.textViewAnswer2);
        textViewOption3 = findViewById(R.id.textViewAnswer3);
        textViewOption4 = findViewById(R.id.textViewAnswer4);
        options.add(textViewOption1);
        options.add(textViewOption2);
        options.add(textViewOption3);
        options.add(textViewOption4);

        textViewQuestion = findViewById(R.id.textViewQuestion);
        textViewScore = findViewById(R.id.textViewScore);
        textViewTimer = findViewById(R.id.textViewTimer);
    }

    private void generateProblem(){
        viewModel.generateQuestion();
        textViewQuestion.setText(viewModel.getGeneratedQuestion());
        int pos = viewModel.getRightAnswerPosition();
        for (int i=0; i<options.size(); i++) {
            if (i==pos) {
                options.get(pos).setText(String.valueOf(viewModel.getRightAnswer()));
            } else {
                options.get(i).setText(String.valueOf(viewModel.generateIncorrectAnswer()));
            }
        }
    }

    private void setScore(){
        textViewScore.setText(String.format(Locale.ENGLISH,"%d/%d", viewModel.getScore(), viewModel.getRightAnswersScore()));
    }

    public static Intent newIntent(Context context, boolean b) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXTRA_IS_NEW_GAME, b);
        return intent;
    }
}