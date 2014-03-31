package edu.umass.cs.gns.main;

import edu.umass.cs.gns.util.Logging;
import java.io.IOException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

public class GNS {

  public static final int DEFAULT_TTL_SECONDS = 0;
  public static final int STARTINGPORT = 24400;
  public static final String GNS_URL_PATH = "GNS";
  

  // This is designed so we can run LNS and NS on the same host if needed
  public enum PortType {

    NS_TCP_PORT(0), // TCP port at name servers
    LNS_TCP_PORT(1), // TCP port at local name servers
    NS_UDP_PORT(2), // UDP port at local name servers
    LNS_UDP_PORT(3), // UDP port at local name servers
    NS_ADMIN_PORT(4),
    LNS_ADMIN_PORT(5),
    ADMIN_PORT(-1), // selects either NS_ADMIN_PORT or LNS_ADMIN_PORT
    LNS_ADMIN_RESPONSE_PORT(6),
    LNS_ADMIN_DUMP_RESPONSE_PORT(7),
    NS_PING_PORT(8),
    LNS_PING_PORT(9),
    PING_PORT(-1); // selects either NS_PING_PORT or LNS_PING_PORT
    
    //
    int offset;

    PortType(int offset) {
      this.offset = offset;
    }

    public static int maxOffset() {
      int result = 0;
      for (PortType p : values()) {
        if (p.offset > result) {
          result = p.offset;
        }
      }
      return result;
    }

    public int getOffset() {
      return offset;
    }
  }
  public static boolean enableEmailAccountAuthentication = true;
  public static boolean enableSignatureVerification = true;
  /**
   * Number of primary nameservers. Default is 3 *
   */
  public static final int DEFAULT_NUM_PRIMARY_REPLICAS = 3;
  public static int numPrimaryReplicas = DEFAULT_NUM_PRIMARY_REPLICAS;
  public static final ReplicationFrameworkType DEFAULT_REPLICATION_FRAMEWORK = ReplicationFrameworkType.LOCATION;
  /**
   * default query timeout in ms.
   */
  public static int DEFAULT_QUERY_TIMEOUT = 2000;
  /**
   * maximum query wait time in milliseconds
   */
  public static int DEFAULT_MAX_QUERY_WAIT_TIME = 10000; // currently 10 seconds
  /**
   * default number of transmissions
   */
  public static int DEFAULT_NUMBER_OF_TRANSMISSIONS = 3;
  /**
   * Logging level for main logger
   */
  public static String fileLoggingLevel = "FINE";
  /**
   * Console output level for main logger
   */
  public static String consoleOutputLevel = "FINE";
  /**
   * Logging level for stat logger
   */
  public static String statFileLoggingLevel = "FINE";
  /**
   * Console output level for stat logger
   */
  public static String statConsoleOutputLevel = "FINE";  // don't send these to the console normally
  private final static Logger LOGGER = Logger.getLogger(GNS.class.getName());
  public static boolean initRun = false;

  public static Logger getLogger() {
    if (!initRun) {
      System.out.println("Setting Logger console level to " + consoleOutputLevel + " and file level to " + fileLoggingLevel);
      Logging.setupLogger(LOGGER, consoleOutputLevel, fileLoggingLevel, "log" + "/gns.xml");
      initRun = true;
    }
    return LOGGER;
  }
  private final static Logger STAT_LOGGER = Logger.getLogger("STAT_" + GNS.class.getName());
  public static boolean initStatRun = false;

  public static Logger getStatLogger() {

    if (!initStatRun) {
      // don't send these to the console normally
      System.out.println("Setting STAT Logger console level to " + statConsoleOutputLevel + " and file level to " + statFileLoggingLevel);
      Logging.setupLogger(STAT_LOGGER, statConsoleOutputLevel, statFileLoggingLevel, "log" + "/gns_stat.xml");
      initStatRun = true;
    }
    return STAT_LOGGER;
  }

  /**
   * Attempts to look for a MANIFEST file in that contains the Build-Version attribute.
   * 
   * @return 
   */
  public static String readBuildVersion() {
    String result = null;
    try {
      Class clazz = GNS.class;
      String className = clazz.getSimpleName() + ".class";
      String classPath = clazz.getResource(className).toString();
      if (classPath.startsWith("jar")) {
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1)
                + "/META-INF/MANIFEST.MF";
        Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        result = attr.getValue("Build-Version");
      }
    } catch (IOException e) {
    }
    return result;
  }
}
