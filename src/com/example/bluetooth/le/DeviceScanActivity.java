/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.bluetooth.le.iBeaconClass.iBeacon;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends Activity {
	private final static String TAG = DeviceScanActivity.class.getSimpleName();
	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";

    private LeDeviceListAdapter mLeDeviceListAdapter;
    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;
    private MediaPlayer player1 = null;
    private MediaPlayer player2 = null;
    private MediaPlayer player3 = null;
    private MediaPlayer player4 = null;
    
   // private ImageView imageView0;
	private ImageView currentImage;
    private boolean mScanning;
    private Handler mHandler;
    private MySQLiteOpenHelper openHelper;
    private   SQLiteDatabase database;
    private int TIME = 1000*5; //5秒定时
    Activity activity;
    public static volatile int dFlag;
    private int location = 0;
    private Thread thread;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 600000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        getActionBar().setTitle(R.string.title_devices);
 // TODO: 2017/6/2  
        //创建数据库
        openHelper = new MySQLiteOpenHelper(this, 1);
        database = openHelper.getWritableDatabase();
        activity = this;
        handler1.postDelayed(runnable, TIME); //每隔30s执行
        // TODO: 2017/6/2 
        mHandler = new Handler();
        setContentView(R.layout.main_view);
        //imageView0 = (ImageView) findViewById(R.id.imageView0);
        Button button1=(Button)findViewById(R.id.button1);
        
        button1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://xuputcommunication.duapp.com/MusicZ/"));
				startActivity(intent);
			}
        	
        });
        MyApplication.getInstance().addActivity(this);
       
       
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //开启蓝牙
        mBluetoothAdapter.enable();
        
    }
    
    @Override 
    public void onConfigurationChanged(Configuration newConfig)
    { 
        super.onConfigurationChanged(newConfig); 
     if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
     {
//land
     }
     else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
     {
//port
     }
    }
 
    @Override
    protected void onResume() {
        super.onResume();

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        
        
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                   
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

        	final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecord,database);
        	runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(ibeacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                    location=mLeDeviceListAdapter.getPosition();
                    playMessage(location);
                    changeImage(location);
                    
                }
            });
        	
        }
    };

   private void playMessage(int position){ 
    
	   if((position==4)&&(player1==null)){
           Toast.makeText(DeviceScanActivity.this,"现在是:陕西省无线通信与信息处理技术国际联合研究中心", Toast.LENGTH_SHORT).show();
           if(player2!=null){
        	   player2.stop();
        	   player2=null;
           }
           if(player3!=null){
        	   player3.stop();
        	   player3=null;
           }
           if(player4!=null){
        	   player4.stop();
        	   player4=null;
           }
     player1 = MediaPlayer.create(DeviceScanActivity.this, R.raw.test0); 
     player1.start();
     player1.setOnCompletionListener(  
             new MediaPlayer.OnCompletionListener()   
           {   
             // @Override   
             /*覆盖文件播出完毕事件*/ 
             public void onCompletion(MediaPlayer arg0)   
             {   
               try   
               {   
                 /*解除资源与MediaPlayer的赋值关系  
                  * 让资源可以为其它程序利用*/ 
            	   player1.stop();
            	   Toast.makeText(DeviceScanActivity.this,"已播放完毕，重新播放", Toast.LENGTH_SHORT).show();
            	   player1 = null;  
               }   
               catch (Exception e)   
               {    
                 e.printStackTrace();   
               }   
             }   
           });   
     
    }
   else 
	   if((position==1)&&(player2==null)){
           Toast.makeText(DeviceScanActivity.this,"现在是:现代通信技术省级实验教学示范中心", Toast.LENGTH_SHORT).show();
           if(player1!=null){
        	   player1.stop();
        	   player1=null;
           }
           if(player3!=null){
        	   player3.stop();
        	   player3=null;
           }
           if(player4!=null){
        	   player4.stop();
        	   player4=null;
           }
     player2 = MediaPlayer.create(DeviceScanActivity.this, R.raw.test1); 
     player2.start();
     player2.setOnCompletionListener(  
             new MediaPlayer.OnCompletionListener()   
           {   
             // @Override   
             /*覆盖文件播出完毕事件*/ 
             public void onCompletion(MediaPlayer arg0)   
             {   
               try   
               {   
                 /*解除资源与MediaPlayer的赋值关系  
                  * 让资源可以为其它程序利用*/ 
            	   player2.stop();
            	   Toast.makeText(DeviceScanActivity.this,"已播放完毕，重新播放", Toast.LENGTH_SHORT).show();
            	   player2 = null;  
               }   
               catch (Exception e)   
               {    
                 e.printStackTrace();   
               }   
             }   
           });   
     
    }
    else if((position==2)&&(player3==null)){
           Toast.makeText(DeviceScanActivity.this,"现在是:信号与信息处理省级人才培养模式创新实验区", Toast.LENGTH_SHORT).show();
           if(player1!=null){
        	   player1.stop();
        	   player1=null;
           }
           if(player2!=null){
        	   player2.stop();
        	   player2=null;
           }
           if(player4!=null){
        	   player4.stop();
        	   player4=null;
           }
    player3 = MediaPlayer.create(DeviceScanActivity.this, R.raw.test2); 
    player3.start();
    player3.setOnCompletionListener(  
            new MediaPlayer.OnCompletionListener()   
          {   
            // @Override   
            /*覆盖文件播出完毕事件*/ 
            public void onCompletion(MediaPlayer arg0)   
            {   
              try   
              {   
                /*解除资源与MediaPlayer的赋值关系  
                 * 让资源可以为其它程序利用*/ 
            	  player3.stop();
            	  Toast.makeText(DeviceScanActivity.this,"已播放完毕，重新播放", Toast.LENGTH_SHORT).show();
            	  player3 = null;                                       
              }   
              catch (Exception e)   
              {    
                e.printStackTrace();   
              }   
            }   
          });            
 } 
    else if((position==3)&&(player4==null)){
           Toast.makeText(DeviceScanActivity.this,"现在是:现代通信技术省级实验教学示范中心", Toast.LENGTH_SHORT).show();
           if(player1!=null){
        	   player1.stop();
        	   player1=null;
           }
           if(player3!=null){
        	   player3.stop();
        	   player3=null;
           }
           if(player2!=null){
        	   player2.stop();
        	   player2=null;
           }
      player4 = MediaPlayer.create(DeviceScanActivity.this, R.raw.test3); 
      player4.start();
      player4.setOnCompletionListener(  
              new MediaPlayer.OnCompletionListener()   
            {   
              // @Override   
              /*覆盖文件播出完毕事件*/ 
              public void onCompletion(MediaPlayer arg0)   
              {   
                try   
                {   
                  /*解除资源与MediaPlayer的赋值关系  
                   * 让资源可以为其它程序利用*/ 
         
             	   player4.stop();
              
             	   Toast.makeText(DeviceScanActivity.this,"已播放完毕，重新播放", Toast.LENGTH_SHORT).show();
             	   player4 = null;  
                }   
                catch (Exception e)   
                {    
                  e.printStackTrace();   
                

                }   
              }   
            });   
      
     }

   }
    private void changeImage(int position){
        switch (position) {
        case 0:
        	setContentView(R.layout.main_view);
         	break;
        case 1:
			
        	setContentView(R.layout.text1);
			break;
		case 2:
			setContentView(R.layout.text2);
        	break;
		case 3:
			setContentView(R.layout.text3);
    		break;
		case 4:
			
			setContentView(R.layout.text4);
    		break;
		default:
		}
    }
	Handler handler1 = new Handler();
    Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // handler自带方法实现定时器
            try {
                handler1.postDelayed(this, TIME);
                database.delete("btrssi", null, null);
                dFlag = 1;
                Log.e(TAG, "定时清空数据库");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };
  
	@Override
	protected void onDestroy(){
		
		player1.release();
		player2.release();
		player3.release();
		player4.release();
		
		MyApplication.getInstance().destroy();
		super.onDestroy();
	}





}



