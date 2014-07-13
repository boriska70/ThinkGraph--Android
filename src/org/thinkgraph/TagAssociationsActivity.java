package org.thinkgraph;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import org.thinkgraph.model.RecordItem;
import org.thinkgraph.model.TaggedRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Notes list for the selected tag
 */
public class TagAssociationsActivity extends ListActivity {

    private RecordAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_browse_layout);
        Intent intent = getIntent();
        TaggedRecord rec = (TaggedRecord) intent
                .getSerializableExtra(SmartTagActivity.SELECTED_RECORD);
        adapter = new RecordAdapter(this, R.layout.record_item_layout,
                rec.getAssociations());
        setListAdapter(adapter);
        registerForContextMenu(this.getListView());
        adapter.setNotifyOnChange(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        RecordItem note = adapter.getItem(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(loadNote(note))
                .setCancelable(false)
                .setPositiveButton("Close",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
        AlertDialog alert = builder.create();
        alert.setOwnerActivity(this);
        alert.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Notes menu:");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Done or Undone");
        menu.add(0, v.getId(), 0, "Back");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == "Delete") {
            RecordItem recordItem = adapter.getItem(info.position);
            /*Toast.makeText(this, "Delete called for " + recordItem.getPath(), Toast.LENGTH_SHORT).show();*/
            File file = new File(recordItem.getPath());
            File folder = new File(file.getParent());
            if (folder.listFiles().length == 1) {
                //Remove also tag if we removing the last note under it
                file.delete();
                adapter.notifyDataSetInvalidated();
                //folder.delete();
            }
            else {
                file.delete();
            }
            adapter.remove(recordItem);
            return true;
        }
        else if (item.getTitle() == "Done or Undone") {
            RecordItem recordItem = adapter.getItem(info.position);
            /*Toast.makeText(this, "Mark as done called for " + recordItem.getPath(), Toast.LENGTH_SHORT).show();*/
            File file = new File(recordItem.getPath());
            File newFile = null;
            if (file.getName().startsWith("1")) {
                newFile = new File(file.getParent() + File.separator + "0" + file.getName().substring(1));
            }
            else if (file.getName().startsWith("0")) {
                newFile = new File(file.getParent() + File.separator + "1" + file.getName().substring(1));
            }
            file.renameTo(newFile);
            RecordItem ri=new RecordItem(recordItem.getCreatedAt(),recordItem.getSizeInBytes(),newFile.getPath(),recordItem.getTags().toString()) ;
            adapter.remove(recordItem);
            adapter.add(ri);
            return true;
        }
        else {
            return false;
        }
    }

    private String loadNote(RecordItem item) {
        FileReader in = null;
        StringBuilder buf = new StringBuilder();
        try {
            in = new FileReader(item.getPath());
            int symbol;

            while ((symbol = in.read()) != -1) {
                buf.appendCodePoint(symbol);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buf.toString();
    }

    private class RecordAdapter extends ArrayAdapter<RecordItem> {

        private List<RecordItem> items;

        public RecordAdapter(Context context, int textViewResourceId,
                             List<RecordItem> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.record_item_layout, null);
            }
            RecordItem rec = items.get(position);

            if (rec != null) {
                TextView top = (TextView) v.findViewById(R.id.toptext);
                TextView low = (TextView) v.findViewById(R.id.bottomtext);

                if (top != null) {
                    String topText = "";
                    if (rec.getPath().contains(File.separator + '0')) {
                        File file = new File(rec.getPath());
                        if (file.getName().startsWith("0")) {
                            rec.setCreatedAt(new Long("1" + file.getName().substring(1, file.getName().indexOf("."))));
                            topText = "Done!!! ";
                        }

                    }
                    top.setText(topText + "Created on: " + new Date(rec.getCreatedAt()));
                }
                if (low != null) {
                    low.setText("Size (bytes): " + rec.getSizeInBytes());
                }
            }
            return v;
        }
    }
}