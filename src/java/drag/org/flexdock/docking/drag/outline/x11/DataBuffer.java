package org.flexdock.docking.drag.outline.x11;
/*
 * Created on Aug 29, 2004
 */

/**
 * @author Christopher Butler
 */
public class DataBuffer {
    public static final int DEFAULT_BUFFER_SIZE = 256;
    private byte[] dataBuffer;
    private int bufferOffset;

    public DataBuffer() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public DataBuffer(int initialBufferSize) {
        dataBuffer = new byte[initialBufferSize];
    }

    public DataBuffer(byte[] rawData) {
        dataBuffer = new byte[rawData.length];
        bufferOffset = rawData.length;
        System.arraycopy(rawData, 0, dataBuffer, 0, bufferOffset);
    }

    public void appendByte(int data) {
        ensureCapacity(1);
        dataBuffer[bufferOffset] = (byte)data;
        bufferOffset++;
    }

    public void appendTwoBytes(int data) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (data>>8 & 0xff);
        bytes[1] = (byte) (data & 0xff);
        appendBytesImpl(bytes);
    }

    public void appendFourBytes(int data) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ((data >> 24) & 0xff);
        bytes[1] = (byte) ((data >> 16) & 0xff);
        bytes[2] = (byte) ((data >> 8) & 0xff);
        bytes[3] = (byte) (data & 0xff);
        appendBytesImpl(bytes);
    }

    private void appendBytesImpl(byte[] formatted) {
        for(int i=0; i<formatted.length; i++) {
            appendByte(formatted[i]);
        }
    }

    private void ensureCapacity(int dataSize) {
        if(bufferOffset + dataSize >= dataBuffer.length) {
            byte[] tmp = new byte[dataBuffer.length*2];
            System.arraycopy(dataBuffer, 0, tmp, 0, dataBuffer.length);
            dataBuffer = tmp;
        }
    }

    public byte[] getBytes() {
        byte[] ret = new byte[bufferOffset];
        System.arraycopy(dataBuffer, 0, ret, 0, ret.length);
        return ret;
    }

    public int readByte(int offset) {
        return dataBuffer[offset] & 0xff;
    }

    public int readTwoBytes(int offset) {
        int b0 = readByte(offset) << 8;
        int b1 = readByte(offset+1);
        return b0 | b1;
    }

    public int readFourBytes(int offset) {
        int b0 = readByte(offset) << 24;
        int b1 = readByte(offset+1) << 16;
        int b2 = readByte(offset+2) << 8;
        int b3 = readByte(offset+3);
        return b0 | b1 | b2 | b3;
    }

    public byte[] getSubset(int offset, int length) {
        byte[] subset = new byte[length];
        System.arraycopy(dataBuffer, offset, subset, 0, length);
        return subset;
    }

    public DataBuffer getSubBuffer(int offset, int length) {
        byte[] subset = getSubset(offset, length);
        return new DataBuffer(subset);
    }

    public static int decode(byte[] data) {
        if(data==null || data.length==0)
            return -1;

        int shift = 8 * (data.length-1);
        int ret = 0;
        for(int i=0; i<data.length; i++) {
            int currVal =  (data[i] & 0xff) << shift;
            shift -= 8;
            ret = i==0? currVal: ret | currVal;
        }
        return ret;
    }

    public static byte[] encode(int data, int byteCount) {
        if(byteCount<0)
            return null;

        byte[] ret = new byte[byteCount];
        int shift = 8 * (byteCount-1);

        for(int i=0; i<byteCount; i++) {
            ret[i] = (byte) ((data >> shift) & 0xff);
            shift -= 8;
        }
        return ret;
    }

    public static int padLength(int len) {
        return (len + 3) & 0xfffffffc;
    }
}
