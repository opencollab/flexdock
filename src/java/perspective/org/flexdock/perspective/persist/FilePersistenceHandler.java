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

import org.flexdock.test.xml.XMLDebugger;

/**
 * Created on 2005-06-03
 * 
 * @author <a href="mailto:mati@sz.home.pl">Mateusz Szczap</a>
 * @version $Id: FilePersistenceHandler.java,v 1.4 2005-07-03 19:18:31 winnetou25 Exp $
 */
public class FilePersistenceHandler implements PersistenceHandler {
	public static final File DEFAULT_PERSPECTIVE_DIR = new File(System.getProperty("user.home") + "/flexdock/perspectives");
	
	protected File perspectiveFile;
    protected Persister m_persister = null;
    
	public FilePersistenceHandler(String absolutePath) {
		this(new File(absolutePath), null);
	}
	
	public FilePersistenceHandler(File file) {
		this(file, null);
	}
	
	public FilePersistenceHandler(String absolutePath, Persister persister) {
		this(new File(absolutePath), persister);
	}
	
	public FilePersistenceHandler(File file, Persister persister) {
		perspectiveFile = file;
		if(persister==null)
			persister = createDefaultPersister();
		m_persister = persister;
	}
	
	public static FilePersistenceHandler createDefault(String fileName) {
		String path = DEFAULT_PERSPECTIVE_DIR.getAbsolutePath() + "/" + fileName;
		return new FilePersistenceHandler(path);
	}
    
    /**
     * @throws PersisterException 
     * @see org.flexdock.perspective.persist.PersistenceHandler#store(java.lang.String, org.flexdock.perspective.persist.PerspectiveInfo)
     */
    public boolean store(PerspectiveModel perspectiveInfo) throws IOException, PersisterException {
        File file = getPerspectiveFile();
        validatePerspectiveFile();
        
        XMLDebugger.println(perspectiveInfo);

        FileOutputStream fos = new FileOutputStream(file);
        try {
            return m_persister.store(fos, perspectiveInfo);
        } finally {
            fos.close();
        }
    }

    /**
     * @throws PersisterException 
     * @see org.flexdock.perspective.persist.PersistenceHandler#load(java.lang.String)
     */
    public PerspectiveModel load() throws IOException, PersisterException {
        File file = getPerspectiveFile();
        if(file==null || !file.exists())
        	return null;

        FileInputStream fis = new FileInputStream(file);

        try {
            PerspectiveModel perspectiveModel = m_persister.load(fis);
            return perspectiveModel;
        } finally {
            fis.close();
        }
    }
    
	protected void validatePerspectiveFile() throws IOException {
		File file = getPerspectiveFile();
		File dir = file.getParentFile();
		if(!dir.exists())
			dir.mkdirs();
		
		if(!file.exists())
			file.createNewFile();
	}
	

	public File getPerspectiveFile() {
		return perspectiveFile;
	}
	
	public void setPerspectiveFile(File file) {
		perspectiveFile = file;
	}
	
	public void setPerspectiveFile(String absolutePath) {
		perspectiveFile = new File(absolutePath);
	}
	
	public Persister createDefaultPersister() {
		return new DefaultFilePersister();
	}

}
