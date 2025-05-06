package com.dardev.view;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.dardev.R;
import com.dardev.databinding.ReportBinding;

public class ReportActivity extends AppCompatActivity {
    private static final String TAG = "ReportActivity";

    private ReportBinding binding;
    private Spinner issueTypeSpinner;
    private EditText reportDescriptionEditText;
    private Button submitReportButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.report);

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white, getTheme()));

        // Setup toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Report an Issue");
        }

        // Initialize views
        issueTypeSpinner = binding.issueTypeSpinner;
        reportDescriptionEditText = binding.reportDescription;
        submitReportButton = binding.submitReportButton;

        // Setup spinner
        setupIssueTypeSpinner();

        // Setup button click listener
        submitReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReport();
            }
        });
    }

    private void setupIssueTypeSpinner() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.issue_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        issueTypeSpinner.setAdapter(adapter);
    }

    private void submitReport() {
        String issueType = issueTypeSpinner.getSelectedItem().toString();
        String description = reportDescriptionEditText.getText().toString().trim();

        // Validate input
        if (description.isEmpty()) {
            reportDescriptionEditText.setError("Please provide a description");
            return;
        }

        // In a real app, you would send this report to your server
        // For now, just show a success message
        Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_LONG).show();

        // Clear fields
        reportDescriptionEditText.setText("");
        issueTypeSpinner.setSelection(0);

        // Finish activity after a delay
        binding.submitReportButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1500);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}