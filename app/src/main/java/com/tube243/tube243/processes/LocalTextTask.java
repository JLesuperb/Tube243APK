package com.tube243.tube243.processes;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.webkit.MimeTypeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by JonathanLesuperb on 2/14/2017.
 */

public class LocalTextTask extends AsyncTask<Void,Void,String>
{
    private final Map<String, Object> params;

    public Map<String, Object> getParams() {
        return params;
    }

    public interface ResultListener
    {
        void onResult(Map<String, Object> result);
    }

    public LocalTextTask()
    {
        params = new HashMap<>();
    }

    private String urlString;
    private ResultListener listener;

    public void setListener(ResultListener listener)
    {
        this.listener = listener;
    }
    public void setUrlString(String urlString)
    {
        this.urlString = urlString;
    }

    private Map<String,Object> toMap(JSONObject jsonObject) throws JSONException
    {
        Map<String,Object> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            Object value = jsonObject.get(key);
            if(value instanceof JSONArray)
            {
                value = toList((JSONArray)value);
            }
            else if(value instanceof JSONObject)
            {
                value = toMap((JSONObject)value);
            }
            map.put(key,value);
        }
        return map;
    }

    private List<Object> toList(JSONArray jsonArray) throws JSONException
    {
        List<Object>list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            Object value = jsonArray.get(i);

            if(value instanceof JSONArray)
                value = toList((JSONArray)value);
            else if(value instanceof JSONObject)
                value = toMap((JSONObject)value);
            list.add(value);
        }
        return list;
    }

    private HostnameVerifier vefiry(){
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("tube243.com", session);
            }
        };
    }

    @NonNull
    private String stringFromURL(String urlString) throws IOException
    {
        String crlf = "\r\n";
        String twoHyphens = "--";
        String boundary = "----WebKitFormBoundarybCnKMUTVeIDZL3aL";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        URL url = new URL(urlString);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        connection.setHostnameVerifier(vefiry());
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setInstanceFollowRedirects(true);
        connection.setUseCaches(false);
        connection.setConnectTimeout(5000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Connection","Keep-Alive");
        connection.setRequestProperty("Cache-Control","no-cache");
        connection.setRequestProperty("ENCTYPE","multipart/form-data");
        connection.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        for (Map.Entry<String,Object> entry:params.entrySet())
        {
            if(entry.getValue() instanceof String)
            {
                outputStream.writeBytes(twoHyphens+boundary+crlf);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+entry.getKey()+"\""+crlf);
                outputStream.writeBytes(crlf);
                String value = entry.getValue().toString();
                outputStream.writeBytes(value);
                outputStream.writeBytes(crlf);
                outputStream.flush();
            }
            if(entry.getValue() instanceof Long)
            {
                outputStream.writeBytes(twoHyphens+boundary+crlf);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+entry.getKey()+"\""+crlf);
                outputStream.writeBytes(crlf);
                String value = Long.toString(Long.valueOf(entry.getValue().toString()));
                outputStream.writeBytes(value);
                outputStream.writeBytes(crlf);
                outputStream.flush();
            }
            if(entry.getValue() instanceof Integer)
            {
                outputStream.writeBytes(twoHyphens+boundary+crlf);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+entry.getKey()+"\""+crlf);
                outputStream.writeBytes(crlf);
                String value = Integer.toString(Integer.valueOf(entry.getValue().toString()));
                outputStream.writeBytes(value);
                outputStream.writeBytes(crlf);
                outputStream.flush();
            }
            if(entry.getValue() instanceof Double)
            {
                outputStream.writeBytes(twoHyphens+boundary+crlf);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+entry.getKey()+"\""+crlf);
                outputStream.writeBytes(crlf);
                String value = Double.toString(Double.valueOf(entry.getValue().toString()));
                outputStream.writeBytes(value);
                outputStream.writeBytes(crlf);
                outputStream.flush();
            }
            if(entry.getValue() instanceof File)
            {
                File file = (File) entry.getValue();
                outputStream.writeBytes(twoHyphens+boundary+crlf);
                outputStream.writeBytes("Content-Disposition: form-data; name=\""+entry.getKey()+"\";filename=\""+file.getName()+"\""+crlf);
                outputStream.writeBytes("Content-Type: "+getMimeType(file.getName())+crlf);
                outputStream.writeBytes(crlf);
                int bytesRead;
                int bytesAvailable;
                int bufferSize;

                byte[] buffer;

                int maxBufferSize = 512;

                FileInputStream fileInputStream = new FileInputStream(file);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer,0,bufferSize);
                while (bytesRead>0)
                {
                    outputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }
                outputStream.writeBytes(crlf);
                outputStream.flush();
            }
        }

        outputStream.writeBytes(crlf);
        outputStream.writeBytes(twoHyphens+boundary+twoHyphens);
        outputStream.flush();
        connection.connect();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while ((line = bufferedReader.readLine())!=null)
        {
            stringBuilder.append(line);
        }
        bufferedReader.close();
        outputStream.close();
        return stringBuilder.toString();
    }

    private String getMimeType(String url)
    {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if(extension!=null)
        {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    @Override
    protected String doInBackground(Void... params)
    {
        String data;
        try
        {
            data = stringFromURL(urlString);
        }
        catch (UnknownHostException | ConnectException e)
        {
            data = "{'isSucceeded':false,'error':'network-failed','type':'connection','typing':'"+e.getMessage()+"'}";
        }
        catch (Exception e)
        {
            data = "{'isSucceeded':false,'error':'exception','type':'connection','typing':'"+e.getMessage()+"'}";
        }
        return data;
    }
    @Override
    protected void onPostExecute(String result)
    {
        Map<String,Object> map;
        try
        {
            map = mapFromJSONString(result);
        }
        catch (JSONException e)
        {
            map = new HashMap<>();
            map.put("isSucceeded",false);
            map.put("error","json-error");
            map.put("typing",e.getMessage());
            map.put("text",result);
        }
        listener.onResult(map);
    }
    
    
    //----------------------------------------------------------------------


    private static JSONObject jsonFromMap(Map<String, Object> map) throws JSONException
    {
        JSONObject object = new JSONObject();
        for(Map.Entry<?,?> entry : map.entrySet())
        {
            if(entry.getKey()==null)
            {
                throw new NullPointerException("Key not found");
            }
            else
            {
                object.put(entry.getKey().toString(), jsonWrap(entry.getValue()));
            }
        }
        return object;
    }

    private static Object jsonWrap(Object o) throws JSONException
    {
        if(o==null)
        {
            return null;
        }
        if(o instanceof JSONObject || o instanceof JSONArray || o instanceof Boolean || o instanceof Byte || o instanceof Character || o instanceof Double || o instanceof Float || o instanceof Integer || o instanceof Short || o instanceof String)
        {
            return o;
        }
        if(o instanceof Collection)
        {
            return jsonFromCollection((Collection)o);
        }
        if(o instanceof Map)
        {
            return jsonFromMap((Map)o);
        }
        if(o.getClass().isArray())
        {
            return jsonFromArray(o);
        }
        if(o.getClass().getPackage().getName().startsWith("java."))
        {
            return o.toString();
        }
        return null;
    }

    private static JSONArray jsonFromCollection(Collection collection) throws JSONException
    {
        JSONArray jsonArray = new JSONArray();
        if(collection!=null)
        {
            for(Object data:collection)
            {
                jsonArray.put(jsonWrap(data));
            }
        }
        return jsonArray;
    }

    private static JSONArray jsonFromArray(Object object) throws JSONException
    {
        if(!object.getClass().isArray()) throw new JSONException("Not a primitive data:"+object.getClass());
        final Integer length = Array.getLength(object);
        JSONArray jsonArray = new JSONArray();
        for(Integer i=0;i<length;i++)
        {
            jsonArray.put(jsonWrap(Array.get(object,i)));
        }
        return jsonArray;
    }

    private static Map<String, Object> mapFromJSONString(String txt) throws JSONException
    {
        Map<String,Object> map;
        map = mapFromJSONObject(new JSONObject(txt));
        return map;
    }
    private static Map<String, Object> mapFromJSONObject(JSONObject jsonObject) throws JSONException
    {
        Map<String,Object> map = new HashMap<>();
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext())
        {
            String key = iterator.next().toString();
            Object value = jsonObject.get(key);
            if(value instanceof JSONArray)
            {
                value = mapFromJSONArray((JSONArray)value);
            }
            else if(value instanceof JSONObject)
            {
                value = mapFromJSONObject((JSONObject)value);
            }
            map.put(key,value);
        }
        return map;
    }
    private static List<Object> mapFromJSONArray(JSONArray jsonArray) throws JSONException
    {
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++)
        {
            Object value = jsonArray.get(i);

            if(value instanceof JSONArray)
                value = mapFromJSONArray((JSONArray)value);
            else if(value instanceof JSONObject)
                value = mapFromJSONObject((JSONObject)value);
            list.add(value);
        }
        return list;
    }
}
