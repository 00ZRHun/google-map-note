package com.example.googlemapnote.adapters;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.googlemapnote.EditNoteActivity;
import com.example.googlemapnote.R;
import com.example.googlemapnote.models.notes.Note;
import com.example.googlemapnote.models.notes.NoteList;
import com.google.gson.Gson;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder> {

    private static final String TAG = "NoteListAdapter";
    public static final String EDIT_NOTE_INTENT = "com.example.googlemapnote.edit_note";
    private NoteList noteList;


    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView textViewDesc;
        private final LinearLayout linearLayout;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.textView);
            textViewDesc = (TextView) view.findViewById(R.id.textViewDesc);
            linearLayout = view.findViewById(R.id.linear_layout);
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     */
    public NoteListAdapter(NoteList list) {
        this.noteList = list;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        final Note note = noteList.getNotes().get(position);

        Log.d(TAG, new Gson().toJson(note));

        viewHolder.textView.setText(note.getTitle());
        viewHolder.textViewDesc.setText(note.getDescription());
        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent editNoteIntent = new Intent(view.getContext(), EditNoteActivity.class);
                editNoteIntent.putExtra(EDIT_NOTE_INTENT, note);

                view.getContext().startActivity(editNoteIntent);
//                Toast.makeText(view.getContext(),"click on item: "+note.getTitle(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return noteList.getNotes().size();
    }
}
