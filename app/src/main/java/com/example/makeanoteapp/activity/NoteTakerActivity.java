package com.example.makeanoteapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.example.makeanoteapp.R;
import com.example.makeanoteapp.model.Notes;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NoteTakerActivity extends AppCompatActivity {

    @BindView(R.id.edt_title)
    EditText edt_title;
    @BindView(R.id.edt_notes)
    EditText edt_notes;
    Notes notes;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taker);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        notes = new Notes();
        try {
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            edt_title.setText(notes.getTitle());
            edt_notes.setText(notes.getNotes());
            isOldNote = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.iv_save)
    void iv_saveClick() {
        String title = edt_title.getText().toString();
        String description = edt_notes.getText().toString();

        if (description.isEmpty()) {
            Toast.makeText(this, "Please add to notes", Toast.LENGTH_SHORT).show();
            return;
        }

        //to get date and time in desired format
        //to change format check simple date format page on developers android
        SimpleDateFormat formatter = new SimpleDateFormat("EEE,d MMM yyyy HH:mm:ss a");
        Date date = new Date();

        if (!isOldNote) {
            notes = new Notes();
        }
        notes.setTitle(title);
        notes.setNotes(description);
        notes.setDate(formatter.format(date));

        Intent intent = new Intent(NoteTakerActivity.this, HomeActivity.class);
        intent.putExtra("note", notes);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }
}