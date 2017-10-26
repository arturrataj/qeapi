/*
 * AbstractQasm.java
 *
 * Created on Oct 18, 2017
 *
 * Copyright (c) 2017  Artur Rataj.
 *
 * This code is distributed under the terms of the GNU Library
 * General Public License, either version 3 of the license or, at
 * your option, any later version.
 */

package pl.iitis.qeapi;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An abstract task, i.e. a part of a batch of tasks within a job.
 * 
 * @author Artur Rataj
 */
public abstract class AbstractQasm {
    @JsonProperty("qasm") String source;
}
