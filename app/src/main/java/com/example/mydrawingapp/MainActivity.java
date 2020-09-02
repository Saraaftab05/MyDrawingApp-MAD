package com.example.mydrawingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutLinearInInterpolator;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
//import android.text.Layout;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import android.provider.MediaStore;
//import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Dialog;


import java.util.zip.Inflater;
import com.example.mydrawingapp.View.DrawingView;


public class MainActivity extends AppCompatActivity {


private DrawingView Dview;
private AlertDialog.Builder currentAlertDialog;
private  ImageView widthImageView;
private AlertDialog dialogLineWidth;
private AlertDialog colorDialog;
private AlertDialog brushDialog;
private SeekBar alphaSeekBar;
private SeekBar redSeekBar;
private SeekBar greenSeekBar;
private SeekBar blueSeekBar;
private View colorView;

//new
    private float smallBrush, mediumBrush, largeBrush;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Dview = findViewById(R.id.view);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        //set initial size
        Dview.setBrushSize(mediumBrush);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.cleraId:
                Dview.clear();
                break;


                case R.id.saveId:

                    AlertDialog.Builder editalert = new AlertDialog.Builder(this);
                editalert.setTitle("Please Enter the name with which you want to Save");
                final EditText input = new EditText(this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT);
                input.setLayoutParams(lp);
                editalert.setView(input);
                editalert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Dview.setDrawingCacheEnabled(true);
                        String name= input.getText().toString();
                        Bitmap bitmap = Dview.getDrawingCache();

                        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File file = new File("/SDCARD/"+name+".png");

                        if (file!=null) {
                            Toast.makeText(getApplicationContext(),"Image Saved!", Toast.LENGTH_LONG).show();
                        } else{
                            Toast.makeText(getApplicationContext(),"Not saved!" , Toast.LENGTH_LONG).show();
                        }
                        try
                        {
                            if(!file.exists())
                            {
                                file.createNewFile();
                            }
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
                            System.out.println("saving......................................................"+path);
                            ostream.close();
                            Dview.invalidate();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }finally
                        {

                            Dview.setDrawingCacheEnabled(false);
                        }
                    }
                });

                editalert.show();
                break;
            case R.id.colorId:
                showColorDialog();
                break;
            case R.id.lineWidth:
                showLineWidthDialog();
                break;
            case R.id.eraseId:

                final Dialog brushDialog = new Dialog(this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                //size buttons
                ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dview.setErase(true);
                        Dview.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dview.setErase(true);
                        Dview.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dview.setErase(true);
                        Dview.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();

                break;


        }

        return true;
    }





    /**

    void showEraseDialog() {
        currentAlertDialog = new AlertDialog.Builder(this);

        View view = getLayoutInflater().inflate(R.layout.erase_dialog, null);



        //currentAlertDialog.setTitle("Choose Eraser Size");

        ImageButton smallbtn = view.findViewById(R.id.small_brush);

        smallbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dview.setErase(true);
                Dview.setBrushSize(smallBrush);
                brushDialog.dismiss();


            }
        });

        ImageButton mediumbtn = view.findViewById(R.id.medium_brush);

        mediumbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dview.setErase(true);
                Dview.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });

        currentAlertDialog.setView(view);
        currentAlertDialog.setTitle("Choose brush size");
        brushDialog = currentAlertDialog.create();
        brushDialog.show();



    }

     **/

    void showColorDialog() {


        currentAlertDialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.color_dialog,null);
        alphaSeekBar = view.findViewById(R.id.alphaSeekbar);
        redSeekBar = view.findViewById(R.id.redSeekbar);
        greenSeekBar = view.findViewById(R.id.greenSeekbar);
        blueSeekBar = view.findViewById(R.id.blueSeekbar);
        colorView = view.findViewById(R.id.colorView);

        //Register Seekbar event listener

        alphaSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        redSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        greenSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);
        blueSeekBar.setOnSeekBarChangeListener(colorSeekBarChanged);

        int color = Dview.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        Button setColorButton = view.findViewById(R.id.setColorButton);

        setColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dview.setDrawingColor(Color.argb(
                        alphaSeekBar.getProgress(),
                        redSeekBar.getProgress(),
                        greenSeekBar.getProgress(),
                        blueSeekBar.getProgress()
                ));
                colorDialog.dismiss();
            }
        });

        currentAlertDialog.setView(view);
        currentAlertDialog.setTitle("Choose Color");
        colorDialog = currentAlertDialog.create();
        colorDialog.show();
    }

void showLineWidthDialog() {

currentAlertDialog = new AlertDialog.Builder(this);
    View view = getLayoutInflater().inflate(R.layout.width_dialog, null);
    final SeekBar widthSeekbar = view.findViewById(R.id.widthDseekBar);
    Button setWidthButton = view.findViewById(R.id.widthDialogButton);
    widthImageView = view.findViewById(R.id.widthimageViewId);


    setWidthButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Dview.setLineWidth(widthSeekbar.getProgress());
            dialogLineWidth.dismiss();
            currentAlertDialog = null;


        }
    });


    widthSeekbar.setOnSeekBarChangeListener(widthSeekbarChange);


    currentAlertDialog.setView(view);
   dialogLineWidth = currentAlertDialog.create();
    dialogLineWidth.setTitle("Set Line Width");
    dialogLineWidth.show();
    }

    private SeekBar.OnSeekBarChangeListener colorSeekBarChanged = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean FromUser) {
            Dview.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()


            ));

            //Display the current color

            colorView.setBackgroundColor(Color.argb(
                    alphaSeekBar.getProgress(),
                    redSeekBar.getProgress(),
                    greenSeekBar.getProgress(),
                    blueSeekBar.getProgress()
            ));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };



     private SeekBar.OnSeekBarChangeListener widthSeekbarChange = new SeekBar.OnSeekBarChangeListener() {
        Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        @Override
         public void onProgressChanged(SeekBar seekBar, int progress, boolean FromUser) {

            Paint p = new Paint();
             p.setColor(Dview.getDrawingColor());
             p.setStrokeCap(Paint.Cap.ROUND);

            p.setStrokeWidth(progress);
             bitmap.eraseColor(Color.WHITE);
             canvas.drawLine(30,50,370, 50, p);
             widthImageView.setImageBitmap(bitmap);
         }

         @Override
         public void onStartTrackingTouch(SeekBar seekBar) {

         }

         @Override
         public void onStopTrackingTouch(SeekBar seekBar) {

         }
     };


}