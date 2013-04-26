package org.haroid.HaroPang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HaroPangStage3 extends Activity implements OnClickListener {
	private Button btPrev;
	private Button btNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.haropangstage3);
        
        TextView tv1 = (TextView) findViewById (R.id.textViewStage3);
        tv1.setText("HaroPang Stage 3");
        
        btPrev = (Button)findViewById(R.id.buttonStage3Prev);
        btNext = (Button)findViewById(R.id.buttonStage3Next);
        
        btPrev.setOnClickListener((OnClickListener) this);
        btNext.setOnClickListener((OnClickListener) this);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch (v.getId())
    	{
	    	case R.id.buttonStage3Prev:
	    		//Intent intentPrev = new Intent(this, HaroPangStage2.class);
	    		//startActivity(intentPrev);
	    		
	    		Button btPrev = (Button)findViewById(R.id.buttonStage3Prev);
	    		setResult(RESULT_CANCELED, (new Intent()).setAction(btPrev.getText().toString()));
	    		
	    		finish();
	    		break;
	    		
	    	case R.id.buttonStage3Next:
	    		//Intent intentNext = new Intent(this, HaroPangStage4.class);
	    		//startActivity(intentNext);
	    		
	    		Button btNext = (Button)findViewById(R.id.buttonStage3Next);
	    		setResult(RESULT_OK, (new Intent()).setAction(btNext.getText().toString()));
	    		
	    		finish();
	    		break;
    	}
	}
}
