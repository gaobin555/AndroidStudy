package com.xuhao.android.libsocket.impl.iocore;

import com.xuhao.android.common.basic.bean.OriginalData;
import com.xuhao.android.common.basic.protocol.IWNormalReaderProtocol;
import com.xuhao.android.common.interfacies.IReaderProtocol;
import com.xuhao.android.common.utils.BytesUtils;
import com.xuhao.android.common.utils.SLog;
import com.xuhao.android.libsocket.impl.exceptions.ReadException;
import com.xuhao.android.libsocket.sdk.client.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.client.action.IAction;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by xuhao on 2017/5/31.
 */

public class ReaderImpl extends AbsReader {

    private ByteBuffer mRemainingBuf;

    @Override
    public void read() throws RuntimeException {
        OriginalData originalData = new OriginalData();
        IReaderProtocol headerProtocol = mOkOptions.getReaderProtocol();

        if (headerProtocol instanceof IWNormalReaderProtocol) {
            customIWRead(originalData);
        } else {
            defaultRead(originalData, headerProtocol);
        }
    }

    private void defaultRead(OriginalData originalData, IReaderProtocol readerProtocol) {
        ByteBuffer headBuf = ByteBuffer.allocate(readerProtocol.getHeaderLength());
        headBuf.order(mOkOptions.getReadByteOrder());
        try {
            if (mRemainingBuf != null) {
                mRemainingBuf.flip();
                int length = Math.min(mRemainingBuf.remaining(), readerProtocol.getHeaderLength());
                headBuf.put(mRemainingBuf.array(), 0, length);
                if (length < readerProtocol.getHeaderLength()) {
                    //there are no data left
                    mRemainingBuf = null;
                    readHeaderFromChannel(headBuf, readerProtocol.getHeaderLength() - length);
                } else {
                    mRemainingBuf.position(readerProtocol.getHeaderLength());
                }
            } else {
                readHeaderFromChannel(headBuf, headBuf.capacity());
            }
            originalData.setHeadBytes(headBuf.array());
            if (OkSocketOptions.isDebug()) {
                SLog.i("read head: " + BytesUtils.toHexStringForLog(headBuf.array()));
            }
            int bodyLength = readerProtocol.getBodyLength(originalData.getHeadBytes(), mOkOptions.getReadByteOrder());
            if (OkSocketOptions.isDebug()) {
                SLog.i("need read body length: " + bodyLength);
            }
            if (bodyLength > 0) {
                if (bodyLength > mOkOptions.getMaxReadDataMB() * 1024 * 1024) {
                    throw new ReadException("Need to follow the transmission protocol.\r\n" +
                            "Please check the client/server code.\r\n" +
                            "According to the packet header data in the transport protocol, the package length is " + bodyLength + " Bytes.\r\n" +
                            "You need check your <ReaderProtocol> definition");
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLength);
                byteBuffer.order(mOkOptions.getReadByteOrder());
                if (mRemainingBuf != null) {
                    int bodyStartPosition = mRemainingBuf.position();
                    int length = Math.min(mRemainingBuf.remaining(), bodyLength);
                    byteBuffer.put(mRemainingBuf.array(), bodyStartPosition, length);
                    mRemainingBuf.position(bodyStartPosition + length);
                    if (length == bodyLength) {
                        if (mRemainingBuf.remaining() > 0) {//there are data left
                            ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                            temp.order(mOkOptions.getReadByteOrder());
                            temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                            mRemainingBuf = temp;
                        } else {//there are no data left
                            mRemainingBuf = null;
                        }
                        //cause this time data from remaining buffer not from channel.
                        originalData.setBodyBytes(byteBuffer.array());
                        mStateSender.sendBroadcast(IAction.ACTION_READ_COMPLETE, originalData);
                        return;
                    } else {//there are no data left in buffer and some data pieces in channel
                        mRemainingBuf = null;
                    }
                }
                readBodyFromChannel(byteBuffer);
                originalData.setBodyBytes(byteBuffer.array());
            } else if (bodyLength == 0) {
                originalData.setBodyBytes(new byte[0]);
                if (mRemainingBuf != null) {
                    //the body is empty so header remaining buf need set null
                    if (mRemainingBuf.hasRemaining()) {
                        ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                        temp.order(mOkOptions.getReadByteOrder());
                        temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                        mRemainingBuf = temp;
                    } else {
                        mRemainingBuf = null;
                    }
                }
            } else if (bodyLength < 0) {
                throw new ReadException(
                        "read body is wrong,this socket input stream is end of file read " + bodyLength + " ,that mean this socket is disconnected by server");
            }
            mStateSender.sendBroadcast(IAction.ACTION_READ_COMPLETE, originalData);
        } catch (Exception e) {
            ReadException readException = new ReadException(e);
            throw readException;
        }
    }

    /**
     * IW协议
     * @param originalData iw
     */
    private void customIWRead(OriginalData originalData) {
        int len = -1;
        String str = "";
        byte[] bufArray = new byte[mOkOptions.getReadPackageBytes()];
        ByteBuffer byteBuffer = null;
        while (true) {
            try {
                if ((len = mInputStream.read(bufArray, 0, bufArray.length)) != -1) {
                    str = new String(bufArray, 0, len);
                }
                if (len > 0) {
                    if (len > mOkOptions.getMaxReadDataMB() * 1024 * 1024) {
                        throw new ReadException("Need to follow the transmission protocol.\r\n" +
                                "Please check the client/server code.\r\n" +
                                "According to the packet header data in the transport protocol, the package length is " + len + " Bytes.");
                    }
                    SLog.i("k1 len: " + len);
                    SLog.i("k1 str.length(): " + str.length());
                    byteBuffer = ByteBuffer.allocate(len);
                    int remaining = byteBuffer.remaining();
                    if (len > remaining) {
                        byteBuffer.put(bufArray, 0, remaining);
                        mRemainingBuf = ByteBuffer.allocate(len - remaining);
                        mRemainingBuf.order(mOkOptions.getReadByteOrder());
                        mRemainingBuf.put(bufArray, remaining, len - remaining);
                    } else {
                        byteBuffer.put(bufArray, 0, len);
                    }
                    SLog.i("k1 byteBuffer.array(): " + byteBuffer.array().length);
                    originalData.setBodyBytes(byteBuffer.array());
                } else if (len == 0) {
                    originalData.setBodyBytes(new byte[0]);
                    if (mRemainingBuf != null) {
                        //the body is empty so header remaining buf need set null
                        if (mRemainingBuf.hasRemaining()) {
                            ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                            temp.order(mOkOptions.getReadByteOrder());
                            temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                            mRemainingBuf = temp;
                        } else {
                            mRemainingBuf = null;
                        }
                    }
                } else {  //len < 0
                    throw new ReadException(
                            "this socket input stream is end of file read " + len + " ,we'll disconnect");
                }
                mStateSender.sendBroadcast(IAction.ACTION_READ_COMPLETE, originalData);
            } catch (Exception e) {
                throw new ReadException(e);
            }
            if (!str.isEmpty())
                break;
        }
        if (OkSocketOptions.isDebug() && byteBuffer != null) {
            SLog.i("read total bytes: " + BytesUtils.toHexStringForLog(byteBuffer.array()));
            SLog.i("read total length:" + (byteBuffer.capacity() - byteBuffer.remaining()));
        }
    }

    private void readHeaderFromChannel(ByteBuffer headBuf, int readLength) throws IOException {
        for (int i = 0; i < readLength; i++) {
            byte[] bytes = new byte[1];
            int value = mInputStream.read(bytes);
            if (value == -1) {
                throw new ReadException(
                        "read head is wrong, this socket input stream is end of file read " + value + " ,that mean this socket is disconnected by server");
            }
            headBuf.put(bytes);
        }
    }

    private void readBodyFromChannel(ByteBuffer byteBuffer) throws IOException {
        while (byteBuffer.hasRemaining()) {
            try {
                byte[] bufArray = new byte[mOkOptions.getReadPackageBytes()];
                int len = mInputStream.read(bufArray);
                if (len == -1) {
                    break;
                }
                int remaining = byteBuffer.remaining();
                if (len > remaining) {
                    byteBuffer.put(bufArray, 0, remaining);
                    mRemainingBuf = ByteBuffer.allocate(len - remaining);
                    mRemainingBuf.order(mOkOptions.getReadByteOrder());
                    mRemainingBuf.put(bufArray, remaining, len - remaining);
                } else {
                    byteBuffer.put(bufArray, 0, len);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        if (OkSocketOptions.isDebug()) {
            SLog.i("read total bytes: " + BytesUtils.toHexStringForLog(byteBuffer.array()));
            SLog.i("read total length:" + (byteBuffer.capacity() - byteBuffer.remaining()));
        }
    }

}
