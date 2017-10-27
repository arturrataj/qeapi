/*
 * DeviceRef.java
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
 * A reference to a server device (a backend).
 * 
 * @author Artur Rataj
 */
public class QDeviceRef {
    @JsonProperty("name") String name;
    
    /**
     * Creates a reference to a device of a given name.
     * 
     * @param name device name
     */
    public QDeviceRef(String name) {
        this.name = name;
    }
    public QDeviceRef() {
    }
}
