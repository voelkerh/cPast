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
import com.voelkerh.cPast.domain.model.Archive;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Custom {@link ArrayAdapter} for displaying {@link Archive} entries in a Spinner.
 *
 * <p>This adapter extends the standard Spinner behavior by:
 * <ul>
 *   <li>Rendering archive entries with an inline edit button</li>
 *   <li>Providing an additional trailing item for creating new archives</li>
 *   <li>Using different layouts for the selected view and the dropdown list</li>
 * </ul>
 *
 * <p>User interactions are delegated to {@link AddArchiveDialog.Listener} and
 * {@link EditArchiveDialog.Listener}. This adapter belongs to the UI layer and
 * contains no business logic.</p>
 */
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

    /**
     * Returns the number of Spinner items, including an additional entry for triggering archive creation.
     */
    @Override
    public int getCount() {
        return archives.size() + 1;
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

    /**
     * Creates the dropdown view for each Spinner item.
     *
     * <p>Archive entries display an edit button, while the last entry acts as an action item to create a new archive.</p>
     */
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
