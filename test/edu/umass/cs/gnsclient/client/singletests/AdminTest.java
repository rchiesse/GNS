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
package edu.umass.cs.gnsclient.client.singletests;


import edu.umass.cs.gnsclient.client.GNSClient;
import edu.umass.cs.gnsclient.client.GNSClientCommands;
import edu.umass.cs.gnsclient.client.util.GuidEntry;
import edu.umass.cs.gnsclient.client.util.GuidUtils;
import edu.umass.cs.gnscommon.utils.RandomString;
import edu.umass.cs.gnscommon.utils.ThreadUtils;

import java.io.IOException;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Basic test for the GNS using the UniversalTcpClient.
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AdminTest {

  private static GNSClientCommands client;
  private static GuidEntry masterGuid;
	private static final String DEFAULT_ACCOUNT_ALIAS = "support@gns.name";

	private static String accountAlias = DEFAULT_ACCOUNT_ALIAS; 
																
	private static final String PASSWORD = "password";

  /*public AdminTest() {
    if (client == null) {
     try {
        client = new GNSClientCommands();
        client.setForceCoordinatedReads(true);
      } catch (IOException e) {
        fail("Exception creating client: " + e);
      }
    }
  }*/
  @BeforeClass
  public static void setupBeforeClass() throws IOException{
	  System.out.println("Starting client");

		client = new GNSClientCommands();
		// Make all the reads be coordinated
		client.setForceCoordinatedReads(true);
		// arun: connectivity check embedded in GNSClient constructor
		boolean connected = client instanceof GNSClient;
		if (connected) {
			System.out.println("Client created and connected to server.");
		}
		//
		int tries = 5;
		boolean accountCreated = false;

		
		do {
			try {
				System.out.println("Creating account guid: " + (tries - 1)
						+ " attempt remaining.");
				masterGuid = GuidUtils.lookupOrCreateAccountGuid(client,
						accountAlias, PASSWORD, true);
				accountCreated = true;
			} catch (Exception e) {
				e.printStackTrace();
				ThreadUtils.sleep((5 - tries) * 5000);
			}
		} while (!accountCreated && --tries > 0);
		if (accountCreated == false) {
			fail("Failure setting up account guid; aborting all tests.");
		}
  }
  
  @Test
	public void test_001_CreateEntity() throws Exception {
		String alias = "testGUID" + RandomString.randomString(12);
		GuidEntry guidEntry = null;
		guidEntry = client.guidCreate(masterGuid, alias);
		assertNotNull(guidEntry);
		assertEquals(alias, guidEntry.getEntityName());
	}


  @Test
  public void test_01_AdminEnter() {
    try {
      client.adminEnable();
    } catch (Exception e) {
      fail("Exception while enabling admin mode: " + e);
    }
  }

  @Test
  public void test_02_ParameterGet() {
    try {
      String result = client.parameterGet("email_verification");
      assertEquals("true", result);
    } catch (Exception e) {
      fail("Exception while enabling admin mode: " + e);
    }
  }

  @Test
  public void test_03_ParameterSet() {
    try {
      client.parameterSet("max_guids", 2000);
    } catch (Exception e) {
      fail("Exception while enabling admin mode: " + e);
    }
    try {
      String result = client.parameterGet("max_guids");
      assertEquals("2000", result);
    } catch (Exception e) {
      fail("Exception while enabling admin mode: " + e);
    }
  }
  
  @Test
  public void test_04_ParameterList() {
    try {
      String result = client.parameterList();
      assertEquals("true", result);
    } catch (Exception e) {
      fail("Exception while enabling admin mode: " + e);
    }
  }
  
  

}
