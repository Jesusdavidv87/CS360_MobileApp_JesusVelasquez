package com.example.myfirstapp_jesusvelasquez.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myfirstapp_jesusvelasquez.R;

public class LoginActivity1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Use your real layout name here:
        setContentView(R.layout.activity_login2); // change to activity_login if that's your file
    }

    // Called by android:onClick="openInventory"
    public void openInventory(View view) {
        startActivity(new Intent(this, InventoryActivity.class));
    }
}
