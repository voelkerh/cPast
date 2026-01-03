package com.voelkerh.cPast.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.voelkerh.cPast.R;
import com.voelkerh.cPast.domain.Archive;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ArchiveAdapter extends ArrayAdapter<Archive> {

    private final LayoutInflater inflater;
    private final List<Archive> archives;
    private final Context context;
    private final AddArchiveDialog.Listener addArchiveListener;
    private final EditArchiveDialog.Listener editArchiveListener;

    public ArchiveAdapter(Context context, List<Archive> archives, AddArchiveDialog.Listener listener, EditArchiveDialog.Listener editListener) {
        super(context, 0, archives);
        this.context = context;
        this.archives = archives;
        this.inflater = LayoutInflater.from(context);
        this.addArchiveListener = listener;
        this.editArchiveListener = editListener;
    }

    @Override
    public int getCount() {
        return archives.size() + 1; // plus 1 for "Add archive"
    }

    @Override
    public Archive getItem(int position) {
        if (position < archives.size()) {
            return archives.get(position);
        }
        return null; // last row is "Add archive"
    }

    // When spinner closed, only show archive name
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            row = inflater.inflate(R.layout.item_archive_selected, parent, false);
        }

        TextView tv = row.findViewById(R.id.textArchiveSelected);

        if (position < archives.size()) {
            tv.setText(archives.get(position).toString());
            tv.setTextColor(ContextCompat.getColor(context, R.color.fontColor));
        } else {
            tv.setText(R.string.add_archive);
            tv.setTextColor(ContextCompat.getColor(context, R.color.fontColor));
        }

        return row;
    }


    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        View row = inflater.inflate(R.layout.item_archive, parent, false);
        TextView name = row.findViewById(R.id.textArchiveName);
        ImageButton edit = row.findViewById(R.id.btnEditArchive);

        if (position < archives.size()) {
            String archiveName = archives.get(position).toString();
            name.setText(archiveName);

            edit.setVisibility(View.VISIBLE);
            edit.setOnClickListener(v -> v.post(() -> {
                if (editArchiveListener != null) {
                    EditArchiveDialog.show(context, archiveName, editArchiveListener);
                }
            }));

            edit.setFocusable(false);
            edit.setFocusableInTouchMode(false);

        } else {
            name.setText(R.string.add_archive);
            name.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            edit.setVisibility(View.GONE);  // no edit symbol
            row.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            row.setOnClickListener(v -> {
                if (addArchiveListener != null) {
                    AddArchiveDialog.show(context, addArchiveListener);
                }
            });
        }

        return row;
    }
}
