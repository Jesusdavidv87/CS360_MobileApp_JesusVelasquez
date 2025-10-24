package com.example.myfirstapp_jesusvelasquez.ui; // keep exactly as your package shows

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp_jesusvelasquez.R;

public class MainActivity extends AppCompatActivity {

    private EditText nameText;
    private TextView textGreeting;
    private Button buttonSayHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameText = findViewById(R.id.nameText);
        textGreeting = findViewById(R.id.textGreeting);
        buttonSayHello = findViewById(R.id.buttonSayHello);

        buttonSayHello.setEnabled(false);

        nameText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasText = s != null && s.toString().trim().length() > 0;
                buttonSayHello.setEnabled(hasText);
            }
            @Override public void afterTextChanged(Editable s) { }
        });
    }

    public void SayHello(View view) {
        if (nameText == null) return;
        String name = nameText.getText() != null ? nameText.getText().toString().trim() : "";
        if (name.isEmpty()) return;
        String msg = "Hello " + name;
        textGreeting.setText(msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}

