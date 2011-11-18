/*
 * Created on Feb 27, 2005
 */
package org.flexdock.plaf.mappings;

import javax.swing.UIManager;

import com.l2fprod.gui.plaf.skin.Skin;
import com.l2fprod.gui.plaf.skin.SkinLookAndFeel;

/**
 * @author Christopher Butler
 */
public class SkinLFResolver extends RefResolver {

    public String getRef(String plaf) {
        Skin skin = SkinLookAndFeel.getSkin();
        String skinName = skin==null? null: skin.getClass().getName();

        // redirect to the mapping for the skin, instead of the plaf itself
        String view = PlafMappingFactory.getPlafReference(skinName);
        return view==null? getDefaultRef(): view;
    }

    public String getDefaultRef() {
        String systemPlaf = UIManager.getSystemLookAndFeelClassName();
        return PlafMappingFactory.getPlafReference(systemPlaf);
    }

}
