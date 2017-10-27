/*
 * Device.java
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;

/**
 * A device (a backend) on the server.
 * 
 * @author Artur Rataj
 */
public class QDevice {
    @JsonProperty("name") String name;
    @JsonProperty("status") String status;
    @JsonProperty("serialNumber") String serialNumber;
    @JsonProperty("description") String description;
    @JsonProperty("id") String id;
    @JsonProperty("topologyId") String topologyId;
    @JsonProperty("simulator") boolean simulator;
    @JsonProperty("nQubits") int nQubits;
    @JsonProperty("couplingMap") int[][] couplingMap;
    @JsonProperty("chipName") String chipName;
    @JsonProperty("onlineDate") Date onlineDate;
    @JsonProperty("gateSet") String gateSet;
    @JsonProperty("basisGates") String basisGates;
    @JsonProperty("version") int version;
    @JsonProperty("url") String url;

    /**
     * Returns a reference to this device.
     * 
     * @return 
     */
    QDeviceRef ref() {
        return new QDeviceRef(name);
    }
    @Override
    public String toString() {
        try {
            return (new ObjectMapper()).writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("unexpected: " + e.getMessage());
        }
    }
}
