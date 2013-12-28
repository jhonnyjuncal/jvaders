package com.jhonny.jvaders;

import com.jhonny.jvaders.R;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


public class OpcionesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opciones);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.opciones, menu);
		return true;
	}

}
