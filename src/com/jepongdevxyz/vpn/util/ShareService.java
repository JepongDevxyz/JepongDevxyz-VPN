package com.jepongdevxyz.vpn.util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ShareService extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    public static void SendMessage(Context context, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT,message);
        
        /**if (intent.resolveActivity(getPackageManager()) == null) {
            Toast.makeText(this, "Whatsapp no Instalado", Toast.LENGTH_SHORT).show();
            return;
        }**/
        context.startActivity(intent);
    }
}
