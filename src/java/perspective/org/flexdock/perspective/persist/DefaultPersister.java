/* 
 * Copyright (c) 2005 FlexDock Development Team. All rights reserved. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of 
 * this software and associated documentation files (the "Software"), to deal in the 
 * Software without restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 * to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE.
 */
package org.flexdock.perspective.persist;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Created on 2005-03-30
 * 
 * @author <a href="mailto:marius@eleritec.net">Christopher Butler</a>
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: DefaultPersister.java,v 1.3 2005-06-08 20:59:15 winnetou25 Exp $
 */
public class DefaultPersister implements Persister {

    /**
     * @see org.flexdock.perspective.persist.Persister#load(java.io.InputStream)
     */
    public PerspectiveInfo load(InputStream is) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
            return (PerspectiveInfo) ois.readObject();
        } catch (ClassNotFoundException ex) {
			IOException ex2 = new IOException("Unable to unmarshall stored data.");
			ex.initCause(ex);
			throw ex2;
        }
    }
    
    /**
     * @see org.flexdock.perspective.persist.Persister#store(java.io.OutputStream, org.flexdock.perspective.persist.PerspectiveInfo)
     */
    public boolean store(OutputStream os, PerspectiveInfo perspectiveInfo) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(perspectiveInfo);

        return true;
    }
    
    
//	public PerspectiveInfo load(InputStream is) throws IOException {
//		if(appKey==null)
//			return null;
//		
//		File inFile = getPerspectiveFile(appKey);
//		if(!inFile.exists())
//			return null;
//		
//		ObjectInputStream in = null;
//		try {
//			in = new ObjectInputStream(new FileInputStream(inFile));
//			return (PerspectiveInfo)in.readObject();
//		} catch(ClassNotFoundException e) {
//			IOException ex = new IOException("Unable to unmarshall stored data.");
//			ex.initCause(e);
//			throw ex;
//		}
//		finally {
//			if(in!=null)
//				in.close();
//		}
//	}
    
//	public boolean store(String appKey, PerspectiveInfo info) throws IOException {
//		if(appKey==null || info==null)
//			return false;
//		
//		File outFile = getPerspectiveFile(appKey);
//		if(!outFile.exists()) {
//			outFile.createNewFile();
//		}
//
//		ObjectOutputStream out = null;
//		try {
//			out = new ObjectOutputStream(new FileOutputStream(outFile));
//			out.writeObject(info);
//			return true;
//		}
//		finally {
//			if(out!=null)
//				out.close();
//		}
//	}
//	
//	protected File getPerspectiveFile(String appKey) {
//		String dirPath = getPerspectiveFilePath();
//		File dir = new File(dirPath);
//		if(!dir.exists())
//			dir.mkdirs();
//		
//		File file = new File(dir.getAbsolutePath() + "/" + getPerspectiveFilename(appKey));
//		return file;
//	}
//	
//	protected String getPerspectiveFilename(String appKey) {
//		return appKey + ".data";
//	}
//	
//	public String getPerspectiveFilePath() {
//		if(perspectiveFilePath==null)
//			perspectiveFilePath = System.getProperty("user.home") + "/flexdock/perspectives";
//		return perspectiveFilePath;
//	}
//	
//	public void setPerspectiveFilePath(String path) {
//		perspectiveFilePath = path;
//	}
}
