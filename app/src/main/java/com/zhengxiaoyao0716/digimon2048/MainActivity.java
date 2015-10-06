package com.zhengxiaoyao0716.digimon2048;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.aboutItem:
		{
			new AlertDialog.Builder(this).setTitle(getString(R.string.about))
					.setMessage(R.string.copyright)
					.setPositiveButton(R.string.iKnow, null)
					.setNegativeButton(R.string.attentionMe,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface d, int i) {
							Intent intent = new Intent(Intent.ACTION_VIEW,
									Uri.parse("http://zhengxiaoyao0716.lofter.com/"));
							startActivity(intent);
						}
					}).show();
		}break;
		
		case R.id.exitItem:
		{
			finish();
		}break;
		
		}
		return true;
	}
	public void onStartClick(View view)
	{
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onRankClick(View view)
	{
		Intent intent = new Intent(this, RankActivity.class);
		startActivity(intent);
	}
	public void onHelpClick(View view)
	{
		final ImageView helpButton =
		(ImageView)findViewById(R.id.helpButton);
		helpButton.setImageResource(R.mipmap.help1);
		Toast helpToast = Toast.makeText(this,
				R.string.helpString0 + new Random().nextInt(3),
				Toast.LENGTH_SHORT);
		helpToast.setGravity(Gravity.CENTER, 0, 0);
		helpToast.show();
		new Handler().postDelayed(new Runnable(){
			public void run()
			{
				helpButton.setImageResource(R.mipmap.help0);
			}
		}, 2000); 
	}
}
