// Implement your application frontend here using the SJS DSL.

/*
 * The workspaces and modules
 */

// Describe your workspaces and modules here.

bean 'viewFactoryBase', parent:'abstractViewFactory',
    custom: [defaultActionMapRenderingOptions:'LABEL_ICON', propertyViewModifiers_ref:['properyViewActionAddModifierBean']]
    
workspace('statistics.workspace', icon:'tools.png') {
   
  pivotModule ('employee.statistics.module', 
    dimensions:['salary(40, 80, 100)',
              'age(30, 40)',
              'gender', 
              'managedOu.ouId',
              //'managedOu.manager.name',
              //'company.name',
              'company.departments'],
    measures:['ssn@count',
              'salary@sum', 
              'salary@percentile90', 
              'salary@average/managedOu.ouId'],
    refiner:'employeePivotRefiner',
    component:'Employee')  
  
}
    
workspace('administration.workspace', icon:'classpath:org/jspresso/framework/application/images/execute-48x48.png') {
  
  module ('userqueries.admin.module',
    parent:'userqueries.admin.module')
  
  module ('pivot.admin.module',
    parent:'pivot.admin.module')
  
}

workspace('tools.workspace', icon:'tools.png') { 
  
  filterModule ('furniture.module', 
    startup:'furnitureModuleInitFrontAction',
    detailView:'Furniture.detail.view',
    component:'Furniture') {
  }
    
  filterModule ('Employee.test.module',
    component:'Employee',
    moduleView:'Employee.test.view')  
}
 
controller 'hrsample-ext.name',
    icon:'icon.png',
    context:'hrsample-ext',
    language:'en',
    startup:'startupHrsampleExtAction',
    onModuleEnter:'manageAnyModuleEnterFrontAction',
    actionMap:'controllerActionMap',
    workspaces:[
      'statistics.workspace',
      'organization.workspace',
      'employees.workspace',
      'masterdata.workspace', 
      'tools.workspace',
      'usage.workspace',
      'administration.workspace']
    
action ('furnitureModuleInitFrontAction',
  class:'org.jspresso.framework.application.frontend.action.FrontendAction') {
  
  wrapped ref:'initModuleFilterAction'
  next class:'org.jspresso.hrsample.ext.frontend.FurnitureModuleInitFrontAction'
}    
    
action ('startupHrsampleExtAction', 
    parent:'permalinkSelectionAction',
    custom:[defaultAction_ref:'startupHrsampleAction']
)    

action ('manageAnyModuleEnterFrontAction',
  class:'org.jspresso.framework.application.frontend.action.FrontendAction',
  wrapped:'anyModuleEnterFrontAction', 
  next:'loadPinnedQueryCriteriasFrontAction')   

spec('remote') {
  bean ('remotePeerRegistryBase', class:'org.jspresso.framework.util.remote.registry.BasicRemotePeerRegistry', custom:[automationEnabled:true])
  bean ('remoteFrontController', class:'org.jspresso.hrsample.ext.frontend.remote.CustomRemoteController', parent:'abstractFrontController')
  bean ('remoteViewFactory', class:'org.jspresso.framework.ext.view.remote.EnhancedRemoteViewFactory', parent:'viewFactoryBase')
}

spec('swing') {
  bean ('swingFrontController', class:'org.jspresso.hrsample.ext.frontend.swing.CustomSwingController', parent:'abstractFrontController')
}


spec('remote-recording') {
  bean('remoteFrontController',
      class:'org.jspresso.framework.ext.application.testing.RecordingRemoteController',
      parent:'abstractFrontController', 
      custom:[captureDirectory:'/tmp/commands'])
}
