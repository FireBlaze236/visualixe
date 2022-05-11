package com.example.visualixe;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class OpenGLActivity extends Activity {

    private MyGLSurfaceView glView;
    private LinearLayout container;
    private Spinner propertySpinner;
    private Spinner objectSpinner;
    private String[] objectOptions = {"Object", "Light"};
    private String currentObject;

    private String[] propertyOptions = {"Rotate", "Move", "Scale"};
    private String currentOption;

    private Button applyButton;

    private EditText x_text, y_text, z_text;
    private TextView x_disp, y_disp, z_disp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        glView = new MyGLSurfaceView(this.getApplicationContext());
        setContentView(R.layout.activity_open_glactivity);



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

        propertySpinner = findViewById(R.id.property_spinner);
        PrepareSpinner(propertySpinner, propertyOptions);

        propertySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentOption = propertyOptions[i];

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
                currentOption = propertyOptions[0];
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

    private void PrepareSpinner(Spinner s, String[] options)
    {
        ArrayAdapter aa =new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, options);
        aa.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        s.setAdapter(aa);
    }
}