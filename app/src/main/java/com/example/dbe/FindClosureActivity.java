package com.example.dbe;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashSet;

public class FindClosureActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;

    private EditText editTextLeftAttribute;
    private EditText editTextRightAttribute;
    private LinearLayout layoutAttributes;
    private ArrayList<FD> dependencies;
    private TextView textViewClosureResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_closure);

        editTextLeftAttribute = findViewById(R.id.editTextLeftAttribute);
        editTextRightAttribute = findViewById(R.id.editTextRightAttribute);
        layoutAttributes = findViewById(R.id.layoutAttributes);
        dependencies = new ArrayList<>();
        textViewClosureResult = findViewById(R.id.textViewClosureResult);
        textViewClosureResult.setTextSize(25);
        updateAttributeListUI();
        // Check for write storage permission at runtime
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    public void onAddAttributesClicked(View view) {
        String leftAttribute = editTextLeftAttribute.getText().toString().trim();
        String rightAttribute = editTextRightAttribute.getText().toString().trim();
        if (!leftAttribute.isEmpty() && !rightAttribute.isEmpty()) {
            FD dependency = new FD(leftAttribute, rightAttribute);
            dependencies.add(dependency);
            updateAttributeListUI();
            editTextLeftAttribute.setText("");
            editTextRightAttribute.setText("");
        }
    }

    private void updateAttributeListUI() {
        layoutAttributes.removeAllViews();
        for (FD dependency : dependencies) {
            TextView textView = new TextView(this);
            String attributePair = dependency.left + " -> " + dependency.right;
            textView.setText(attributePair);

            // Set the desired text size here (in SP units)
            textView.setTextSize(25);

            layoutAttributes.addView(textView);
        }
    }

    public void onFindClosureClicked(View view) {
        if (dependencies.isEmpty()) {
            textViewClosureResult.setText("No attributes added. Add attribute pairs first.");
            return;
        }

        StringBuilder closureResult = new StringBuilder("Closure Result:\n");
        for (FD dependency : dependencies) {
            String closure = calculateClosure(dependency);
            closureResult.append(dependency.left).append("+ = {").append(formatResult(closure)).append("}\n");
        }
        textViewClosureResult.setText(closureResult.toString());
    }

    private String calculateClosure(FD dependency) {
        String closure = dependency.left;

        HashSet<Character> closureSet = new HashSet<>();

        for (char ch : closure.toCharArray()) {
            closureSet.add(ch);
        }

        boolean closureChanged = true;

        while (closureChanged) {
            closureChanged = false;

            for (char ch : dependency.right.toCharArray()) {
                if (!closureSet.contains(ch)) {
                    closureSet.add(ch);
                    closure += ch;
                    closureChanged = true;
                }
            }
        }

        return closure;
    }





    private static class FD {
        String left;
        String right;

        FD(String left, String right) {
            this.left = left;
            this.right = right;
        }
    }

    private String formatResult(String closure) {
        StringBuilder formattedClosure = new StringBuilder();
        for (int i = 0; i < closure.length(); i++) {
            char ch = closure.charAt(i);
            if (i > 0) {
                formattedClosure.append(", ");
            }
            formattedClosure.append(ch);
        }
        return formattedClosure.toString();
    }
}
