package edu.umass.cs.contextservice.integration;

import static edu.umass.cs.gnscommon.GNSCommandProtocol.FIELD;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.GUID;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.N;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.OLD_VALUE;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.SIGNATURE;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.SIGNATUREFULLMESSAGE;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.USER_JSON;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.VALUE;
import static edu.umass.cs.gnscommon.GNSCommandProtocol.WRITER;

import org.json.JSONObject;

import edu.umass.cs.contextservice.client.ContextServiceClient;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.BasicCommand;
import edu.umass.cs.gnsserver.main.GNSConfig;
import edu.umass.cs.utils.Config;

import java.util.logging.Level;

/**
 * This class interacts with context service using context service client.
 * It also implements the ContextServiceInterface.
 *
 * @author adipc
 *
 */
public class ContextServiceGNSClient implements ContextServiceGNSInterface {

  private ContextServiceClient<Integer> csClient;

  public ContextServiceGNSClient(String hostName, int portNum) {
    // catching everything here, otherwise exception doesn't get printed in executor service.
    try {
      csClient = new ContextServiceClient<Integer>(hostName, portNum);
    } catch (Error | Exception er) {
      er.printStackTrace();
    }
  }

//	@Override
//	public void sendUpdateToCS(String GUID, JSONObject attrValPairJSON, long versionNum, boolean blocking) 
//	{	
//	}
  @Override
  public void sendTiggerOnGnsCommand(JSONObject jsonFormattedCommand, BasicCommand command, boolean blocking) {
    try {
      // code copied exactly from AbstractUpdate class
      String guid = jsonFormattedCommand.getString(GUID);
      String field = jsonFormattedCommand.optString(FIELD, null);
      String value = jsonFormattedCommand.optString(VALUE, null);
      String oldValue = jsonFormattedCommand.optString(OLD_VALUE, null);
      int index = jsonFormattedCommand.optInt(N, -1);
      JSONObject userJSON = jsonFormattedCommand.has(USER_JSON) ? new JSONObject(jsonFormattedCommand.getString(USER_JSON)) : null;
      // writer might be unspecified so we use the guid
      String writer = jsonFormattedCommand.optString(WRITER, guid);
      String signature = jsonFormattedCommand.optString(SIGNATURE, null);
      String message = jsonFormattedCommand.optString(SIGNATUREFULLMESSAGE, null);

      if (writer.equals(Config.getGlobalString(GNSConfig.GNSC.INTERNAL_OP_SECRET))) {
        writer = null;
      }

      if (field == null) {
        //responseCode = FieldAccess.updateUserJSON(guid, userJSON, writer, signature, message, handler);

        // full json update
        // send the full JSON, contextServiceClient will check what attributes are supported 
        // by context service and send them to CS
        GNSConfig.getLogger().log(Level.FINE, "Trigger to CS guid {0} userJSON {1}",
                new Object[]{guid, userJSON});

        csClient.sendUpdate(guid, userJSON, -1, blocking);
      } else {
        // single field update
        JSONObject attrValJSON = new JSONObject();
        attrValJSON.put(field, value);

        GNSConfig.getLogger().log(Level.FINE, "Trigger to CS guid {0} attrValJSON {1}",
                new Object[]{guid, attrValJSON});

        csClient.sendUpdate(guid, attrValJSON, -1, blocking);
      }
    } catch (Exception | Error ex) {
      ex.printStackTrace();
    }
  }

}
