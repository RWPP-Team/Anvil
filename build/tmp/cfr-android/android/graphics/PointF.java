/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Parcel
 *  android.os.Parcelable
 */
package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;

public class PointF
implements Parcelable {
    public float a;
    public float b;

    public PointF() {
    }

    public PointF(float f2, float f3) {
        this.a = f2;
        this.b = f3;
    }

    public final void a(float f2, float f3) {
        this.a = f2;
        this.b = f3;
    }

    public final void a(PointF pointF) {
        this.a = pointF.a;
        this.b = pointF.b;
    }

    public final void b(float f2, float f3) {
        this.a += f2;
        this.b += f3;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int n2) {
        parcel.writeFloat(this.a);
        parcel.writeFloat(this.b);
    }
}

