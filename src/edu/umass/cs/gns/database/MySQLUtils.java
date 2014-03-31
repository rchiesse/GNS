/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.umass.cs.gns.database;

import edu.umass.cs.gns.main.GNS;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**************** FIXME All functionality of this package is provided currently by class nsdesign/recordMap/MongoRecords.java.
 * FIXME Make changes to that file until we include this package again.. **/
/**
 *
 * @author westy
 */
public class MySQLUtils {
  
  // Utility function
  public static boolean tableExists(String name) {
    try {
      Connection c = Connect.getConnection();
      DatabaseMetaData dbm = c.getMetaData();
      ResultSet rs = dbm.getTables(null, null, name, null);
      return rs.next();
    } catch (SQLException e) {
      GNS.getLogger().severe("Error... problem executing statement : " + e);
      return false;
    }
  }
  
  public static void maybeCreateTable(String tableName, String creationString) {
    try {
      Connection conn = Connect.getConnection();
      Statement s = conn.createStatement();
      if (!tableExists(tableName)) {
        String statement = "CREATE TABLE " + tableName + " " + creationString;
        GNS.getLogger().finer("Statement:" + statement);
        s.executeUpdate(statement);
      }
    } catch (SQLException e) {
      GNS.getLogger().severe("Error... problem executing statement : " + e);
      e.printStackTrace();
    }
  }
  
   public static void dropTable(String tableName) {
    try {
      Connection conn = Connect.getConnection();
      Statement s = conn.createStatement();
      if (tableExists(tableName)) {
        String statement = "DROP TABLE " + tableName;
        GNS.getLogger().finer("Statement:" + statement);
        s.executeUpdate(statement);
      }
    } catch (SQLException e) {
      GNS.getLogger().severe("Error... problem executing statement : " + e);
      e.printStackTrace();
    }
  }

  
}
