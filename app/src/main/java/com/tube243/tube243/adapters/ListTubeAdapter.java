package com.tube243.tube243.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tube243.tube243.R;
import com.tube243.tube243.entities.Tube;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JonathanLesuperb on 2018/05/27.
 */

public class ListTubeAdapter extends BaseAdapter
{

    private Context context;
    private List<Tube> tubes;

    public ListTubeAdapter(Context context)
    {
        this.context = context;
        tubes = new ArrayList<>();
    }

    public void add(Tube tube)
    {
        tubes.add(tube);
    }

    public void addAll(List<Tube> tubes)
    {
        this.tubes.addAll(tubes);
    }

    @Override
    public int getCount()
    {
        return tubes.size();
    }

    @Override
    public Object getItem(int position)
    {
        return tubes.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder;

        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_tube_item, parent, false);
            viewHolder.txtName = convertView.findViewById(R.id.aNametxt);
            viewHolder.txtVersion = convertView.findViewById(R.id.aVersiontxt);
            viewHolder.icon = convertView.findViewById(R.id.appIconIV);

            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.txtName.setText("Android");
        viewHolder.txtVersion.setText("Version: ");

        return convertView;
    }

    static class ViewHolder
    {

        TextView txtName;
        TextView txtVersion;
        ImageView icon;

    }
}
