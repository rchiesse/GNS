/*
 * Copyright (C) 2014
 * University of Massachusetts
 * All Rights Reserved 
 *
 * Initial developer(s): Westy.
 */
package edu.umass.cs.gns.nsdesign.clientsupport;

import com.google.common.collect.Sets;
import edu.umass.cs.gns.clientsupport.GroupAccess;
import edu.umass.cs.gns.clientsupport.GuidInfo;
import edu.umass.cs.gns.clientsupport.MetaDataTypeName;
import edu.umass.cs.gns.exceptions.FailedDBOperationException;
import edu.umass.cs.gns.exceptions.FieldNotFoundException;
import edu.umass.cs.gns.exceptions.RecordNotFoundException;
import edu.umass.cs.gns.main.GNS;
import edu.umass.cs.gns.nsdesign.gnsReconfigurable.GnsReconfigurable;
import edu.umass.cs.gns.util.Base64;
import edu.umass.cs.gns.util.ByteUtils;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static edu.umass.cs.gns.clientsupport.Defs.*;
import java.net.InetSocketAddress;

/**
 * Provides signing and ACL checks for commands.
 *
 * @author westy
 */
public class NSAccessSupport {

  private static boolean debuggingEnabled = false;

  // try this for now
  private static final Set<String> WORLDREADABLEFIELDS = new HashSet<String>(Arrays.asList(GroupAccess.JOINREQUESTS, GroupAccess.LEAVEREQUESTS));

  public static boolean verifySignature(GuidInfo guidInfo, String signature, String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
    if (!GNS.enableSignatureVerification) {
      return true;
    }
    byte[] publickeyString = Base64.decode(guidInfo.getPublicKey());
    if (publickeyString == null) { // bogus public key
      return false;
    }
    if (debuggingEnabled) {
      GNS.getLogger().info("NS: User " + guidInfo.getName() + " signature:" + signature + " message: " + message);
    }
    KeyFactory keyFactory = KeyFactory.getInstance(RASALGORITHM);
    X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publickeyString);
    PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

    Signature sig = Signature.getInstance(SIGNATUREALGORITHM);
    sig.initVerify(publicKey);
    sig.update(message.getBytes());
    boolean result = sig.verify(ByteUtils.hexStringToByteArray(signature));
    if (debuggingEnabled) {
      GNS.getLogger().fine("User " + guidInfo.getName() + (result ? " verified " : " NOT verified ") + "as author of message " + message);
    }
    return result;
  }

  /**
   * Checks to see if the reader given in readerInfo can access the field of the user given by guidInfo. Access type is some combo
   * of read, write, blacklist and whitelist. Note: Blacklists are currently not activated.
   *
   * @param access
   * @param guidInfo
   * @param field
   * @param accessorInfo
   * @return
   */
  public static boolean verifyAccess(MetaDataTypeName access, GuidInfo guidInfo, String field, GuidInfo accessorInfo,
          GnsReconfigurable activeReplica, InetSocketAddress lnsAddress) throws FailedDBOperationException {
    if (debuggingEnabled) {
      GNS.getLogger().info("User: " + guidInfo.getName() + " Reader: " + accessorInfo.getName() + " Field: " + field);
    }
    if (guidInfo.getGuid().equals(accessorInfo.getGuid())) {
      return true; // can always read your own stuff
    } else if (hierarchicalAccessCheck(access, guidInfo, field, accessorInfo, activeReplica, lnsAddress)) {
      return true; // accessor can see this field
//    } else if (checkForAccess(access, guidInfo, field, accessorInfo, activeReplica)) {
//      return true; // accessor can see this field
    } else if (checkForAccess(access, guidInfo, ALLFIELDS, accessorInfo, activeReplica, lnsAddress)) {
      return true; // accessor can see all fields
    } else {
      if (debuggingEnabled) {
        GNS.getLogger().info("User " + accessorInfo.getName() + " NOT allowed to access user " + guidInfo.getName() + "'s " + field + " field");
      }
      return false;
    }
  }

  /**
   * Handles checking of fields with dot notation.
   * Checks deepest field first then backs up.
   * 
   * @param access
   * @param guidInfo
   * @param field
   * @param accessorInfo
   * @param activeReplica
   * @return
   * @throws FailedDBOperationException 
   */
  private static boolean hierarchicalAccessCheck(MetaDataTypeName access, GuidInfo guidInfo, String field,
          GuidInfo accessorInfo, GnsReconfigurable activeReplica, InetSocketAddress lnsAddress) throws FailedDBOperationException {
    if (debuggingEnabled) {
      GNS.getLogger().info("###field=" + field);
    }
    if (checkForAccess(access, guidInfo, field, accessorInfo, activeReplica, lnsAddress)) {
      return true;
    }
    // otherwise go up the hierarchy and check
    if (field.contains(".")) {
      return hierarchicalAccessCheck(access, guidInfo, field.substring(0, field.lastIndexOf(".")), accessorInfo, activeReplica, lnsAddress);
    } else {
      return false;
    }
  }

  private static boolean checkForAccess(MetaDataTypeName access, GuidInfo guidInfo, String field, GuidInfo accessorInfo,
          GnsReconfigurable activeReplica, InetSocketAddress lnsAddress) throws FailedDBOperationException {
    // first check the always world readable ones
    if (WORLDREADABLEFIELDS.contains(field)) {
      return true;
    }
    try {
      Set<String> allowedusers = NSFieldMetaData.lookupOnThisNameServer(access, guidInfo, field, activeReplica);
      if (debuggingEnabled) {
        GNS.getLogger().info(guidInfo.getName() + " allowed users of " + field + " : " + allowedusers);
      }
      if (checkAllowedUsers(accessorInfo.getGuid(), allowedusers, activeReplica, lnsAddress)) {
        if (debuggingEnabled) {
          GNS.getLogger().info("User " + accessorInfo.getName() + " allowed to access "
                  + (field != ALLFIELDS ? ("user " + guidInfo.getName() + "'s " + field + " field") : ("all of user " + guidInfo.getName() + "'s fields")));
        }
        return true;
      }
      return false;
    } catch (FieldNotFoundException e) {
      // This is actually a normal result.. so no warning here.
      return false;
    } catch (RecordNotFoundException e) {
      GNS.getLogger().warning("User " + accessorInfo.getName() + " access problem for " + guidInfo.getName() + "'s " + field + " field: " + e);
      return false;
    }

  }

  private static boolean checkAllowedUsers(String accesserGuid, Set<String> allowedUsers, GnsReconfigurable activeReplica,
          InetSocketAddress lnsAddress) throws FailedDBOperationException {
    if (allowedUsers.contains(accesserGuid)) {
      return true;
    } else if (allowedUsers.contains(EVERYONE)) {
      return true;
    } else {
      // see if allowed users (the list of guids and group guids that is in the ACL) intersects with the groups that this
      // guid is a member of (which is stored with this guid)
      return !Sets.intersection(allowedUsers, NSGroupAccess.lookupGroups(accesserGuid, activeReplica, lnsAddress)).isEmpty();
    }
  }

  public static boolean fieldAccessibleByEveryone(MetaDataTypeName access, String guid, String field, GnsReconfigurable activeReplica) throws FailedDBOperationException {
    try {
      return NSFieldMetaData.lookupOnThisNameServer(access, guid, field, activeReplica).contains(EVERYONE)
              || NSFieldMetaData.lookupOnThisNameServer(access, guid, ALLFIELDS, activeReplica).contains(EVERYONE);
    } catch (FieldNotFoundException e) {
      // This is actually a normal result.. so no warning here.
      return false;
    } catch (RecordNotFoundException e) {
      GNS.getLogger().warning("User " + guid + " access problem for " + field + "'s " + access.toString() + " field: " + e);
      return false;
    }
  }
}
