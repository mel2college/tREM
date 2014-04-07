package com.trem.squeechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity /*extends BlunoLibrary*/ implements OnItemSelectedListener {
	
	Boolean debugMode = true; // set to false if not in debug mode
	int action;
	int force;
	Button startSqueeze, bBlue;
	TextView debugDisplay, actionDisplay, levelDisplay;
	Spinner strengthSpin;
	String[] levels = { "Soft", "Medium", "Hard" };
	String level;
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
//        onCreateProcess();
//        serialBegin(115200);
        
        //--------------------------------------UI-------------------------------------------------
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, levels);
        
        action = 0;
        startSqueeze = (Button) findViewById(R.id.bSqueeS);
        bBlue = (Button) findViewById(R.id.bBlue);
        actionDisplay = (TextView) findViewById(R.id.tvAction);
        debugDisplay = (TextView) findViewById(R.id.tvDebug);
        levelDisplay = (TextView) findViewById(R.id.tvLevel);
        
        if (debugMode) {
        	debugDisplay.setVisibility(View.VISIBLE);
        	actionDisplay.setVisibility(View.VISIBLE);
        	levelDisplay.setVisibility(View.VISIBLE);
        }
        
        strengthSpin = (Spinner) findViewById(R.id.spinner1);
        strengthSpin.setAdapter(adapter);
/*        startSqueeze.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (action == 0) {
					action = 1;
//					serialSend("<start>"+level+";");
					actionDisplay.setText("Action Value: "  + action);
					levelDisplay.setText("Strength Level: " + level);
					startSqueeze.setText(R.string.bSqueeE);
				}
				else {
					action = 0;
//					serialSend("<stop>;");
					actionDisplay.setText("Action Value: "  + action);
					levelDisplay.setText("Strength Level: " + level);
					startSqueeze.setText(R.string.bSqueeS);
				}
			}
		});*/
        
        startSqueeze.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 if (event.getAction() == MotionEvent.ACTION_DOWN ) {
					action = 1;
//					serialSend("<start>"+level+";");
					actionDisplay.setText("Action Value: "  + action);
					levelDisplay.setText("Strength Level: " + level);
					startSqueeze.setText(R.string.bSqueeE);
					return true;
				 }
				 else if (event.getAction() == MotionEvent.ACTION_UP ) {
					action = 0;
//					serialSend("<stop>;");
					actionDisplay.setText("Action Value: "  + action);
					levelDisplay.setText("Strength Level: " + level);
					startSqueeze.setText(R.string.bSqueeS);
					return true;
				 }
				return false;
			}
		});
        bBlue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				buttonScanOnClickProcess();
			}
        });
        strengthSpin.setOnItemSelectedListener(this);
        //------------------------------------------------UI-------------------------------------------------
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
/*
    protected void onResume() {
    	super.onResume();
    	onResumeProcess();
    }
    
    @Override
    protected void onPause(){
    	super.onPause();
    	onPauseProcess();
    }
    
    protected void onStop(){
    	super.onStop();
    	onStopProcess();
    }
    
    @Override
    protected void onDestroy(){
    	super.onDestroy();
    	onDestroyProcess();
    }
  */  
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		int position = strengthSpin.getSelectedItemPosition();
		switch (position) {
		case 0:
			level = "0";
			break;
		case 1:
			level = "1";
			break;
		case 2:
			level = "2";
			break;
		}
	}


	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO.... Nothing to do.
	}

/*
	@Override
	public void onConectionStateChange(
			connectionStateEnum theConnectionState) {
		switch(theConnectionState) {
		case isConnected:
			bBlue.setText("Connected");
			break;
		case isConnecting:
			bBlue.setText("Connecting");
			break;
		case isToScan:
			bBlue.setText("Scan");
			break;
		case isScanning:
			bBlue.setText("Scanning");
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {
		// TODO parse the incoming force
		force = Integer.parseInt(theString);
		
		
	}
    */
}
