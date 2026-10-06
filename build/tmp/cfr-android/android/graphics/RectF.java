/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 */
package android.graphics;

import android.graphics.Rect;
import android.graphics.RectF$1;
import android.os.Parcel;
import android.os.Parcelable;

public class RectF
implements Parcelable {
    public float a;
    public float b;
    public float c;
    public float d;
    public static final Parcelable.Creator e = new RectF$1();

    public RectF() {
    }

    public RectF(float f2, float f3, float f4, float f5) {
        this.a = f2;
        this.b = f3;
        this.c = f4;
        this.d = f5;
    }

    public RectF(RectF rectF) {
        this.a = rectF.a;
        this.b = rectF.b;
        this.c = rectF.c;
        this.d = rectF.d;
    }

    public RectF(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public String toString() {
        return "RectF(" + this.a + ", " + this.b + ", " + this.c + ", " + this.d + ")";
    }

    public final boolean a() {
        return this.a >= this.c || this.b >= this.d;
    }

    public final float b() {
        return this.c - this.a;
    }

    public final float c() {
        return this.d - this.b;
    }

    public final float d() {
        return (this.a + this.c) * 0.5f;
    }

    public final float e() {
        return (this.b + this.d) * 0.5f;
    }

    public void f() {
        this.d = 0.0f;
        this.b = 0.0f;
        this.c = 0.0f;
        this.a = 0.0f;
    }

    public void a(float f2, float f3, float f4, float f5) {
        this.a = f2;
        this.b = f3;
        this.c = f4;
        this.d = f5;
    }

    public void a(RectF rectF) {
        this.a = rectF.a;
        this.b = rectF.b;
        this.c = rectF.c;
        this.d = rectF.d;
    }

    public void a(Rect rect) {
        this.a = rect.a;
        this.b = rect.b;
        this.c = rect.c;
        this.d = rect.d;
    }

    public void a(float f2, float f3) {
        this.a += f2;
        this.b += f3;
        this.c += f2;
        this.d += f3;
    }

    public boolean b(float f2, float f3) {
        return this.a < this.c && this.b < this.d && f2 >= this.a && f2 < this.c && f3 >= this.b && f3 < this.d;
    }

    public boolean b(float f2, float f3, float f4, float f5) {
        if (this.a < f4 && f2 < this.c && this.b < f5 && f3 < this.d) {
            if (this.a < f2) {
                this.a = f2;
            }
            if (this.b < f3) {
                this.b = f3;
            }
            if (this.c > f4) {
                this.c = f4;
            }
            if (this.d > f5) {
                this.d = f5;
            }
            return true;
        }
        return false;
    }

    public boolean b(RectF rectF) {
        return this.b(rectF.a, rectF.b, rectF.c, rectF.d);
    }

    public static boolean a(RectF rectF, RectF rectF2) {
        return rectF.a < rectF2.c && rectF2.a < rectF.c && rectF.b < rectF2.d && rectF2.b < rectF.d;
    }

    public void c(float f2, float f3) {
        if (f2 < this.a) {
            this.a = f2;
        } else if (f2 > this.c) {
            this.c = f2;
        }
        if (f3 < this.b) {
            this.b = f3;
        } else if (f3 > this.d) {
            this.d = f3;
        }
    }

    public void g() {
        float f2;
        if (this.a > this.c) {
            f2 = this.a;
            this.a = this.c;
            this.c = f2;
        }
        if (this.b > this.d) {
            f2 = this.b;
            this.b = this.d;
            this.d = f2;
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int n2) {
        parcel.writeFloat(this.a);
        parcel.writeFloat(this.b);
        parcel.writeFloat(this.c);
        parcel.writeFloat(this.d);
    }

    public void a(Parcel parcel) {
        this.a = parcel.readFloat();
        this.b = parcel.readFloat();
        this.c = parcel.readFloat();
        this.d = parcel.readFloat();
    }
}

