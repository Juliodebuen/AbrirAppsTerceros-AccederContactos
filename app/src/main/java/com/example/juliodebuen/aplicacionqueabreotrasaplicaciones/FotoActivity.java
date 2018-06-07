package com.example.juliodebuen.aplicacionqueabreotrasaplicaciones;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class FotoActivity extends AppCompatActivity {
    ImageView imCamera;
    String imgPath = "/Fototeca/"+"foto.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto);

        imCamera = findViewById(R.id.imgCamera);

        Bitmap bMap = BitmapFactory.decodeFile(
                Environment.getExternalStorageDirectory()+
                        imgPath);

        //Bitmap b = BitmapFactory.decodeByteArray(
          //      getIntent().getByteArrayExtra("img"),0,getIntent()
            //            .getByteArrayExtra("img").length);

        imCamera.setImageBitmap(redimensionarImagenMaximo(bMap, 3120, 4096));
    }


    public void enviarImagenPorWhatsapp(View v){
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + imgPath));
           // intent.putExtra("jid", phoneNumber + "@s.whatsapp.net"); //numero telefonico sin prefijo "+"!
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Whatsapp no esta instalado.", Toast.LENGTH_LONG).show();
        }
    }

    public void compartir(View v){
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + imgPath));
            // intent.putExtra("jid", phoneNumber + "@s.whatsapp.net"); //numero telefonico sin prefijo "+"!
         //   intent.setPackage("com.facebook.orca");
            startActivity(intent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Messenger no esta instalado.", Toast.LENGTH_LONG).show();
        }
    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }
}
