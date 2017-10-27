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

import java.util.*;

/**
 * A new task within a new job, specified in OpenQASM and to be sent to the
 * server. As opposed to <code>Qasm</code>, it does not contain any data
 * added by the server.
 * 
 * @author Artur Rataj
 */
public class NewQasm extends AbstractQasm {
    /**
     * Creates a new task, specified using QAsm.
     * 
     * @param source source in QAsm
     */
    public NewQasm(String source) {
        this.source = source;
    }
}
