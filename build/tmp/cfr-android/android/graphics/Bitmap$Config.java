/*
 * Decompiled with CFR 0.152.
 */
package android.graphics;

public enum Bitmap$Config {
    a(2),
    b(4),
    c(5),
    d(6);

    final int e;
    private static Bitmap$Config[] f;

    /*
     * WARNING - Possible parameter corruption
     * WARNING - void declaration
     */
    private Bitmap$Config() {
        void var3_2;
        this.e = var3_2;
    }

    static Bitmap$Config a(int n2) {
        return f[n2];
    }

    static {
        f = new Bitmap$Config[]{null, null, a, null, b, c, d};
    }
}

