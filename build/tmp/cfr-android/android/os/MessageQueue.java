/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Binder
 */
package android.os;

import android.os.Binder;
import android.os.Message;
import android.os.MessageQueue$IdleHandler;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;

public final class MessageQueue {
    private final boolean c;
    private long d;
    Message a;
    private final ArrayList e = new ArrayList();
    private MessageQueue$IdleHandler[] f;
    private boolean g;
    private boolean h;
    static Object b = new Object();

    private long b() {
        return 100L;
    }

    private void a(long l2) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(long l2, int n2) {
        Object object = b;
        synchronized (object) {
            try {
                b.wait(n2);
            }
            catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void b(long l2) {
        Object object = b;
        synchronized (object) {
            b.notifyAll();
        }
    }

    MessageQueue(boolean bl2) {
        this.c = bl2;
        this.d = this.b();
    }

    protected void finalize() {
        try {
            this.c();
        }
        finally {
            super.finalize();
        }
    }

    private void c() {
        if (this.d != 0L) {
            this.a(this.d);
            this.d = 0L;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    Message a() {
        long l2 = this.d;
        if (l2 == 0L) {
            return null;
        }
        int n2 = -1;
        int n3 = 0;
        while (true) {
            Object object;
            if (n3 != 0) {
                Binder.flushPendingCommands();
            }
            System.out.println("corroding: nativePollOnce:" + l2 + "," + n3);
            this.a(l2, n3);
            MessageQueue messageQueue = this;
            synchronized (messageQueue) {
                long l3;
                block21: {
                    l3 = SystemClock.a();
                    object = null;
                    Message message = this.a;
                    if (message != null && message.j == null) {
                        do {
                            object = message;
                        } while ((message = message.l) != null && !message.e());
                    }
                    if (message != null) {
                        if (l3 < message.h) {
                            n3 = (int)Math.min(message.h - l3, Integer.MAX_VALUE);
                            break block21;
                        } else {
                            this.h = false;
                            if (object != null) {
                                ((Message)object).l = message.l;
                            } else {
                                this.a = message.l;
                            }
                            message.l = null;
                            return message;
                        }
                    }
                    n3 = -1;
                }
                if (this.g) {
                    this.c();
                    return null;
                }
                if (n2 < 0 && (this.a == null || l3 < this.a.h)) {
                    n2 = this.e.size();
                }
                if (n2 <= 0) {
                    this.h = true;
                    continue;
                }
                if (this.f == null) {
                    this.f = new MessageQueue$IdleHandler[Math.max(n2, 4)];
                }
                this.f = this.e.toArray(this.f);
            }
            for (int i2 = 0; i2 < n2; ++i2) {
                MessageQueue$IdleHandler messageQueue$IdleHandler = this.f[i2];
                this.f[i2] = null;
                boolean bl2 = false;
                try {
                    bl2 = messageQueue$IdleHandler.a();
                }
                catch (Throwable throwable) {
                    Log.c("MessageQueue", "IdleHandler threw exception", throwable);
                }
                if (bl2) continue;
                object = this;
                synchronized (object) {
                    this.e.remove(messageQueue$IdleHandler);
                    continue;
                }
            }
            n2 = 0;
            n3 = 0;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    boolean a(Message message, long l2) {
        if (message.j == null) {
            throw new IllegalArgumentException("Message must have a target.");
        }
        if (message.f()) {
            throw new IllegalStateException(message + " This message is already in use.");
        }
        MessageQueue messageQueue = this;
        synchronized (messageQueue) {
            if (this.g) {
                IllegalStateException illegalStateException = new IllegalStateException(message.j + " sending message to a Handler on a dead thread");
                Log.a("MessageQueue", illegalStateException.getMessage(), illegalStateException);
                message.b();
                return false;
            }
            message.g();
            message.h = l2;
            Message message2 = this.a;
            if (message2 == null || l2 == 0L || l2 < message2.h) {
                message.l = message2;
                this.a = message;
                boolean bl2 = this.h;
            } else {
                boolean bl3 = this.h && message2.j == null && message.e();
                while (true) {
                    Message message3 = message2;
                    message2 = message2.l;
                    if (message2 == null || l2 < message2.h) break;
                    if (!bl3 || !message2.e()) continue;
                    bl3 = false;
                }
                message.l = message2;
                message3.l = message;
            }
            this.b(this.d);
        }
        return true;
    }
}

