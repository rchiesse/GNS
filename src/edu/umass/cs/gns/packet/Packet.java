package edu.umass.cs.gns.packet;

import edu.umass.cs.gns.main.GNS;
import edu.umass.cs.gns.main.StartLocalNameServer;
import edu.umass.cs.gns.util.BestServerSelection;
import edu.umass.cs.gns.util.ConfigFileInfo;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
/**************** FIXME Package deprecated by nsdesign/packet. this will soon be deleted. **/
/**
 * So we have these packets see and we convert them back and forth to and from JSON Objects.
 * And send them over UDP and TCP connections. And we have an enum called PacketType that we
 * use to keep track of the type of packet that it is.
 * 
 * And it appears that there are two separate but equal mechanisms to keep track of packet sizes
 * during transmission (one in here and one elsewhere ...  multiple authors again).
 * 
 * This is our basic way of transmitting data.
 * It's a bit of a hodgepodge at this point due to multiple authors, but it works.
 * @deprecated
 * @author westy
 */
public class Packet {

  /**
   * Defines the type of this packet *
   */
  public final static String TYPE = "type";
  //Type of packets

  public enum PacketType {

    // SPECIAL CASES FOR DNS PACKETS WHICH USE ONE PACKET FOR ALL THESE
    // these 3 are here for completeness and instrumentation - DNS packets currently don't include a packet type field
    DNS(-1),
    DNS_RESPONSE(-2),
    DNS_ERROR_RESPONSE(-3),
    //
    ADD_RECORD_LNS(1),
    ADD_RECORD_NS(2),
    CONFIRM_ADD_LNS(3),
    CONFIRM_ADD_NS(4),
    ADD_COMPLETE(5),
    REMOVE_RECORD_LNS(6),
    REMOVE_RECORD_NS(7),
    CONFIRM_REMOVE_LNS(8),
    //
    REMOVE_REPLICATION_RECORD(10), // NOT USED
    REPLICATE_RECORD(11), // NOT USED
    UPDATE_ADDRESS_LNS(12), // this is for packets involving the LNS (that is client support -> LNS and LNS -> NS)
    UPDATE_ADDRESS_NS(13), // this is for packets that are sent between NSs
    CONFIRM_UPDATE_LNS(14),
    CONFIRM_UPDATE_NS(15),
    REQUEST_ACTIVES(18),
    //
    UPDATE_NAMESERVER(20),
    ACTIVE_NAMESERVER_UPDATE(21),
    NAMESERVER_SELECTION(22),
    NEW_ACTIVE_PROPOSE(23),
    //
    NAME_RECORD_STATS_REQUEST(30),
    NAME_RECORD_STATS_RESPONSE(31),
    ACTIVE_NAMESERVER_INFO(32),
    //
    DUMP_REQUEST(40),
    SENTINAL(41),
    ADMIN_REQUEST(42),
    ADMIN_RESPONSE(43),
    //
    STATUS(50),
    TRAFFIC_STATUS(51),
    STATUS_INIT(52),
    //
    NAME_SERVER_LOAD(60),
    //
    SELECT_REQUEST(70),
    SELECT_RESPONSE(71),
    //
    PAXOS_PACKET(90),
    //
    NEW_ACTIVE_START(101),
    NEW_ACTIVE_START_FORWARD(102),
    NEW_ACTIVE_START_RESPONSE(103),
    NEW_ACTIVE_START_CONFIRM_TO_PRIMARY(104),
    NEW_ACTIVE_START_PREV_VALUE_REQUEST(105),
    NEW_ACTIVE_START_PREV_VALUE_RESPONSE(106),

    //
    OLD_ACTIVE_STOP(111),
    ACTIVE_PAXOS_STOP(112),
    OLD_ACTIVE_STOP_CONFIRM_TO_PRIMARY(113),
    PRIMARY_PAXOS_STOP(114),
    DELETE_OLD_ACTIVE_STATE(115), // Abhigyan: (new packet type). send to old active replica to delete state.

    // Abhigyan: do not use these packet types. I have defined them for refactoring name server code
    ACTIVE_ADD(121), // on an add request replica controller sends to active replica
    ACTIVE_ADD_CONFIRM(122), // after adding name, active replica confirms to replica controller
    ACTIVE_REMOVE(123), // on a remove request, replica controller sends to active replica
    ACTIVE_REMOVE_CONFIRM(124), // after removing name, active replica confirms to replica controller
    ACTIVE_GROUPCHANGE(125), // replica controller requests an active replica to transition from the old to the new set of active replicas
    ACTIVE_GROUPCHANGE_CONFIRM(126), // after transition from old to the new active replicas is complete, the active replica confirms to replica controller
    ACTIVE_COORDINATION(127), // after transition from old to the new active replicas is complete, the active replica confirms to replica controller
    REPLICA_CONTROLLER_COORDINATION(128);  // after transition from old to the new active replicas is complete, the active replica confirms to replica controller
    private int number;
    private static final Map<Integer, PacketType> map = new HashMap<Integer, PacketType>();

    static {
      for (PacketType type : PacketType.values()) {
        if (map.containsKey(type.getInt())) {
          GNS.getLogger().warning("**** Duplicate ID number for packet type " + type + ": " + type.getInt());
        }
        map.put(type.getInt(), type);
      }
    }

    private PacketType(int number) {
      this.number = number;
    }

    public int getInt() {
      return number;
    }

    public static PacketType getPacketType(int number) {
      return map.get(number);
    }
  }

  public static PacketType getDNSPacketType(DNSPacket dnsPacket) throws JSONException {

    if (dnsPacket.isQuery()) { // Query
      return PacketType.DNS;
    } else if (dnsPacket.isResponse() && !dnsPacket.containsAnyError()) {
      return PacketType.DNS_RESPONSE;
    } else if (dnsPacket.containsAnyError()) {
      return PacketType.DNS_ERROR_RESPONSE;
    }
    return null;
  }

  // some shorthand helpers
  public static PacketType getPacketType(int number) {
    return PacketType.getPacketType(number);
  }

  public static PacketType getPacketType(JSONObject json) throws JSONException {
    //System.out.println("*****PACKETTYPE****:: " + json.getInt(TYPE) + " : " + PacketType.getPacketType(json.getInt(TYPE)));
    if (Packet.hasPacketTypeField(json)) {
      return PacketType.getPacketType(json.getInt(TYPE));
    }
    // why... why not just put the type in the packet like all the other ones do?
    return PacketType.DNS;
  }

  public static boolean hasPacketTypeField(JSONObject json) {
    return json.has(TYPE);
  }

  public static void putPacketType(JSONObject json, PacketType type) throws JSONException {
    json.put(TYPE, type.getInt());
  }
  /**
   * Delimiter that separates size from data in each frame transmitted *
   */
  public final static String DELIMITER = ":";

  /**
   * **
   * Reads bytes until we see delimiter ":". All bytes before ":" indicates the size of the data. Bytes after ":" is the actual
   * data.
   *
   * @return Size of a frame (packet) or -1 if the input stream is closed *
   */
  public static int getDataSize(InputStream inStream) {

    String delimiter = " ";
    String vectorSizeStr = "";
    byte[] tempBuffer = new byte[1];

    try {
      //Keep reading from input stream until we see ":". The bytes 
      //before ":" represents the size of the frame. The bytes after
      //":" is the actual frame.
      while (delimiter.compareTo(DELIMITER) != 0) {
        if (inStream.read(tempBuffer, 0, 1) == -1) {
          return -1;
        }

        delimiter = new String(tempBuffer);
        if (delimiter.compareTo(DELIMITER) != 0) {
          vectorSizeStr += delimiter;
        }
      }
    } catch (Exception e) {
      return -1;
    }

    return Integer.parseInt(vectorSizeStr.trim());
  }

  /**
   * **
   * Reads bytes from the input stream until we have read bytes equal the size of a frame (packet) and returns a JSONObject that
   * represents the frame.
   *
   * @param input Input stream to read the frame.
   * @param sizeOfFrame Size of a frame (packet)
   * @throws IOException
   * @throws JSONException *
   */
  private static JSONObject getJSONObjectFrame(InputStream input, int sizeOfFrame)
          throws IOException, JSONException {
    byte[] jsonByte = new byte[sizeOfFrame];
    int tempSize = input.read(jsonByte);

    //Keep reading bytes until we have read number of bytes equal the expected
    //size of the frame.
    while (tempSize != sizeOfFrame) {
      tempSize += input.read(jsonByte, tempSize, sizeOfFrame - tempSize);
    }

    JSONObject json = new JSONObject(new String(jsonByte));
    return json;
  }

  public static int getDataSize(ByteBuffer buffer) {
    String delimiter = " ";
    String vectorSizeStr = "";
    byte[] tempBuffer = new byte[1];

    //Keep reading from input stream until we see ":". The bytes 
    //before ":" represents the size of the frame. The bytes after
    //":" is the actual frame.
    while (delimiter.compareTo(DELIMITER) != 0) {
      if (!buffer.hasRemaining()) {
        return -1;
      }
      buffer.get(tempBuffer);

      delimiter = new String(tempBuffer);
      if (delimiter.compareTo(DELIMITER) != 0) {
        vectorSizeStr += delimiter;
      }
    }
    return Integer.parseInt(vectorSizeStr.trim());
  }

  private static JSONObject getJSONObjectFrame(ByteBuffer buffer, int sizeOfFrame)
          throws IOException, JSONException {
    if (sizeOfFrame > buffer.remaining()) {
      return null;
    }
    byte[] jsonByte = new byte[sizeOfFrame];
    buffer.get(jsonByte);
    //System.out.println(new String(jsonByte));
    JSONObject json = new JSONObject(new String(jsonByte));
    return json;
  }

  public static JSONObject getJSONObjectFrame(ByteBuffer buffer)
          throws IOException, JSONException {

    int sizeOfPacket = Packet.getDataSize(buffer);

    if (sizeOfPacket == -1) {
      return null;
    }

    //Read the packet from the input stream
    return Packet.getJSONObjectFrame(buffer, sizeOfPacket);
  }

  /**
   * **
   * Reads bytes from the input stream until we have read bytes equal the size of a frame (packet) and returns a JSONObject that
   * represents the frame.
   *
   * @param socket Socket on which the frame has arrived
   * @return Returns JSONObject representing the packet. Returns <i>null</i> if the socket is closed.
   * @throws IOException
   * @throws JSONException *
   */
  public static JSONObject getJSONObjectFrame(Socket socket)
          throws IOException, JSONException {
    if (socket == null) {
      throw new IOException("Socket is null");
    }

    InputStream input = socket.getInputStream();
    //Get the size of the packet
    int sizeOfPacket = Packet.getDataSize(input);

    //Check for error: input stream reached the end of file
    if (sizeOfPacket == -1) {
      throw new IOException("Input stream reached the end of file");
    }

    //Read the packet from the input stream
    return Packet.getJSONObjectFrame(input, sizeOfPacket);
  }

  /**
   * **
   * Send a Packet to a name server using UDP
   *
   * @param id Name server id
   * @param socket DatagramSocket over which the packet is sent
   * @param json JsonObject representing the packet
   * @throws IOException *
   */
  public static void sendUDPPacket(int id, DatagramSocket socket, JSONObject json, GNS.PortType type)
          throws IOException {
    InetAddress address = ConfigFileInfo.getIPAddress(id);
    int port = getPort(id, type);

    if (address == null || port == -1) {
      GNS.getLogger().warning("sendUDPPacket:: FAIL! address: " + address + " port: " + port + " to: " + id + " json: " + json.toString());
      return;
    }

    if (StartLocalNameServer.debugMode) {
      GNS.getLogger().finer("sendUDPPacket:: to: " + id + " (" + address.getHostName() + ":" + port + ")" + " json: " + json.toString());
    }

    sendUDPPacket(socket, json, address, port);

//
//
//    byte[] buffer = json.toString().getBytes();
//    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
//    socket.send(packet);
  }

  /**
   * **
   * Send a response to a name server using UDP
   *
   * //
   *
   * @param id Name server id
   * @param socket DatagramSocket over which the packet is sent
   * @param json JsonObject representing the packet
   * @throws IOException *
   */
  public static void sendUDPPacket(DatagramSocket socket, JSONObject json, InetAddress address, int port) throws IOException {
    if (address == null || json == null || socket == null || port == -1) {
      GNS.getLogger().warning("sendUDPPacket:: FAIL! address: " + address + " port: " + port + " json: " + json.toString());
      return;
    }

//    GNRS.getLogger().finer("sendUDPPacket:: address: " + address.getHostName() + " port: " + port + " json: " + json.toString());

    byte[] buffer = json.toString().getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
    socket.send(packet);
  }

  public static void multicastUDP(Set<Integer> nameServerIds, JSONObject json, GNS.PortType portType, int excludeNameServerId, DatagramSocket socket) {
    for (Integer id : nameServerIds) {
      if (id.intValue() == excludeNameServerId) {
        continue;
      }
      try {
        int port = getPort(id, portType);
        InetAddress addr = ConfigFileInfo.getIPAddress(id);
        sendUDPPacket(socket, json, addr, port);
      } catch (IOException e) {
        GNS.getLogger().severe("Exception: socket closed by nameserver " + id);
        e.printStackTrace();
      }
    }
  }

  /**
   * **
   * Returns port number for the specified port type. Return -1 if the specified port type does not exist.
   *
   * @param nameServerId Name server id //
   * @param portType	GNS port type*
   */
  public static int getPort(int nameServerId, GNS.PortType portType) {
    switch (portType) {
      case NS_TCP_PORT:
        return ConfigFileInfo.getNSTcpPort(nameServerId);
      case LNS_TCP_PORT:
        return ConfigFileInfo.getLNSTcpPort(nameServerId);
      case NS_UDP_PORT:
        return ConfigFileInfo.getNSUdpPort(nameServerId);
      case LNS_UDP_PORT:
        return ConfigFileInfo.getLNSUdpPort(nameServerId);
      case NS_ADMIN_PORT:
        return ConfigFileInfo.getNSAdminRequestPort(nameServerId);
      case LNS_ADMIN_PORT:
        return ConfigFileInfo.getLNSAdminRequestPort(nameServerId);
      case ADMIN_PORT:
        return ConfigFileInfo.getAdminRequestPort(nameServerId);
      case LNS_ADMIN_RESPONSE_PORT:
        return ConfigFileInfo.getLNSAdminResponsePort(nameServerId);
      case LNS_ADMIN_DUMP_RESPONSE_PORT:
        return ConfigFileInfo.getLNSAdminDumpReponsePort(nameServerId);
      case LNS_PING_PORT:
        return ConfigFileInfo.getLNSPingPort(nameServerId);
      case NS_PING_PORT:
        return ConfigFileInfo.getNSPingPort(nameServerId);
      case PING_PORT:
        return ConfigFileInfo.getPingPort(nameServerId);
    }
    return -1;
  }

  /**
   * Sends a Packet to a name server using TCP.
   *
   * @param json JsonObject representing the packet
   * @param nameserverId Name server id
   * @param portType Type of port
   * @return Returns the Socket over which the packet was sent, or null if the port type is incorrect.
   * @throws IOException *
   */
  public static Socket sendTCPPacket(JSONObject json, int nameserverId, GNS.PortType portType) throws IOException {
    int port = getPort(nameserverId, portType);
    if (port == -1) {
      GNS.getLogger().warning("sendTCPPacket:: FAIL, BAD PORT! to: " + nameserverId + " json: " + json.toString());
      throw new IOException("Invalid port number " + port);
    }

    InetAddress addr = ConfigFileInfo.getIPAddress(nameserverId);
    if (addr == null) {
      GNS.getLogger().warning("sendTCPPacket:: FAIL, BAD ADDRESS! to: " + nameserverId + " port: " + port + " json: " + json.toString());
      return null;
    }
    GNS.getLogger().finer("sendTCPPacket:: to: " + nameserverId + " (" + addr.getHostName() + ":" + port + ")" + " json: " + json.toString());
    Socket socket = new Socket(addr, port);
    sendTCPPacket(json, socket);
    return socket;
  }

  /**
   * Sends a Packet to a name server using TCP.
   *
   * @param json JsonObject representing the packet //
   * @param nameserverId Name server id
   * @throws IOException *
   */
  public static void sendTCPPacket(JSONObject json, Socket socket) throws IOException {
    if (json == null || socket == null) {
      return;
    }
    String packet = json.toString();
    Integer jsonSize = packet.getBytes().length;
    String msg = jsonSize.toString() + ":" + packet;

    GNS.getLogger().finer("sendTCPPacket:: to: " + socket.getInetAddress().getHostName() + ":" + socket.getPort() + " json: " + json.toString());
    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
    output.println(msg);
    output.flush();
  }

  /**
   * Multicast TCP packet to all name servers in <i>nameServerIds</i>. Excludes name server whose id match
   * <i>excludeNameServerId</i>
   *
   * @param nameServerIds Set of name server ids where packet is sent
   * @param json JSONObject representing the packet
   * @param numRetry Number of re-try if the connection fails before successfully sending the packet.
   * @param portType Type of port to connect *
   */
  public static void multicastTCP(Set<Integer> nameServerIds, JSONObject json, int numRetry, GNS.PortType portType) {
    multicastTCP(nameServerIds, json, numRetry, portType, -1);
  }

  /**
   * Multicast TCP packet to all name servers in <i>nameServerIds</i>. Excludes name server whose id match
   * <i>excludeNameServerId</i>
   *
   * @param nameServerIds Set of name server ids where packet is sent
   * @param json JSONObject representing the packet
   * @param numRetry Number of re-try if the connection fails before successfully sending the packet.
   * @param portType Type of port to connect
   * @param excludeNameServerId Id of name server which is excluded from multicast *
   */
  public static void multicastTCP(Set<Integer> nameServerIds, JSONObject json, int numRetry, GNS.PortType portType, int excludeNameServerId) {
    multicastTCP(nameServerIds, json, numRetry, portType, new HashSet<Integer>(Arrays.asList(excludeNameServerId)));
  }

  /**
   * Multicast TCP packet to all name servers in <i>nameServerIds</i>. This method excludes name server id in
   * <i>excludeNameServers</i>
   *
   * @param nameServerIds Set of name server ids where packet is sent
   * @param json JSONObject representing the packet
   * @param numRetry Number of re-try if the connection fails before successfully sending the packet.
   * @param portType Type of port to connect
   * @param excludeNameServers *
   */
  public static void multicastTCP(Set<Integer> nameServerIds, JSONObject json, int numRetry, GNS.PortType portType, Set<Integer> excludeNameServers) {
    int tries;
    for (Integer id : nameServerIds) {
      if (excludeNameServers != null && excludeNameServers.contains(id)) {
        continue;
      }

      tries = 0;
      do {
        tries += 1;
        try {
          Socket socket = Packet.sendTCPPacket(json, id, portType);
          if (socket != null) {
            socket.close();
          }
          break;
        } catch (IOException e) {
          GNS.getLogger().severe("Exception: socket closed by nameserver " + id);
          e.printStackTrace();
        }
      } while (tries < numRetry);
    }
  }

  /**
   * Sends a packet to the closest name server in <i>nameServerIds</i> over TCP. If the connection fails, the packet is sent to the
   * next closest name server untill <i>numRetry</i> attemps have been made to successfully send the packet.
   *
   * @param nameServerIds Set of name server ids
   * @param json JSONObject representing the packet
   * @param numRetry Number of attempts if the connection fails before successfully sending the packet.
   * @param portType Type of port to connect
   * @return Returns the <i>Socket</i> over which the packet was transmitted. Returns <i>null</i> if no connection was successful in
   * sending the packet. *
   */
  public static Socket sendTCPPacketToClosestNameServer(Set<Integer> nameServerIds, JSONObject json,
          int numRetry, GNS.PortType portType) {
    int attempt = 0;
    Set<Integer> excludeNameServer = new HashSet<Integer>();
    int id;

    do {
      attempt++;
      id = BestServerSelection.getSmallestLatencyNS(nameServerIds, excludeNameServer);
      excludeNameServer.add(id);
      try {
        return Packet.sendTCPPacket(json, id, portType);
      } catch (IOException e) {
        GNS.getLogger().severe("Exception: socket closed by nameserver " + id);
        e.printStackTrace();
      }
    } while (attempt < numRetry);
    return null;
  }

  /**
   * Test
   *
   * @throws IOException *
   */
  public static void main(String[] args) throws IOException {
    System.out.println(getPacketType(16));
//    ConfigFileInfo.readHostInfo("ns3", 3);
//    Set<Integer> ids = new HashSet<Integer>();
//    ids.add(0);
//    ids.add(1);
//    ids.add(2);
//    ids.add(3);
//    ids.add(4);
//
//    sendTCPPacketToClosestNameServer(ids, new JSONObject(), 3, GNRS.PortType.REPLICATION_PORT);
  }
}
