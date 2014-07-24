/*
 * Copyright (C) 2014
 * University of Massachusetts
 * All Rights Reserved 
 *
 * Initial developer(s): Westy.
 */
package edu.umass.cs.gns.clientsupport;

import edu.umass.cs.gns.util.ResultValue;
import edu.umass.cs.gns.util.ValuesMap;
import edu.umass.cs.gns.util.NSResponseCode;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Either a ValuesMap or an Error. Also
 * has some instrumentation for round trip times and what server responded.
 * 
 * @author westy
 */
public class QueryResult implements Serializable{

  /**
   * Set if the response is not an error.
   */
  private ValuesMap valuesMap;
  /**
   * Set if the response is an error.
   */
  private NSResponseCode errorCode;
  /** 
   * Instrumentation - records the time between the LNS sending the request to the NS and the return message.
   */
  private long roundTripTime; // how long this query took
  /**
   * Instrumentation - what nameserver responded to this query
   */
  private int responder;

  /**
   * Creates a "normal" (non-error) QueryResult.
   * 
   * @param valuesMap 
   */
  public QueryResult(ValuesMap valuesMap, int responder) {
    this.valuesMap = valuesMap;
    this.responder = responder;
  }

  /**
   * Creates an error QueryResult.
   * 
   * @param errorCode 
   */
  public QueryResult(NSResponseCode errorCode, int responder) {
    this.errorCode = errorCode;
    this.responder = responder;
  }

  /**
   * Gets the ValuesMap of this QueryResult.
   * 
   * @return 
   */
  public ValuesMap getValuesMap() {
    return valuesMap;
  }

  /**
   * Gets the ValuesMap, but scrubs any internal fields first.
   * @return 
   */
  public ValuesMap getValuesMapSansInternalFields() {
    ValuesMap copy = new ValuesMap(valuesMap);
    removeInternalFields(copy);
    return copy;
  }

  /**
   * Gets one ResultValue from the ValuesMap.
   * 
   * @param key
   * @return 
   */
  public ResultValue get(String key) {
    if (valuesMap != null) {
      return valuesMap.getAsArray(key);
    } else {
      return null;
    }
  }

  /**
   * Remove any keys / value pairs used internally by the GNS.
   * 
   * @param valuesMap
   * @return 
   */
  private static ValuesMap removeInternalFields(ValuesMap valuesMap) {
    Iterator<String> keyIter = valuesMap.keys();
    //Iterator<String> keyIter = newContent.keys();
    while (keyIter.hasNext()) {
      String key = keyIter.next();
      if (InternalField.isInternalField(key)) {
        keyIter.remove();
        //valuesMap.remove(key);
      }
    }

    return valuesMap;
  }

  public NSResponseCode getErrorCode() {
    return errorCode;
  }

  /**
   * Does this QueryResult represent an error result.
   * 
   * @return 
   */
  public boolean isError() {
    return this.errorCode != null;
  }

  /** 
   * Instrumentation - holds the time between the LNS sending the request to the NS and the return message
   * being received.
   */
  public long getRoundTripTime() {
    return roundTripTime;
  }

  /** 
   * Instrumentation - holds the time between the LNS sending the request to the NS and the return message
   * being received.
   */
  public void setRoundTripTime(long roundTripTime) {
    this.roundTripTime = roundTripTime;
  }

  public int getResponder() {
    return responder;
  }

  @Override
  public String toString() {
    return "QueryResult{" + "valuesMap=" + valuesMap + ", errorCode=" + errorCode + ", roundTripTime=" + roundTripTime + ", responder=" + responder + '}';
  }

}
