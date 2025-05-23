package com.lzyp.common.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import androidx.core.os.ConfigurationCompat;
import androidx.core.os.LocaleListCompat;

import com.lzyp.common.CommonAppContext;
import com.lzyp.common.Constants;

import java.util.Locale;

/**
 * Created by cxf on 2019/6/5.
 */

public class LanguageUtil {

    private static LanguageUtil instance;
    private String mLanguage;

    public LanguageUtil() {
        mLanguage = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);
        if (TextUtils.isEmpty(mLanguage)) {
            getSystemLanguage();
        }
    }


    public static LanguageUtil getInstance() {
        if (instance == null) {
            synchronized (LanguageUtil.class) {
                if (instance == null) {
                    instance = new LanguageUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 判断系统语言是否是中文
     */
    private void getSystemLanguage() {
        String lang = Constants.LANG_ZH;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleListCompat listCompat = ConfigurationCompat.getLocales(Resources.getSystem().getConfiguration());
            if (listCompat.size() > 0) {
                Locale locale = listCompat.get(0);
                if (locale != null) {
                    String localeName = locale.toString();
                    if (localeName.startsWith("en")) {
                        lang = Constants.LANG_EN;
                    } else {
                        lang = Constants.LANG_ZH;
                    }
                }
            }
        } else {
            Locale locale = Locale.getDefault();
            if (locale != null) {
                String localeName = locale.toString();
                if (localeName.startsWith("en")) {
                    lang = Constants.LANG_EN;
                } else {
                    lang = Constants.LANG_ZH;
                }
            }
        }
        setLanguage(lang);
    }

    /**
     * 设置语言
     */
    private static void setConfiguration() {
        Locale targetLocale = getLanguageLocale();
        Resources resources = CommonAppContext.getInstance().getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(targetLocale);
        DisplayMetrics dm = resources.getDisplayMetrics();
        resources.updateConfiguration(configuration, dm);//语言更换生效的代码!
    }


    public static Locale getLanguageLocale() {
        String lang = LanguageUtil.getInstance().getLanguage();
        if (!TextUtils.isEmpty(lang)) {
            if (Constants.LANG_ZH.equals(lang)) {
                return Locale.SIMPLIFIED_CHINESE;
            } else if (Constants.LANG_EN.equals(lang)) {
                return Locale.US;
            }
        }
        return Locale.SIMPLIFIED_CHINESE;
    }


    public void updateLanguage(String language) {
        setLanguage(language);
        setConfiguration();
    }


    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return createConfigurationResources(context);
        } else {
            setConfiguration();
            return context;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static Context createConfigurationResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = getLanguageLocale();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public String getLanguage() {
        if (TextUtils.isEmpty(mLanguage)) {
            mLanguage = SpUtil.getInstance().getStringValue(SpUtil.LANGUAGE);
            if (TextUtils.isEmpty(mLanguage)) {
                getSystemLanguage();
            }
        }
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage = language;
        SpUtil.getInstance().setStringValue(SpUtil.LANGUAGE, language);
    }

    public static boolean isZh() {
        return Constants.LANG_ZH.equals(LanguageUtil.getInstance().getLanguage());
    }

    public static boolean isEn() {
        return Constants.LANG_EN.equals(LanguageUtil.getInstance().getLanguage());
    }
}
