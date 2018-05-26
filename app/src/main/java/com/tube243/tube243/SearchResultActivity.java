package com.tube243.tube243;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.tube243.tube243.adapters.SearchAdapter;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.entities.Search;
import com.tube243.tube243.processes.LocalTextTask;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SearchResultActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, LocalTextTask.ResultListener,
        View.OnFocusChangeListener{

    List<Search> searchList;
    private SearchAdapter searchAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SearchView searchView = (SearchView) toolbar.findViewById(R.id.searchView);
        searchView.setLayoutParams(new Toolbar.LayoutParams(Gravity.RIGHT));
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Rechercher ici");
        searchView.setOnQueryTextListener(this);
        searchView.setOnFocusChangeListener(this);
        searchView.performClick();
        // Toolbar back button
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        else if (getActionBar() != null)
        {
            getActionBar().setHomeButtonEnabled(true);
        }
        requestFocus(searchView);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        searchList = new LinkedList<>();
        searchAdapter = new SearchAdapter(searchList);
        recyclerView.setAdapter(searchAdapter);

        Intent intent = getIntent();
        if(intent.hasExtra("query"))
        {
            searchView.setQuery(intent.getStringExtra("query"),true);
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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
    public boolean onQueryTextSubmit(String query) {
        searchList.clear();
        searchAdapter.notifyDataSetChanged();
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Connexion...");
        progressDialog.show();
        LocalTextTask textTask = new LocalTextTask();
        textTask.setUrlString(Params.SERVER_HOST+"?controller=utilities&method=search-data&search="+query);
        textTask.setListener(this);
        textTask.execute();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
    }

    @Override
    public void onResult(Map<String, Object> result)
    {
        progressDialog.hide();
        Toast.makeText(SearchResultActivity.this,result.toString(),Toast.LENGTH_LONG).show();
        if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
        {
            if(result.containsKey("data"))
            {
                List<Map<String,Object>> data = (List<Map<String,Object>>)result.get("data");
                for(int i=0;i<data.size();i++)
                {
                    Map<String,Object> map = data.get(i);
                    String title = map.get("title").toString();
                    String subTitle = map.get("subTitle").toString();
                    String type = map.get("type").toString();
                    switch (type) {
                        case "artist":
                            type = Search.TYPE_ARTIST;
                            break;
                        case "group":
                            type = Search.TYPE_GROUP;
                            break;
                        default:
                            type = Search.TYPE_TUBE;
                            break;
                    }
                    Search search = new Search(title,subTitle,type);
                    searchList.add(search);
                }
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        
    }
}
