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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: SimplePersisterGateway.java,v 1.1 2005-06-08 20:59:15 winnetou25 Exp $
 */
public class SimplePersisterGateway implements PersisterGateway {

    private String perspectiveFilePath;
    
    private Persister m_persister = null;
    
    public SimplePersisterGateway(Persister persister) {
        if (persister == null) throw new IllegalArgumentException("persister cannot be null");
        m_persister = persister;
    }
    
    /**
     * @see org.flexdock.perspective.persist.PersisterGateway#store(java.lang.String, org.flexdock.perspective.persist.PerspectiveInfo)
     */
    public boolean store(String appKey, PerspectiveInfo perspectiveInfo) throws IOException {
        File file = getPerspectiveFile(appKey);

        FileOutputStream fos = new FileOutputStream(file);
        
        boolean result = m_persister.store(fos, perspectiveInfo);
        fos.close();

        return result;
    }

    /**
     * @see org.flexdock.perspective.persist.PersisterGateway#load(java.lang.String)
     */
    public PerspectiveInfo load(String appKey) throws IOException {
        File file = getPerspectiveFile(appKey);

        FileInputStream fis = new FileInputStream(file);
        
        PerspectiveInfo perspectiveInfo = m_persister.load(fis);

        fis.close();
        
        return perspectiveInfo;
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
