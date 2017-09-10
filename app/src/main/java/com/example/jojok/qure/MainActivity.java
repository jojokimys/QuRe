package com.example.jojok.qure;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private TextView ageText;
    private ListView theListView;

    private String[] bloodType =  { "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-" };
    private String[] AllergyAndConditions = { "Penicillin Antibiotics", "NSAIDs", "Sulfonamide antibiotics", "Anticonvulsants", "Codeine",
            "AIDS/HIV", "Alzheimer's", "Dementia", "Diabetes", "Hepatitis", "Obesity", "Arthritis", "Lupus", "Hypertension", "Multiple Sclerosis" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkPermission()) {
                Toast.makeText(MainActivity.this, "Permission is granted", Toast.LENGTH_LONG).show();
            } else
                requestPermission();
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[])
    {
        switch (requestCode) {
            case REQUEST_CAMERA :
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted){
                        Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                displayAlertMessage("You need to allow acces for both persmissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkPermission()) {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        scannerView.stopCamera();
        setContentView(R.layout.activity_main);
        //exit out of scannerView and display a list of items that the patient has

        int[] numbers = new int[2];
        numbers[0] = Integer.parseInt(myResult.substring(0, 7), 2);
        numbers[1] = Integer.parseInt(myResult.substring(7, 10), 2);
        long num = Long.parseLong(myResult.substring(10, 25));

        ageText = (TextView)findViewById(R.id.textView);
        ageText.setText("Year of Birth : " + numbers[0] + ",  Blood Type : " + bloodType[numbers[1]] );

        ArrayList<String> arrList = new ArrayList<>();
        int c = 0;
        while (num / 10 > 0) {
            if (num % 10 == 1) {
                arrList.add(AllergyAndConditions[15-1-c]);
            }
            num = num/10;
            c ++;
        }

        theListView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                arrList
        );
       theListView.setAdapter(arrayAdapter);

    }

}
