/*
 * Copyright (C) 2013
 * University of Massachusetts
 * All Rights Reserved 
 */
package edu.umass.cs.gns.database;

import org.json.JSONObject;

import java.util.HashMap;

/**************** FIXME All functionality of this package is provided currently by class nsdesign/recordMap/MongoRecords.java.
 * FIXME Make changes to that file until we include this package again.. **/

/**
 * A cursor that can be used to iterate through a collection of GNS served records as JSONObjects.
 * 
 * @author westy
 */
public abstract class BasicRecordCursor implements RecordCursorInterface {
  
  @Override
  public JSONObject nextJSONObject() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  @Override
  public HashMap<ColumnField, Object> nextHashMap() {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
