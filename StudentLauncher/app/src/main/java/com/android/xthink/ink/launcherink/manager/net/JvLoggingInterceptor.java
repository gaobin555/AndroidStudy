package com.android.xthink.ink.launcherink.manager.net;

import android.annotation.TargetApi;
import android.os.Build;

import com.android.xthink.ink.launcherink.common.utils.MyLog;

import java.io.EOFException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

/**
 * Created by liyuyan on 2016/12/23.
 */

public class JvLoggingInterceptor implements Interceptor {
    private String LOG_TAG = "LogInterceptor";

    private static int mCounter = 0;
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private boolean isDebug = true;

    public JvLoggingInterceptor setLogTag(String tag) {
        LOG_TAG = tag;
        return this;
    }

    public JvLoggingInterceptor setDebug(boolean isDebug) {
        this.isDebug = isDebug;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        if (!isDebug) {
            return chain.proceed(request);
        } else {
            /**
             * print request log
             */
            StringBuilder strUrl = new StringBuilder(request.url().toString());

            if (request.method().equals("POST")) {
                strUrl.append("? ");
                FormBody formBody;
                if (request.body() instanceof FormBody) {
                    formBody = (FormBody) request.body();
                    try {
                        Field encodedNames = formBody.getClass().getDeclaredField("encodedNames");
                        Field encodedValues = formBody.getClass().getDeclaredField("encodedValues");
                        encodedNames.setAccessible(true);
                        encodedValues.setAccessible(true);
                        List<String> names = (List<String>) encodedNames.get(formBody);
                        List<String> values = (List<String>) encodedValues.get(formBody);

                        for (int i = 0; i < names.size(); i++) {
                            strUrl.append(names.get(i)).append('=').append(values.get(i)).append('&');
                        }
                        strUrl.deleteCharAt(strUrl.length() - 1);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

            int localCount = mCounter++;
            MyLog.d(LOG_TAG, localCount + "  Times Request : " + request.method() + ' ' + strUrl);

            long startNs = System.nanoTime();
            Response response;
            try {
                response = chain.proceed(request);
            } catch (Exception e) {
                MyLog.e(LOG_TAG, "<--------- HTTP FAILED: " + e);
                throw e;
            }
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            ResponseBody responseBody = response.body();

            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            if (!isPlaintext(buffer)) {
                MyLog.d(LOG_TAG, "\n<-- END HTTP (binary " + buffer.size() + "-byte body omitted)");
                return response;
            }

            if (responseBody.contentLength() != 0) {
                /**
                 * print response log
                 */
                MyLog.d(LOG_TAG, localCount + "  Times Response : " + response.code() + ' ' + response.message() + ' '
                        + '(' + tookMs + "ms" + ')' + " Result : " + buffer.clone().readString(UTF8));
            }

            return response;
        }
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }
}
