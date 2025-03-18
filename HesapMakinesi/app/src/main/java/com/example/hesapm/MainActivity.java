package com.example.hesapm;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private String currentInput = "";
    private String lastOperator = "";
    private double firstValue = 0;
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = findViewById(R.id.tvResult);
        setButtonListeners();
    }

    private void setButtonListeners() {
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btnDot, R.id.btnPlus, R.id.btnMinus, R.id.btnMultiply,
                R.id.btnDivide, R.id.btnEqual, R.id.btnClear,
                R.id.btnPower, R.id.btnFactorial, R.id.btnSqrt
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this::onButtonClick);
        }
    }

    private void onButtonClick(View view) {
        Button clickedButton = (Button) view;
        String buttonText = clickedButton.getText().toString();

        switch (buttonText) {
            case "C":
                currentInput = "";
                firstValue = 0;
                lastOperator = "";
                isNewOperation = true;
                break;
            case "=":
                calculateResult();
                lastOperator = "";
                isNewOperation = true;
                break;
            case "+":
            case "-":
            case "*":
            case "/":
            case "x^y":
                if (!currentInput.isEmpty()) {
                    firstValue = Double.parseDouble(currentInput);
                    lastOperator = buttonText;
                    currentInput = "";
                }
                break;
            case "n!":
                int num = Integer.parseInt(currentInput);
                currentInput = String.valueOf(factorial(num));
                break;
            case "√":
                currentInput = String.valueOf(Math.sqrt(Double.parseDouble(currentInput)));
                break;
            default:
                if (isNewOperation) {
                    currentInput = "";
                    isNewOperation = false;
                }
                currentInput += buttonText;
                break;
        }

        tvResult.setText(currentInput);
    }

    private void calculateResult() {
        double secondValue = Double.parseDouble(currentInput);
        switch (lastOperator) {
            case "+": currentInput = String.valueOf(firstValue + secondValue); break;
            case "-": currentInput = String.valueOf(firstValue - secondValue); break;
            case "*": currentInput = String.valueOf(firstValue * secondValue); break;
            case "/": currentInput = String.valueOf(firstValue / secondValue); break;
            case "x^y": currentInput = String.valueOf(Math.pow(firstValue, secondValue)); break;
        }
        tvResult.setText(currentInput);
    }

    private int factorial(int n) {
        return (n == 0) ? 1 : n * factorial(n - 1);
    }
}
