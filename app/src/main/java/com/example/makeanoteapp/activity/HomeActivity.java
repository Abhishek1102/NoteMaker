package com.example.makeanoteapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.makeanoteapp.NotesClickListener;
import com.example.makeanoteapp.R;
import com.example.makeanoteapp.database.RoomDB;
import com.example.makeanoteapp.helper.AppConstant;
import com.example.makeanoteapp.helper.SecurePreferences;
import com.example.makeanoteapp.helper.Utils;
import com.example.makeanoteapp.model.Notes;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    @BindView(R.id.rv_home)
    RecyclerView rv_home;

    @BindView(R.id.searchview_home)
    SearchView searchView_home;

    @BindView(R.id.iv_more)
    ImageView iv_more;

    List<Notes> notes = new ArrayList<>();

    RoomDB database;
    Notes selectedNote;

    NotesListAdapter adapter;
    List<Notes> notesList;

    UiModeManager uiModeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAll();
        if (SecurePreferences.getBooleanPreference(getApplicationContext(), AppConstant.IS_LIST_SELECTED)) {
            updateRecyclerList(notes);
        } else {
            updateRecyclerGrid(notes);
        }
//        updateRecycler(notes);

//        Utils.blackIconStatusBar(HomeActivity.this, R.color.white);

        searchView_home.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });

    }

//    @OnClick(R.id.iv_mode)
//    void iv_modeClick() {
//        if (SecurePreferences.getBooleanPreference(getApplicationContext(), AppConstant.IS_DARKMODE_ON)) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//            SecurePreferences.savePreferences(getApplicationContext(), AppConstant.IS_DARKMODE_ON, false);
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//            SecurePreferences.savePreferences(getApplicationContext(), AppConstant.IS_DARKMODE_ON, true);
//        }
//    }

    private void filter(String newText) {
        List<Notes> filteredList = new ArrayList<>();
        for (Notes singleNote : notes) {
            if (singleNote.getTitle().toLowerCase().contains(newText.toLowerCase()) || singleNote.getNotes().toLowerCase().contains(newText.toLowerCase())) {
                filteredList.add(singleNote);
            }
        }
        adapter.filteredList(filteredList);
    }

    @OnClick(R.id.card_add)
    void card_addClick() {
        Intent intent = new Intent(HomeActivity.this, NoteTakerActivity.class);
        startActivityForResult(intent, 101);
    }

    @OnClick(R.id.iv_more)
    void iv_moreClick() {
        showPopupMore(iv_more);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().insert(new_notes);
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == 102) {
            if (resultCode == Activity.RESULT_OK) {
                Notes new_notes = (Notes) data.getSerializableExtra("note");
                database.mainDAO().update(new_notes.getID(), new_notes.getTitle(), new_notes.getNotes());
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                adapter.notifyDataSetChanged();
            }
        }

    }

    private void updateRecyclerGrid(List<Notes> notes) {
        rv_home.setHasFixedSize(true);
        rv_home.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        adapter = new NotesListAdapter(HomeActivity.this, notes, notesClickListener);

        //to set layout animation everytime recycler view is being set
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.rcy_item_animation);
        rv_home.setLayoutAnimation(controller);
        rv_home.setAdapter(adapter);
        rv_home.scheduleLayoutAnimation();

    }

    private void updateRecyclerList(List<Notes> notes) {
        rv_home.setHasFixedSize(true);
        rv_home.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapter = new NotesListAdapter(HomeActivity.this, notes, notesClickListener);

        //to set layout animation everytime recycler view is being set
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.rcy_item_animation);
        rv_home.setLayoutAnimation(controller);
        rv_home.setAdapter(adapter);
        rv_home.scheduleLayoutAnimation();

    }

    private final NotesClickListener notesClickListener = new NotesClickListener() {
        @Override
        public void onClick(Notes notes) {
            Intent intent = new Intent(HomeActivity.this, NoteTakerActivity.class);
            intent.putExtra("old_note", notes);
            startActivityForResult(intent, 102);
        }

        @Override
        public void onLongClick(Notes notes, CardView cardView) {
            selectedNote = new Notes();
            selectedNote = notes;
            showPopup(cardView);
        }
    };

    private void showPopup(CardView cardView) {
        PopupMenu popupMenu = new PopupMenu(this, cardView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_menu);
        popupMenu.show();
    }

    private void showPopupMore(ImageView imageView) {
        PopupMenu popupMenu = new PopupMenu(this, imageView);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.top_menu);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.pin:
                if (selectedNote.isPinned()) {
                    database.mainDAO().pin(selectedNote.getID(), false);
                    Toast.makeText(this, "unpinned", Toast.LENGTH_SHORT).show();
                } else {
                    database.mainDAO().pin(selectedNote.getID(), true);
                    Toast.makeText(this, "pinned", Toast.LENGTH_SHORT).show();
                }
                notes.clear();
                notes.addAll(database.mainDAO().getAll());
                adapter.notifyDataSetChanged();
                return true;

            case R.id.delete:
                database.mainDAO().delete(selectedNote);
                notes.remove(selectedNote);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.grid:
                if (SecurePreferences.getBooleanPreference(getApplicationContext(), AppConstant.IS_LIST_SELECTED)) {
                    SecurePreferences.savePreferences(getApplicationContext(), AppConstant.IS_LIST_SELECTED, false);
                    updateRecyclerGrid(notes);
                    return true;
                } else {
                    SecurePreferences.savePreferences(getApplicationContext(), AppConstant.IS_LIST_SELECTED, true);
                    updateRecyclerList(notes);
                    return true;
                }

            case R.id.settings:
                Toast.makeText(this, "coming soon", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return false;
        }
    }

    public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.ItemViewHolder> {

        Context context;
        List<Notes> list;
        NotesClickListener listener;


        public NotesListAdapter(Context context, List<Notes> list, NotesClickListener listener) {
            this.context = context;
            this.list = list;
            this.listener = listener;
        }

        public NotesListAdapter(HomeActivity homeActivity, List<Notes> notes) {
        }

        @NonNull
        @Override
        public NotesListAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notes_list, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotesListAdapter.ItemViewHolder holder, int position) {
            holder.tv_title.setText(list.get(position).getTitle());
            holder.tv_title.setSelected(true);

            holder.tv_notes.setText(list.get(position).getNotes());

            holder.tv_date.setText(list.get(position).getDate());
            holder.tv_date.setSelected(true);

            if (list.get(position).isPinned()) {
                holder.iv_pin.setImageResource(R.drawable.pin);
            } else {
                holder.iv_pin.setImageResource(0);
            }

            int color_code = getRandomColor();
            holder.card_notes_container.setCardBackgroundColor(holder.itemView.getResources().getColor(color_code));

            holder.card_notes_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClick(list.get(holder.getAdapterPosition()));
                }
            });

            holder.card_notes_container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onLongClick(list.get(holder.getAdapterPosition()), holder.card_notes_container);
                    return true;
                }
            });

        }

        private int getRandomColor() {
            List<Integer> colorCode = new ArrayList<>();

            colorCode.add(R.color.color_1);
            colorCode.add(R.color.color_2);
            colorCode.add(R.color.color_3);
            colorCode.add(R.color.color_4);
            colorCode.add(R.color.color_5);

            Random random = new Random();
            int random_color = random.nextInt(colorCode.size());
            return colorCode.get(random_color);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void filteredList(List<Notes> filteredList) {
            list = filteredList;
            notifyDataSetChanged();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.notes_container)
            CardView card_notes_container;
            @BindView(R.id.tv_title)
            TextView tv_title;
            @BindView(R.id.iv_pin)
            ImageView iv_pin;
            @BindView(R.id.tv_notes)
            TextView tv_notes;
            @BindView(R.id.tv_date)
            TextView tv_date;

            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}