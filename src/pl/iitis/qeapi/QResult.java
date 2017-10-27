/*
 * Result.java
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
 * A computation result of running a single task. Part of
 * <code>QResponse</code>.
 * 
 * @author Artur Rataj
 */
public class QResult {
    public static class Data {
        public static class Additional {
            @JsonProperty("seed") long seed;
        };
        @JsonProperty("creg_labels") String cregLabels;
        @JsonProperty("additionalData") Additional additionalData;
        @JsonProperty("time") double time;
        @JsonProperty("counts") Map<String, Integer> counts;
    };
    @JsonProperty("date") Date date;
    @JsonProperty("data") Data data;
}
