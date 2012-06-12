package org.jspresso.hrsample.ext.model.usage.service;
 
import org.jspresso.framework.model.component.service.EmptyLifecycleInterceptor;
import org.jspresso.framework.model.entity.IEntityFactory;
import org.jspresso.framework.model.entity.IEntityLifecycleHandler;
import org.jspresso.framework.security.UserPrincipal;
import org.jspresso.hrsample.ext.model.usage.MUItem;
import org.jspresso.hrsample.ext.model.usage.MUModule;
import org.jspresso.hrsample.ext.model.usage.MUStat;
import org.jspresso.hrsample.ext.model.usage.MUWorkspace;

/**
 * Generated by Jspresso Developer Studio
 **/
public class MUStatInterceptor extends EmptyLifecycleInterceptor<MUStat> {

  @Override
  public boolean onCreate(MUStat stat, IEntityFactory entityFactory,
      UserPrincipal principal, IEntityLifecycleHandler entityLifecycleHandler) {
    
    stat.setPeriod(MUStat.PERIOD_WEEK);
    
    // users count
    stat.setUsersCount(69);
    stat.addToUsersPerModule(getItem("Météos", 29, entityFactory));
    stat.addToUsersPerModule(getItem("Coproj", 13, entityFactory));
    stat.addToUsersPerModule(getItem("Copil", 17, entityFactory));
    
    // users count
    stat.setAccessCount(358);
    stat.addToAccessPerModule(getItem("Météos", 63, entityFactory));
    stat.addToAccessPerModule(getItem("Coproj", 236, entityFactory));
    stat.addToAccessPerModule(getItem("Copil", 59, entityFactory));
    
    // history
    stat.setHistoryModule(getModule("Météos", entityFactory));
    stat.addToHistoryDetails(getItem("Lun", 13, entityFactory));
    stat.addToHistoryDetails(getItem("Mar", 19, entityFactory));
    stat.addToHistoryDetails(getItem("Mer", 8, entityFactory));
    stat.addToHistoryDetails(getItem("Jeu", 23, entityFactory));
    stat.addToHistoryDetails(getItem("Ven", 35, entityFactory));
    stat.addToHistoryDetails(getItem("Sam", 2, entityFactory));
    stat.addToHistoryDetails(getItem("Dim", 1, entityFactory));
    
    // add all workspaces
    stat.addToAllWorkspaces(getWorkspace("Projets", entityFactory));
    stat.addToAllWorkspaces(getWorkspace("Achats", entityFactory));
    stat.addToAllWorkspaces(getWorkspace("Produits", entityFactory));
    stat.addToAllWorkspaces(getWorkspace("Admin", entityFactory));
    
    return super.onCreate(stat, entityFactory, principal, entityLifecycleHandler);
  }
  
  public MUItem getItem(String label, int count, IEntityFactory entityFactory) {
    MUItem item = entityFactory.createComponentInstance(MUItem.class);
    item.setCount(count);
    item.setLabel(label);
    return item;
  }
  
  public MUModule getModule(String label, IEntityFactory entityFactory) {
    MUModule module = entityFactory.createComponentInstance(MUModule.class);
    module.setLabel(label);
    return module;
  }
  
  public MUWorkspace getWorkspace(String label, IEntityFactory entityFactory) {
    MUWorkspace workspace = entityFactory.createComponentInstance(MUWorkspace.class);
    workspace.setLabel(label);
    return workspace;
  }
}
