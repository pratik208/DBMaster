package com.example.dbe;


import android.os.Bundle;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class FindCandidateKeyActivity extends AppCompatActivity {

    private EditText editTextLeftAttribute;
    private EditText editTextRightAttribute;
    private LinearLayout layoutAttributes;
    private List<FD> dependencies;
    private TextView textViewCandidateKeyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_candidate_key);

        editTextLeftAttribute = findViewById(R.id.editTextLeftAttribute);
        editTextRightAttribute = findViewById(R.id.editTextRightAttribute);
        layoutAttributes = findViewById(R.id.layoutAttributes);
        dependencies = new ArrayList<>();
        textViewCandidateKeyResult = findViewById(R.id.textViewCandidateKeyResult);
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
            layoutAttributes.addView(textView);
        }
    }

    public void onFindCandidateKeyClicked(View view) {
        if (dependencies.isEmpty()) {
            Toast.makeText(this, "No attributes added. Add attribute pairs first.", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<Character> allAttributes = new HashSet<>();
        for (FD dependency : dependencies) {
            allAttributes.addAll(toCharSet(dependency.left));
            allAttributes.addAll(toCharSet(dependency.right));
        }

        List<String> candidateKeys = new ArrayList<>();
        for (String attribute : allAttributes.stream().map(Object::toString).collect(Collectors.toList())) {
            Set<String> closure = calculateClosure(toCharSet(attribute));
            if (closure.containsAll(allAttributes.stream().map(Object::toString).collect(Collectors.toList()))) {
                candidateKeys.add(attribute);
            }
        }

        if (candidateKeys.isEmpty()) {
            textViewCandidateKeyResult.setText("No candidate keys found.");
        } else {
            textViewCandidateKeyResult.setText("Candidate keys: " + String.join(", ", candidateKeys));
        }
        textViewCandidateKeyResult.setVisibility(View.VISIBLE);
    }

    private Set<Character> toCharSet(String str) {
        return str.chars().mapToObj(ch -> (char) ch).collect(Collectors.toSet());
    }

    private Set<String> calculateClosure(Set<Character> dependency) {
        Set<Character> closure = new HashSet<>(dependency);

        boolean closureChanged = true;

        while (closureChanged) {
            closureChanged = false;

            for (FD dependencyFD : dependencies) {
                Set<Character> left = toCharSet(dependencyFD.left);
                Set<Character> right = toCharSet(dependencyFD.right);

                if (closure.containsAll(left) && !closure.containsAll(right)) {
                    closure.addAll(right);
                    closureChanged = true;
                }
            }
        }

        return closure.stream().map(Object::toString).collect(Collectors.toSet());
    }

    private static class FD {
        String left;
        String right;

        FD(String left, String right) {
            this.left = left;
            this.right = right;
        }
    }
}
