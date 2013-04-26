package org.haroid.HaroPang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HaroPangStage6 extends Activity implements OnClickListener {
	private Button btPrev;
	private Button btNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.haropangstage6);
        
        TextView tv1 = (TextView) findViewById (R.id.textViewStage6);
        tv1.setText("HaroPang Stage 6");
        
        btPrev = (Button)findViewById(R.id.buttonStage6Prev);
        btNext = (Button)findViewById(R.id.buttonStage6Next);
        
        btPrev.setOnClickListener((OnClickListener) this);
        btNext.setOnClickListener((OnClickListener) this);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch (v.getId())
    	{
	    	case R.id.buttonStage6Prev:
	    		//Intent intentPrev = new Intent(this, HaroPangStage5.class);
	    		//startActivity(intentPrev);
	    		
	    		Button btPrev = (Button)findViewById(R.id.buttonStage6Prev);
	    		setResult(RESULT_CANCELED, (new Intent()).setAction(btPrev.getText().toString()));
	    		
	    		finish();
	    		break;
	    		
	    	case R.id.buttonStage6Next:
	    		//Intent intentNext = new Intent(this, HaroPangStage7.class);
	    		//startActivity(intentNext);
	    		
	    		Button btNext = (Button)findViewById(R.id.buttonStage6Next);
	    		setResult(RESULT_OK, (new Intent()).setAction(btNext.getText().toString()));
	    		
	    		finish();
	    		break;
    	}
	}
}
