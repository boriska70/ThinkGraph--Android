package org.thinkgraph;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Resources ressources = getResources();
		TabHost tabHost = getTabHost();

		// Voice Records tab
		Intent intentVoice = new Intent().setClass(this,
				VoiceRecordActivity.class);
		TabSpec tabSpecVoice = tabHost
				.newTabSpec("Voice")
				.setIndicator("Record",
						ressources.getDrawable(R.drawable.icon_voice_config))
				.setContent(intentVoice);

		// Tags tab
		Intent intentTags = new Intent().setClass(this, SmartTagActivity.class);
		TabSpec tabSpecTags = tabHost
				.newTabSpec("Tags")
				.setIndicator("Browse",
						ressources.getDrawable(R.drawable.icon_tags_config))
				.setContent(intentTags);

		tabHost.addTab(tabSpecVoice);
		tabHost.addTab(tabSpecTags);

		// set Voice tab as default (zero based)
		tabHost.setCurrentTab(0);
	}

	public void startRecording(View view) {

	}

	public void stopRecording(View view) {

	}
}
