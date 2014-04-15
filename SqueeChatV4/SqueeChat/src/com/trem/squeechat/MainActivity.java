package com.trem.squeechat;


import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BlunoLibrary {
	
	final int MIN_FORCE = 150;
	final int MAX_FORCE = 200;
	
	Button bBlue, bCab, bDialog, bCalibrate;
	int position;
	TextView tvForce, tvDialog;
	MyGLSurfaceView meter;
	Dialog dialog;
	int force = 0;
	String buffer = new String();
	String dialogText;
	private Handler mHandler = new Handler();

    private Runnable forceTask = new Runnable() {
        @Override
        public void run() {
           serialSend("<force>;");
           mHandler.postDelayed(forceTask, 100);
        }
    };
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        onCreateProcess();
        serialBegin(115200);
        
        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("Calibration");
        
        tvForce = (TextView) findViewById(R.id.tvForce);
        bBlue = (Button) findViewById(R.id.bBlue);
        bDialog = (Button) dialog.findViewById(R.id.Bdialog);
        bCalibrate = (Button) findViewById(R.id.bCalibrate);
        tvDialog = (TextView) dialog.findViewById(R.id.TVdialog);
        meter = (MyGLSurfaceView) findViewById(R.id.meter);
        
        bCalibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.show();
			}
		});
        
        bDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
        
        meter.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float y = event.getY();
				switch (event.getAction()) {
	        		case MotionEvent.ACTION_DOWN:
	        		case MotionEvent.ACTION_MOVE:
		            	if(y < meter.getHeight() && y > 0) {
		            		position = (int) (((1f - 2f * y / (meter.getHeight() - 1f)) + 1f) / 2f * 180f);
		            		//System.out.println("Position is " + position);
		            		//System.out.println("Y is " + y);
		            		serialSend("<position>" + position + ";");
		            	}
		                break;
		            case MotionEvent.ACTION_UP:
		            	serialSend("<stop>;");
		            	break;
				}
				return false;
			}

		});
        
        bBlue.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonScanOnClickProcess();
			}
        });       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

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
  
	@Override
	public void onConectionStateChange(
			connectionStateEnum theConnectionState) {
		switch(theConnectionState) {
		case isConnected:
			bBlue.setText(R.string.connectedd);
			mHandler.post(forceTask);
			break;
		case isConnecting:
			bBlue.setText(R.string.connecting);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case isToScan:
			bBlue.setText(R.string.scan);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			mHandler.removeCallbacks(forceTask);
			tvDialog.setText(R.string.dialog);
			tvDialog.setTextColor(Color.WHITE);
			tvForce.setText("0");
			break;
		case isScanning:
			bBlue.setText(R.string.scanning);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSerialReceived(String theString) {
		String[] tokens;
		//System.out.println("i am here 1");
		if (theString != null) {
			buffer += theString;
			//System.out.println("i am here 2");
			//System.out.println("Current Buffer: " + buffer);
			tokens = buffer.split("\r\n");
			
			for (int i = 0; i < tokens.length; i++ ) {
				if (i == (tokens.length -1)) {
					if (buffer.substring(buffer.length()-2, buffer.length()).equals("\r\n")){
						buffer = "";
					}
					else {
						buffer = tokens[i];
						break;
					}
				}
				try	{
					//System.out.println("i am here 3");
					force = Integer.parseInt(tokens[i]);
					tvForce.setText("" + force);
					//System.out.println("Force equals " + force);
					
					if (force >= 0 && force < MIN_FORCE) {
						tvDialog.setText("The armband is too loose.");
						tvDialog.setTextColor(Color.RED);
					}
					else if (force >= MIN_FORCE && force <= MAX_FORCE) {
						tvDialog.setText("The armband is just right!");
						tvDialog.setTextColor(Color.GREEN);
					}
					else if (force > MAX_FORCE) {
						tvDialog.setText("The armband is too tight.");
						tvDialog.setTextColor(Color.RED);
					}
					else {
						tvDialog.setText("This is impossible!!");
					}
				}
				catch (NumberFormatException nfe) {
					System.out.println("NumberFormatException: " + nfe.getMessage());
				}
			}	
		}
	} 
	
	
}
