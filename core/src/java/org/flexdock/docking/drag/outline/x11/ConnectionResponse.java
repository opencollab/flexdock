/*
 * Created on Aug 29, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.docking.drag.outline.x11;

/**
 * @author marius
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ConnectionResponse {
	private DataBuffer dataBuffer;
	private DataBuffer[] screens;
	private int resourceIndex;
	
	public ConnectionResponse(DataBuffer dataBuffer) {
		this.dataBuffer = dataBuffer;
		int offset = getScreenDataOffset();
		int screenLen = getScreenLength();
		screens = new DataBuffer[getScreenCount()];
		
		for(int i=0; i<screens.length; i++) {
			screens[i] = dataBuffer.getSubBuffer(offset, screenLen);
			offset += screenLen;
		}
	}
	
	public int getScreenCount() {
		// PixMap count is 28 bytes into the buffer
		// and one byte long
		return dataBuffer.readByte(28);
	}
	
	public int getVendorLength() {
		// length of vendor is 24 bytes into the buffer
		// and two bytes long
		return dataBuffer.readTwoBytes(24);
	}
	
	public int getPixMapFormatCount() {
		// PixMap count is 29 bytes into the buffer
		// and one byte long
		return dataBuffer.readByte(29);
	}
	
	public int getPixMapOffset() {
		// first 40 bytes are static.  
		int offset = 40;
		
		// After that, there is a variable-length 'vendor' string, with some 
		// possible padding to ensure a 4-byte data unit boundary.  Return the first 
		// 40 bytes, plus the padded vendor length.
		int vendorLength = DataBuffer.padLength(getVendorLength());
		return offset += vendorLength;
	}
	
	public int getScreenDataOffset() {
		// screen data starts after the pixmap format data.  pixmap format data
		// starts at its own variable-length offset, and there are a variable
		// number of these format structures.  each pixmap format structure is 
		// 8 bytes, we start at the pixmap format offset and add 8 times the number
		// of pixmap format structures in the data buffer to arrive at the screen data offset.
		return getPixMapOffset() + 8*getPixMapFormatCount();
	}
	
	public int getScreenLength() {
		// screen buffers are 40 bytes long, followed by the number of pixmap formats
		return 40 + getPixMapFormatCount();
	}
	
	public int getResourceIdBase() {
		// resource-id-base is 12 bytes into the buffer
		// and one byte long
		return dataBuffer.readFourBytes(12);
	}
	
	public int getResourceIdMask() {
		// resource-id-mask is 16 bytes bytes into the buffer
		// and one byte long
		return dataBuffer.readFourBytes(16);		
	}
	
	public synchronized int getNextResourceId() {
		// poor man's resource ID allocation.  I think we're supposed to use subsets of 
		// resourceIdMask indices, but this works fine for the time being, since we only 
		// intend to allocate a small numer of resources (maybe only one).  Who knows.  
		// This will probably break something down the road and we'll be forced to fix it.
		int id = resourceIndex++ | getResourceIdBase();
		return id; 
	}
	
	public int getRootWindowID(int screenIndex) {
		DataBuffer screen = screens[screenIndex];
		return screen.readFourBytes(0);
	}
	
	public int getWhitePixel(int screenIndex) {
		// white pixel is 8 bytes into the buffer
		// and four bytes long
		return screens[screenIndex].readFourBytes(8);		
	}
	
	public int getBlackPixel(int screenIndex) {
		// black pixel is 12 bytes into the buffer
		// and four bytes long
		return screens[screenIndex].readFourBytes(12);
	}
}
