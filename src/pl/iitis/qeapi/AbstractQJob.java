/*
 * AbstractJob.java
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
 * An abstract job.
 * 
 * @author Artur Rataj
 */
public abstract class AbstractQJob {
    @JsonProperty("backend") QDeviceRef backend;
    @JsonProperty("shots") int shots;
    @JsonProperty("maxCredits") int maxCredits;
}
