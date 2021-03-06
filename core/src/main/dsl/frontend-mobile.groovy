controller 'hrsample-ext.name',
    icon:'icon.png',
    context:'hrsample-ext',
    language:'en',
//  startup:'startupHrsampleExtAction',
    onModuleEnter:'manageAnyModuleEnterFrontAction',
    actionMap:'controllerActionMap',
    workspaces:[
      'organization.workspace',
      'employees.workspace',
      'masterdata.workspace',
//    'tools.workspace',
//    'usage.workspace'
    ]

bean ('remoteFrontController', class:'org.jspresso.hrsample.ext.frontend.remote.mobile.CustomRemoteController',
    parent:'abstractMobileFrontController')

bean ('remoteViewFactory', class:'org.jspresso.framework.ext.view.remote.mobile.MobileEnhancedRemoteViewFactory',
    parent:'mobileViewFactoryBase')
