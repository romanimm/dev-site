/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.gwt.site.uploader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.repackaged.com.google.common.io.Files;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;
import com.google.appengine.tools.remoteapi.RemoteApiOptions;
import com.google.common.base.Charsets;

public class SaveCredentials {

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void run() throws IOException {
        File f = new File("credentials");
        if (!f.exists()) {
            p("Expected a file named 'credentials' to exist.");
            p("It should be in gwt-site-uploader.");
            p("Please make sure you're in the right directory and touch the file if it's not there.");
            System.exit(1);
        }
        if (!f.canWrite()) {
            p("Can't write to credentials.");
            System.exit(1);
        }

        p("Please enter your dev.arcbees.com credentials.");

        String appId;
        String email;
        String password;

        if (System.console() != null) {
            appId = System.console().readLine("Application ID: ");
            email = System.console().readLine("Email: ");
            password = new String(System.console().readPassword("Password: "));
        } else  {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String[] strings = new String(reader.readLine().toCharArray()).split("#", 3);
            appId = strings[0];
            email = strings[1];
            password = strings[2];
        }

        RemoteApiOptions options =
                new RemoteApiOptions().server(appId + ".appspot.com", 443).credentials(email, password);
        RemoteApiInstaller installer = new RemoteApiInstaller();
        installer.install(options);
        // test the connection
        DatastoreServiceFactory.getDatastoreService().allocateIds("foo", 1);

        String creds = installer.serializeCredentials();
        Files.write(creds, f, Charsets.UTF_8);
        System.out.println("Credentials saved.");

        installer.uninstall();
    }

    private static void p(String s) {
        System.out.println(s);
    }
}
