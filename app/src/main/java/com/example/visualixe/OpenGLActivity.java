package com.example.visualixe;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class OpenGLActivity extends Activity{

    private MyGLSurfaceView glView;
    private LinearLayout container;
    private Spinner propertySpinner;
    private Spinner objectSpinner;
    private ArrayList<String> objectOptions = new ArrayList<>();
    private String currentObject;

    private ArrayList<String>  propertyOptions = new ArrayList<>();
    private String currentOption;

    private Button applyButton;
    private Button openButton;

    private EditText x_text, y_text, z_text;
    private TextView x_disp, y_disp, z_disp;

    private CheckBox hideCheckBox;

    static final int REQUEST_OBJECT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        propertyOptions.add("Rotate");
        propertyOptions.add("Move");
        propertyOptions.add("Scale");

        objectOptions.add("Light");
        objectOptions.add("Mesh");

        glView = new MyGLSurfaceView(this.getApplicationContext());
        setContentView(R.layout.activity_open_glactivity);

        hideCheckBox = findViewById(R.id.hideCheck);
        hideCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                glView.SetMeshHide(b);
            }
        });

        container = (LinearLayout) findViewById(R.id.mainContainer);

        container.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        x_text = findViewById(R.id.axis_x);
        x_text.setText("0");
        y_text = findViewById(R.id.axis_y);
        y_text.setText("0");
        z_text = findViewById(R.id.axis_z);
        z_text.setText("0");

        x_disp = findViewById(R.id.val_x);
        y_disp = findViewById(R.id.val_y);
        z_disp = findViewById(R.id.val_z);

        applyButton = findViewById(R.id.apply_button);
        openButton = findViewById(R.id.open_button);

        UpdateSelectionValues();
        applyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                float x_val = GetFloatTextValue(x_text.getText().toString());
                float y_val = GetFloatTextValue(y_text.getText().toString());
                float z_val = GetFloatTextValue(z_text.getText().toString());


                glView.setSelectedAxis(x_val, y_val, z_val);
                UpdateSelectionValues();

            }
        });

        objectSpinner = findViewById(R.id.object_spinner);
        PrepareSpinner(objectSpinner, objectOptions);

        objectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                glView.setSelectedMesh(objectOptions.get(i));
                hideCheckBox.setChecked(glView.IsSelectedMeshHidden());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        propertySpinner = findViewById(R.id.property_spinner);
        PrepareSpinner(propertySpinner, propertyOptions);

        propertySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentOption = propertyOptions.get(i);

                switch (currentOption)
                {
                    case "Move":
                        glView.setTransformMode(TRANSFORM_MODE.MOVE);
                        break;
                    case "Rotate":
                        glView.setTransformMode(TRANSFORM_MODE.ROTATE);
                        break;
                    case "Scale":
                        glView.setTransformMode(TRANSFORM_MODE.SCALE);
                        break;
                }
                UpdateSelectionValues();
                UpdateDisplayValues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                currentOption = propertyOptions.get(0);
                glView.setTransformMode(TRANSFORM_MODE.ROTATE);
                UpdateDisplayValues();
            }
        });

        glView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                UpdateDisplayValues();
                return  false;
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFile();
                openButton.setVisibility(View.INVISIBLE);
            }
        });


    }

    private static float GetFloatTextValue(String s)
    {
        float v = 0f;
        if(!s.isEmpty()) v = Float.parseFloat(s.trim());
        return v;
    }

    private void UpdateDisplayValues()
    {
        float[] prevVal = glView.getPrevAxisValues();
        x_disp.setText(String.valueOf(prevVal[0]));
        y_disp.setText(String.valueOf(prevVal[1]));
        z_disp.setText(String.valueOf(prevVal[2]));
    }

    private void UpdateSelectionValues()
    {
        float[] prevVal = glView.getSelectedAxis();
        x_text.setText(String.valueOf(prevVal[0]));
        y_text.setText(String.valueOf(prevVal[1]));
        z_text.setText(String.valueOf(prevVal[2]));
    }

    private void PrepareSpinner(Spinner s, ArrayList<String> options)
    {
        ArrayAdapter aa =new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, options);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        s.setAdapter(aa);
    }


    private void OpenFile()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_OBJECT_FILE);
        }
    }
    private void LoadFile(InputStream file, String name)
    {
        glView.LoadMeshFromFile(file, name);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_OBJECT_FILE && resultCode == RESULT_OK)
        {
            Uri uri = data.getData();
            String name = uri.getLastPathSegment();
            objectOptions.add(name);
            try {
                LoadFile(getContentResolver().openInputStream(uri), name);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }



        }
    }
}