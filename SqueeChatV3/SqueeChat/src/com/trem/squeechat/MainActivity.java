package com.trem.squeechat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends BlunoLibrary {
	
	Button bBlue;
	int position;
    //--------------------------------------------------------------------------------------------------
	TextView tvForce;
    //--------------------------------------------------------------------------------------------------
	MyGLSurfaceView meter;
	int force = 0;
	String buffer = new String();
    //--------------------------------------------------------------------------------------------------
	String[] tokens;
	//private Object lock = new Object();
    //--------------------------------------------------------------------------------------------------
	

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        onCreateProcess();
        serialBegin(115200);
        //--------------------------------------------------------------------------------------------------
        tvForce = (TextView) findViewById(R.id.tvForce);
        //--------------------------------------------------------------------------------------------------
        bBlue = (Button) findViewById(R.id.bBlue);
        meter = (MyGLSurfaceView) findViewById(R.id.meter);
        
        
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
       /*//-------------------------------------------------------------------------------------------------- 
        class Worker implements Runnable {

			@Override
			public void run() {
				synchronized (lock) {
					System.out.println("HELLO!!");
					if (!buffer.isEmpty() && (buffer.length() > 2)) {
						  System.out.println("HEL2LO!!2");
						if (buffer.substring(buffer.length()-2, buffer.length()-1).equals("\r\n")) {
							System.out.println("HE3LLO!!3");
							tokens = buffer.split("\r\n");
							
							for (String t : tokens) {
								try	{      
									  force = Integer.parseInt(t);
									  System.out.println("Force equals " + force);
									}
									catch (NumberFormatException nfe)
									{
									  System.out.println("NumberFormatException: " + nfe.getMessage());
									}
							}
						}
						tvForce.setText("force is " + force);
					}
				}
			}
        	
        }
        
        Worker w = new Worker();
        Thread t = new Thread(w);
        t.start();
        //--------------------------------------------------------------------------------------------------*/   
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
		
	       //--------------------------------------------------------------------------------------------------
		//synchronized (lock) {
			if (theString != null) {
				buffer += theString;
				
				tokens = buffer.split("\r\n");
				
				for (int i = 0; i < tokens.length; i++ ) {
					if (i == (tokens.length -1) ) {
						if (!buffer.substring(buffer.length()-2, buffer.length()-1).equals("\r\n")){
							buffer = tokens[i];
						}
						else {
							buffer = "";
						}
					}
					else {
						try	{      
							force = Integer.parseInt(tokens[i]);
							tvForce.setText("force is " + force);
							System.out.println("Force equals " + force);
						}
						catch (NumberFormatException nfe) {
							System.out.println("NumberFormatException: " + nfe.getMessage());
						}
					}
				}
				
			}
			//System.out.println("Force equals " + force);
		//}
	       //--------------------------------------------------------------------------------------------------
		
	}

}
