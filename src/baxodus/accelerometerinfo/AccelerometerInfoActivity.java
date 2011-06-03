package baxodus.accelerometerinfo;

import android.view.Gravity;
import android.view.View;
import android.os.Bundle;
import android.app.Activity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

import android.widget.Toast;
import android.widget.Spinner;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;

public class AccelerometerInfoActivity extends Activity implements SensorEventListener {
	/* SeekBar components */
	private SeekBar xAxisBar;
	private SeekBar yAxisBar;
	private SeekBar zAxisBar;
	
	/* TextView components */
	private TextView xAxisValue;
	private TextView yAxisValue;
	private TextView zAxisValue;

	/* the Spinner component */
	private Spinner delayRateChooser;
	
	/* the Toast object used when the delay rate changes */
	private Toast toastObject;
	
	/* Sensor Manager */
	private SensorManager sensorManager = null;
	
	/* Sensor Accelerometer Rates */
	private static final int delayRates[] = {
		SensorManager.SENSOR_DELAY_NORMAL,
		SensorManager.SENSOR_DELAY_UI,
		SensorManager.SENSOR_DELAY_GAME,
		SensorManager.SENSOR_DELAY_FASTEST
	};
	private static final String delayRatesDescription[] = {
		"Normal", "UI", "Game", "Fastest"
	};
	private int curDelayRate;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.main);
        
        // retrieve all the needed components
        xAxisBar = (SeekBar)findViewById(R.id.xAxisBar);
        yAxisBar = (SeekBar)findViewById(R.id.yAxisBar);
        zAxisBar = (SeekBar)findViewById(R.id.zAxisBar);
        xAxisValue = (TextView)findViewById(R.id.xAxisValue);
        yAxisValue = (TextView)findViewById(R.id.yAxisValue);
        zAxisValue = (TextView)findViewById(R.id.zAxisValue);
        
        // populate the spinner
        delayRateChooser = (Spinner)findViewById(R.id.delayRateChooser);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, 
        		R.array.delay_rates, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delayRateChooser.setAdapter(adapter);
        
        // set the action to perform when an item is selected
        delayRateChooser.setOnItemSelectedListener(new OnItemSelectedListener() {
        	@Override
        	public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
        		if (curDelayRate != position) {
	        		curDelayRate = position;
	        		registerListener();
	        		
	        		// show a toast message
	        		toastObject = Toast.makeText(AccelerometerInfoActivity.this, 
	        				"Delay rate changed to '" + delayRatesDescription[position] + "' mode",
	        				Toast.LENGTH_SHORT);
	        		toastObject.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 0);
	        		toastObject.show();
        		}
        	}
        	
        	@Override
        	public void onNothingSelected(AdapterView<?> parentView) {
        		// DO NOTHING
        	}
        });
        
        // disable all the SeekBars and set the max value to 20 (it should be 19.62)
        xAxisBar.setEnabled(false);
        yAxisBar.setEnabled(false);
        zAxisBar.setEnabled(false);
        xAxisBar.setMax(20);
        yAxisBar.setMax(20);
        zAxisBar.setMax(20);
        
        // set the current delay rate to the NORMAL DELAY
        curDelayRate = 0;
    }
    
    /*
     * This method will update SeekBars with the current values of the accelerometer sensor. 
     * @see android.hardware.SensorEventListener#onSensorChanged(android.hardware.SensorEvent)
     */
    public void onSensorChanged(SensorEvent sensorEvent) {	
    	synchronized (this) {
    		if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
    			// set the value as the text of every TextView
    			xAxisValue.setText(Float.toString(sensorEvent.values[0]));
    			yAxisValue.setText(Float.toString(sensorEvent.values[1]));
    			zAxisValue.setText(Float.toString(sensorEvent.values[2]));
    			// set the value on to the SeekBar
    			xAxisBar.setProgress((int)(sensorEvent.values[0]+9.81F));
    			yAxisBar.setProgress((int)(sensorEvent.values[1]+9.81F));
    			zAxisBar.setProgress((int)(sensorEvent.values[2]+9.81F));
    		}
    	}
    }    
	
    /*** NOT IMPLEMENTED YET ***/
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    	/* DO NOTHING */
    }
    
	/**
	 * Register this class as sensor listener with the current delay rate.
	 */
    public void registerListener() {
    	sensorManager.unregisterListener(this);
    	sensorManager.registerListener(this, 
    			sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
    			delayRates[curDelayRate]);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	registerListener();
    }
    
    @Override
    protected void onStop() {
    	sensorManager.unregisterListener(this);
    	super.onStop();
    }
}