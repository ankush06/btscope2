package com.example.myapplication1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView selectedInput = findViewById(R.id.id_selected_input);
        final BTinterface btInterface = (BTinterface) getApplicationContext();
        selectedInput.setText(btInterface.getInputDevice_MAC());
    }

    /** Called when the user taps the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void scanForInputDevice(View view) {
        Intent intent = new Intent(this, scan_input.class);
        //EditText editText = (EditText) findViewById(R.id.editText);
        intent.putExtra(Intent.EXTRA_TEXT, "hellow");
        startActivity(intent);
        //return "DeviceName1";

    }


}
