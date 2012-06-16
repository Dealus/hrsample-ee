package org.jspresso.hrsample.ext.model.usage.extension;

import java.util.ArrayList;
import java.util.List;

import org.jspresso.framework.model.component.AbstractComponentExtension;
import org.jspresso.hrsample.ext.model.usage.MUModule;
import org.jspresso.hrsample.ext.model.usage.MUModuleInterface;

/**
 * Generated by Jspresso Developer Studio
 */
public class MUModuleInterfaceExtension extends AbstractComponentExtension<MUModuleInterface> {

	public MUModuleInterfaceExtension(MUModuleInterface component) {
		super(component);

		//registerNotificationForwarding(component, MUModuleInterface.FIELD, MUModuleInterface.COMPUTED_FIELD);
	}

  /**
   * generated by Jspresso Developer Studio
   * 
   * @return
   */
  public List<MUModule> getAllModules() {
    ArrayList<MUModule> allModules = new ArrayList<MUModule>();
    for (MUModule m : getComponent().getModules()) {
      fillModules(m, allModules);
    }
    return allModules;
  }
  private void fillModules(MUModule masterModule, ArrayList<MUModule> allModules) {
    allModules.add(masterModule);
    for (MUModule m : masterModule.getModules()) {
      fillModules(m, allModules);
    }
  }
  
}