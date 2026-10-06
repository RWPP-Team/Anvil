/*
 * Decompiled with CFR 0.152.
 */
package android.graphics;

import java.util.HashMap;
import java.util.Locale;

public class Color {
    private static final HashMap a = new HashMap();

    public static int a(int n2) {
        return n2 >>> 24;
    }

    public static int b(int n2) {
        return n2 >> 16 & 0xFF;
    }

    public static int c(int n2) {
        return n2 >> 8 & 0xFF;
    }

    public static int d(int n2) {
        return n2 & 0xFF;
    }

    public static int a(int n2, int n3, int n4) {
        return 0xFF000000 | n2 << 16 | n3 << 8 | n4;
    }

    public static int a(int n2, int n3, int n4, int n5) {
        return n2 << 24 | n3 << 16 | n4 << 8 | n5;
    }

    public static int a(String string2) {
        if (string2.charAt(0) == '#') {
            long l2 = Long.parseLong(string2.substring(1), 16);
            if (string2.length() == 7) {
                l2 |= 0xFFFFFFFFFF000000L;
            } else if (string2.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int)l2;
        }
        Integer n2 = (Integer)a.get(string2.toLowerCase(Locale.ROOT));
        if (n2 != null) {
            return n2;
        }
        throw new IllegalArgumentException("Unknown color");
    }

    static {
        a.put("black", -16777216);
        a.put("darkgray", -12303292);
        a.put("gray", -7829368);
        a.put("lightgray", -3355444);
        a.put("white", -1);
        a.put("red", -65536);
        a.put("green", -16711936);
        a.put("blue", -16776961);
        a.put("yellow", -256);
        a.put("cyan", -16711681);
        a.put("magenta", -65281);
        a.put("aqua", -16711681);
        a.put("fuchsia", -65281);
        a.put("darkgrey", -12303292);
        a.put("grey", -7829368);
        a.put("lightgrey", -3355444);
        a.put("lime", -16711936);
        a.put("maroon", -8388608);
        a.put("navy", -16777088);
        a.put("olive", -8355840);
        a.put("purple", -8388480);
        a.put("silver", -4144960);
        a.put("teal", -16744320);
    }
}

