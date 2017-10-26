/*
 * QeApiMain.java
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
import java.io.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;

/**
 * A connection layer to an IBM Quantum Experience server.
 */
public class QeSession {
    /**
     * Server address.
     */
    static final String SERVER_URL = "https://quantumexperience.ng.bluemix.net";
    /**
     * User's API token.
     */
    final protected String API_TOKEN;
    /**
     * An access token for the current session.
     */
    final protected String ACCESS_TOKEN;
    /**
     * A JSON object mapper.
     */
    final ObjectMapper OM;
    /**
     * An executor of requests.
     */
    final Executor EXECUTOR;
    /**
     * If true, some debug diagnostics are printed to stdout.
     */
    final boolean DEBUG = false;
    /**
     * Creates a session for a given user.
     * 
     * @param apiToken api token of the user
     */
    public QeSession(String apiToken) throws IOException {
        API_TOKEN = apiToken;
        OM = new ObjectMapper();
        EXECUTOR = Executor.newInstance();
        ACCESS_TOKEN = login();
    }
    /**
     * Performs a request to the server. An exception is thrown only for low-level
     * network problems.
     * 
     * @param post false for GET, true for POST
     * @param addressPostfix a string to attach to <code>SERVER_URL</code>,
     * should begin with <code>/</code>
     * @param bodyForm body form, null for none
     * @param bodyJson a body string in JSON, null for none
     * @return a response with either an error or an unprocessed content string.
     */
    protected QResponse request(boolean post, String addressPostfix,
            List<NameValuePair> bodyForm, String bodyJson) throws IOException {
        String uri = SERVER_URL + addressPostfix;
        Request request;
        if(post)
            request = Request.Post(uri);
        else
            request = Request.Get(uri);
        request = request
                .useExpectContinue()
                .version(HttpVersion.HTTP_1_1)
                .userAgent("python-requests/2.18.4")
                .addHeader("x-qx-client-application", "qiskit-api-py")
                .addHeader("Accept-Encoding", "gzip, deflate");
        if(bodyForm != null)
            request = request
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .bodyForm(bodyForm);
        if(bodyJson != null)
            request = request
                    .addHeader("Content-Type", "application/json")
                    .bodyString(bodyJson, ContentType.APPLICATION_JSON);
        Response response = EXECUTOR.execute(request);
        HttpResponse httpResponse = response.returnResponse();
        int status = httpResponse.getStatusLine().getStatusCode();
        String content;
        try(java.util.Scanner s = new java.util.Scanner(httpResponse.getEntity().getContent())) {
            content = s.useDelimiter("\\A").hasNext() ? s.next() : "";
        }
        QResponse qr;
        if(status != 200) {
            qr = OM.readValue(content, QResponse.class);
            if(DEBUG)
                System.out.println("ERROR: " + qr.error.message);
        } else {
            qr = new QResponse(content);
        }
        return qr;
    }
    /**
     * Logs in to the server. Creates an access token for the current session.
     * 
     * @param newJob a new job, with one or more tasks
     * @return an access token
     */
    protected String login() throws IOException {
        QResponse response = request(true,
                "/api/users/loginWithToken",
                Form.form()
                        .add(
                                "apiToken", API_TOKEN)
                        .build(),
                null);
        if(response.ok) {
            Map<String, String> authContents = OM.readValue(response.contentString, HashMap.class);
            return authContents.get("id");
        } else
            throw new IOException("authorization error: " + response.error.message);
    }
    /**
     * Returns the devices (backends) on the server.
     * 
     * @return a map of devices keyed with their names, null for none
     */
    public SortedMap<String, QDevice> listDevices() throws IOException {
        QResponse response = request(false,
                "/api/backends?access_token=" + ACCESS_TOKEN,
                null, null);
        if(response.ok) {
            QDevice[] devices = OM.readValue(response.contentString, QDevice[].class);
            SortedMap<String, QDevice> out = new TreeMap<>();
            for(QDevice d : devices)
                out.put(d.name, d);
            return out;
        } else
            throw new IOException("impossible to obtain a list of devices: " + response.error.message);
    }
    /**
     * Sends a new job.
     * 
     * @param newJob a new job, with one or more tasks
     * @return the jobs just sent, with new ids attached
     */
    public QJob sendJob(NewQJob newJob) throws IOException {
        QResponse response = request(true,
                "/api/Jobs?access_token=" + ACCESS_TOKEN,
                null,
                OM.writeValueAsString(newJob));
        if(response.ok)
            return OM.readValue(response.contentString, QJob.class);
        else
            throw new IOException("impossible to send jobs: " + response.error.message);
    }
    /**
     * Receives a given job from the server, with its status and possible computation results.
     * 
     * @param job a job to fetch from the server
     * @return a description of the same job, with its current status and a possible result
     */
    public QJob receiveJob(QJob job) throws IOException {
        QResponse response = request(false,
                "/api/Jobs/" + job.id + "?access_token=" + ACCESS_TOKEN,
                null, null);
        if(response.ok) {
            return OM.readValue(response.contentString, QJob.class);
        } else
            throw new IOException("impossible to receive a job: status=" +
                    response.error.status + " message=" + response.error.message);
    }
    /**
     * <p>Attempts periodically to receive resultats from a given job
     * from the server, until its status is "COMPLETED".</p>
     * 
     * <p>This method requires that <code>initialSleep + sleepIncrease*10 &gt;= 5.0</code>.</p>
     * 
     * @param job a job to fetch from the server
     * @param initialSleep a period to wait before the first attempt, [seconds]
     * @param sleepIncrease an increase of the sleep period before each successive
     * fetching attempt
     * @param maxTime fail if this waiting time is reached
     * @return a description of the same job, with its current status and a possible result
     */
    public QJob receiveJob(QJob job, double initialSleep, double sleepIncrease, double maxTime)
            throws IOException {
        if(initialSleep + sleepIncrease*10 < 5.0)
            throw new IOException("pool periods too small");
        Calendar start = Calendar.getInstance();
        double sleep = initialSleep;
        QJob serverJob;
        do {
            Calendar current = Calendar.getInstance();
            double elapsed = (current.getTimeInMillis() - start.getTimeInMillis())/1000.0;
            double sleepLimited = Math.min(sleep, maxTime - elapsed);
            if(sleepLimited > 1e-3) {
                try {
                    Thread.sleep((int)Math.round(sleepLimited*1000.0));
                } catch (InterruptedException e) {
                    throw new IOException("unexpected interrupt: " + e.getMessage());
                }
            } else if(sleepLimited < -1e-3)
                throw new IOException("timeout waiting for a completed job: " + elapsed + "sec");
            sleep += sleepIncrease;
            if(DEBUG)
                System.out.println("asking...");
            serverJob = receiveJob(job);
        } while(!serverJob.status.equals("COMPLETED"));
        return serverJob;
    }
    /**
     * A test.
     * 
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        System.out.println("logging in");
        QeSession qe = new QeSession(
                "3f5d253ea43dfd7c356d3f49c0a6fc84212012733df57e5355ec67c74c195193238007d8792e33e8ea9683f6f5d5f81e0691bf581607693a1c42c448e0d04141");
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
    }
}
