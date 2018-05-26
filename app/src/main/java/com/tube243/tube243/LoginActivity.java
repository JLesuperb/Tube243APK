package com.tube243.tube243;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.tube243.tube243.core.Single;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Country;
import com.tube243.tube243.processes.LocalTextTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{

    private AppCompatEditText countryCode;
    private AppCompatEditText phoneNumber;

    private Spinner countryList;
    private AppCompatButton btnConnect;
    private LocalData data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setKeys();
        data = new LocalData(getApplicationContext());
    }

    private void setKeys()
    {
        countryCode = findViewById(R.id.country_code);
        phoneNumber = findViewById(R.id.phone_number);
        countryList = findViewById(R.id.country_list);
        countryList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(countryList.getSelectedItem()!=null)
                {
                    Country country = (Country)countryList.getSelectedItem();
                    CharSequence sequence = country.getCode();
                    countryCode.setText(sequence);
                }
                /*AppCompatSpinner spinner = (AppCompatSpinner)view;
                if(spinner.getSelectedItem()!=null){
                    Country country = (Country)spinner.getSelectedItem();
                    CharSequence sequence = country.getCode();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        loadCountries();
        btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);
        phoneNumber.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if(phoneNumber.getText().toString().length()>=9 && countryList.getSelectedItem()!=null)
                    btnConnect.setEnabled(true);
                else
                    btnConnect.setEnabled(false);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });
    }

    private void loadCountries() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Chargement...");
        progressDialog.show();
        LocalTextTask task = new LocalTextTask();
        task.setUrlString(Params.SERVER_HOST+"?controller=countries&method=get-countries");
        task.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result)
            {
                progressDialog.dismiss();
                try{
                    if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
                    {
                        List<Country> countries = new LinkedList<>();
                        List<Map<String,Object>> countriesList = (List<Map<String,Object>>)  result.get("countries");
                        for(int i=0;i<countriesList.size();i++){
                            Map<String,Object> object = countriesList.get(i);
                            countries.add(new Country(Long.parseLong(object.get("id").toString()),
                                    object.get("name").toString(),
                                    object.get("code").toString(),
                                    object.get("pattern").toString()));
                        }
                        if(countries.size()>0){
                            ArrayAdapter<Country> dataAdapter = new ArrayAdapter<>(LoginActivity.this, R.layout.spinner_item, countries);
                            countryList.setAdapter(dataAdapter);
                        }
                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
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
                                loadCountries();
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
                catch (Exception ex){
                    Single.parsingException(getApplicationContext(),ex);
                }
            }
        });
        task.execute();
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnConnect:
                submitForm();
                break;
        }
    }

    private void submitForm() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connexion...");
        progressDialog.show();
        LocalTextTask task = new LocalTextTask();
        task.setUrlString(Params.SERVER_HOST+"?controller=users&method=sign-up");
        Country country = (Country)countryList.getSelectedItem();
        final String phone = country.getCode()+phoneNumber.getText().toString();
        task.getParams().put("country_id",country.getId());
        task.getParams().put("phone_number",phone);
        task.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result) {
                progressDialog.dismiss();
                try{
                    if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
                    {
                        if(!(Boolean) result.get("isNewUser"))
                        {
                            data.setLong("userId",Long.parseLong(result.get("userId").toString()));
                            data.setString("firstName",result.get("firstName").toString());
                            data.setString("lastName",result.get("lastName").toString());
                        }
                        data.setLong("userId",Long.parseLong(result.get("userId").toString()));
                        data.setString("phoneNumber",phone);

                        Intent intent = new Intent(getApplicationContext(),ProfileEditorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                    else
                    {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
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
                                submitForm();
                            }
                        });
                        alertDialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alertDialog.show();
                    }
                }
                catch (Exception ignored){

                }
            }
        });
        task.execute();
    }
}
