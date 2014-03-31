package edu.umass.cs.gns.packet.admin;

import edu.umass.cs.gns.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;
/**************** FIXME Package deprecated by nsdesign/packet. this will soon be deleted. **/
/**
 * This class implements a packet that tells the name server
 * to initial the status system.
 * 
 * @author Westy
 * @deprecated
 */
public class StatusInitPacket extends AdminPacket {

  /**
   * Constructs a new status init packet
   * @param id
   * @param jsonObject 
   */
  public StatusInitPacket() {
    this.type = Packet.PacketType.STATUS_INIT;
  }

  /**
   * Constructs new StatusInitPacket from a JSONObject
   * @param json JSONObject representing this packet
   * @throws JSONException
   */
  public StatusInitPacket(JSONObject json) throws JSONException {
    if (Packet.getPacketType(json) != Packet.PacketType.STATUS_INIT) {
      Exception e = new Exception("StatusInitPacket: wrong packet type " + Packet.getPacketType(json));
      e.printStackTrace();
      return;
    }

    this.type = Packet.getPacketType(json);
  }

  /**
   * Converts a StatusInitPacket to a JSONObject.
   * @return JSONObject representing this packet.
   * @throws JSONException
   */
  @Override
  public JSONObject toJSONObject() throws JSONException {
    JSONObject json = new JSONObject();
    Packet.putPacketType(json, getType());

    return json;
  }
}
