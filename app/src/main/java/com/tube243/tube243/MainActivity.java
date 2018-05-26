package com.tube243.tube243;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.widget.Toast;

import com.tube243.tube243.core.Single;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.ui.activities.HomeActivity;

public class MainActivity extends AppCompatActivity
{
    private LocalData data;
    private Single single;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatTextView slogan = findViewById(R.id.slogan);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/Aliquam.ttf");
        slogan.setTypeface(typeface);
        data = new LocalData(getApplicationContext());
        single = Single.getInstance();
        if(checkWriteExternalPermission())
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run() {
                    launch();
                }
            },3000);
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
            else
            {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults)
    {
        switch (requestCode)
        {
            case 1:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(MainActivity.this, "Permission accordée de lire et d'écrire dans la base de données", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable()
                    {
                        @Override
                        public void run() {
                            launch();
                        }
                    },3000);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Permission réfusée de lire et d'écrire dans la base de données", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private boolean checkWriteExternalPermission()
    {
        String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    private void launch()
    {
        single.setParentFolder(getApplicationContext().getCacheDir());
        Intent intent;
        if(data.getLong("userId")!=null)
        {
            if(data.getString("firstName")!=null && data.getString("lastName")!=null)
            {
                intent = new Intent(getApplicationContext(),HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                intent = new Intent(getApplicationContext(),ProfileEditorActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
        else
        {
            intent = new Intent(getApplicationContext(),LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

}
