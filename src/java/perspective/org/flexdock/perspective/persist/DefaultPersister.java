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

import org.flexdock.util.ResourceManager;

/**
 * @author Christopher Butler
 */
public class DefaultPersister implements Persister {

	public PerspectiveInfo load(String appKey) {
		if(appKey==null)
			return null;
		
		File inFile = getPerspectiveFile(appKey);
		if(!inFile.exists())
			return null;
		
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(inFile));
			return (PerspectiveInfo)in.readObject();
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			ResourceManager.close(in);
		}
	}
	
	public boolean store(String appKey, PerspectiveInfo info) {
		if(appKey==null || info==null)
			return false;
		
		File outFile = getPerspectiveFile(appKey);
		if(!outFile.exists()) {
			try {
				outFile.createNewFile();
			} catch(IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(outFile));
			out.writeObject(info);
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			ResourceManager.close(out);
		}
	}
	
	protected File getPerspectiveFile(String appKey) {
		String dirPath = System.getProperty("user.home") + "/flexdock/perspectives";
		File dir = new File(dirPath);
		if(!dir.exists())
			dir.mkdirs();
		
		File file = new File(dir.getAbsolutePath() + "/" + getPerspectiveFilename(appKey));
		return file;
	}
	
	protected String getPerspectiveFilename(String appKey) {
		return appKey + ".data";
	}
}
