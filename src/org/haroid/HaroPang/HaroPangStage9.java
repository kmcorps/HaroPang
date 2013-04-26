package org.haroid.HaroPang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HaroPangStage9 extends Activity implements OnClickListener {
	private Button btPrev;
	private Button btNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.haropangstage9);
        
        TextView tv1 = (TextView) findViewById (R.id.textViewStage9);
        tv1.setText("HaroPang Stage 9");
        
        btPrev = (Button)findViewById(R.id.buttonStage9Prev);
        btNext = (Button)findViewById(R.id.buttonStage9Next);
        
        btPrev.setOnClickListener((OnClickListener) this);
        btNext.setOnClickListener((OnClickListener) this);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch (v.getId())
    	{
	    	case R.id.buttonStage9Prev:
	    		//Intent intentPrev = new Intent(this, HaroPangStage8.class);
	    		//startActivity(intentPrev);
	    		
	    		Button btPrev = (Button)findViewById(R.id.buttonStage9Prev);
	    		setResult(RESULT_CANCELED, (new Intent()).setAction(btPrev.getText().toString()));
	    		
	    		finish();
	    		break;
	    		
	    	case R.id.buttonStage9Next:
	    		//Intent intentNext = new Intent(this, HaroPangStage0.class);
	    		//startActivity(intentNext);
	    		
	    		Button btNext = (Button)findViewById(R.id.buttonStage9Next);
	    		setResult(RESULT_OK, (new Intent()).setAction(btNext.getText().toString()));
	    		
	    		finish();
	    		break;
    	}
	}
}
