// dummy ClipboardObject that makes the Clipboard's dataFlavor happy.

package com.ti.et.phoenix.jni;

import java.io.Serializable;

public class ClipboardObject implements Serializable
{
    private static final long serialVersionUID = -5700061155785307847L;
    private byte[] mData = null;
    public ClipboardObject() { }
}