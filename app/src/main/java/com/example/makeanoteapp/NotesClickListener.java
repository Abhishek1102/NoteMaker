package com.example.makeanoteapp;

import androidx.cardview.widget.CardView;

import com.example.makeanoteapp.model.Notes;

public interface NotesClickListener {

    void onClick(Notes notes);

    void onLongClick(Notes notes, CardView cardView);

}
