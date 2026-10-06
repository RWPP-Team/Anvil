/*
 * Decompiled with CFR 0.152.
 */
package android.net.http;

import android.util.Log;

class b {
    private final String a;
    private final int b;

    private boolean a() {
        return Log.isLoggable(this.a, this.b);
    }

    private void a(String string2) {
        Log.a(this.b, this.a, string2);
    }

    static /* synthetic */ boolean a(b b2) {
        return b2.a();
    }

    static /* synthetic */ void a(b b2, String string2) {
        b2.a(string2);
    }
}

