/*
 * Created on 2005-06-03
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.flexdock.perspective.persist.proposal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.flexdock.perspective.PerspectiveManager;
import org.flexdock.perspective.persist.PerspectiveInfo;

/**
 * @author mati
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SimplePersisterGateway implements PersisterGateway {

    private String perspectiveFilePath;
    
    //TODO it would match, it is only proposal.
    private Persister persister = PerspectiveManager.getPersister();
    
    /**
     * @see org.flexdock.perspective.persist.proposal.PersisterGateway#store(java.lang.String, org.flexdock.perspective.persist.PerspectiveInfo)
     */
    public boolean store(String appKey, PerspectiveInfo perspectiveInfo) throws IOException {
        File file = getPerspectiveFile(appKey);

        FileOutputStream fos = new FileOutputStream(file);
        
        boolean result = persister.store(fos, perspectiveInfo);
        fos.close();

        return result;
    }

    /**
     * @see org.flexdock.perspective.persist.proposal.PersisterGateway#load(java.lang.String)
     */
    public PerspectiveInfo load(String appKey) throws IOException {
        File file = getPerspectiveFile(appKey);

        FileInputStream fis = new FileInputStream(file);
        
        PerspectiveInfo perspectiveInfo = persister.load(fis);

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
