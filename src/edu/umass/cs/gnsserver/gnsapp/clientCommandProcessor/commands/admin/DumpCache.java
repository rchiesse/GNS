/*
 *
 *  Copyright (c) 2015 University of Massachusetts
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you
 *  may not use this file except in compliance with the License. You
 *  may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  Initial developer(s): Westy
 *
 */
package edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.admin;

import static edu.umass.cs.gnscommon.GNSCommandProtocol.*;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.ClientRequestHandlerInterface;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commandSupport.CommandResponse;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.CommandModule;
import edu.umass.cs.gnsserver.main.GNSConfig;
import edu.umass.cs.gnscommon.CommandType;
import edu.umass.cs.gnscommon.GNSResponseCode;
import edu.umass.cs.gnsserver.gnsapp.clientCommandProcessor.commands.BasicCommand;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author westy
 */
public class DumpCache extends BasicCommand {

  /**
   *
   * @param module
   */
  public DumpCache(CommandModule module) {
    super(module);
  }

  @Override
  public CommandType getCommandType() {
    return CommandType.DumpCache;
  }

  @Override
  public String[] getCommandParameters() {
    return new String[]{};
  }

//  @Override
//  public String getCommandName() {
//    return DUMPCACHE;
//  }
  @Override
  @SuppressWarnings("unchecked")
  public CommandResponse execute(JSONObject json, ClientRequestHandlerInterface handler) throws InvalidKeyException, InvalidKeySpecException,
          JSONException, NoSuchAlgorithmException, SignatureException {
      return new CommandResponse(GNSResponseCode.NO_ERROR, handler.getAdmintercessor().sendDumpCache(handler));
  }

  @Override
  public String getCommandDescription() {
    return "[ONLY IN ADMIN MODE] Returns the contents of the local name server cache.";
  }
}
