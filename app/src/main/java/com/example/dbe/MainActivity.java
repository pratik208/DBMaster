package com.example.dbe;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onFindClosureClicked(View view) {
        showToast("Going  to Find Closure module");

        startActivity(new Intent(MainActivity.this , FindClosureActivity.class));

    }

    public void onFindCandidateKeyClicked(View view) {
        showToast("Going to Find Candidate Key module");
        // Implement the logic to navigate to the Find Candidate Key module
        startActivity(new Intent(MainActivity.this , FindCandidateKeyActivity.class));
    }

    public void onNormalizationClicked(View view) {
        showToast("Going to Normalization module");
        // Implement the logic to navigate to the Normalization module
        startActivity(new Intent(MainActivity.this , NormalizationActivity.class));
    }

    // Helper method to show a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
