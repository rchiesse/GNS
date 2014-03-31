package edu.umass.cs.gns.packet.admin;

import edu.umass.cs.gns.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;
/**************** FIXME Package deprecated by nsdesign/packet. this will soon be deleted. **/
/**
 * This class implements the packet used to mark the end of a dump request.
 * 
 * @author Westy
 * @deprecated
 **/
public class SentinalPacket extends AdminPacket {

  public final static String ID = "id";
  private int id;

  /**
   * Constructs new SentinalPacket.
   **/
  public SentinalPacket(int id) {
    this.type = Packet.PacketType.SENTINAL;
    this.id = id;
  }

  /**
   * Constructs new SentinalPacket from a JSONObject
   * @param json JSONObject representing this packet
   * @throws JSONException
   **/
  public SentinalPacket(JSONObject json) throws JSONException {
    if (Packet.getPacketType(json) != Packet.PacketType.SENTINAL) {
      Exception e = new Exception("SentinalPacket: wrong packet type " + Packet.getPacketType(json));
      e.printStackTrace();
      return;
    }

    this.id = json.getInt(ID);
    this.type = Packet.getPacketType(json);
  }

  /**
   * Converts a SentinalPacket to a JSONObject.
   * @return JSONObject representing this packet.
   * @throws JSONException
   **/
  @Override
  public JSONObject toJSONObject() throws JSONException {
    JSONObject json = new JSONObject();
    Packet.putPacketType(json, getType());
    json.put(ID, id);
    return json;
  }

  public int getId() {
    return id;
  }
}
