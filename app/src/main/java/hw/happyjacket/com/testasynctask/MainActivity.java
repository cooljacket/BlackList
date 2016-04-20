package hw.happyjacket.com.testasynctask;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    private EditText weatherText;
    private String weatherInfo;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        Log.d("msg", (String)msg.obj);
        weatherText.setText((String)msg.obj);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherText = (EditText) findViewById(R.id.weather);
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetWeatherTask("广州").execute();
            }
        });

        TelephonyManager mTelephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    if (incomingNumber.equals("18819461671")) {
                        PhoneCallUtis ph = new PhoneCallUtis(MainActivity.this);
                        ph.endCall(incomingNumber);
                    }

                    break;
                default:
                    break;
            }
        }
    };

//    //挂断电话
//    public void endCall(String incomingNumber){
//        try {
//            Class<?> clazz = Class.forName("android.os.ServiceManager");
//            Method method = clazz.getMethod("getService", String.class);
//            IBinder ibinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
//            ITelephony iTelephony = ITelephony.Stub.asInterface(ibinder);
//            iTelephony.endCall();
//        }catch(Exception e){
//            e.printStrackTrace();
//        }
//    }

    class GetWeatherTask extends AsyncTask<Void, Void, Void> {
        private String weatherInfo, location;

        GetWeatherTask(String loc) {
            location = loc;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String weatherURL = CommonSettingsAndFuncs.HostURL + String.format(CommonSettingsAndFuncs.GetWeatherURLFormat,
                    CommonSettingsAndFuncs.changeCharset(location, "UTF-8"));
            HttpConnectionUtil.getIt(weatherURL, new HttpConnectionUtil.HttpCallbackListener() {
                @Override
                public void onFinish(String response) {
                    weatherInfo = response;
                }

                @Override
                public void onError(Exception e) {
                    weatherInfo = null;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            try {
                weatherInfo = CommonSettingsAndFuncs.ParseWeatherXML(new ByteArrayInputStream(weatherInfo.getBytes()));
            } catch (XmlPullParserException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } catch (IOException e) {
                weatherInfo = "error";
                e.printStackTrace();
            } finally {
                weatherText.setText(weatherInfo);
            }
        }
    }
}