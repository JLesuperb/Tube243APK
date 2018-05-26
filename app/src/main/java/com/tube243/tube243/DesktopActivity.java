package com.tube243.tube243;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.tube243.tube243.data.Params;
import com.tube243.tube243.processes.LocalTextTask;

import java.util.Map;

public class DesktopActivity extends AppCompatActivity implements LocalTextTask.ResultListener
{
    private AppCompatTextView presenterName;
    private AppCompatTextView presenterPhoneNumber;
    private AppCompatTextView presentionChannel;
    private AppCompatTextView presentionFreq;
    private AppCompatTextView presentionTime1;
    private AppCompatTextView presentionTime2;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desktop);
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else if(getActionBar()!=null)
            getActionBar().setHomeButtonEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connexion...");
        progressDialog.show();

        presenterName = findViewById(R.id.presenterName);
        presenterPhoneNumber = findViewById(R.id.presenterPhoneNumber);
        presentionChannel = findViewById(R.id.presentionChannel);
        presentionFreq = findViewById(R.id.presentionFreq);
        presentionTime1 = findViewById(R.id.presentionTime1);
        presentionTime2 = findViewById(R.id.presentionTime2);

        presenterPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(presenterPhoneNumber.getText()))
                {
                    if (ActivityCompat.checkSelfPermission(DesktopActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    String phoneNumber = presenterPhoneNumber.getText().toString();
                    phoneNumber = phoneNumber.replace("-","");
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phoneNumber)));
                }
            }
        });
        launch();
    }

    private void launch() {
        LocalTextTask task = new LocalTextTask();
        task.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=info-tv");
        task.setListener(this);
        task.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResult(Map<String, Object> result)
    {
        progressDialog.dismiss();
        if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
        {
            /*Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(),result.toString(),Toast.LENGTH_LONG).show();*/
            presenterName.setText(result.get("presenterName").toString());
            presenterPhoneNumber.setText(result.get("presenterPhoneNumber").toString());
            presentionChannel.setText(result.get("channel").toString());
            presentionFreq.setText(result.get("frequency").toString());
            //presentionTime1.setText(result.get("error").toString());
            presentionTime2.setText(result.get("replay").toString());
        }
        else{
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DesktopActivity.this);
            alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
            alertDialog.setTitle("Erreurs");
            String msg = "Veuillez vérifier votre connexion internet";
            alertDialog.setMessage(msg);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Réessayer", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    launch();
                }
            });
            alertDialog.setNegativeButton("Quitter", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alertDialog.show();
        }
    }
}
