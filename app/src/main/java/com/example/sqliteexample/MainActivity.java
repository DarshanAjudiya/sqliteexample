package com.example.sqliteexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.audiofx.EnvironmentalReverb;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    EditText name,roll,str;
    Button add,show,checkupdate;
    TextView dta;
    DatabaseHandler handler=new DatabaseHandler(this);
    public String[] PERMISSIONS={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.REQUEST_COMPANION_RUN_IN_BACKGROUND};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkpermision();

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
                task t=new task(getApplicationContext());
                t.execute();
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

    public class task extends AsyncTask<Void,Void,Void>
    {
        Context context;
        public task(Context context)
        {
            this.context=context;
        }
        @Override
        protected Void doInBackground(Void... voids) {
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
                JSONArray array=new JSONArray(data);
                JSONObject main=array.getJSONObject(0);
                JSONObject object=main.getJSONObject("apkData");
                long versioncode=object.getLong("versionCode");

                PackageInfo info=context.getPackageManager().getPackageInfo(getPackageName(),0);
                long appversioncode=-1;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    appversioncode = info.getLongVersionCode();
                }
                else {
                    appversioncode = info.versionCode;
                }

                System.out.println("Current version:" +
                        appversioncode+"\nserverappversion:"+versioncode);
             /*   File f=getApplicationContext().getFilesDir();
                    System.out.println(f.getAbsolutePath());*/
                if(appversioncode<=versioncode)
                {
                    url=new URL("https://github.com/DarshanAjudiya/sqliteexample/blob/master/app/release/app-release.apk?raw=true");
                    connection= (HttpURLConnection) url.openConnection();
                    //File dest=new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS),"app-release.apk");


                    File dest=new File(getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),"app-release.apk");
                  //  File dest=new File(getApplicationContext().getFilesDir(),"app-release.apk");
                   // System.out.println(Environment.getRootDirectory().canWrite());
                    System.out.println(dest.getAbsolutePath());

                    FileOutputStream outputStream=new FileOutputStream(dest,false);
                    stream= connection.getInputStream();
                    byte[] buffer=new byte[1024];
                    int length=0;
                    while((length=stream.read(buffer))!=-1) {
                        outputStream.write(buffer, 0, length);
                    }
                    outputStream.close();
                    Uri fileuri;
                    if(Build.VERSION.SDK_INT>Build.VERSION_CODES.M)
                    {
                        fileuri=getURI(getApplicationContext(),dest);
                        System.out.println(fileuri.toString());
                    }
                    else

                        fileuri=Uri.fromFile(dest);
                    Intent intent=new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileuri,"application/vnd.android.package-archive");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
                    intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }
            } catch (IOException | JSONException | PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        private Uri getURI(Context context,File dest) {
            return FileProvider.getUriForFile(getBaseContext(),getApplicationContext().getPackageName() + ".provider", dest);
        }
    }



    public void checkpermision()
    {
        int permission= ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,PERMISSIONS,1);
        }
        else
        {
            Toast.makeText(this, "Already granted", Toast.LENGTH_SHORT).show();
        }
    }
}
