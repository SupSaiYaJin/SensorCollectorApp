package com.saiyajin.sensor.activities;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.saiyajin.sensor.R;

public class SelfIntroActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selfintro);
		Button callAuthor=(Button)findViewById(R.id.callauthor);
		callAuthor.setOnClickListener(new Listener1());
		Button watchImage=(Button)findViewById(R.id.watchimage);
		watchImage.setOnClickListener(new Listener2());
		Button realImage=(Button)findViewById(R.id.realimage);
		realImage.setOnClickListener(new Listener3());
	}

	class Listener1 implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			Uri uri=Uri.parse("smsto:18601242331");
			Intent intent=new Intent(Intent.ACTION_SENDTO,uri);
			intent.putExtra("SMS_Body", "The SMS Text");
			startActivity(intent);
		}
	}
	
	class Listener2 implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
        AlertDialog.Builder alert=new AlertDialog.Builder(SelfIntroActivity.this);
        alert.setTitle("你居心叵测").setMessage("你为什么想看写真？你在想些什么？").setNegativeButton("我忏悔，我认错",new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				Toast.makeText(SelfIntroActivity.this, "你做了正确的选择！", Toast.LENGTH_LONG).show();
			}
		}).show().setCanceledOnTouchOutside(false);
		}
	}
	
	class Listener3 implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
	    Drawable drawable = getResources().getDrawable(R.drawable.myface);  
        TextView selfText=(TextView)findViewById(R.id.selftext);
        selfText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
		}
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
	     case android.R.id.home:
	     finish();
	     default:
	    	 return super.onOptionsItemSelected(item);
	     }
	}

}
