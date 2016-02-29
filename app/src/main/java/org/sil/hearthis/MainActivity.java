package org.sil.hearthis;

import org.sil.palaso.Graphite;

import Script.FileSystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends Activity implements AcceptNotificationHandler.NotificationListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Graphite.loadGraphite();
		setContentView(R.layout.activity_main);
		ServiceLocator.getServiceLocator().init(this);
		Button sync = (Button) findViewById(R.id.mainSyncButton);
		final MainActivity thisActivity = this; // required to use it in touch handler
		sync.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent e) {
				// When a sync completes we want to evaluate the results and launch the book chooser if appropriate
				AcceptNotificationHandler.addNotificationListener(thisActivity);
				LaunchSyncActivity();
				return true; // we handle all touch events on this button.
			}

		});
		launchChooseBookIfProject();
	}

	private ArrayList<String> getProjectRootDirectories() {
		FileSystem fs = ServiceLocator.getServiceLocator().getFileSystem();
		String rootDir = ServiceLocator.getServiceLocator().externalFilesDirectory;
		return fs.getDirectories(rootDir);
	}

	private boolean launchChooseBookIfProject() {
		ArrayList<String> rootDirs = getProjectRootDirectories();
		if (rootDirs.isEmpty()) {
			return false; // Leave the main activity active (allows user to sync a project).
		}
		Intent chooseBook = new Intent(MainActivity.this, ChooseBookActivity.class);
		startActivity(chooseBook);
		return true;
	}

	void LaunchSyncActivity() {
		Intent sync = new Intent(this, SyncActivity.class);
		startActivity(sync);
	}

	// Called when desktop sync completes (if no project initially)
	@Override
	public void onNotification(String message) {
		if (!launchChooseBookIfProject())
			return;
		AcceptNotificationHandler.removeNotificationListener(this);
	}
}
