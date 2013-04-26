package org.haroid.HaroPang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HaroPangStage4 extends Activity implements OnClickListener {
	private Button btPrev;
	private Button btNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.haropangstage4);
        
        TextView tv1 = (TextView) findViewById (R.id.textViewStage4);
        tv1.setText("HaroPang Stage 4");
        
        btPrev = (Button)findViewById(R.id.buttonStage4Prev);
        btNext = (Button)findViewById(R.id.buttonStage4Next);
        
        btPrev.setOnClickListener((OnClickListener) this);
        btNext.setOnClickListener((OnClickListener) this);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch (v.getId())
    	{
	    	case R.id.buttonStage4Prev:
	    		//Intent intentPrev = new Intent(this, HaroPangStage3.class);
	    		//startActivity(intentPrev);
	    		
	    		Button btPrev = (Button)findViewById(R.id.buttonStage4Prev);
	    		setResult(RESULT_CANCELED, (new Intent()).setAction(btPrev.getText().toString()));
	    		
	    		finish();
	    		break;
	    		
	    	case R.id.buttonStage4Next:
	    		//Intent intentNext = new Intent(this, HaroPangStage5.class);
	    		//startActivity(intentNext);
	    		
	    		Button btNext = (Button)findViewById(R.id.buttonStage4Next);
	    		setResult(RESULT_OK, (new Intent()).setAction(btNext.getText().toString()));
	    		
	    		finish();
	    		break;
    	}
	}
}
