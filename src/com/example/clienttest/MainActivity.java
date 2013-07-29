package com.example.clienttest;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView error;
	private String message;
	private byte [] imgbyte;
	String filepath;
	String ip;
	Socket sock;
	PrintStream out;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		error = (TextView)findViewById(R.id.error);

		new Thread(new Runnable()
		{
			public void run()
			{
				try{
					//String ip = ((TextView)findViewById(R.id.ip)).getText().toString();
					ip = "192.168.1.7";
					sock = new Socket(ip, 5006);
					out = new PrintStream(sock.getOutputStream(), true);
				}
				catch(UnknownHostException e)
				{
					error.setText("Unknown Host");
				}
				catch(IOException e)
				{
					error.setText("IO Exception");
				}
			}
		}).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void send(View v)
	{
		message = ((EditText)findViewById(R.id.message)).getText().toString();
		out.print(message);
	}

	public void sendImage(View v)
	{
		filepath = "/sdcard/DCIM/Camera/IMG_20121001_200118.jpg";
		File imagefile = new File(filepath);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(imagefile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try{
			int size = (int)imagefile.length();

			byte buf[] = new byte[size];

			DataInputStream dis = new DataInputStream(fis);
			dis.readFully(buf);
			//Bitmap bm = BitmapFactory.decodeStream(fis);
			//imgbyte = getBytesFromBitmap(bm);


			OutputStream output = sock.getOutputStream();
			output.write(buf);
			output.flush();
		}
		catch(IOException e)
		{
			error.setText("IOException");
		}
		catch(Exception e)
		{
			error.setText("Other Exception");
		}
	}

	public byte[] getBytesFromBitmap(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();
	}
}
