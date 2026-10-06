/*
 * Decompiled with CFR 0.152.
 */
package android.util;

class a {
    static final boolean[] a = new boolean[0];
    static final int[] b = new int[0];
    static final long[] c = new long[0];
    static final Object[] d = new Object[0];

    static int a(int[] nArray, int n2, int n3) {
        int n4 = 0;
        int n5 = n2 - 1;
        while (n4 <= n5) {
            int n6 = n4 + n5 >>> 1;
            int n7 = nArray[n6];
            if (n7 < n3) {
                n4 = n6 + 1;
                continue;
            }
            if (n7 > n3) {
                n5 = n6 - 1;
                continue;
            }
            return n6;
        }
        return ~n4;
    }
}

