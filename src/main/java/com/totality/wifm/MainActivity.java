package com.totality.wifm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.drawable.Icon;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.*;
import java.net.*;
import java.net.URLConnection;
import android.os.Build;

import java.security.Signature;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import au.edu.jcu.v4l4j.DeviceInfo;
import au.edu.jcu.v4l4j.Tuner;
import au.edu.jcu.v4l4j.TunerInfo;
import au.edu.jcu.v4l4j.Control;
import au.edu.jcu.v4l4j.V4L4JConstants;
import au.edu.jcu.v4l4j.VideoDevice;
import au.edu.jcu.v4l4j.exceptions.V4L4JException;

public class MainActivity extends AppCompatActivity {

    private Tuner tuner;
    private TunerInfo tunerInfo;
    private DeviceInfo deviceInfo;
    private V4L4JConstants v4L4JConstants;
    private VideoDevice videoDevice;
    String dev = "/dev/video0";


    // Generates sine wave based on incoming byte value
    private static void noteGenerator(int frequency, short[] buffer, int numSamples,
                                      int sampleRate, int startVal){
        for (int i = 0; i < numSamples; i++) {    // Fill the sample array
            buffer[startVal + i] = (short)((Math.sin(2 * Math.PI * i * frequency
                    / sampleRate)) * Short.MAX_VALUE);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageView imageView = findViewById(R.id.searchImage);
        final EditText editText = findViewById(R.id.searchText);
        Button goButton = findViewById(R.id.button0);

        final int sampleRate = 44100;
        final int numSamples = 512;

        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // requested url
                    URL url = new URL("https://www." + editText.getText().toString());
                    // Unique "ip address" for every user


                    //byte[] signature = ("AAAAAAAAAAAA" + Build.SERIAL).getBytes();
                    byte[] signature = ("AzAzAzAzAzAzAzAz").getBytes();
                    short[] finalSignatureBuffer = new short[signature.length * numSamples];
                    //byte[] request = (url.openConnection().toString()+ "ZZZZ").getBytes();

                    // Generates sound request for signature and sends it
                    for (int i = 0; i< signature.length; i++){
                        int note = 44100 * (signature[i]) / 256;
                        int startVal = numSamples * i;
                        noteGenerator(note, finalSignatureBuffer, numSamples,
                                sampleRate, startVal);

                    }
                    AudioTrack signatureTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, finalSignatureBuffer.length,
                            AudioTrack.MODE_STREAM);

                    signatureTrack.write(finalSignatureBuffer, 0,
                            finalSignatureBuffer.length);
                    signatureTrack.play();
                    /*
                    for (int i = 0; i< request.length; i++){
                        short[] requestBuffer = new short[request.length * 2];
                        AudioTrack requestTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                                sampleRate, AudioFormat.CHANNEL_OUT_MONO,
                                AudioFormat.ENCODING_PCM_8BIT, request.length,
                                AudioTrack.MODE_STREAM);
                        //noteGenerator(note, requestBuffer, Dampening, request[i],i);
                        requestTrack.write(request, 0, request.length);
                        requestTrack.play();
                        requestTrack.release();
                    }
                     */
                    } catch (Exception e) {
                    Log.d("WebError", "Unable to get connection" + e.toString());
                }
            }
        });



        /*
        try {
            videoDevice = new VideoDevice(dev);
            deviceInfo = videoDevice.getDeviceInfo();
            Log.i("tunerlist", "getTunerList() returned: " + (videoDevice.getTunerList()));
        } catch (V4L4JException e) {
            Log.d("Failure", "Can't read file :" + dev);
            e.printStackTrace();
        }
         */
    }

}