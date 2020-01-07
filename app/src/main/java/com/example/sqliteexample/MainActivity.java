package com.example.sqliteexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    EditText name,roll,str;
    Button add,show;
    TextView dta;
    DatabaseHandler handler=new DatabaseHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name=(EditText)findViewById(R.id.name);
        roll=(EditText)findViewById(R.id.roll);
        str=(EditText)findViewById(R.id.stream);
        add=(Button)findViewById(R.id.insert);
        show=(Button)findViewById(R.id.disp);
        dta=(TextView)findViewById(R.id.data);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                  long i=handler.insert(name.getText().toString(),roll.getText().toString(),str.getText().toString());
                  if(i==-1)
                      dta.setText("Insertion Failed");
                  else
                      dta.setText("Insetion Successfull");
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c=handler.getdata();
                dta.setText("");
                if (c.moveToFirst())
                {
                    do {
                        dta.append("id:" + c.getInt(0) + "\n" +
                                "name:" + c.getString(1) + "\n" +
                                "roll no:" + c.getString(2) + "\n" +
                                "stream:" + c.getString(3) + "\n\n");
                    }while(c.moveToNext());
                }
            }
        });
    }
}
