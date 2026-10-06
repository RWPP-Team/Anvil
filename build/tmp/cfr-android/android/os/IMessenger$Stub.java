/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Binder
 *  android.os.IBinder
 *  android.os.Parcel
 */
package android.os;

import android.os.Binder;
import android.os.IBinder;
import android.os.IMessenger;
import android.os.Message;
import android.os.Parcel;

public abstract class IMessenger$Stub
extends Binder
implements IMessenger {
    public IMessenger$Stub() {
        this.attachInterface(this, "android.os.IMessenger");
    }

    public IBinder asBinder() {
        return this;
    }

    public boolean onTransact(int n2, Parcel parcel, Parcel parcel2, int n3) {
        switch (n2) {
            case 1598968902: {
                parcel2.writeString("android.os.IMessenger");
                return true;
            }
            case 1: {
                parcel.enforceInterface("android.os.IMessenger");
                Message message = 0 != parcel.readInt() ? (Message)Message.m.createFromParcel(parcel) : null;
                this.a(message);
                return true;
            }
        }
        return super.onTransact(n2, parcel, parcel2, n3);
    }
}

