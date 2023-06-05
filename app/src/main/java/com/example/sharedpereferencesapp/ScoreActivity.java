package com.example.sharedpereferencesapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class ScoreActivity extends AppCompatActivity {

    private static final String EXTRA_SCORE = "score";
    private static final String EXTRA_RIGHT_ANSWERS = "rightAnswersScore";

    private TextView textViewScore;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        int max = preferences.getInt("max", 0);

        textViewScore = findViewById(R.id.textViewResult);
        button = findViewById(R.id.button);
        Intent intent = getIntent();
        textViewScore.setText(String.format(
                Locale.getDefault(),
                "Вы ответили правильно на %d из %d\nМаксимальный результат: %d",
                intent.getIntExtra(EXTRA_RIGHT_ANSWERS, 0), intent.getIntExtra(EXTRA_SCORE, 0), max));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.newIntent(ScoreActivity.this, true));
            }
        });
    }

    public static Intent newIntent(Context context, int score, int rightAnswersScore) {
        Intent intent = new Intent(context, ScoreActivity.class);
        intent.putExtra(EXTRA_SCORE, score);
        intent.putExtra(EXTRA_RIGHT_ANSWERS, rightAnswersScore);
        return intent;
    }
}