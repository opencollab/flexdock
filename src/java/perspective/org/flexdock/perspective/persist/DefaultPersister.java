/*
 * Created on May 30, 2005
 */
package org.flexdock.perspective.persist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Christopher Butler
 */
public class DefaultPersister implements Persister {
	protected String perspectiveFilePath;
	
	public PerspectiveInfo load(String appKey) throws IOException {
		if(appKey==null)
			return null;
		
		File inFile = getPerspectiveFile(appKey);
		if(!inFile.exists())
			return null;
		
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(inFile));
			return (PerspectiveInfo)in.readObject();
		} catch(ClassNotFoundException e) {
			IOException ex = new IOException("Unable to unmarshall stored data.");
			ex.initCause(e);
			throw ex;
		}
		finally {
			if(in!=null)
				in.close();
		}
	}
	
	public boolean store(String appKey, PerspectiveInfo info) throws IOException {
		if(appKey==null || info==null)
			return false;
		
		File outFile = getPerspectiveFile(appKey);
		if(!outFile.exists()) {
			outFile.createNewFile();
		}

		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(outFile));
			out.writeObject(info);
			return true;
		}
		finally {
			if(out!=null)
				out.close();
		}
	}
	
	protected File getPerspectiveFile(String appKey) {
		String dirPath = getPerspectiveFilePath();
		File dir = new File(dirPath);
		if(!dir.exists())
			dir.mkdirs();
		
		File file = new File(dir.getAbsolutePath() + "/" + getPerspectiveFilename(appKey));
		return file;
	}
	
	protected String getPerspectiveFilename(String appKey) {
		return appKey + ".data";
	}
	
	public String getPerspectiveFilePath() {
		if(perspectiveFilePath==null)
			perspectiveFilePath = System.getProperty("user.home") + "/flexdock/perspectives";
		return perspectiveFilePath;
	}
	
	public void setPerspectiveFilePath(String path) {
		perspectiveFilePath = path;
	}
}
