/*
 * Main.java
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
import java.io.*;

import com.martiansoftware.jsap.*;

import pl.gliwice.iitis.hedgeelleth.compiler.util.CompilerUtils;

/**
 * CLI.
 * 
 * @author Artur Rataj
 */
public class Main {
    /**
     * Reads a user config file, initialized the options with possible
     * defaults, these are overridable by respective CLI options.
     * 
     * @param options options
     */
    protected static void readUserFile(Options options) throws IOException {
        try(Scanner sc = CompilerUtils.newScanner(new File(options.userFile))) {
            int lineCount = 1;
            try {
                while(sc.hasNextLine()) {
                    String line = sc.nextLine();
                    int pos = line.indexOf('=');
                    if(pos == -1)
                        throw new IOException("malformed line: no `=' found");
                    String key = line.substring(0, pos).trim();
                    String value = line.substring(pos + 1).trim();
                    if(key.isEmpty())
                        throw new IOException("empty key");
                    if(value.isEmpty())
                        throw new IOException("empty value");
                    switch(key) {
                        case "token":
                            options.apiToken = value;
                            break;
                            
                        case "device":
                            options.device = value;
                            break;
                            
                        case "maxCredits":
                            try {
                                options.maxCredits = Integer.parseInt(value);
                            } catch(NumberFormatException e) {
                                throw new IOException("expected integer value, found " + value);
                            }
                            break;
                            
                        case "shots":
                            try {
                                options.shots = Integer.parseInt(value);
                            } catch(NumberFormatException e) {
                                throw new IOException("expected integer value, found " + value);
                            }
                            break;
                            
                        default:
                            throw new IOException("unknown value " + value);
                    }
                    ++lineCount;
                }
            } catch(IOException e) {
                throw new IOException(options.userFile + ":" + lineCount + ": " + e.getMessage());
            }
        }
    }
    /**
     * Parses CLI options, prints CLI help if a respective option is given.
     * 
     * @param args CLI args
     * @return options or null if parsing was not possible
     */
    protected static Options parseOptions(String[] args) {
        final String HELP = "help";
        final String VERBOSE = "verbose";
        final String USER_FILE = "user config file";
        final String SHOTS = "number of shots";
        final String LIST_DEVICES = "list devices";
        final String DEVICE = "choose a device";
        final String MAX_CREDITS = "maximum number of credits";
        final String INPUT_FILES = "input files";
        
        final String DEFAULT_USER_FILE =
                System.getProperty("user.home") + File.separatorChar + ".jqeAssembler";
        final int DEFAULT_NUM_SHOTS = 1024;
        final String DEFAULT_DEVICE = "ibmqx_qasm_simulator";
        final int DEFAULT_MAX_CREDITS = 3;
        
        try {
            //
            // registering
            //
            Options options = new Options();
            JSAP jsap = new JSAP();
            // -h --help
            Switch helpParam = new Switch(HELP).
                setShortFlag('h').
                setLongFlag("help");
            helpParam.setHelp("show help and exit");
            jsap.registerParameter(helpParam);
            // -v --verbose
            Switch verboseParam = new Switch(VERBOSE).
                setShortFlag('v').
                setLongFlag("verbose");
            verboseParam.setHelp("be verbose");
            jsap.registerParameter(verboseParam);
            // -u --user
            FlaggedOption userParam = new FlaggedOption(USER_FILE)
                                    .setStringParser(JSAP.STRING_PARSER)
                                    .setDefault(DEFAULT_USER_FILE)
                                    .setRequired(false) 
                                    .setShortFlag('u') 
                                    .setLongFlag("user");
            userParam.setHelp("select a user config file containing the api token and overridable defaults");
            jsap.registerParameter(userParam);
            // -s --shots
            FlaggedOption shotsParam = new FlaggedOption(SHOTS)
                                    .setStringParser(JSAP.INTEGER_PARSER)
                                    .setRequired(false) 
                                    .setShortFlag('s') 
                                    .setLongFlag("shots");
            shotsParam.setHelp("number of shots (default: " + DEFAULT_NUM_SHOTS + ")");
            shotsParam.setCategory("MODEL");
            jsap.registerParameter(shotsParam);
            // -l --list-devices
            Switch listDevicesParam = new Switch(LIST_DEVICES).
                setShortFlag('l').
                setLongFlag("list-devices");
            listDevicesParam.setHelp("list devices and exit");
            listDevicesParam.setCategory("SERVER");
            jsap.registerParameter(listDevicesParam);
            // -d --device
            FlaggedOption deviceParam = new FlaggedOption(DEVICE)
                                    .setStringParser(JSAP.STRING_PARSER)
                                    .setRequired(false) 
                                    .setShortFlag('d') 
                                    .setLongFlag("device");
            deviceParam.setHelp("select a device (a backend) (default: " + DEFAULT_DEVICE + ")");
            deviceParam.setCategory("SERVER");
            jsap.registerParameter(deviceParam);
            // -c --max-credits
            FlaggedOption maxCreditsParam = new FlaggedOption(MAX_CREDITS)
                                    .setStringParser(JSAP.INTEGER_PARSER)
                                    .setRequired(false) 
                                    .setShortFlag('c') 
                                    .setLongFlag("max-credits");
            maxCreditsParam.setHelp("maximum number of credits to spend (default: " + DEFAULT_MAX_CREDITS + ")");
            maxCreditsParam.setCategory("SERVER");
            jsap.registerParameter(maxCreditsParam);
            // input files
            UnflaggedOption inputFilesParam = new UnflaggedOption(INPUT_FILES)
                                    .setStringParser(JSAP.STRING_PARSER)
                                    .setRequired(false)
                                    .setGreedy(true);
            inputFilesParam.setCategory("SOURCE");
            jsap.registerParameter(inputFilesParam);
            inputFilesParam.setHelp("qasm source files, each is a separate task in the job to send");
            //
            // parsing
            //
            JSAPResult config = jsap.parse(args);
            if(!config.success()) {
                Iterator<String> it = (Iterator<String>)config.getErrorMessageIterator();
                StringBuilder errors = new StringBuilder();
                while(it.hasNext()) {
                    if(errors.length() != 0)
                        errors.append("; ");
                    errors.append(it.next());
                }
                throw new JSAPException(errors.toString());
            }
            options.help = config.getBoolean(HELP);
            options.verbose = config.getBoolean(VERBOSE);
            options.userFile = config.getString(USER_FILE);
            // pre--user config defaults
            options.shots = DEFAULT_NUM_SHOTS;
            options.device = DEFAULT_DEVICE;
            options.maxCredits = DEFAULT_MAX_CREDITS;
            // read user config defaults
            if(options.userFile != null)
                readUserFile(options);
            // override the defaults
            if(config.contains(SHOTS))
                options.shots = config.getInt(SHOTS);
            options.listDevices = config.getBoolean(LIST_DEVICES);
            if(config.contains(DEVICE))
                options.device = config.getString(DEVICE);
            if(config.contains(MAX_CREDITS))
                options.maxCredits = config.getInt(MAX_CREDITS);
            options.sourceFilenames = CompilerUtils.expandFiles(
                    Arrays.asList(config.getStringArray(INPUT_FILES)));
            //
            //
            //
            if(options.help) {
                System.out.println("jqeAssembler 0.1\n" +
                        "command line syntax:\n\n" +
                        jsap.getHelp());
            }
            return options;
        } catch(JSAPException|IOException e) {
            System.out.println("could not parse options: " + e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        String[] args_ = {
            "-v",
            //"-u", "/home/art/.jqeAssembler",
            //"-l",
            "flips.qasm"
        };
        //args = args_;
        Options options = parseOptions(args);
        if(options != null && !options.help) {
            try {
                if(options.apiToken == null)
                    throw new IOException("no api token given, cannot start session");
                if(options.verbose)
                    System.out.println("logging in");
                QeSession qe = new QeSession(options.apiToken);
                if(options.verbose)
                    System.out.println("obtaining a list of backends");
                SortedMap<String, QDevice> devices = qe.listDevices();
                if(options.listDevices) {
                    for(String device: devices.keySet()) {
                        System.out.println(device + " " +
                                CompilerUtils.indent(2, devices.get(device).toString()).trim());
                    }
                } else {
                    QDevice device = devices.get(options.device);
                    if(device == null)
                        throw new IOException("device " + options.device + " not found");
                    QDeviceRef deviceRef = device.ref();
                    List<NewQasm> sources = new LinkedList<>();
                    for(String filename : options.sourceFilenames) {
                        if(options.verbose)
                            System.out.println("reading source file " + filename);
                        String s = CompilerUtils.readFile(new File(filename));
                        if(s.trim().isEmpty())
                            throw new IOException("empty source file " + filename);
                        sources.add(new NewQasm(s));
                    }
                    if(sources.isEmpty())
                        System.out.println("no input files");
                    else {
                        if(options.verbose)
                            System.out.println("sending a new job");
                        NewQJob newJob = new NewQJob(deviceRef,
                                options.shots, options.maxCredits, sources);
                        QJob sentJob = qe.sendJob(newJob);
                        System.out.println("job id=" + sentJob.id);
                        if(options.verbose)
                            System.out.println("waiting for job completion");
                        QJob completedJob = qe.receiveJob(sentJob, 1.0, 1.0, 60.0);
                        if(options.verbose)
                            System.out.println("received computation results");
                        System.out.println(completedJob.toString());
                    }
                }
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
