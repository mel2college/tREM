package comt.REM.tamu.squeechat;

import java.io.OutputStream;
import java.util.ArrayList;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {
	private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	private BluetoothSocket mBluetoothSocket;
	private OutputStream outStream;
	private ArrayList<String> names;
	private ArrayList<String> addresses;
	private BTInfoAdapter mAdapter = new BTInfoAdapter(this, names, addresses);
	private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
	private ListView mListView;
	
	private static final int REQUEST_ENABLE_BT = 1;
	
	// Create a BroadcastReceiver for ACTION_FOUND
	public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if(BluetoothDevice.ACTION_FOUND.equals(action)){
				// get the BluetoothDevice object from the intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// add the name and address to an array adapter to show in a ListView
				names.add(device.getName());
				addresses.add(device.getAddress());
				mAdapter.updateAdapter(names, addresses);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mListView = (ListView) findViewById(R.id.list_view);
		mListView.setAdapter(mAdapter);
		registerReceiver(mReceiver, filter);
		
		// device does not support bluetooth, quit application
		if(mBluetoothAdapter == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Your device does not support Bluetooth.");
			builder.setCancelable(false);
			builder.setPositiveButton("Quit", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			builder.create().show();
		}
		if(!mBluetoothAdapter.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
		
		mBluetoothAdapter.startDiscovery();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		switch(requestCode){
		case REQUEST_ENABLE_BT:
			if(resultCode == Activity.RESULT_CANCELED){
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage("Bluetooth was not enabled. Enable now?");
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
					}
				});
				builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				builder.create().show();
			}
			break;
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		
		unregisterReceiver(mReceiver);
	}
}
