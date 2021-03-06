/*******************************************************************************
 *
 * Mobility First - mSocket library
 * Copyright (C) 2013, 2014 - University of Massachusetts Amherst
 * Contact: arun@cs.umass.edu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Arun Venkataramani, Aditya Yadav, Emmanuel Cecchet.
 * Contributor(s): ______________________.
 *
 *******************************************************************************/

package edu.umass.cs.msocket.common.policies;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;

import edu.umass.cs.gnscommon.exceptions.client.ClientException;
import edu.umass.cs.msocket.common.Constants;
import edu.umass.cs.msocket.gns.DefaultGNSClient;
import edu.umass.cs.msocket.proxy.location.GeodeticCalculator;
import edu.umass.cs.msocket.proxy.location.GlobalPosition;
import edu.umass.cs.msocket.proxy.location.ProxyStatusInfo;

/**
 * This class defines a GeolocationProxyPolicy
 * 
 * @author <a href="mailto:cecchet@cs.umass.edu">Emmanuel Cecchet</a>
 * @version 1.0
 */
public class GeolocationProxyPolicy extends ProxySelectionPolicy
{
  private static final long   serialVersionUID = 1781946475577921232L;
  private String              proxyGroupName;
  private int                 numProxies;
  private static final Logger logger           = Logger.getLogger("GnsProxy");

  /**
   * Creates a new <code>GeolocationProxyPolicy</code> object
   * 
   * @param proxyGroupName name of the proxy group
   * @param gnsCredentials The GNS credentials to use, usually the account GUID
   *          and default GNS (if null default GNS credentials are used). If the
   *          guid entry is null in the credentials, the code will attempt a
   *          non-signed read of active location services (only works if field
   *          is readable for all).
   * @param numProxies
   */
  public GeolocationProxyPolicy(String proxyGroupName, int numProxies)
  {
    this.proxyGroupName = proxyGroupName;
    this.numProxies = numProxies;
  }

  /**
   * @throws Exception if a GNS error occurs
   * @see edu.umass.cs.msocket.common.policies.ProxySelectionPolicy#getNewProxy()
   */
  @Override
  public List<InetSocketAddress> getNewProxy() throws Exception
  {
    // Lookup for active location service GUIDs
    //final GNSClientCommands gnsClient = gnsCredentials.getGnsClient();
    //final GuidEntry guidEntry = gnsCredentials.getGuidEntry();
    JSONArray guids;
    try
    {
      guids = DefaultGNSClient.getGnsClient().fieldReadArray
   (proxyGroupName, Constants.ACTIVE_LOCATION_FIELD, DefaultGNSClient.getMyGuidEntry() ); }
    catch (Exception e)
    {
      throw new ClientException("Could not find active location services (" + e + ")");
    }

    // Try every location proxy in the list until one works
    for (int i = 0; i < guids.length(); i++)
    {
      // Retrieve the location service IP and connect to it
      String locationGuid = guids.getString(i);
      String locationIP = DefaultGNSClient.getGnsClient().fieldReadArray
    		  (locationGuid, Constants.LOCATION_SERVICE_IP, DefaultGNSClient.getMyGuidEntry()).getString(0);
      logger.fine("Contacting location service " + locationIP + " to request " + numProxies + " proxies");

      // Location IP is stored as host:port
      StringTokenizer st = new StringTokenizer(locationIP, ":");
      try
      {
        // Protocol is send the number of desired proxies and receive strings
        // containing proxy IP:port
        Socket s = new Socket(st.nextToken(), Integer.parseInt(st.nextToken()));
        ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
        oos.writeInt(numProxies);
        oos.flush();
        List<InetSocketAddress> result = new LinkedList<InetSocketAddress>();
        while (!s.isClosed() && result.size() < numProxies)
        {
          String proxyIP = ois.readUTF();
          StringTokenizer stp = new StringTokenizer(proxyIP, ":");
          result.add(new InetSocketAddress(stp.nextToken(), Integer.parseInt(stp.nextToken())));
        }
        if (!s.isClosed()) // We receive all the proxies we need, just close the
                           // socket
          s.close();
        return result;
      }
      catch (Exception e)
      {
        logger.info("Failed to obtain proxy from location service" + locationIP + " (" + e + ")");
      }
    }

    throw new ClientException("Could not find any location service to provide a geolocated proxy");
  }

  @Override
  public List<String> getProxyIPs(List<ProxyStatusInfo> proxies, Socket acceptedSocket)
  {
    final SocketAddress remoteIp = acceptedSocket.getRemoteSocketAddress();
    double clientLatitude = 0;
    double clientLongitude = 0;
    try
    {
      // Contact http://freegeoip.net/csv/[IP] to resolve IP address location
      URL locator = new URL("http://freegeoip.net/csv/" + remoteIp);
      BufferedReader in = new BufferedReader(new InputStreamReader(locator.openStream()));
      String csv = in.readLine();
      in.close();
      // Read location result
      StringTokenizer st = new StringTokenizer(csv, ",");
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read IP");
      st.nextToken(); // IP
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read country code");
      st.nextToken(); // country code
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read country name");
      st.nextToken(); // country name
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read state code");
      st.nextToken(); // state code
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read state name");
      st.nextToken(); // state name
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read city");
      st.nextToken(); // city
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read zip");
      st.nextToken(); // zip
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read latitude");
      String latitudeStr = st.nextToken().replace("\"", "");
      clientLatitude = Double.parseDouble(latitudeStr);
      if (!st.hasMoreTokens())
        throw new IOException("Failed to read longitude");
      String longitudeStr = st.nextToken().replace("\"", "");
      clientLongitude = Double.parseDouble(longitudeStr);
    }
    catch (Exception e)
    {
      logger.log(Level.WARNING, "Failed to geolocate IP address " + remoteIp, e);
    }

    // Compute the distance with each proxy
    Map<Double, ProxyStatusInfo> distances = new HashMap<Double, ProxyStatusInfo>();
    for (ProxyStatusInfo proxyStatusInfo : proxies)
    {
      double distance = GeodeticCalculator.calculateGeodeticMeasurement(
          new GlobalPosition(clientLatitude, clientLongitude, 0),
          new GlobalPosition(proxyStatusInfo.getLatitude(), proxyStatusInfo.getLontitude(), 0))
          .getPointToPointDistance();
      distances.put(distance, proxyStatusInfo);
    }

    // Build the result list
    List<String> ipPorts = new LinkedList<String>();
    int proxiesLeft = numProxies;
    while (proxiesLeft > 0 && !distances.isEmpty())
    {
      // Set of keys is ordered so just take the first one and send it
      ProxyStatusInfo proxy = distances.remove(distances.keySet().iterator().next());
      ipPorts.add(proxy.getIp());
    }
    return ipPorts;
  }

  @Override
  public boolean hasAvailableProxies()
  {
    try
    {
      return getNewProxy() != null;
    }
    catch (Exception e)
    {
      return false; // Failed to retrieve proxies
    }
  }

  /**
   * Returns the numProxies value.
   * 
   * @return Returns the numProxies.
   */
  public int getNumProxies()
  {
    return numProxies;
  }

}
