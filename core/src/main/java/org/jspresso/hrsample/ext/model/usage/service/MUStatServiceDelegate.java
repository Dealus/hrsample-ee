package org.jspresso.hrsample.ext.model.usage.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.jspresso.framework.model.component.service.IComponentService;
import org.jspresso.framework.model.entity.IEntityFactory;
import org.jspresso.hrsample.ext.model.usage.MUItem;
import org.jspresso.hrsample.ext.model.usage.MUModule;
import org.jspresso.hrsample.ext.model.usage.MUStat;
import org.jspresso.hrsample.ext.model.usage.MUWorkspace;
import org.jspresso.hrsample.ext.model.usage.service.MUEvent.EMUEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

/**
 * Generated by Jspresso Developer Studio
 */
public class MUStatServiceDelegate implements IComponentService {

  private JdbcTemplate jdbcTemplate;
  private IEntityFactory entityFactory;
  
  private static final String SQL_COUNT_BASE = 
      "SELECT COUNT(DISTINCT ACCESS_BY), COUNT(*) \n" + 
      "  FROM MODULE_USAGE mu \n" + 
      " WHERE ACCESS_DATE > ? \n" +
      " --AND--";
  
  private static final String SQL_COUNT_USERS_PER_MODULES = 
      "SELECT MODULE_ID, COUNT(DISTINCT ACCESS_BY) \n" + 
      "FROM MODULE_USAGE \n" + 
      "WHERE ACCESS_DATE > ? \n" + 
      " --AND-- \n" +
      "GROUP BY MODULE_ID \n " +
      "ORDER BY MODULE_ID";
  
  private static final String SQL_COUNT_ACCESS_PER_MODULES = 
      "SELECT MODULE_ID, COUNT(*) \n" + 
      "  FROM MODULE_USAGE \n" + 
      " WHERE ACCESS_DATE > ? \n" + 
      " --AND-- \n" +
      " GROUP BY MODULE_ID \n" +
      " ORDER BY MODULE_ID";  
  
  private static final String SQL_COUNT_ACCESS_PER_PERIOD_FOR_MODULE = 
      "SELECT period, COUNT(*) \n" + 
      "FROM MODULE_USAGE mu,  \n" + 
      "   (select DISTINCT (--PERIOD--) AS period, \n" + 
      "      m.ID \n" + 
      "      from MODULE_USAGE m \n" + 
      "     where m.ACCESS_DATE > ?) mu2  \n" + 
      "WHERE mu.ID = mu2.ID \n" + 
      "  AND mu.MODULE_ID = ? \n" +
      "GROUP BY period  \n" + 
      "ORDER BY period DESC;";
  
  /**
   * Configures the datasource .
   * @param dataSource
   */
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }
  
  /**
   * set the entityfactory to use
   * @param entityFactory
   */
  public void setEntityFactory(IEntityFactory entityFactory) {
    this.entityFactory = entityFactory;
  }
  
  /**
   * refresh data using datasource
   */
  public void refresh(final MUStat muStat, EMUEvent event) {
    
    Logger.getLogger(getClass()).debug("Refreshing stats");
    
    // global counters
    {
      Object[] restrictionsValues = new Object[] {getStartDate(muStat)};
      int[] restrictionsTypes = new int[] {Types.DATE};
      String query = SQL_COUNT_BASE.replaceAll("--AND--", getWorkspaceRestriction(muStat.getWorkspace()));
      jdbcTemplate.query(query, restrictionsValues, restrictionsTypes,
          new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
              muStat.setUsersCount(rs.getInt(1));
              muStat.setAccessCount(rs.getInt(2));
            }
          });
    }
    
    // users per modules
    {
      Object[] restrictionsValues = new Object[] {getStartDate(muStat)};
      int[] restrictionsTypes = new int[] {Types.DATE};
      final ArrayList<MUItem> items = new ArrayList<MUItem>();
      String query = SQL_COUNT_USERS_PER_MODULES.replaceAll("--AND--", getWorkspaceRestriction(muStat.getWorkspace()));
      jdbcTemplate.query(query, restrictionsValues, restrictionsTypes,
          new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
              items.add(getItemForModule(muStat, rs.getString(1), rs.getInt(2)));;
            }
      });
      muStat.setUsersPerModule(items);
    }
    
    // access per module
    {
      Object[] restrictionsValues = new Object[] {getStartDate(muStat)};
      int[] restrictionsTypes = new int[] {Types.DATE};
      final ArrayList<MUItem> items = new ArrayList<MUItem>();
      String query = SQL_COUNT_ACCESS_PER_MODULES.replaceAll("--AND--", getWorkspaceRestriction(muStat.getWorkspace()));
      jdbcTemplate.query(query, restrictionsValues, restrictionsTypes,
          new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
              items.add(getItemForModule(muStat, rs.getString(1), rs.getInt(2)));;
            }
      });
      muStat.setAccessPerModule(items);
    }
    
    // access per period for modules
    // Optimization : do not refresh if event is WORKSPACE kind, because a second event MODULE_HISTORY will be raised
    if (event != EMUEvent.WORKSPACE) {
        
      final ArrayList<MUItem> items = new ArrayList<MUItem>();
      if (muStat.getHistoryModule() !=null  && muStat.getHistoryModule().getModuleId()!=null) {
        Object[] restrictionsValues = new Object[] {getStartDate(muStat), muStat.getHistoryModule().getModuleId()};
        int[] restrictionsTypes = new int[] {Types.DATE, Types.CHAR};
        String query = SQL_COUNT_ACCESS_PER_PERIOD_FOR_MODULE.replaceAll("--PERIOD--", getSQLForPeriod(muStat));
        jdbcTemplate.query(query, restrictionsValues, restrictionsTypes,
            new RowCallbackHandler() {
              @Override
              public void processRow(ResultSet rs) throws SQLException {
                rs.getObject(1);
                items.add(getItem(formatDateForPeriod(muStat, rs.getString(1)), rs.getInt(2)));
              }
        });
      }
      muStat.setHistoryDetails(items);
    }
  }

  protected MUItem getItem(String label, int count) {
    MUItem item = entityFactory.createComponentInstance(MUItem.class);
    item.setCount(count);
    item.setLabel(label);
    return item;
  }
  
  protected MUItem getItemForModule(MUStat muStat, String moduleId, int count) {
    MUModule module = muStat.getModule(moduleId);
    String label = module!=null ? module.getLabel() : moduleId;
    
    MUItem item = entityFactory.createComponentInstance(MUItem.class);
    item.setCount(count);
    item.setLabel(label);
    item.setItemId(moduleId);
    
    return item;
  }

  private java.sql.Date getStartDate(MUStat muStat) {
    int delta = 0;
    if (MUStat.PERIOD_DAY.equals(muStat.getPeriod())) {
      delta = -1;
    }
    else if (MUStat.PERIOD_WEEK.equals(muStat.getPeriod())) {
      delta = -7;
    }
    else if (MUStat.PERIOD_MONTH.equals(muStat.getPeriod())) {
      delta = -30;
    }
    else {
      delta = -365;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.DAY_OF_YEAR, delta);

    return new java.sql.Date(cal.getTimeInMillis());
  }
  
  private String getSQLForPeriod(MUStat muStat) {
    if (MUStat.PERIOD_DAY.equals(muStat.getPeriod())) {
      return "CONCAT(YEAR(m.ACCESS_DATE),'/',MONTH(m.ACCESS_DATE),'/',DAY(m.ACCESS_DATE),'-',HOUR(m.ACCESS_DATE))";
    }
    else if (MUStat.PERIOD_WEEK.equals(muStat.getPeriod())) {
      return "CONCAT(YEAR(m.ACCESS_DATE),'/',MONTH(m.ACCESS_DATE),'/',DAY(m.ACCESS_DATE),'-',HOUR(m.ACCESS_DATE))";
    }
    else if (MUStat.PERIOD_MONTH.equals(muStat.getPeriod())) {
      return "CONCAT(YEAR(m.ACCESS_DATE),'/',MONTH(m.ACCESS_DATE),'/',DAY(m.ACCESS_DATE))";
    }
    else {
      return "CONCAT(YEAR(m.ACCESS_DATE),'/',MONTH(m.ACCESS_DATE),'/',DAY(m.ACCESS_DATE))";
    }
  }

  protected String formatDateForPeriod(MUStat muStat, String period) {
    if (MUStat.PERIOD_DAY.equals(muStat.getPeriod())) {
      return period.substring(period.lastIndexOf('-') +1) + ":00";
    }
    else if (MUStat.PERIOD_WEEK.equals(muStat.getPeriod())) {
      return period.substring(0, period.lastIndexOf('-'));
    }
    else if (MUStat.PERIOD_MONTH.equals(muStat.getPeriod())) {
      return period;
    }
    else {
      return period.substring(0, period.lastIndexOf('/'));
    }
  }
  
  /**
   * get module from module id
   * @return module
   */
  public MUModule getModule(MUStat muStat, String moduleId) {
    for (MUModule m : muStat.getAllModules()) {
      if (moduleId.equals(m.getModuleId())) {
        return m;
      }
    }
    return null;
  }
  
  /**
   * get workspace restriction
   * @param workspace
   * @return
   */
  private String getWorkspaceRestriction(MUWorkspace workspace) {
    if (workspace==null || workspace.getLabel()==null) { //WORKAROUND because component is never null !!
      return "";
    }
    else if (workspace.getModules() == null || workspace.getModules().isEmpty()) {
      return " AND MODULE_ID IS NULL "; // This seams to be studpid but this is a rare case... for which query should return an empty result 
    }
    else {
      StringBuffer sb = new StringBuffer();
      sb.append(" AND MODULE_ID IN (");
      Iterator<MUModule> iter = workspace.getAllModules().iterator();
      while (iter.hasNext()) {
        MUModule m = iter.next();
        sb.append('\'').append(m.getModuleId()).append('\'');
        if (iter.hasNext()) {
          sb.append(", ");
        }
      }
      sb.append(") ");
      return sb.toString();
    }
  }


}