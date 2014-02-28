// Implement your views here using the SJS DSL.
external id:['restartModuleWithConfirmationFrontAction']

/* defined in view.xml because SJS does not know about the fields coming from the FW
form('loginViewDescriptor', parent:'loginViewDescriptorBase', model:'CaptchaUsernamePasswordHandler', columnCount:2) {
  fields {
    propertyView name:'username', width:2
    propertyView name:'password', width:2
    propertyView name:'captchaImageUrl', width:2
    propertyView name:'captchaChallenge', width:2
    propertyView name:'language', width:2
    propertyView name:'rememberMe', width:2
    propertyView name:'register', readOnly:true, action:'registerFrontAction'
    propertyView name:'help', readOnly:true, action:'helpFrontAction'
  }
}
*/

form('Registration.form', fields:['name', 'firstName']) {
  actionMap {
    actionList {
      action ref:'englishFrontAction'
      action ref:'frenchFrontAction'
    }
  }
}

action('helpFrontAction', parent:'staticInfoFrontAction', custom:[messageCode:'help'])

action('registerFrontAction', class:'org.jspresso.hrsample.ext.frontend.RegisterFrontAction', parent:'editComponentAction',
    custom:[okAction_ref:'performRegistrationFrontAction',
      cancelAction_ref:'cancelDialogFrontAction',
      viewDescriptor_ref:'Registration.form'])

action('performRegistrationFrontAction', parent:'staticInfoFrontAction', name:'doRegister',
    custom:[messageCode:'register'])

action('changeRegistrationLanguageFrontAction',
    class:'org.jspresso.hrsample.ext.frontend.ChangeRegistrationLanguageFrontAction', next:'registerFrontAction')

action('englishFrontAction', parent:'changeRegistrationLanguageFrontAction',
    custom:['targetLanguage':'en'], icon:'classpath:org/jspresso/contrib/images/i18n/en.png')
action('frenchFrontAction', parent:'changeRegistrationLanguageFrontAction',
    custom:['targetLanguage':'fr'], icon:'classpath:org/jspresso/contrib/images/i18n/fr.png')

tabs('Furniture.detail.view', actionMap:'beanModuleActionMap') {
  views {
    border {
      north {
        form (model:'Furniture')
      }
      center {
        table (model:'Furniture-previous', borderType:'TITLED') {
          actionMap {
            actionList('ALL') {
              action parent:'chooseEntityFrontAction', custom:[selectionMode:'MULTIPLE_INTERVAL_CUMULATIVE_SELECTION']
              action ref:'unlinkComponentFrontAction'
            }
          }
        }
      }
    }


    table(parent:'ITranslatable-translations.table')
  }
}

/** 
 * OVERRIDE JSPRESSO DEFAULT ACTION MAP
 */
actionMap ('beanModuleActionMap') {
  actionList('SAVE', collapsable:true){
    action ref:'saveModuleObjectFrontAction'
    action ref:'reloadModuleObjectFrontAction'
  }
  actionList('FILE') {
    action ref:'removeModuleObjectFrontAction'
    action ref:'parentModuleConnectorSelectionFrontAction'
  }
  actionList ('PERMALINK', collapsable:true) {
    action parent:'createPermalinkAndCopyToClipboardFrontAction', custom:[tinyURL:false]
    action parent:'createPermalinkAndMailToFrontAction', custom:[tinyURL:false]
  }
}

/**
 * OVERRIDE JSPRESSO DEFAULT ACTION MAP
 */
actionMap('beanCollectionModuleActionMap') {
  actionList('SAVE', collapsable:true){
    action ref:'saveModuleObjectFrontAction'
    action ref:'reloadModuleObjectFrontAction'
    action ref:'restartModuleWithConfirmationFrontAction'
  }
  actionList('FILE') {
    action ref:'queryModuleFilterAction'
  }
  actionList('PIN', renderingOptions:'ICON') {
    action ref:'pinQueryCriteriasFrontAction'
  }
  actionList('PIN2') {
    action ref:'chooseQueryCriteriasFrontAction'
  }
  
  actionList('SERVICE') {
    action ref:'addAsChildModuleFrontAction'
  }
  actionList('ADD', collapsable:true) {
    action ref:'addToMasterFrontAction'
    action ref:'cloneEntityCollectionFrontAction'
  }
  actionList('REMOVE') {
    action ref:'removeFromModuleObjectsFrontAction'
  }
  actionList('EXPORT') {
    action ref:'exportFilterModuleResultToHtmlAction'
    action ref:'importBoxAction'
  }
  actionList('TMAR', collapsable:true) {
    action ref:'exportAllToTmarRecursivelyAction'
    action ref:'exportTableToTmarAction'
  }
  actionList ('PERMALINK', collapsable:true) {
    action parent:'createPermalinkAndCopyToClipboardFrontAction', custom:[tinyURL:false]
    action parent:'createPermalinkAndMailToFrontAction', custom:[tinyURL:false]
  }
}

table('Employee.test.view') {
  
   actionMap {
    actionList('SAVE', collapsable:true) {
      action ref:'saveModuleObjectFrontAction'
      action ref:'reloadModuleObjectFrontAction'
    }
    actionList('FILE') {
      action ref:'queryModuleFilterAction'
    }
    actionList('SERVICE') {
      action parent:'openEmployeeFrontAction'
    }
    actionList('EXPORT') {
      action parent:'exportFilterModuleResultToHtmlAction',
                custom:[hideMyExportPopup:false, hideMoreColumnsPopup:false, moreColumnsOneToManyDepth:4]
      action parent:'importEmployeeBoxAction', 
                custom:[mergeFields:['name', 'firstName'], extraColumns:['zip'], additionalFields:['teams']] 
    } 
    actionList('TMAR', collapsable:true) {
      action ref:'exportAllToTmarRecursivelyAction'
      action ref:'exportTableToTmarAction'
    }
  }
   
  columns {
    propertyView name:'company', action:'openEmployeeCompanyFrontAction', readOnly:true
    propertyView name:'company.workforce', action:'openEmployeeCompanyWorkforceFrontAction', readOnly:true
    propertyView name:'name', action:'openEmployeeFrontAction', readOnly:true
    propertyView name:'firstName', readOnly:true
    propertyView name:'gender', readOnly:true
    propertyView name:'birthDate', readOnly:true
    propertyView name:'contact.address', readOnly:true
    propertyView name:'contact.city', readOnly:true
    propertyView name:'contact.phone', readOnly:true
    propertyView name:'contact.phone', readOnly:true
  }
}

action ('openEmployeeFrontAction',
  parent:'addAsChildModuleFrontAction',
  custom:[parentWorkspaceName:'employees.workspace', parentModuleName:'employees.module'])

action ('openEmployeeCompanyFrontAction', 
  parent:'addAsChildModuleFrontAction',
  custom:[parentWorkspaceName:'organization.workspace', parentModuleName:'companies.module', referencePath:'company'])

action ('openEmployeeCompanyWorkforceFrontAction',
  parent:'addAsChildModuleFrontAction',
  custom:[parentWorkspaceName:'employees.workspace', parentModuleName:'employees.module',
      referencePath:'company.employees'])

action ('importEmployeeBoxAction',
  parent:'importBoxAction',
  custom:[okAction_ref:'importEmployeeBoxOkAction'])

action ('importEmployeeBoxOkAction', 
  parent:'importBoxOkAction',
  next:'importEmployeeAction')

action ('importEmployeeAction',
  parent:'importEntitiesAction',
  custom:[fileOpenCallback_ref:'importEmployeeFactoryBean'])

bean ('importEmployeeFactoryBean',
  parent:'importEntitiesFactoryBean',
  class:'org.jspresso.hrsample.ext.frontend.ImportEmployeeFactoryBean')




