/*
 * Qasm.java
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
 * One of the tasks within a job, specified in QAsm and already sent to the server.
 * 
 * @author Artur Rataj
 */
public class Qasm extends AbstractQasm {
    @JsonProperty("status") String status;
    @JsonProperty("executionId") String executionId;
    @JsonProperty("result") QResult result;
}
