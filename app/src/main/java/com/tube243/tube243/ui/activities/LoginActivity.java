package com.tube243.tube243.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
//import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

import com.tube243.tube243.R;
import com.tube243.tube243.core.Single;
import com.tube243.tube243.customs.CustomPhoneNumberFormattingTextWatcher;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Country;
import com.tube243.tube243.processes.LocalTextTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by JonathanLesuperb on 2018/04/27.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener
{
    private ArrayAdapter<Country> dataAdapter;
    private AppCompatEditText countryCode;
    private List<Country> countries;
    private ProgressBar loadingBar;
    private AppCompatEditText phoneNumber;
    private AppCompatSpinner countriesSpr;
    private LocalData data;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        countries = new LinkedList<>();
        countries.add(new Country(1L," ","",""));
        dataAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item_country, countries);
        setContentView(R.layout.activity_login);
        phoneNumber = findViewById(R.id.phone_number);
        phoneNumber.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher());
        loadingBar = findViewById(R.id.loadingBar);
        countriesSpr = findViewById(R.id.android_material_design_spinner);
        countriesSpr.setAdapter(dataAdapter);
        countryCode = findViewById(R.id.country_code);

        AppCompatButton btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(this);

        data = new LocalData(getApplicationContext());

        countriesSpr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Country country = (Country)parent.getAdapter().getItem(position);
                if(country!=null)
                {
                    CharSequence sequence = country.getCode();
                    countryCode.setText(sequence);
                    phoneNumber.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher("CD"));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });
        loadCountries();
    }

    private void loadCountries()
    {
        loadingBar.setVisibility(View.VISIBLE);
        LocalTextTask task = new LocalTextTask();
        task.setUrlString(Params.SERVER_HOST+"?controller=countries&method=get-countries");
        task.setListener(new LocalTextTask.ResultListener() {
            @Override
            public void onResult(Map<String, Object> result)
            {
                loadingBar.setVisibility(View.GONE);
                try
                {
                    if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
                    {
                        countries.clear();
                        List<Map<String,Object>> countriesList = (List<Map<String,Object>>)  result.get("countries");
                        for(int i=0;i<countriesList.size();i++){
                            Map<String,Object> object = countriesList.get(i);
                            countries.add(new Country(Long.parseLong(object.get("id").toString()),
                                    object.get("name").toString(),
                                    object.get("code").toString(),
                                    object.get("pattern").toString()));
                        }
                        if(countries.size()>0)
                        {
                            dataAdapter.notifyDataSetChanged();
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
                    Single.parsingException(LoginActivity.this,ex);
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
        Country country = (Country)countriesSpr.getSelectedItem();
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

                        Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
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
                catch (Exception ex)
                {
                    Single.parsingException(LoginActivity.this,ex);
                }
            }
        });
        task.execute();
    }
}
