package com.example.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String input = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editText = (EditText) findViewById(R.id.input);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideSoftKeyboard();
                return false;
            }
        });

    }
    public void hideSoftKeyboard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
    }

    public void onClick(View v){
        EditText input_field = (EditText) findViewById(R.id.input);

        Button btn = (Button) v;
        String sign = btn.getText().toString();
        switch (sign){
            case "sin":
            case "cos":
            case "tng":
            case "ctg":
                sign += '(';
        }
        input += sign;
        input_field.setText(input);
    }

    public void onClickEqual(View v){
        TextView answer_filed = (TextView) findViewById(R.id.result);
        try {
            String ans = Double.toString(Calculator.result(input));
            answer_filed.setText(ans);
        }catch (Exception ex){
            String message = ex.getMessage();
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickClear(View v){
        EditText input_field = (EditText) findViewById(R.id.input);
        TextView answer_filed = (TextView) findViewById(R.id.result);
        input = "";
        input_field.setText(input);
        answer_filed.setText(input);

    }

    public void onClickDel(View v){
        EditText input_field = (EditText) findViewById(R.id.input);
        if (input.length() != 0) {
            int lastIndex = input.length() - 1;

            char lastChar = input.charAt(lastIndex);
            if (lastChar == 'n' || lastChar == 's'){
                lastIndex -= 2;
            }
            input = input.substring(0, lastIndex);
            input_field.setText(input);
        }else{
            Toast.makeText(this, "There is nothing to delete!", Toast.LENGTH_SHORT).show();
        }
    }
}