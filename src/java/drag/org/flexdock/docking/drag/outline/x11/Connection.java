/*
 * Created on Aug 29, 2004
 */
package org.flexdock.docking.drag.outline.x11;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import org.flexdock.util.ResourceManager;
import org.flexdock.logging.Log;

/**
 * @author Christopher Butler
 */
public class Connection {
	public static final String LOCALHOST = "127.0.0.1";
	public static final String DISPLAY_VAR = "DISPLAY";
	private static final ConnectionInfo CONNECTION_INFO = getConnectionInfo();
	private static final boolean SERVER_AVAILABLE = isServerAvailable();
	
	private Socket socket;
	private DataInputStream dataIn;
	private OutputStream dataOut;
	
	public Connection() throws IOException {
		if(!SERVER_AVAILABLE)
			throw new RuntimeException("X Server is unavailable.  Please check your xhost access control list to ensure localhost may connect.");
			
		try {
			socket = new Socket(CONNECTION_INFO.host, 6000 + CONNECTION_INFO.display);
			dataIn = new DataInputStream(socket.getInputStream());
			dataOut = socket.getOutputStream();
		} catch(IOException e) {
			close();
			throw e;
		} 
	}
	
	public void close() {
		ResourceManager.close(dataOut);
		ResourceManager.close(dataIn);
		ResourceManager.close(socket);
	}
	
	public void sendRequest(DataBuffer req) throws IOException {
		sendRequest(req, false);
	}
	
	public DataBuffer sendRequest(DataBuffer req, boolean readReply) throws IOException {
		if(req==null)
			return null;
		
		synchronized(dataOut) {
			dataOut.write(req.getBytes());
			if(!readReply)
				return null;
			
			byte[] tmp = new byte[4096];
			int bytesRead = -1;
			synchronized(dataIn) {
				bytesRead = dataIn.read(tmp);
			}
			
			byte[] ret = new byte[bytesRead];
			System.arraycopy(tmp, 0, ret, 0, bytesRead);
			return new DataBuffer(ret);			
		}

	}
	
	private static boolean isServerAvailable() {
		try {
			Process process = Runtime.getRuntime().exec("xhost +" + CONNECTION_INFO.host);
			process.waitFor();
			DataInputStream in = new DataInputStream(process.getInputStream());
			in.readFully(new byte[in.available()]);
			in.close();
			return true;
		} catch (Exception e) {
			Log.debug(e.getMessage(), e);
			return false;
		}
	}

	
	private static class ConnectionInfo {
		private String host = LOCALHOST;
		private int display;
		private int screen;
	}
	private static ConnectionInfo getConnectionInfo() {

		String displayInfo = getEnv().getProperty(DISPLAY_VAR);
		ArrayList list = new ArrayList();
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<displayInfo.length(); i++) {
			char c = displayInfo.charAt(i);
			if(c==':' || c=='.') {
				list.add(sb.toString());
				sb = new StringBuffer();
			}
			else
				sb.append(c);
		}
		if(sb.length()>0)
			list.add(sb.toString());
		while(list.size()<3)
			list.add("");
		
		
		ConnectionInfo info = new ConnectionInfo();
		info.host = getHost(list.get(0));
		info.display = getInt(list.get(1));
		info.screen = getInt(list.get(2));
		return info;
	}
	
	private static int getInt(Object data) {
		try {
			return Integer.parseInt(data.toString());
		} catch(Exception e) {
			return 0;
		}
	}
	
	private static String getHost(Object data) {
		if(data==null || data.toString().trim().length()==0)
			return LOCALHOST;
		return data.toString().trim();
	}
	
	private static Properties getEnv() {
		try {
			Process proc = Runtime.getRuntime().exec("env");
			proc.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			
			Properties p = new Properties();
			while(br.ready()) {
				String data = br.readLine();
				int indx = data.indexOf('=');
				if(indx!=-1) {
					String key = data.substring(0, indx);
					String value = data.substring(indx+1, data.length());
					p.setProperty(key, value);
				}
			}
			return p;
		} catch(Exception e) {
			Log.debug(e.getMessage(), e);
			return new Properties();
		}
	}
}
