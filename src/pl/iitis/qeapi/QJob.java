/*
 * Jobs.java
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

import java.util.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A job submitted to and then returned from the server.
 * 
 * @author Artur Rataj
 */
public class QJob extends AbstractQJob {
    @JsonProperty("qasms") Qasm[] sources;
    @JsonProperty("status") String status;
    @JsonProperty("usedCredits") int usedCredits;
    @JsonProperty("creationDate") Date creationDate;
    @JsonProperty("deleted") boolean deleted;
    @JsonProperty("id") String id;
    @JsonProperty("userId") String userId;
    @JsonProperty("calibration") Calibration calibration;
    
    @Override
    public String toString() {
        try {
            return (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unexpected: " + e.getMessage());
        }
    }
}
