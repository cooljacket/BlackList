package hw.happyjacket.com.testasynctask;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jacket on 2016/4/19.
 */
public class CommonSettingsAndFuncs {
    public static String HostURL = "http://webservice.webxml.com.cn";
    public static String GetWeatherURLFormat = "/WebServices/WeatherWS.asmx/getWeather?theCityCode=%s&theUserID=";

    // 将字符串转换成指定的字符集格式（这里用到的是utf8）
    public static String changeCharset(String str, String newCharset) {
        if (str != null) {
            try {
                byte[] bytes = str.getBytes(newCharset);
                String newStr = "";
                for (int i = 0; i < bytes.length; ++i)
                    newStr += String.format("%%%X", bytes[i]);
                Log.d("response newStr", newStr);
                return newStr;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static ArrayList<String> ParseXMLHelper(InputStream xml) throws XmlPullParserException, IOException {
        ArrayList<String> contents = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(xml, "UTF-8");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, null, "ArrayOfString");
        while (parser.nextTag() == XmlPullParser.START_TAG) {
            parser.require(XmlPullParser.START_TAG, null, "string");
            contents.add(parser.nextText());
            if (parser.getEventType() != XmlPullParser.END_TAG) {
                parser.nextTag();
            }
            parser.require(XmlPullParser.END_TAG, null, "string");
        }
        parser.require(XmlPullParser.END_TAG, null, "ArrayOfString");

        return contents;
    }

    public static String ParseWeatherXML(InputStream xml) throws XmlPullParserException, IOException {
        ArrayList<String> weather = ParseXMLHelper(xml);
        for (int i = 0; i < weather.size(); ++i)
            Log.d("index " + i, weather.get(i));
        String location = weather.get(1);
        String wea = weather.get(7).split(" ")[1];
        String temperature = weather.get(8);
        String sun = weather.get(5).replace("：", "").replace("。", "，");

        String content = weather.get(6);
        Pattern p = Pattern.compile("(感冒指数：[^，]*，([^。]*)。)(穿衣指数：[^，]*，([^。]*)。)(洗车指数：[^。]*。)(运动指数：[^，]*，([^。]*)。)");
        Matcher m = p.matcher(content);
        if (m.find())
            content = m.group(2) + "；" + m.group(4) + "；" + m.group(7);

        for (int i = 1; i <= m.groupCount(); ++i)
            Log.d("index " + i, m.group(i));

        return String.format("<妈妈>，今天%s%s，气温%s，%s%s。注意好身体，爱你们。", location, wea, temperature, sun, content).replaceAll("您", "");
    }
}
