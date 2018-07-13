package com.example.armando.marketbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class BarCodeActivity extends AppCompatActivity implements View.OnClickListener {

    private Button scanBtn;
    private TextView contentTxt, formatTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        // Prendo i riferimenti alle view
        //scanBtn = (Button)findViewById(R.id.scan_button);
        //formatTxt = (TextView)findViewById(R.id.scan_format);
        //contentTxt = (TextView)findViewById(R.id.scan_content);

        //Attacco il OnClickListener al pulsante
        scanBtn.setOnClickListener(this);

        //QR Code Scanner Object & Initialize the Scan Object
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        //initiating the qr code scan
        scanIntegrator.initiateScan();

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanningResult != null) { //We have the result

            //Check to see if QR Code has nothing in it
            if (scanningResult.getContents() == null) {
                Toast.makeText(this, "Nessun Codice trovato", Toast.LENGTH_LONG).show();
            } else {
                //QR Code contains some data
                String scanContent = scanningResult.getContents();
                String scanFormat = scanningResult.getFormatName();
                formatTxt.setText("FORMAT: " + scanFormat);
                contentTxt.setText("CONTENT: " + scanContent);
            }
        }
    }



    @Override

    public void onClick(View view) {

        //if(view.getId()==R.id.scan_button){
        //scan

            //QR Code Scanner Object & Initialize the Scan Object
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            //initiating the qr code scan
            scanIntegrator.initiateScan();

        //}

    }

}