/*
 * Options.java
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

import java.util.*;

/**
 * A set of compiler/runtime options.
 * 
 * @author Artur Rataj
 */
public class Options {
    /**
     * If to print help and exit.
     */
    public boolean help;
    /**
     * If to be verbose.
     */
    public boolean verbose;
    /**
     * A user config file, containing the api token and some defaults.
     */
    public String userFile;
    /**
     * User's api token.
     */
    public String apiToken;
    /**
     * If to list devices and exit.
     */
    public boolean listDevices;
    /**
     * A selected device name (backend).
     */
    public String device;
    /**
     * Maximum number of credits to spend on a job.
     */
    public int maxCredits;
    /**
     * Number of shots.
     */
    public int shots;
    /**
     * Names of input Qasm files.
     */
    List<String> sourceFilenames;
}
