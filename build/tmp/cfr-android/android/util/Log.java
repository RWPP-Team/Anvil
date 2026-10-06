/*
 * Decompiled with CFR 0.152.
 */
package android.util;

import android.util.Log$1;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class Log {
    private static final ThreadLocal a = new Log$1();

    private Log() {
    }

    public static int a(String string2, String string3) {
        return Log.a(0, 2, string2, string3);
    }

    public static int b(String string2, String string3) {
        return Log.a(0, 3, string2, string3);
    }

    public static int c(String string2, String string3) {
        return Log.a(0, 5, string2, string3);
    }

    public static int a(String string2, String string3, Throwable throwable) {
        return Log.a(0, 5, string2, string3 + '\n' + Log.a(throwable));
    }

    public static native boolean isLoggable(String var0, int var1);

    public static int d(String string2, String string3) {
        return Log.a(0, 6, string2, string3);
    }

    public static int b(String string2, String string3, Throwable throwable) {
        return Log.a(0, 6, string2, string3 + '\n' + Log.a(throwable));
    }

    public static int c(String string2, String string3, Throwable throwable) {
        return Log.a(0, string2, string3, throwable, false);
    }

    static int a(int n2, String string2, String string3, Throwable throwable, boolean bl2) {
        throw new RuntimeException("removed");
    }

    public static String a(Throwable throwable) {
        StackTraceElement[] stackTraceElementArray;
        StringWriter stringWriter = new StringWriter();
        for (StackTraceElement stackTraceElement : stackTraceElementArray = new Throwable().getStackTrace()) {
            stringWriter.write(stackTraceElement.toString() + "\n");
        }
        return stringWriter.toString();
    }

    public static int a(int n2, int n3, String string2, String string3) {
        Log.a(n2, string2, string3);
        return 0;
    }

    public static int a(int n2, String string2, String string3) {
        String string4 = ((SimpleDateFormat)a.get()).format(new Date());
        System.out.println(string4 + ": " + string3);
        return 0;
    }
}

