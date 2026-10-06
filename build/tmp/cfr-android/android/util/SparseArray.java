/*
 * Decompiled with CFR 0.152.
 */
package android.util;

import android.util.a;

public class SparseArray
implements Cloneable {
    private static final Object a = new Object();
    private boolean b = false;
    private int[] c;
    private Object[] d;
    private int e;

    public SparseArray() {
        this(10);
    }

    public SparseArray(int n2) {
        if (n2 == 0) {
            this.c = android.util.a.b;
            this.d = android.util.a.d;
        } else {
            n2 = com.a.a.a.a.c(n2);
            this.c = new int[n2];
            this.d = new Object[n2];
        }
        this.e = 0;
    }

    public SparseArray a() {
        SparseArray sparseArray = null;
        try {
            sparseArray = (SparseArray)super.clone();
            sparseArray.c = (int[])this.c.clone();
            sparseArray.d = (Object[])this.d.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return sparseArray;
    }

    public Object a(int n2) {
        return this.a(n2, null);
    }

    public Object a(int n2, Object object) {
        int n3 = android.util.a.a(this.c, this.e, n2);
        if (n3 < 0 || this.d[n3] == a) {
            return object;
        }
        return this.d[n3];
    }

    private void c() {
        int n2 = this.e;
        int n3 = 0;
        int[] nArray = this.c;
        Object[] objectArray = this.d;
        for (int i2 = 0; i2 < n2; ++i2) {
            Object object = objectArray[i2];
            if (object == a) continue;
            if (i2 != n3) {
                nArray[n3] = nArray[i2];
                objectArray[n3] = object;
                objectArray[i2] = null;
            }
            ++n3;
        }
        this.b = false;
        this.e = n3;
    }

    public void b(int n2, Object object) {
        int n3 = android.util.a.a(this.c, this.e, n2);
        if (n3 >= 0) {
            this.d[n3] = object;
        } else {
            if ((n3 ^= 0xFFFFFFFF) < this.e && this.d[n3] == a) {
                this.c[n3] = n2;
                this.d[n3] = object;
                return;
            }
            if (this.b && this.e >= this.c.length) {
                this.c();
                n3 = ~android.util.a.a(this.c, this.e, n2);
            }
            if (this.e >= this.c.length) {
                int n4 = com.a.a.a.a.c(this.e + 1);
                int[] nArray = new int[n4];
                Object[] objectArray = new Object[n4];
                System.arraycopy(this.c, 0, nArray, 0, this.c.length);
                System.arraycopy(this.d, 0, objectArray, 0, this.d.length);
                this.c = nArray;
                this.d = objectArray;
            }
            if (this.e - n3 != 0) {
                System.arraycopy(this.c, n3, this.c, n3 + 1, this.e - n3);
                System.arraycopy(this.d, n3, this.d, n3 + 1, this.e - n3);
            }
            this.c[n3] = n2;
            this.d[n3] = object;
            ++this.e;
        }
    }

    public int b() {
        if (this.b) {
            this.c();
        }
        return this.e;
    }

    public int b(int n2) {
        if (this.b) {
            this.c();
        }
        return this.c[n2];
    }

    public Object c(int n2) {
        if (this.b) {
            this.c();
        }
        return this.d[n2];
    }

    public void c(int n2, Object object) {
        int n3;
        if (this.e != 0 && n2 <= this.c[this.e - 1]) {
            this.b(n2, object);
            return;
        }
        if (this.b && this.e >= this.c.length) {
            this.c();
        }
        if ((n3 = this.e) >= this.c.length) {
            int n4 = com.a.a.a.a.c(n3 + 1);
            int[] nArray = new int[n4];
            Object[] objectArray = new Object[n4];
            System.arraycopy(this.c, 0, nArray, 0, this.c.length);
            System.arraycopy(this.d, 0, objectArray, 0, this.d.length);
            this.c = nArray;
            this.d = objectArray;
        }
        this.c[n3] = n2;
        this.d[n3] = object;
        this.e = n3 + 1;
    }

    public String toString() {
        if (this.b() <= 0) {
            return "{}";
        }
        StringBuilder stringBuilder = new StringBuilder(this.e * 28);
        stringBuilder.append('{');
        for (int i2 = 0; i2 < this.e; ++i2) {
            if (i2 > 0) {
                stringBuilder.append(", ");
            }
            int n2 = this.b(i2);
            stringBuilder.append(n2);
            stringBuilder.append('=');
            Object object = this.c(i2);
            if (object != this) {
                stringBuilder.append(object);
                continue;
            }
            stringBuilder.append("(this Map)");
        }
        stringBuilder.append('}');
        return stringBuilder.toString();
    }

    public /* synthetic */ Object clone() {
        return this.a();
    }
}

