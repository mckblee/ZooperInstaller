package com.mckenzie.Zooper.Installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	private static final String ZOOPER_SKIN_APK = "faint.apk";


	

	
	// Do not touch code that follows unless you know what you are doing
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().hide();
        setContentView(R.layout.zooper_launcher);
    
	context = this;

	//Sets the fonts for the activity. There's gotta be a better way to do this...

	    
	// Buttons!     
		findViewById(R.id.buttonInstallWidget).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Calls the Installable Skins method.
						showInstallableSkins();
					}

				});
		findViewById(R.id.buttonGetZooper).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
				   		 Uri uri = Uri.parse("market://details?id=org.zooper.zwpro");
				   		 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				   		 startActivity(intent);
				          
					}

				});

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
			in = assetManager.open(ZOOPER_SKIN_APK);
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
