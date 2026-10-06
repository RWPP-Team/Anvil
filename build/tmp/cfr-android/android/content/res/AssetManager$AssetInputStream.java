/*
 * Decompiled with CFR 0.152.
 */
package android.content.res;

import android.content.res.AssetManager;
import java.io.InputStream;

public final class AssetManager$AssetInputStream
extends InputStream {
    private int b;
    private long c;
    private long d;
    final /* synthetic */ AssetManager a;

    @Override
    public final int read() {
        return AssetManager.a(this.a, this.b);
    }

    @Override
    public final boolean markSupported() {
        return true;
    }

    @Override
    public final int available() {
        long l2 = AssetManager.b(this.a, this.b);
        return l2 > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)l2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final void close() {
        AssetManager assetManager = this.a;
        synchronized (assetManager) {
            if (this.b != 0) {
                AssetManager.c(this.a, this.b);
                this.b = 0;
                AssetManager.d(this.a, this.hashCode());
            }
        }
    }

    @Override
    public final void mark(int n2) {
        this.d = AssetManager.a(this.a, this.b, 0L, 0);
    }

    @Override
    public final void reset() {
        AssetManager.a(this.a, this.b, this.d, -1);
    }

    @Override
    public final int read(byte[] byArray) {
        return AssetManager.a(this.a, this.b, byArray, 0, byArray.length);
    }

    @Override
    public final int read(byte[] byArray, int n2, int n3) {
        return AssetManager.a(this.a, this.b, byArray, n2, n3);
    }

    @Override
    public final long skip(long l2) {
        long l3 = AssetManager.a(this.a, this.b, 0L, 0);
        if (l3 + l2 > this.c) {
            l2 = this.c - l3;
        }
        if (l2 > 0L) {
            AssetManager.a(this.a, this.b, l2, 0);
        }
        return l2;
    }

    protected void finalize() {
        this.close();
    }
}

