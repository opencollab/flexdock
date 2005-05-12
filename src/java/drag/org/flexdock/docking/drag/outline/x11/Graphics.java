/*
 * Created on Aug 30, 2004
 */
package org.flexdock.docking.drag.outline.x11;

import java.awt.Rectangle;
import java.io.IOException;

import org.flexdock.docking.drag.effects.RubberBand;
import org.flexdock.util.Utilities;

/**
 * @author Christopher Butler
 */
public class Graphics {
	public static final char MOST_SIGNIFICANT_BIT_FIRST = 'B';
	public static final int MAJOR_VERSION = 11;
	public static final int MINOR_VERSION = 0;
	public static final int DEFAULT_AUTH_NAME_LENGTH = 0;
	public static final int DEFAULT_AUTH_DATA_LENGTH = 0;
	
	public static final int CREATE_GC_OPCODE = 55;
	public static final int CHANGE_GC_OPCODE = 56;
	public static final int POLY_RECT_OPCODE = 67;
	
	public static final int BITMASK_FUNCTION = 0x00000001;
	public static final int BITMASK_FOREGROUND = 0x00000004;
	public static final int BITMASK_BACKGROUND = 0x00000008;
	public static final int BITMASK_SUBWIN_MODE = 0x00008000;
	public static final int XOR_MODE = 6;
	public static final int SUBWIN_MODE_CLIP_BY_CHILDREN = 0;
	public static final int SUBWIN_MODE_INCLUDE_INFERIORS = 1;
	
	private static final Graphics SINGLETON = create();
	
	private Connection connection;
	private int rootWindowId;
	private int rootGraphicsId;
	private int blackPixel;
	private int whitePixel;
	
	
	
	
	private static Graphics create() {
		setupShutdownHooks();

		try {
			Connection conn = new Connection();
			DataBuffer connectionBuffer = getConnectionRequest();
			DataBuffer replyBuffer = conn.sendRequest(connectionBuffer, true);
			ConnectionResponse reply = new ConnectionResponse(replyBuffer);
			return new Graphics(conn, reply);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Graphics getGraphics() {
		return SINGLETON;
	}
	
	
	private Graphics(Connection conn, ConnectionResponse serverInfo) {
		// set the connection
		connection = conn;
		
		// cache the relevant server information
		rootWindowId = serverInfo.getRootWindowID(0);
		blackPixel = serverInfo.getBlackPixel(0);
		whitePixel = serverInfo.getWhitePixel(0);
		rootGraphicsId = serverInfo.getNextResourceId();
		
		// initialize the default graphics context
		initGraphicsContext();
	}
	
	private void initGraphicsContext() {
		DataBuffer buffer = new DataBuffer();
		
		// write the opcode
		buffer.appendByte(CREATE_GC_OPCODE);
		buffer.appendByte(0); // padding
	
		// default length will be 6.  4 units for the header, and two 
		// value units for the white background and black foreground
		buffer.appendTwoBytes(6);
		
		// write the GC id
		buffer.appendFourBytes(rootGraphicsId);
		
		// write the window id
		buffer.appendFourBytes(rootWindowId);
		
		// write the bitmask for FOREGROUND OR BACKGROUND
		buffer.appendFourBytes(BITMASK_FOREGROUND | BITMASK_BACKGROUND);
		
		// write the foreground
		buffer.appendFourBytes(blackPixel);
		
		// write the background
		buffer.appendFourBytes(whitePixel);
		
		sendRequest(buffer);
	}
	
	public void setForeground(int color) {
		changeGraphicsAttrib(BITMASK_FOREGROUND, color);
	}
	
	public void setSubWindowMode(int winMode) {
		if(winMode!=SUBWIN_MODE_CLIP_BY_CHILDREN)
			winMode = SUBWIN_MODE_INCLUDE_INFERIORS;
		changeGraphicsAttrib(BITMASK_SUBWIN_MODE, winMode);
	}
	
	public void setXor() {
		changeGraphicsAttrib(BITMASK_FUNCTION, XOR_MODE);
	}
	
	private void changeGraphicsAttrib(int bitmask, int value) {
		DataBuffer buffer = new DataBuffer();
		
		// write the opcode
		buffer.appendByte(CHANGE_GC_OPCODE);
		buffer.appendByte(0); // padding
	
		// default length will be 4.  3 units for the header, and one 
		// value unit.
		buffer.appendTwoBytes(4);
		
		// write the GC id
		buffer.appendFourBytes(rootGraphicsId);
		
		// write the bitmask
		buffer.appendFourBytes(bitmask);
		
		// write the value
		buffer.appendFourBytes(value);
		
		sendRequest(buffer);
	}
	
	private static DataBuffer getConnectionRequest() {
		DataBuffer buffer = new DataBuffer();
		
		// send byte-order, plus 1 empty byte for padding 
		buffer.appendByte(MOST_SIGNIFICANT_BIT_FIRST);
		buffer.appendByte(0);
		
		// send major and minor protocol versions
		buffer.appendTwoBytes(MAJOR_VERSION);
		buffer.appendTwoBytes(MINOR_VERSION);
		
		// send auth protocol lengths, plus two extra bytes for padding
		buffer.appendTwoBytes(DEFAULT_AUTH_NAME_LENGTH);
		buffer.appendTwoBytes(DEFAULT_AUTH_DATA_LENGTH);
		buffer.appendTwoBytes(0);
		
		// normally, here is where we would start appending the
		// authentication protocl name and actual auth data.  But since
		// we have already hardcoded there to be no data present, we
		// won't append any data to the buffer
		
		// now, just return the byte array
		return buffer;
	}
	
	public void dragRectange(int x, int y, int width, int height) {
		drawRectangle(new Rectangle(x, y, width, height));
	}
	
	public void drawRectangle(Rectangle r) {
		if(r==null)
			return;
		
		DataBuffer buffer = new DataBuffer();
		
		// write the opcode
		buffer.appendByte(POLY_RECT_OPCODE);
		buffer.appendByte(0); // padding
	
		// default length will be 5.  3 units for the header, and two units 
		// for the vertices on the rectangle (4 points, 2 bytes apiece).
		buffer.appendTwoBytes(5);
		
		// write the window id
		buffer.appendFourBytes(rootWindowId);
		
		// write the GC id
		buffer.appendFourBytes(rootGraphicsId);
		
		// write the rectangle vertices
		buffer.appendTwoBytes(r.x);
		buffer.appendTwoBytes(r.y);
		buffer.appendTwoBytes(r.width);
		buffer.appendTwoBytes(r.height);
		
		sendRequest(buffer);
	}
	
	
	
	public int getWhitePixel() {
		return whitePixel;
	}
	
	public int getBlackPixel() {
		return blackPixel;
	}
	
	private void sendRequest(DataBuffer buffer) {
		try {
			connection.sendRequest(buffer);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void cleanup() {
		if(SINGLETON!=null && SINGLETON.connection!=null) {
			try {
				SINGLETON.connection.close();
			} catch(Exception ignored) {
			}
		}
	}
	
	public void finalize() {
		cleanup();
	}
	
	private static void setupShutdownHooks() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					cleanup();
				} catch(Throwable t) {
					if(Utilities.sysTrue(RubberBand.DEBUG_OUTPUT))
						t.printStackTrace();
				}
			}
		});
	}


}
