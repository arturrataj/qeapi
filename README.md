# SDK for IBM Q experience

This is an API client written in Java for communicating with the [IBM Quantum experience (QX)](https://quantumexperience.ng.bluemix.net/)
backends. I wrote it as I do not know Python and wanted to play a bit with the real device.
The SDK implements the basic functionality of the [QISKit](https://github.com/QISKit/qiskit-sdk-py)
API client:

* login using a key;
* get the list of devices;
* send a batch of [OpenQASM](https://github.com/QISKit/openqasm) programs to the chosen device;
* download the results.

There is also a simple CLI.

## Requirements

You will need Java 1.8, [Apache Ant](https://ant.apache.org) and a number of libraries. Optionally, you may open the project in [Netbeans](https://netbeans.org).

For convenience, an archive with the required libraries is provided [here](https://drive.google.com/open?id=0B_xKqtw0Rr_MWHFhM0I5MkZXOW8). Just unpack it together with the sources in the same directory.

### Libraries

Some jars from [Apache HTTP Client](https://hc.apache.org/httpcomponents-client-ga/)
```
../lib/httpclient/fluent-hc.jar
../lib/httpclient/httpclient.jar
../lib/httpclient/httpclient-win.jar
../lib/httpclient/httpcore.jar
```
The HTTP client requires [Apache Commons Logging](https://commons.apache.org/proper/commons-logging/)
```
../lib/commons-logging.jar
```

[Jackson Project](https://github.com/FasterXML/jackson) for handling JSON
```
../lib/jackson/jackson-annotations.jar
../lib/jackson/jackson-core.jar
../lib/jackson/jackson-databind.jar
```

### Libraries for CLI

The following libraries are required only by the class `pl.iitis.qeapi.Main`, which in turn is only needed for CLI.

[Wildcard](https://github.com/EsotericSoftware/wildcard)
```
../lib/wildcards.jar
```

A custom version of [JSAP](http://www.martiansoftware.com/jsap/). The standard JSAP will do with minimal changes.
```
../lib/jsap.jar
```

A [part](https://bitbucket.org/arataj/hedgeellethutilities) of the Hedgeelleth package.
```
../HedgeellethUtilities/dist/HedgeellethUtilities.jar
```

## How to use

It is a Netbeans 8 project, but the IDE is not required and the sources can be easily reused
elsewhere.

If you have differently names jars, you may either rename them or modify dependencies in the file `nbproject/project.properties`. Then type
```
ant jar
```
to build. After this, the directories `../lib` and `../HedgeellethUtilities` are no longer needed. To use the Bash CLI script, install it first (it just modifies ./bin/jqea):
```
./install.sh
```

### Example programmatic access

```java
System.out.println("logging in");
QeSession qe = new QeSession(
        "3f5d253ea43dfd7c356d3f4");
System.out.println("obtaining a list of backends");
SortedMap<String, QDevice> devices = qe.listDevices();
QDeviceRef deviceRef = devices.get("ibmqx_qasm_simulator").ref();
NewQasm[] sources = {
    new NewQasm(
        "include \"qelib1.inc\";\n" +
        "qreg q[2];\n" +
        "creg c[5];\n" +
        "h q[0];\n" +
        "cx q[0],q[1];\n" +
        "measure q[0] -> c[0];\n" +
        "measure q[1] -> c[1];\n"
    ),
};
System.out.println("sending a new job");
NewQJob newJob = new NewQJob(deviceRef, 256, 1, Arrays.asList(sources));
QJob sentJob = qe.sendJob(newJob);
System.out.println("id=" + sentJob.id + ", waiting for job completion");
QJob completedJob = qe.receiveJob(sentJob, 1.0, 1.0, 60.0);
System.out.println("received computation results");
System.out.println(completedJob.toString());
```

### Command line syntax

```
  [-h|--help]
        show help and exit

  [-v|--verbose]
        be verbose

  [(-u|--user) <user config file>]
        select a user config file containing the api token and overridable
        defaults (default: /home/art/.jqeAssembler)

MODEL:
  [(-s|--shots) <number of shots>]
        number of shots (default: 1024)

SERVER:
  [-l|--list-devices]
        list devices and exit
  [(-d|--device) <choose a device>]
        select a device (a backend) (default: ibmqx_qasm_simulator)
  [(-c|--max-credits) <maximum number of credits>]
        maximum number of credits to spend (default: 3)

SOURCE:
  [input files1 input files2 ... input filesN]
        qasm source files, each is a separate task in the job to send
```

### Example config file

```
token=33e8ea9683f6f5d5f81e069
maxCredits=3
shots=1024
```
