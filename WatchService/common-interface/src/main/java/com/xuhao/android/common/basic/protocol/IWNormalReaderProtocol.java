package com.xuhao.android.common.basic.protocol;


import com.xuhao.android.common.interfacies.IReaderProtocol;

import java.nio.ByteOrder;

public class IWNormalReaderProtocol implements IReaderProtocol {

    @Override
    public int getHeaderLength() {
        return 2;
    }

    @Override
    public int getBodyLength(byte[] header, ByteOrder byteOrder) {
        return header.length;
    }
}