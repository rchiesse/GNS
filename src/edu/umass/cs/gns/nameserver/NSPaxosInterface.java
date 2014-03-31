package edu.umass.cs.gns.nameserver;

import edu.umass.cs.gns.database.BasicRecordCursor;
import edu.umass.cs.gns.exceptions.RecordExistsException;
import edu.umass.cs.gns.exceptions.RecordNotFoundException;
import edu.umass.cs.gns.main.GNS;
import edu.umass.cs.gns.nameserver.recordmap.NameRecord;
import edu.umass.cs.gns.nameserver.replicacontroller.ComputeNewActivesTask;
import edu.umass.cs.gns.nameserver.replicacontroller.ReplicaController;
import edu.umass.cs.gns.nameserver.recordmap.ReplicaControllerRecord;
import edu.umass.cs.gns.packet.Packet;
import edu.umass.cs.gns.paxos.paxospacket.FailureDetectionPacket;
import edu.umass.cs.gns.paxos.paxospacket.RequestPacket;
import edu.umass.cs.gns.paxos.PaxosInterface;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: abhigyan
 * Date: 6/29/13
 * Time: 8:34 PM
 * To change this template use File | Settings | File Templates.
 *
 * @deprecated
 */
public class NSPaxosInterface implements PaxosInterface {

  @Override
  public void handleDecision(String paxosID, String stringReq, boolean recovery) {
    long t0 = System.currentTimeMillis();
    RequestPacket req=null;
    try {
    	
    	// FIXME: Changed RequestPacket to a string argument. Check and remove FIXME.
    	req = new RequestPacket(new JSONObject(stringReq));

      // messages decided in to paxos between actives
      if (req.clientID == Packet.PacketType.ACTIVE_PAXOS_STOP.getInt()) {
        // current paxos instance stopped
        ListenerReplicationPaxos.handleActivePaxosStop(new JSONObject(req.value));
      }
      else if (req.clientID == Packet.PacketType.UPDATE_ADDRESS_NS.getInt()) {
        // address update is applied
        ClientRequestWorker.handleUpdateAddressNS(new JSONObject(req.value));
      }

      // messages decided for paxos between primaries
      else if (req.clientID == Packet.PacketType.NEW_ACTIVE_PROPOSE.getInt()) {
        ComputeNewActivesTask.applyNewActivesProposed(req.value);
      }
      else if (req.clientID == Packet.PacketType.NEW_ACTIVE_START_CONFIRM_TO_PRIMARY.getInt()) {
        ReplicaController.applyActiveNameServersRunning(req.value);
      }
//            else if (req.clientID == Packet.PacketType.OLD_ACTIVE_STOP_CONFIRM_TO_PRIMARY.getInt()) { // not used
//                ReplicaController.oldActiveStoppedWriteToNameRecord(req.value);
//            }
      else if (req.clientID  == Packet.PacketType.ADD_RECORD_NS.getInt()) {
        ClientRequestWorker.handleAddRecordNS(new JSONObject(req.value), recovery);
      }
      else if (req.clientID  == Packet.PacketType.REMOVE_RECORD_LNS.getInt()) {
        ReplicaController.applyMarkedForRemoval(req.value);
      }
      else if (req.clientID == Packet.PacketType.PRIMARY_PAXOS_STOP.getInt()) {
        ReplicaController.applyStopPrimaryPaxos(req.value);
      }


    } catch (JSONException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      GNS.getLogger().severe(" Exception Exception Exception ... " + e.getMessage());
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }
    long t1 = System.currentTimeMillis();
    if (t1 - t0 > 100)
      GNS.getLogger().warning("Long delay " + (t1 - t0) + "ms. Packet: " + req.value);
  }

  // FIXME: Removed an @Override from here. This method is no loger in PaxosInterface.
  public void handleFailureMessage(FailureDetectionPacket fdPacket) {
    ReplicaController.handleNodeFailure(fdPacket);
  }

  @Override
  public String getState(String paxosID) {

    if (ReplicaController.isPrimaryPaxosID(paxosID)) {
      BasicRecordCursor iterator = NameServer.getReplicaController().getAllRowsIterator();
//    if (StartNameServer.debugMode) GNS.getLogger().info("Got iterator : " + replicationRound);
      StringBuilder sb = new StringBuilder();
      int recordCount = 0;
      while (iterator.hasNext()) {
        try {
          JSONObject jsonObject = iterator.next();
          sb.append(jsonObject.toString());
          sb.append("\n");
          recordCount += 1;
        } catch (Exception e) {
          GNS.getLogger().severe("Problem creating ReplicaControllerRecord from JSON" + e);
        }
      }
      GNS.getLogger().info("Number of records whose state is read from DB: " + recordCount);
      return sb.toString();
    }
    else {
      String name = ReplicaController.getNameFromActivePaxosID(paxosID);
      // read all fields of the record
      try {
        NameRecord nameRecord = NameRecord.getNameRecord(NameServer.getRecordMap(), name);
        return  (nameRecord == null) ? null: nameRecord.toString();
      } catch (RecordNotFoundException e) {
        GNS.getLogger().warning("Exception Record not found. " + e.getMessage() + "\t" + paxosID);
        return null;
      }
    }
  }

  @Override
    public void updateState(String paxosID, String state) {
    try {
      GNS.getLogger().info("Update state: " + paxosID  + "\tState-length: " + state.length());
      if (ReplicaController.isPrimaryPaxosID(paxosID)) {

        if  (state.length() == 0) {
          return;
        }
        GNS.getLogger().info("Here: " + paxosID);
        int recordCount = 0;
        int startIndex = 0;
        GNS.getLogger().info("Update state: " + paxosID);
        while (true) {
          int endIndex = state.indexOf('\n', startIndex);
          if (endIndex == -1) break;
          String x = state.substring(startIndex, endIndex);
          if (x.length() > 0) {
            recordCount += 1;
            JSONObject json = new JSONObject(x);
            ReplicaControllerRecord rcr = new ReplicaControllerRecord(NameServer.getReplicaController(), json);
            GNS.getLogger().fine("Inserting rcr into DB ....: " + rcr + "\tjson = " + json);
            try {
              ReplicaControllerRecord.addNameRecordPrimary(NameServer.getReplicaController(), rcr);
            } catch (RecordExistsException e) {
              ReplicaControllerRecord.updateNameRecordPrimary(NameServer.getReplicaController(), rcr);
            }

            startIndex = endIndex;
          } else {
            startIndex += 1;
          }
        }
        GNS.getLogger().info("Number of rc records updated in DB: " + recordCount);
      } else {
        JSONObject json = new JSONObject(state);
        GNS.getLogger().info("Updated name record in DB: " + paxosID + "\t" + state);
        try {
          NameRecord.addNameRecord(NameServer.getRecordMap(), new NameRecord(NameServer.getRecordMap(), json));
        } catch (RecordExistsException e) {
          NameRecord.updateNameRecord(NameServer.getRecordMap(), new NameRecord(NameServer.getRecordMap(), json));
        }
      }
    } catch (JSONException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deleteStateBeforeRecovery() {
    throw new UnsupportedOperationException();
  }


  @Override
  public void stop(String paxosID, String value) {

  }

//  @Override
//  public String getPaxosKeyForPaxosID(String paxosID) {
//    if (ReplicaController.isPrimaryPaxosID(paxosID)) return paxosID; // paxos between primaries
//    else { // paxos between actives.
//      int index = paxosID.lastIndexOf("-");
//      if (index == -1) return paxosID;
//      return paxosID.substring(0, index);
//    }
//  }


}
