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

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * A new job, to be sent to the server. A job consists of a batch of tasks.
 * 
 * @author Artur Rataj
 */
public class NewQJob extends AbstractQJob {
    @JsonProperty("qasms") List<NewQasm> sources;
    
    /**
     * Creates a new job.
     * 
     * @param deviceRef device (backend) reference
     * @param shots number of shots
     * @param maxCredits execute if the cost does not exceed this value
     * @param sources one or more tasks in QAsm
     */
    public NewQJob(QDeviceRef deviceRef, int shots, int maxCredits, List<NewQasm> sources) {
        this.backend = deviceRef;
        this.shots = shots;
        this.maxCredits = maxCredits;
        this.sources = sources;
    }
}
