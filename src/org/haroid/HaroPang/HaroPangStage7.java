package org.haroid.HaroPang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HaroPangStage7 extends Activity implements OnClickListener {
	private Button btPrev;
	private Button btNext;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.haropangstage7);
        
        TextView tv1 = (TextView) findViewById (R.id.textViewStage7);
        tv1.setText("HaroPang Stage 7");
        
        btPrev = (Button)findViewById(R.id.buttonStage7Prev);
        btNext = (Button)findViewById(R.id.buttonStage7Next);
        
        btPrev.setOnClickListener((OnClickListener) this);
        btNext.setOnClickListener((OnClickListener) this);
    }
    
    public void onClick(View v) {
		// TODO Auto-generated method stub
    	
    	switch (v.getId())
    	{
	    	case R.id.buttonStage7Prev:
	    		//Intent intentPrev = new Intent(this, HaroPangStage6.class);
	    		//startActivity(intentPrev);
	    		
	    		Button btPrev = (Button)findViewById(R.id.buttonStage7Prev);
	    		setResult(RESULT_CANCELED, (new Intent()).setAction(btPrev.getText().toString()));
	    		
	    		finish();
	    		break;
	    		
	    	case R.id.buttonStage7Next:
	    		//Intent intentNext = new Intent(this, HaroPangStage8.class);
	    		//startActivity(intentNext);
	    		
	    		Button btNext = (Button)findViewById(R.id.buttonStage7Next);
	    		setResult(RESULT_OK, (new Intent()).setAction(btNext.getText().toString()));
	    		
	    		finish();
	    		break;
    	}
	}
}
