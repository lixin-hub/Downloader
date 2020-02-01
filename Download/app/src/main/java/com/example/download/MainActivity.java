package com.example.download;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

	TextView  text;
	EditText edit;
	Button bt;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		text=findViewById(R.id.activity_mainTextView);
		edit=findViewById(R.id.activity_mainEditText);
		bt=findViewById(R.id.activity_mainButton);
		bt.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View p1)
				{
					String path= edit.getText().toString();
					try
					{
				new DownloadUtil(MainActivity.this).download(path, 3,text);
				
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			});
		

    }


}
