package org.thinkgraph;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.*;
import android.widget.*;
import org.thinkgraph.model.RecordItem;
import org.thinkgraph.model.TaggedRecord;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Tags list
 */
public class SmartTagActivity extends ListActivity {

    public static final String SELECTED_RECORD = "SelectRec";

    private RecordAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browse_layout);
        adapter = new RecordAdapter(this, R.layout.tag_item_layout,
                listTagRecords());
        setListAdapter(adapter);
        registerForContextMenu(this.getListView());
        adapter.setNotifyOnChange(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.browse_layout);
        adapter = new RecordAdapter(this, R.layout.tag_item_layout,
                listTagRecords());
        setListAdapter(adapter);
        registerForContextMenu(this.getListView());
        adapter.setNotifyOnChange(true);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = new Intent(this, TagAssociationsActivity.class);
        intent.putExtra(SELECTED_RECORD, adapter.getItem(position));
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Notes menu:");
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Back");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if (item.getTitle() == "Delete") {
            TaggedRecord taggedRecord = adapter.getItem(info.position);
            File tag = new File(taggedRecord.getPath());
            if (tag.listFiles().length == 0) {
                tag.delete();
                adapter.remove(taggedRecord);
            }
            else {
                Toast.makeText(this, "Tag cannot be removed if it has associations", Toast.LENGTH_SHORT).show();
            }

            return true;
        }
        else {
            return false;
        }
    }

    private List<TaggedRecord> listTagRecords() {
        List<TaggedRecord> tags = new ArrayList<TaggedRecord>();
        try {
            File root = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/" + VoiceRecordActivity.APP_ROOT);

            for (File tag : root.listFiles()) {
                TaggedRecord rec = new TaggedRecord(tag.getName(),
                        tag.getAbsolutePath(), new RecordItem[0]);

                for (File f : tag.listFiles()) {
                    rec.getAssociations().add(
                            new RecordItem(Long.parseLong(f.getName()
                                    .substring(0, f.getName().length() - 4)), f
                                    .length(), f.getAbsolutePath(), rec
                                    .getTag()));
                }
                tags.add(rec);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tags;
    }

    private class RecordAdapter extends ArrayAdapter<TaggedRecord> {

        private List<TaggedRecord> items;

        public RecordAdapter(Context context, int textViewResourceId,
                             List<TaggedRecord> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.tag_item_layout, null);
            }
            TaggedRecord rec = items.get(position);

            if (rec != null) {
                TextView top = (TextView) v.findViewById(R.id.toptext);
                TextView low = (TextView) v.findViewById(R.id.bottomtext);

                if (top != null) {
                    top.setText("Tag: " + rec.getTag());
                }
                if (low != null) {
                    low.setText("Associations: " + rec.getAssociations().size());
                }
            }
            return v;
        }
    }
}