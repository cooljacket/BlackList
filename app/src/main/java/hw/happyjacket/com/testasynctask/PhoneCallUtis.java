package hw.happyjacket.com.testasynctask;

import android.content.Context;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by jacket on 2016/4/20.
 */
public class PhoneCallUtis {
    public static final String TAG = "PhoneCALLUtils";
    static ITelephony iTelephony;
    private Context mContext;

    PhoneCallUtis(Context context) {
        mContext = context;
        if (iTelephony == null) {
            iTelephony = getITelephony(mContext);
        }
    }

    private ITelephony getITelephony(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
        Class c = TelephonyManager.class;
        Method getITelephonyMethod = null;
        try {
            getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null); // 获取声明的方法
            getITelephonyMethod.setAccessible(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        try {
            ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelephonyManager, (Object[]) null); // 获取实例
            return iTelephony;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void endCall(String incomingNumber) {
        if (iTelephony != null) {
            try {
                iTelephony.endCall(); // 挂断电话
            } catch (RemoteException e) {
                Log.d(TAG, "挂断[" + incomingNumber + "]的电话失败");
            }
        }
    }
}
