package com.mckenzie.Zooper.Installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelper;
import com.mckenzie.Zooper.Installer.R;



import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class Zooper extends Activity {

	// change this to your apk skin name
	private static final String UCCW_TEST_SKIN_APK = "faint.apk";


	

	
	// Do not touch code that follows unless you know what you are doing
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
	// Removes ActionBar title at launch. Delete this line to get it back.
		this.setTitle(getResources().getString(R.string.blank));
		
	// FadingActionBar calls the header and main layouts, and sets the AB background.	
        FadingActionBarHelper helper = new FadingActionBarHelper()
        .actionBarBackground(R.drawable.ab_solid_actionbar)
        .headerLayout(R.layout.zooper_launcher_header)
        .contentLayout(R.layout.zooper_launcher);
        setContentView(helper.createView(this));
        helper.initActionBar(this);
    
	context = this;

	//Sets the fonts for the activity. There's gotta be a better way to do this...
	    TextView tv3=(TextView)findViewById(R.id.ZooperDescription); 
	    Typeface face2=Typeface.createFromAsset(getAssets(), "themefontlight.ttf");
	    tv3.setTypeface(face2);
	    TextView tv4=(TextView)findViewById(R.id.ZooperDescriptionTitle); 
	    tv3.setTypeface(face2);
	    
	// Buttons!     
		findViewById(R.id.buttonInstallWidget).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Calls the Installable Skins method.
						showInstallableSkins();
					}

				});
		
		
		
		findViewById(R.id.buttonWallpaperChooser).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Opens the wallpaper activity.
					    Intent intent = new Intent(com.mckenzie.Zooper.Installer.Zooper.this, com.mckenzie.Zooper.Installer.Wallpaper.class);
					    startActivity(intent);
					}

				});

	}
	
	@Override
	// This is the ActionBar Menu
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu) ;
		return true;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()){
	    
        case R.id.action_share:
        	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
            sharingIntent.setType("text/plain");
            // Don't forget to set this to YOUR app's info.
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Zooper Widget");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.URL.com");
            startActivity(Intent.createChooser(sharingIntent, "Share Widget..."));
            Toast.makeText(this, "You are totally awesome. Thank you!", Toast.LENGTH_SHORT).show();
            return true;
            
        case R.id.action_rate:
        	// Again, set to YOUR app's market info
   		 	Uri uri = Uri.parse("market://URL.com");
   		 	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
   		 	startActivity(intent);
            return true;
	            
            // Default stuff.
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	
	
	// This installs the apk. Change ONE LINE BELOW.
	private class RepairSkinAsyncTask extends AsyncTask<Void, Void, Void> {

		private ProgressDialog mDialog;

		@Override
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(context, "", "Processing...", true);
		}

		@Override
		protected Void doInBackground(Void... nothing) {
			String SDCARD_MYAPK_APK = Environment.getExternalStorageDirectory()
			
			// Change to your apk file that is located in your assets folder.
			// LEAVE EVERYTHING ELSE ALONE IF YOU WANT TO LIVE
			.getPath() + File.separator + "faint.apk";
			deleteOldSkin(SDCARD_MYAPK_APK);
			saveSkinToSdCard(SDCARD_MYAPK_APK);
			startAppInstaller(SDCARD_MYAPK_APK);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mDialog.dismiss();

		}
	}

	/**
	 * 
	 */
	private void showInstallableSkins() {
		if (isSDcardAvailable()) {
			new RepairSkinAsyncTask().execute();
		} else {
			Toast.makeText(this, "SD card not available", Toast.LENGTH_LONG)
					.show();
		}

	}

	private void deleteOldSkin(String pathToSkin) {
		File file = new File(pathToSkin);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * @param assetManager
	 * @param in
	 * @param out
	 * @param pathToSkin
	 */
	private void saveSkinToSdCard(String pathToSkin) {
		AssetManager assetManager = getAssets();

		InputStream in = null;
		OutputStream out = null;

		try {
			in = assetManager.open(UCCW_TEST_SKIN_APK);
			try {
				out = new FileOutputStream(pathToSkin);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}

			in.close();
			in = null;

			out.flush();

			out.close();

			out = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param pathToSkin
	 */
	private void startAppInstaller(String pathToSkin) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(pathToSkin)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}

	private boolean isSDcardAvailable() {
		String state = Environment.getExternalStorageState();
		return state.contentEquals(Environment.MEDIA_MOUNTED)
				|| state.contentEquals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}
	

	
	
	@Override
	  public void onPause(){
		  super.onPause();

	  }
	
	
	
}
