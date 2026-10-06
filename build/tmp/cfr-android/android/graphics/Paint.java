/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.graphics.MaskFilter
 *  android.graphics.PathEffect
 *  android.graphics.Rasterizer
 *  android.graphics.Shader
 */
package android.graphics;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint$Align;
import android.graphics.Paint$Cap;
import android.graphics.Paint$FontMetrics;
import android.graphics.Paint$Join;
import android.graphics.Paint$Style;
import android.graphics.PathEffect;
import android.graphics.Rasterizer;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.TemporaryBuffer;
import android.graphics.Typeface;
import android.graphics.Xfermode;
import java.util.Locale;

public class Paint {
    public int a;
    private ColorFilter r;
    private MaskFilter s;
    private PathEffect t;
    private Rasterizer u;
    private Shader v;
    private Typeface w;
    private Xfermode x;
    private boolean y;
    private float z;
    private float A;
    private Locale B;
    public boolean b;
    public float c;
    public float d;
    public float e;
    public int f;
    public int g = 2;
    static final Paint$Style[] h = new Paint$Style[]{Paint$Style.a, Paint$Style.b, Paint$Style.c};
    static final Paint$Cap[] i = new Paint$Cap[]{Paint$Cap.a, Paint$Cap.b, Paint$Cap.c};
    static final Paint$Join[] j = new Paint$Join[]{Paint$Join.a, Paint$Join.b, Paint$Join.c};
    static final Paint$Align[] k = new Paint$Align[]{Paint$Align.a, Paint$Align.b, Paint$Align.c};
    int l;
    Paint$Style m = Paint$Style.a;
    int n;
    float o = 0.0f;
    Paint$Align p;
    float q = 16.0f;

    public Paint() {
        this(0);
    }

    public Paint(int n2) {
        this.a = Paint.o();
        this.a();
        this.a(n2 | 0x500);
        this.A = 1.0f;
        this.z = 1.0f;
        this.a(Locale.getDefault());
    }

    public Paint(Paint paint) {
        this.a = Paint.d(paint.a);
        this.b(paint);
    }

    public void a() {
        Paint.e(this.a);
        this.a(1280);
        this.n = -1;
        this.m = Paint$Style.a;
        this.q = 16.0f;
        this.p = Paint$Align.a;
        this.r = null;
        this.s = null;
        this.t = null;
        this.u = null;
        this.v = null;
        this.w = null;
        this.x = null;
        this.y = false;
        this.z = 1.0f;
        this.A = 1.0f;
        this.b = false;
        this.c = 0.0f;
        this.d = 0.0f;
        this.e = 0.0f;
        this.f = 0;
        this.g = 2;
        this.a(Locale.getDefault());
    }

    public void a(Paint paint) {
        if (this != paint) {
            Paint.a(this.a, paint.a);
            this.b(paint);
        }
    }

    private void b(Paint paint) {
        this.m = paint.m;
        this.n = paint.n;
        this.q = paint.q;
        this.p = paint.p;
        this.r = paint.r;
        this.s = paint.s;
        this.t = paint.t;
        this.u = paint.u;
        this.w = paint.w;
        this.x = paint.x;
        this.y = paint.y;
        this.z = paint.z;
        this.A = paint.A;
        this.b = paint.b;
        this.c = paint.c;
        this.d = paint.d;
        this.e = paint.e;
        this.f = paint.f;
        this.g = paint.g;
        this.B = paint.B;
        this.o = paint.o;
    }

    public int b() {
        return this.l;
    }

    public void a(int n2) {
        this.l = n2;
    }

    public final boolean c() {
        return (this.b() & 1) != 0;
    }

    public void a(boolean bl2) {
        if (bl2) {
            this.a(this.l | 1);
        } else {
            this.a(this.l & 0xFFFFFFFE);
        }
    }

    public void b(boolean bl2) {
    }

    public void c(boolean bl2) {
    }

    public void d(boolean bl2) {
    }

    public Paint$Style d() {
        return this.m;
    }

    public void a(Paint$Style paint$Style) {
        this.m = paint$Style;
    }

    public int e() {
        return this.n;
    }

    public void b(int n2) {
        this.n = n2;
    }

    public int f() {
        return Color.a(this.n);
    }

    public void c(int n2) {
        this.n = Color.a(n2, Color.b(this.n), Color.c(this.n), Color.d(this.n));
    }

    public void a(int n2, int n3, int n4, int n5) {
        this.b(n2 << 24 | n3 << 16 | n4 << 8 | n5);
    }

    public float g() {
        return this.o;
    }

    public void a(float f2) {
        this.o = f2;
    }

    public void a(Paint$Cap paint$Cap) {
        Paint.b(this.a, paint$Cap.d);
    }

    public ColorFilter h() {
        return this.r;
    }

    public ColorFilter a(ColorFilter colorFilter) {
        int n2 = 0;
        Paint.c(this.a, n2);
        this.r = colorFilter;
        return colorFilter;
    }

    public Xfermode a(Xfermode xfermode) {
        int n2 = 0;
        Paint.d(this.a, n2);
        this.x = xfermode;
        return xfermode;
    }

    public Typeface i() {
        return this.w;
    }

    public Typeface a(Typeface typeface) {
        this.w = typeface;
        return typeface;
    }

    public Paint$Align j() {
        return this.p;
    }

    public void a(Paint$Align paint$Align) {
        this.p = paint$Align;
    }

    public void a(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale cannot be null");
        }
        if (locale.equals(this.B)) {
            return;
        }
        this.B = locale;
        Paint.a(this.a, locale.toString());
    }

    public float k() {
        return this.q;
    }

    public void b(float f2) {
        this.q = f2;
    }

    public float l() {
        return -this.q;
    }

    public float m() {
        return 0.0f;
    }

    public float a(Paint$FontMetrics paint$FontMetrics) {
        return 0.0f;
    }

    public Paint$FontMetrics n() {
        Paint$FontMetrics paint$FontMetrics = new Paint$FontMetrics();
        this.a(paint$FontMetrics);
        return paint$FontMetrics;
    }

    public float a(String string2) {
        if (string2 == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (string2.length() == 0) {
            return 0.0f;
        }
        if (!this.y) {
            return (float)Math.ceil(this.a(string2, this.g));
        }
        float f2 = this.k();
        this.b(f2 * this.z);
        float f3 = this.a(string2, this.g);
        this.b(f2);
        return (float)Math.ceil(f3 * this.A);
    }

    private float a(String string2, int n2) {
        return (float)string2.length() * this.k();
    }

    public int a(char[] cArray, int n2, int n3, float f2, float[] fArray) {
        if (cArray == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (n2 < 0 || cArray.length - n2 < Math.abs(n3)) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArray.length == 0 || n3 == 0) {
            return 0;
        }
        if (!this.y) {
            return this.a(cArray, n2, n3, f2, this.g, fArray);
        }
        float f3 = this.k();
        this.b(f3 * this.z);
        int n4 = this.a(cArray, n2, n3, f2 * this.z, this.g, fArray);
        this.b(f3);
        if (fArray != null) {
            fArray[0] = fArray[0] * this.A;
        }
        return n4;
    }

    private int a(char[] cArray, int n2, int n3, float f2, int n4, float[] fArray) {
        float f3 = this.k();
        if (f2 > f3 * (float)n3) {
            return n3;
        }
        if (f2 == 0.0f) {
            return 1;
        }
        int n5 = (int)(f2 / f3);
        if (n5 < 1) {
            n5 = 1;
        }
        return n5;
    }

    private int a(String string2, boolean bl2, float f2, int n2, float[] fArray) {
        return string2.length();
    }

    public int a(CharSequence charSequence, int n2, int n3, boolean bl2, float f2, float[] fArray) {
        if (charSequence == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((n2 | n3 | n3 - n2 | charSequence.length() - n3) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (charSequence.length() == 0 || n2 == n3) {
            return 0;
        }
        if (n2 == 0 && charSequence instanceof String && n3 == charSequence.length()) {
            return this.a((String)charSequence, bl2, f2, fArray);
        }
        char[] cArray = TemporaryBuffer.a(n3 - n2);
        Paint.a(charSequence, n2, n3, cArray, 0);
        int n4 = bl2 ? this.a(cArray, 0, n3 - n2, f2, fArray) : this.a(cArray, 0, -(n3 - n2), f2, fArray);
        TemporaryBuffer.a(cArray);
        return n4;
    }

    public static void a(CharSequence charSequence, int n2, int n3, char[] cArray, int n4) {
        Class<?> clazz = charSequence.getClass();
        if (clazz == String.class) {
            ((String)charSequence).getChars(n2, n3, cArray, n4);
        } else if (clazz == StringBuffer.class) {
            ((StringBuffer)charSequence).getChars(n2, n3, cArray, n4);
        } else if (clazz == StringBuilder.class) {
            ((StringBuilder)charSequence).getChars(n2, n3, cArray, n4);
        } else {
            for (int i2 = n2; i2 < n3; ++i2) {
                cArray[n4++] = charSequence.charAt(i2);
            }
        }
    }

    public int a(String string2, boolean bl2, float f2, float[] fArray) {
        if (string2 == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if (string2.length() == 0) {
            return 0;
        }
        if (!this.y) {
            return this.a(string2, bl2, f2, this.g, fArray);
        }
        float f3 = this.k();
        this.b(f3 * this.z);
        int n2 = this.a(string2, bl2, f2 * this.z, this.g, fArray);
        this.b(f3);
        if (fArray != null) {
            fArray[0] = fArray[0] * this.A;
        }
        return n2;
    }

    public int a(char[] cArray, int n2, int n3, float[] fArray) {
        if (cArray == null) {
            throw new IllegalArgumentException("text cannot be null");
        }
        if ((n2 | n3) < 0 || n2 + n3 > cArray.length || n3 > fArray.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (cArray.length == 0 || n3 == 0) {
            return 0;
        }
        if (!this.y) {
            return Paint.a(this.a, cArray, n2, n3, this.g, fArray);
        }
        float f2 = this.k();
        this.b(f2 * this.z);
        int n4 = Paint.a(this.a, cArray, n2, n3, this.g, fArray);
        this.b(f2);
        int n5 = 0;
        while (n5 < n4) {
            int n6 = n5++;
            fArray[n6] = fArray[n6] * this.A;
        }
        return n4;
    }

    public void a(String string2, int n2, int n3, Rect rect) {
        if ((n2 | n3 | n3 - n2 | string2.length() - n3) < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (rect == null) {
            throw new NullPointerException("need bounds Rect");
        }
        rect.a(0, 0, 0, (int)this.q);
    }

    protected void finalize() {
        try {
            Paint.f(this.a);
        }
        finally {
            super.finalize();
        }
    }

    private static int o() {
        return 0;
    }

    private static int d(int n2) {
        return 0;
    }

    private static void e(int n2) {
    }

    private static void a(int n2, int n3) {
    }

    private static void b(int n2, int n3) {
    }

    private static int c(int n2, int n3) {
        return 0;
    }

    private static int d(int n2, int n3) {
        return 0;
    }

    private static void a(int n2, String string2) {
    }

    private static int a(int n2, char[] cArray, int n3, int n4, int n5, float[] fArray) {
        return 0;
    }

    private static void f(int n2) {
    }
}

