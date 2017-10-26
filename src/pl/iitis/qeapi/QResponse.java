/*
 * Response.java
 *
 * Created on Oct 17, 2017
 *
 * Copyright (c) 2017  Artur Rataj.
 *
 * This code is distributed under the terms of the GNU Library
 * General Public License, either version 3 of the license or, at
 * your option, any later version.
 */

package pl.iitis.qeapi;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * A response from the server about the connection. Holds possible
 * error messages and a possible result.
 * 
 * @author Artur Rataj
 */
public class QResponse {
    /**
     * If null, there is no error and <code>ok</code> is true.
     */
   @JsonProperty("error") QError error;
   /**
    * If true, there is no error. If false, details can be found in <code>error</code>
    */
   boolean ok;
   /**
    * A generic content string for further processing, null if <code>ok</code> is false.
    */
   String contentString;
   
   /**
    * Creates a new response, with no error and a content string.
    * 
    * @param contentString content string for further processing
    */
   public QResponse(String contentString) {
       ok = true;
       this.contentString = contentString;
   }
   /**
    * For error messages from Json.
    */
   public QResponse() {
       ok = false;
       contentString = null;
   }
   public String toString() {
       if(ok)
           return "content = `" + contentString + "'";
       else
           return "error = `" + error.message + "'";
   }
}
