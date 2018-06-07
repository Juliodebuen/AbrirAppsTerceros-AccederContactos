package com.example.juliodebuen.aplicacionqueabreotrasaplicaciones;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.ImageView;

import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.internal.TwitterApi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.example.juliodebuen.aplicacionqueabreotrasaplicaciones.MainActivity.googleApiClient;

public class loggedActivity extends AppCompatActivity {
    EditText etMensaje;
    EditText etTelefono, etDireccion;
    static final int PICK_CONTACT_REQUEST = 0;
    static final int CAMERA_RESULT = 500;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged);
        etMensaje = findViewById(R.id.etMensaje);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        //img = findViewById(R.id.imgCamera);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            return;
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            return;
        }

        etTelefono.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                }

                /*
                Crear un intent para seleccionar un contacto del dispositivo
                 */
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

                /*
                Iniciar la actividad esperando respuesta a través
                del canal PICK_CONTACT_REQUEST
                 */
                startActivityForResult(i, PICK_CONTACT_REQUEST);
            }
        });
    }

    Uri contactUri;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST) {
            if (resultCode == RESULT_OK) {
                // A contact was picked.  Here we will just display it
                // to the user.
                /*
                Capturar el valor de la Uri
                 */
                contactUri = data.getData();
                /*
                Procesar la Uri
                 */
                renderContact(contactUri);
            }
        }
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            //Creamos un bitmap con la imagen recientemente
            //almacenada en la memoria
            Bitmap bMap = BitmapFactory.decodeFile(
                    Environment.getExternalStorageDirectory()+
                            "/Fototeca/"+"foto.jpg");
            //Añadimos el bitmap al imageView para
            //mostrarlo por pantalla

           // ByteArrayOutputStream bs = new ByteArrayOutputStream();
          //  bMap.compress(Bitmap.CompressFormat.JPEG, 1,bs);

            Intent intent = new Intent(this, FotoActivity.class);
           // intent.putExtra("img", bs.toByteArray());
            startActivity(intent);
           // img.setImageBitmap(bMap);
        }
    }

    private void renderContact(Uri uri) {
        etTelefono.setText(getPhone(uri).replace("+", "").replace(" ", "").replace("52",""));

       // contactName.setText(<strong>getName(uri)</strong>);
       // contactPhone.setText(<strong>getPhone(uri)</strong>);
        //contactPic.setImageBitmap(<strong>getPic(uri)</strong>);
    }

    public void openWhatsApp(View v) {
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.setAction(Intent.ACTION_VIEW);
        sendIntent.setPackage("com.whatsapp");
        String url = "https://api.whatsapp.com/send?phone=" + "52"+etTelefono.getText() + "&text=" + etMensaje.getText();
        sendIntent.setData(Uri.parse(url));
        startActivity(sendIntent);
    }

    public void realizarLlamada(View v){
        Uri number = Uri.parse("tel:"+etTelefono.getText());
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        startActivity(callIntent);
    }

    public void facebook(View v) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, etMensaje.getText().toString());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.facebook.orca");
        startActivity(sendIntent);
    }

    public void publicarFacebook(View v){
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "books.book")
                .putString("og:title", "A Game of Thrones")
                .putString("og:description", "In the frozen wastes to the north of Winterfell, sinister and supernatural forces are mustering.")
                .putString("books:isbn", "0-553-57340-3")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("books.reads")
                .putObject("book", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("book")
                .setAction(action)
                .build();
        ShareDialog.show(this, content);
    }

    public void mostrarChooser(View v){
        Uri webpage = Uri.parse("http://www.android.com");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);

        // Always use string resources for UI text.
        // This says something like "Share this photo with"
        String title = "Chooser";
        // Create intent to show chooser
        Intent chooser = Intent.createChooser(webIntent, title);

        // Verify the intent will resolve to at least one activity
        if (webIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public void mostrarMapa(View v){
        // Build the intent
        Uri location = Uri.parse("geo:0,0?q="+etDireccion.getText());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

        // Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

        // Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(mapIntent);
        }
    }



    public void tomarFoto(View v){
        //Creamos el Intent para llamar a la Camara
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //Creamos una carpeta en la memeria del terminal
        File imagesFolder = new File(
                Environment.getExternalStorageDirectory(), "Fototeca");
        imagesFolder.mkdirs();
        //añadimos el nombre de la imagen
        File image = new File(imagesFolder, "foto.jpg");
        Uri uriSavedImage = Uri.fromFile(image);
        //Le decimos al Intent que queremos grabar la imagen
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        //Lanzamos la aplicacion de la camara con retorno (forResult)
        startActivityForResult(cameraIntent, CAMERA_RESULT);
    }

    public void notificacion(View v){
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr =(NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        int icono = R.mipmap.ic_launcher;
       // Intent i=new Intent(this, MensajeActivity.class);
       // PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, i, 0);

        mBuilder =new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(icono)
                .setContentTitle("Titulo")
                .setContentText("Hola que tal?")
                .setVibrate(new long[] {100, 250, 100, 500})
                .setAutoCancel(true);

        mNotifyMgr.notify(1, mBuilder.build());
    }




    private String getName(Uri uri) {

        /*
        Valor a retornar
         */
        String name = null;

         /*
        Obtener una instancia del Content Resolver
         */
        ContentResolver contentResolver = getContentResolver();

        /*
        Cursor para recorrer los datos de la consulta
         */
        Cursor c = contentResolver.query(
                uri, new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);

        /*
        Consultando el primer y único resultado elegido
         */
        if(c.moveToFirst()){
            name = c.getString(0);
        }

        /*
        Cerramos el cursor
         */
        c.close();

        return name;
    }

    private String getPhone(Uri uri) {
        /*
        Variables temporales para el id y el teléfono
         */
        String id = null;
        String phone = null;

        /************* PRIMERA CONSULTA ************/
        /*
        Obtener el _ID del contacto
         */
        Cursor contactCursor = getContentResolver().query(
                uri, new String[]{ContactsContract.Contacts._ID},
                null, null, null);


        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        /************* SEGUNDA CONSULTA ************/
        /*
        Sentencia WHERE para especificar que solo deseamos
        números de telefonía móvil
         */
        String selectionArgs =
                Phone.CONTACT_ID + " = ? AND " +
                        Phone.TYPE+"= " +
                        Phone.TYPE_MOBILE;

        /*
        Obtener el número telefónico
         */
        Cursor phoneCursor = getContentResolver().query(
                Phone.CONTENT_URI,
                new String[] { Phone.NUMBER },
                selectionArgs
                ,
                new String[] { id },
                null
        );
        if (phoneCursor.moveToFirst()) {
            phone = phoneCursor.getString(0);
        }
        phoneCursor.close();

        return phone;
    }

    private Bitmap getPhoto(Uri uri) {
        /*
        Foto del contacto y su id
         */
        Bitmap photo = null;
        String id = null;

        /************* CONSULTA ************/
        Cursor contactCursor = getContentResolver().query(
                uri, new String[]{ContactsContract.Contacts._ID}, null, null, null);

        if (contactCursor.moveToFirst()) {
            id = contactCursor.getString(0);
        }
        contactCursor.close();

        /*
        Usar el método de clase openContactPhotoInputStream()
         */
        try {
            InputStream input =
                    ContactsContract.Contacts.openContactPhotoInputStream(
                            getContentResolver(),
                            ContentUris.withAppendedId(
                                    ContactsContract.Contacts.CONTENT_URI,
                                    Long.parseLong(id))
                    );
            if (input != null) {
                /*
                Dar formato tipo Bitmap a los bytes del BLOB
                correspondiente a la foto
                 */
                photo = BitmapFactory.decodeStream(input);
                input.close();
            }

        } catch (IOException iox) { /* Manejo de errores */ }

        return photo;
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = getIntent().getExtras();
        switch(bundle.getString("log with")) {
            case "facebook":
                LoginManager.getInstance().logOut();
                break;
            case "number":
                AccountKit.logOut();
                break;
            case "google":
                logoutGoogle();
                break;

        }
        super.onBackPressed();
    }

    public void logoutGoogle() {
        googleApiClient.connect();
        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                if(googleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                Log.d("", "User Logged out");
                                Intent intent = new Intent(loggedActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("", "Google API Client Connection Suspended");
            }
        });
    }
}
