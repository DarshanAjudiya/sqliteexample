package com.example.sqliteexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    EditText name,roll,str;
    Button add,show,checkupdate;
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
        checkupdate=findViewById(R.id.checkupdate);

        checkupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    URL url=new URL("https://github.com/DarshanAjudiya/sqliteexample/raw/master/app/release/output.json");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream stream=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
                    String line=reader.readLine();
                    String data="";
                    while(line!=null)
                    {
                        data+=line;
                        line=reader.readLine();
                    }
                    System.out.println(data);
                    JSONObject object=new JSONObject(data);
                    int versioncode=object.getInt("versionCode");

                    PackageInfo info=getApplicationContext().getPackageManager().getPackageInfo(getPackageName(),0);
                    long appversioncode=-1;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        appversioncode = info.getLongVersionCode();
                    }
                    else {
                        appversioncode = info.versionCode;
                    }

                    System.out.println("Current version:" +
                            appversioncode+"\nserverappversion:"+versioncode);


                } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

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
