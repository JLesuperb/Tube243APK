package com.tube243.tube243;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.tube243.tube243.core.Single;
import com.tube243.tube243.data.LocalData;
import com.tube243.tube243.data.Params;
import com.tube243.tube243.processes.LocalTextTask;
import com.tube243.tube243.ui.activities.HomeActivity;
import com.tube243.tube243.utils.Utility;
import com.tube243.tube243.widgets.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class ProfileEditorActivity extends AppCompatActivity implements View.OnClickListener
{
    private AppCompatEditText first_name_edit;
    private AppCompatEditText last_name_edit;
    private AppCompatButton save_btn;
    private CircleImageView image_profile;
    private LocalData localData;
    private Single single;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_editor);
        localData = new LocalData(getApplicationContext());
        single = Single.getInstance();
        setKeys();

    }

    private void setKeys()
    {
        FloatingActionButton image_edit_btn = (FloatingActionButton)findViewById(R.id.image_edit_btn);
        image_edit_btn.setOnClickListener(this);
        save_btn = (AppCompatButton)findViewById(R.id.save_btn);
        save_btn.setOnClickListener(this);
        CharSequence sequence;
        first_name_edit = (AppCompatEditText)findViewById(R.id.first_name_edit);
        image_profile = (CircleImageView)findViewById(R.id.image_profile);
        if(localData.getString("imageProfile")!=null)
        {
            File file = new File(single.getParentFolder(),"profile/"+localData.getString("imageProfile"));
            if(file.exists())
            {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                image_profile.setImageBitmap(bitmap);
            }
            else
            {
                try
                {
                    Drawable drawable = Drawable.createFromStream(getAssets().open("images/user_default.png"),null);
                    image_profile.setImageDrawable(drawable);
                }
                catch (IOException ignored){}
            }
        }
        else
        {
            try
            {
                Drawable drawable = Drawable.createFromStream(getAssets().open("images/user_default.png"),null);
                image_profile.setImageDrawable(drawable);
            }
            catch (IOException ignored){}
        }

        if(localData.getString("firstName")!=null)
        {
            sequence = localData.getString("firstName");
            first_name_edit.setText(sequence);
        }
        last_name_edit = (AppCompatEditText)findViewById(R.id.last_name_edit);
        if(localData.getString("lastName")!=null)
        {
            sequence = localData.getString("lastName");
            last_name_edit.setText(sequence);
        }
    }

    private void pickUpImageProfile()
    {
        LayoutInflater layoutInflater = LayoutInflater.from(ProfileEditorActivity.this);
        View view = layoutInflater.inflate(R.layout.dialog_image_picker,null,false);
        AlertDialog.Builder builder =  new AlertDialog.Builder(ProfileEditorActivity.this);
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        LinearLayoutCompat fromGallery = (LinearLayoutCompat) view.findViewById(R.id.fromGallery);
        fromGallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pickUpImageFromGallery();
                dialog.dismiss();
            }
        });
        LinearLayoutCompat fromCamera = (LinearLayoutCompat) view.findViewById(R.id.fromCamera);
        fromCamera.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                pickUpImageFromCamera();
                dialog.dismiss();
            }
        });
    }

    private void pickUpImageFromGallery()
    {
        Intent intent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent,Params.PICK_UP_IMAGE_FROM_GALLERY);
    }

    private void pickUpImageFromCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, Params.PICK_UP_IMAGE_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(resultCode==RESULT_OK)
        {
            switch (requestCode)
            {
                case Params.PICK_UP_IMAGE_FROM_GALLERY:
                    if(intent!=null)
                    {
                        try
                        {
                            Uri imageUri = intent.getData();
                            if (imageUri != null)
                            {
                                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                                Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                                cropImage(bmp);
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Something went wrong", Toast.LENGTH_LONG).show();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                    break;
                case Params.PICK_UP_IMAGE_FROM_CAMERA:
                    if(intent!=null)
                    {
                        if(intent.getExtras()!=null)
                        {
                            Bitmap bmp = (Bitmap) intent.getExtras().get("data");
                            try
                            {
                                cropImage(bmp);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    break;
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    CropImage.ActivityResult result = CropImage.getActivityResult(intent);
                    //image_profile.setImageBitmap(result.getBitmap());
                    Uri resultUri = result.getUri();
                    try
                    {
                        InputStream imageStream = getContentResolver().openInputStream(resultUri);
                        Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                        File folder = Utility.getFolder(".profile");
                        if(folder!=null)
                        {
                            try
                            {
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                byte[] byteArray = stream.toByteArray();
                                Utility.cleanFolder(folder);
                                File file = new File(folder,".profile.jpeg");
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                fileOutputStream.write(byteArray);
                                fileOutputStream.close();
                                image_profile.setImageBitmap(bmp);
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    catch (FileNotFoundException e)
                    {
                        e.printStackTrace();
                    }

                    break;
            }
        }
    }

    private void cropImage(Bitmap bmp) throws IOException
    {
        if(bmp!=null)
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            File folder = Utility.getFolder(".tmp");
            if(folder!=null)
            {
                Utility.cleanFolder(folder);
                File file = new File(folder,"tmp.jpeg");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(byteArray);
                fileOutputStream.close();
                CropImage.activity(Uri.fromFile(file))
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(this);
            }
        }
    }

    private boolean checkCameraPermission()
    {
        String permission = "android.permission.CAMERA";
        int res = getApplicationContext().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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
                    Toast.makeText(ProfileEditorActivity.this, "Permission accordée", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(ProfileEditorActivity.this, "Permission réfusée", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.image_edit_btn:
                if(checkCameraPermission())
                {
                    pickUpImageProfile();
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    {
                        ActivityCompat.requestPermissions(ProfileEditorActivity.this,
                                new String[]{Manifest.permission.CAMERA}, 1);
                    }
                    else
                    {
                        //finish();
                    }
                }
                break;
            case R.id.save_btn:
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setIndeterminate(true);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Vérification...");
                progressDialog.show();
                final String firstName = first_name_edit.getText().toString();
                final String lastName = last_name_edit.getText().toString();
                if(!firstName.isEmpty() && !lastName.isEmpty())
                {
                    progressDialog.setMessage("Enregistrement...");
                    LocalTextTask task = new LocalTextTask();
                    task.setUrlString(Params.SERVER_HOST);
                    task.getParams().put("controller","users");
                    task.getParams().put("method","update-names");
                    task.getParams().put("first_name",firstName);
                    task.getParams().put("last_name",lastName);
                    task.getParams().put("user_id", localData.getLong("userId"));
                    task.setListener(new LocalTextTask.ResultListener() {
                        @Override
                        public void onResult(Map<String, Object> result)
                        {
                            progressDialog.dismiss();
                            if(result.containsKey("isDone") && (Boolean)result.get("isDone"))
                            {
                                localData.setString("firstName",firstName);
                                localData.setString("lastName",lastName);

                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileEditorActivity.this);
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
                                        save_btn.performClick();
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
                    });
                    task.execute();
                }
                else
                {
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProfileEditorActivity.this);
                    alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    alertDialog.setTitle("Erreurs");
                    String msg = "Veuillez insèrer correctement votre Prénom et votre Nom";
                    alertDialog.setMessage(msg);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("fermer", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
                break;
        }
    }
}
