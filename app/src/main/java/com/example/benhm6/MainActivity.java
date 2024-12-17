package com.example.benhm6;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView qustionTxt, scoreText;
    Button settingBtn;
    LinearLayout dynamicButtonContainer;
    int score = 0; // Keep track of the score
    int correctAnswerIndex;
    int numberOfButtons = 4; // Default to 4 buttons
    String correctAnswer; // Store the correct answer for the current question

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        qustionTxt = findViewById(R.id.qustionTxt);
        settingBtn = findViewById(R.id.settings);
        dynamicButtonContainer = findViewById(R.id.dynamicButtonContainer);
        scoreText = findViewById(R.id.scoreText);

        // Initial setup
        displayRandomMathQuestion();

        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMultiChoiceDialog();
            }
        });
    }

    private void displayRandomMathQuestion() {
        // Generate a new math question and correct answer
        MathQuestion question = generateMathQuestion();
        correctAnswer = question.correctAnswer;

        // Display the question
        qustionTxt.setText(question.questionText);

        // Set answers for dynamic buttons
        setAnswersForDynamicButtons(numberOfButtons);
    }

    private void setAnswersForDynamicButtons(int buttonCount) {
        Random random = new Random();
        correctAnswerIndex = random.nextInt(buttonCount); // Randomly pick the correct answer index

        // Prepare answers, ensuring the correct one is placed at the correctAnswerIndex
        String[] randomAnswers = new String[buttonCount];
        randomAnswers[correctAnswerIndex] = correctAnswer;

        // Fill remaining buttons with unique incorrect answers
        for (int i = 0; i < buttonCount; i++) {
            if (i != correctAnswerIndex) {
                String incorrectAnswer;
                do {
                    incorrectAnswer = generateRandomIncorrectAnswer(correctAnswer);
                } while (contains(randomAnswers, incorrectAnswer));
                randomAnswers[i] = incorrectAnswer;
            }
        }

        // Add dynamic buttons with the prepared answers
        addDynamicButtons(randomAnswers);
    }

    private void addDynamicButtons(String[] answers) {
        // Clear any existing buttons from the container
        dynamicButtonContainer.removeAllViews();

        // Add buttons dynamically
        for (int i = 0; i < answers.length; i++) {
            Button newButton = new Button(this);
            newButton.setText(answers[i]);
            final int index = i;
            newButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleButtonClick(index);
                }
            });
            dynamicButtonContainer.addView(newButton);
        }
    }

    private void handleButtonClick(int selectedAnswerIndex) {
        if (selectedAnswerIndex == correctAnswerIndex) {
            // Correct answer selected
            score++;
            scoreText.setText("Score: " + score);
            Toast.makeText(MainActivity.this, "Correct! Here's a new question.", Toast.LENGTH_SHORT).show();
            displayRandomMathQuestion();
        } else {
            // Incorrect answer
            Toast.makeText(MainActivity.this, "Incorrect. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private MathQuestion generateMathQuestion() {
        Random random = new Random();
        int num1 = random.nextInt(10) + 1; // Base (1 to 10 for simplicity)
        int num2 = random.nextInt(4) + 1;  // Exponent (1 to 4 for simplicity)
        int operator = random.nextInt(5); // Add a fifth operator for power
        String questionText = "";
        String correctAnswer = "";

        switch (operator) {
            case 0: // Addition
                questionText = num1 + " + " + num2;
                correctAnswer = String.valueOf(num1 + num2);
                break;
            case 1: // Subtraction
                questionText = num1 + " - " + num2;
                correctAnswer = String.valueOf(num1 - num2);
                break;
            case 2: // Multiplication
                questionText = num1 + " * " + num2;
                correctAnswer = String.valueOf(num1 * num2);
                break;
            case 3: // Division
                while (num2 == 0) num2 = random.nextInt(30) + 1; // Prevent division by zero
                questionText = num1 + " / " + num2;
                correctAnswer = String.valueOf(num1 / num2); // Integer division
                break;
            case 4: // Power
                questionText = num1 + " ^ " + num2;
                correctAnswer = String.valueOf((int) Math.pow(num1, num2)); // Compute power
                break;
        }

        return new MathQuestion(questionText, correctAnswer);
    }

    private String generateRandomIncorrectAnswer(String correctAnswer) {
        Random random = new Random();
        int incorrectAnswer;

        do {
            incorrectAnswer = random.nextInt(60) - 30; // Random number between -30 and 30
        } while (String.valueOf(incorrectAnswer).equals(correctAnswer));

        return String.valueOf(incorrectAnswer);
    }

    private boolean contains(String[] array, String value) {
        for (String item : array) {
            if (value.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private void showMultiChoiceDialog() {
        final String[] options = {"4 Buttons", "5 Buttons", "6 Buttons"};

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Number of Buttons")
                .setSingleChoiceItems(options, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) numberOfButtons = 4;
                        else if (which == 1) numberOfButtons = 5;
                        else if (which == 2) numberOfButtons = 6;

                        displayRandomMathQuestion(); // Update question with new button count
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true);

        builder.create().show();
    }

    // Helper class for math question
    private class MathQuestion {
        String questionText;
        String correctAnswer;

        MathQuestion(String questionText, String correctAnswer) {
            this.questionText = questionText;
            this.correctAnswer = correctAnswer;
        }
    }
}