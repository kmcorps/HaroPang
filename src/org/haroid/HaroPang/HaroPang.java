package org.haroid.HaroPang;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays the current chat session.
 */
public class HaroPang extends Activity {
	// Debugging
	private static final String TAG = "HaroPang";
	private static final boolean D = true;

	// Message types sent from the HaroPangService Handler
	public static final int MESSAGE_STATE_CHANGE = 1;
	public static final int MESSAGE_READ = 2;
	public static final int MESSAGE_WRITE = 3;
	public static final int MESSAGE_DEVICE_NAME = 4;
	public static final int MESSAGE_TOAST = 5;
	public static final int MESSAGE_PARSE_DATA = 6;
	public static final int MESSAGE_PARSE_PACKET_DATA = 7;
	public static final int MESSAGE_CHANGE_ACTIVITY = 8;

	// Key names received from the HaroPangService Handler
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";
	public static final String PARSE = "parse";

	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static final int REQUEST_STAGE_RESULT = 3;

	// Layout Views
	private TextView mTitle;
	private ListView mConversationView;
	private EditText mOutEditText;
	private Button mSendButton;

	// Name of the connected device
	private String mConnectedDeviceName = null;
	// Array adapter for the conversation thread
	private ArrayAdapter<String> mConversationArrayAdapter;
	// String buffer for outgoing messages
	private StringBuffer mOutStringBuffer;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;
	// Member object for the chat services
	private HaroPangService mHaroPangService = null;
	
	// global variable
	private static byte[] mActivityCmd = new byte[12];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (D)
			Log.e(TAG, "+++ ON CREATE +++");

		// Set up the window layout
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		// Set up the custom title
		mTitle = (TextView) findViewById(R.id.title_left_text);
		mTitle.setText(R.string.app_name);
		mTitle = (TextView) findViewById(R.id.title_right_text);

		// Get local Bluetooth adapter
		// 釉붾（�ъ뒪 �대럞���앹꽦.
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		for (int i = 0; i < 12; i++)
			mActivityCmd[i] = 0;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (D)
			Log.e(TAG, "++ ON START ++");

		// If BT is not on, request that it be enabled.
		// setupChat() will then be called during onActivityResult
		// 釉붾（�ъ뒪 �쒖꽦���뺤씤.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			// Otherwise, setup the chat session
		} else {
			if (mHaroPangService == null)
				setupChat();
		}
	}

	@Override
	public synchronized void onResume() {
		super.onResume();
		if (D)
			Log.e(TAG, "+ ON RESUME +");

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (mHaroPangService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (mHaroPangService.getState() == HaroPangService.STATE_NONE) {
				// Start the Bluetooth chat services
				mHaroPangService.start();
			}
		}
	}

	private void setupChat() {
		Log.d(TAG, "setupChat()");

		// Initialize the array adapter for the conversation thread
		mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
		mConversationView = (ListView) findViewById(R.id.in);
		mConversationView.setAdapter(mConversationArrayAdapter);

		// Initialize the compose field with a listener for the return key
		mOutEditText = (EditText) findViewById(R.id.edit_text_out);
		mOutEditText.setOnEditorActionListener(mWriteListener);

		// Initialize the send button with a listener that for click events
		mSendButton = (Button) findViewById(R.id.button_send);
		mSendButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Send a message using content of the edit text widget
				TextView view = (TextView) findViewById(R.id.edit_text_out);
				String message = view.getText().toString();
				sendMessage(message);
			}
		});

		// Initialize the HaroPangService to perform bluetooth connections
		mHaroPangService = new HaroPangService(this, mHandler);

		// Initialize the buffer for outgoing messages
		mOutStringBuffer = new StringBuffer("");
	}

	@Override
	public synchronized void onPause() {
		super.onPause();
		if (D)
			Log.e(TAG, "- ON PAUSE -");
	}

	@Override
	public void onStop() {
		super.onStop();
		if (D)
			Log.e(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Stop the Bluetooth chat services
		if (mHaroPangService != null)
			mHaroPangService.stop();
		if (D)
			Log.e(TAG, "--- ON DESTROY ---");
	}

	private void ensureDiscoverable() {
		if (D)
			Log.d(TAG, "ensure discoverable");
		// �ㅼ펲 紐⑤뱶 �ㅼ젙.

		if (mBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}

	/**
	 * Sends a message.
	 * 
	 * @param message
	 *            A string of text to send.
	 */
	private void sendMessage(String message) {
		// Check that we're actually connected before trying anything
		// �곌껐�곹깭瑜��뺤씤�섍퀬 �곌껐 �섏뼱�덈떎硫�硫붿떆吏�� �꾩넚.
		if (mHaroPangService.getState() != HaroPangService.STATE_CONNECTED) {
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if (message.length() > 0) {
			// Get the message bytes and tell the HaroPangService to write
			// byte[] send = message.getBytes();
			if (message.equals(String.valueOf(CmdList.SERVO_0))) {
				mHaroPangService.write(CmdList.Servo0);
			} else if (message.equals(String.valueOf(CmdList.SERVO_90))) {
				mHaroPangService.write(CmdList.Servo90);
			} else if (message.equals(String.valueOf(CmdList.SERVO_180))) {
				mHaroPangService.write(CmdList.Servo180);
			} else if (message.equals(String.valueOf(CmdList.DC_FORWARD))) {
				mHaroPangService.write(CmdList.DCForward);
			} else if (message.equals(String.valueOf(CmdList.DC_BACKWARD))) {
				mHaroPangService.write(CmdList.DCBackward);
			}

			// Reset out string buffer to zero and clear the edit text field
			mOutStringBuffer.setLength(0);
			mOutEditText.setText(mOutStringBuffer);
		}
	}

	// The action listener for the EditText widget, to listen for the return key
	private TextView.OnEditorActionListener mWriteListener = new TextView.OnEditorActionListener() {

		public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
			// If the action is a key-up event on the return key, send the
			// message

			if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
				String message = view.getText().toString();
				sendMessage(message);
			}
			if (D)
				Log.i(TAG, "END onEditorAction");
			return true;
		}
	};

	// The Handler that gets information back from the HaroPangService
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case MESSAGE_STATE_CHANGE:

				if (D)
					Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
				switch (msg.arg1) {
				case HaroPangService.STATE_CONNECTED:
					mTitle.setText(R.string.title_connected_to);
					mTitle.append(mConnectedDeviceName);
					mConversationArrayAdapter.clear();
					break;

				case HaroPangService.STATE_CONNECTING:
					mTitle.setText(R.string.title_connecting);
					break;

				case HaroPangService.STATE_LISTEN:
				case HaroPangService.STATE_NONE:
					mTitle.setText(R.string.title_not_connected);
					break;

				}
				break;

			case MESSAGE_WRITE:
				byte[] writeBuf = (byte[]) msg.obj;
				// construct a string from the buffer
				String writeMessage = new String(writeBuf);
				mConversationArrayAdapter.add("Me:  "
 + bytesToHexString(writeMessage));
				break;

			case MESSAGE_READ:
				byte[] readBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String readMessage = new String(readBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
						+ bytesToHexString(readMessage));
				break;

			case MESSAGE_DEVICE_NAME:
				// save the connected device's name
				mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
				Toast.makeText(getApplicationContext(), "Connected to " + mConnectedDeviceName,
						Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_TOAST:
				Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
						Toast.LENGTH_SHORT).show();
				break;

			case MESSAGE_PARSE_DATA:
				mConversationArrayAdapter.add(mConnectedDeviceName + ": PM ( "
						+ msg.getData().getString(PARSE)
						+ " ) ");

				break;

			case MESSAGE_PARSE_PACKET_DATA:
				byte[] packetBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				String packetMessage = new String(packetBuf, 0, msg.arg1);
				mConversationArrayAdapter.add(mConnectedDeviceName + ":  "
						+ bytesToHexString(packetMessage));
				break;

			case MESSAGE_CHANGE_ACTIVITY:
				byte[] activityBuf = (byte[]) msg.obj;
				// construct a string from the valid bytes in the buffer
				for (int i = 0; i < 6; i++)
					mActivityCmd[i] = activityBuf[i];
				
				onSetActivity(activityBuf);
				
				break;
			}
		}
	};
	
	public void onSetActivity(byte[] activityBuf)
	{
		Intent intentNext = null;
		switch ((byte)activityBuf[3])
		{
			case (byte)0x60:
				intentNext = new Intent(this, HaroPangStage0.class);
				break;
			case (byte)0x61:
				intentNext = new Intent(this, HaroPangStage1.class);
				break;
			case (byte)0x62:
				intentNext = new Intent(this, HaroPangStage2.class);
				break;
			case (byte)0x63:
				intentNext = new Intent(this, HaroPangStage3.class);
				break;
			case (byte)0x64:
				intentNext = new Intent(this, HaroPangStage4.class);
				break;
			case (byte)0x65:
				intentNext = new Intent(this, HaroPangStage5.class);
				break;
			case (byte)0x66:
				intentNext = new Intent(this, HaroPangStage6.class);
				break;
			case (byte)0x67:
				intentNext = new Intent(this, HaroPangStage7.class);
				break;
			case (byte)0x68:
				intentNext = new Intent(this, HaroPangStage8.class);
				break;
			case (byte)0x69:
				intentNext = new Intent(this, HaroPangStage9.class);
				break;
		}
		
		if (intentNext != null)
		{
			if (mHaroPangService.getActivityState() != mHaroPangService.STATE_ACTIVITY_START)
				mHaroPangService.setActivityState(mHaroPangService.STATE_ACTIVITY_START);
			
			startActivityForResult(intentNext, REQUEST_STAGE_RESULT);
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (D)
			Log.d(TAG, "onActivityResult " + resultCode);
		switch (requestCode) {

		case REQUEST_CONNECT_DEVICE:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				// Get the device MAC address
				String address = data.getExtras()
						.getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				// Get the BLuetoothDevice object
				BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
				// Attempt to connect to the device
				mHaroPangService.connect(device);
			}
			break;

		case REQUEST_ENABLE_BT:
			// When the request to enable Bluetooth returns
			if (resultCode == Activity.RESULT_OK) {
				// Bluetooth is now enabled, so set up a chat session
				setupChat();
			} else {
				// User did not enable Bluetooth or an error occured
				Log.d(TAG, "BT not enabled");
				Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
				finish();
			}
			break;
			
		case REQUEST_STAGE_RESULT:
			if (D)
				Log.d(TAG, "Stage ActivityResult " + resultCode);
			
			if (resultCode == Activity.RESULT_OK) {		
				if (mActivityCmd[3] == (byte)0x69)
					mActivityCmd[3] = (byte)0x60;
				else
					mActivityCmd[3] = (byte) ((byte)mActivityCmd[3] + (byte)1);
				
				mHaroPangService.write(mActivityCmd);
			}
						
			if (resultCode == Activity.RESULT_CANCELED){			
				if (mActivityCmd[3] == (byte)0x60) {
					for (int i = 0; i < 6; i++)
						mActivityCmd[i] = 0;
					
					mHaroPangService.setActivityState(mHaroPangService.STATE_ACTIVITY_STOP);
					break;
				} else {
					mActivityCmd[3] = (byte) ((byte)mActivityCmd[3] - (byte)1);
				}
			}	
			
			if (resultCode == Activity.RESULT_OK || resultCode == Activity.RESULT_CANCELED)
				onSetActivity(mActivityCmd);
			
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.option_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.scan:
			// Launch the DeviceListActivity to see devices and do scan
			Intent serverIntent = new Intent(this, DeviceListActivity.class);
			startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
			return true;
		case R.id.discoverable:
			// Ensure this device is discoverable by others
			ensureDiscoverable();
			return true;
		}
		return false;
	}

	public static String bytesToHexString(String msg) {
		byte[] bytes = msg.getBytes();
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X ", b & 0xFF));
		}
		return sb.toString();
	}

}

