/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017 Calgary Scientific Incorporated
 *
 * Copyright (c) 2013-2014 kctang
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package com.calgaryscientific.gradle

import groovy.transform.CompileStatic

@CompileStatic
class VeracodeWorkflowTask extends VeracodeTask {
    static final String NAME = 'veracodeWorkflow'
    String build_version
    String maxUploadAttempts
    String waitTimeBetweenAttempts
    String delete

    VeracodeWorkflowTask() {
        description = "Run through the Veracode Workflow for the given 'app_id' using 'build_version' as the build identifier"
        requiredArguments << 'app_id' << 'build_version'
        app_id = project.findProperty("app_id")
        build_version = project.findProperty("build_version")
    }

    VeracodeGetBuildInfoTask buildInfoTask = new VeracodeGetBuildInfoTask()
    VeracodeCreateBuildTask createBuildTask = new VeracodeCreateBuildTask()
    VeracodeUploadFileTask uploadFileTask = new VeracodeUploadFileTask()
    VeracodeBeginPreScanTask beginPreScanTask = new VeracodeBeginPreScanTask()
    VeracodeBeginScanTask beginScanTask = new VeracodeBeginScanTask()

    void run() {
        // Retrieve the properties in the run method to allow for testing
        // TODO: Look for a better way
        app_id = project.findProperty("app_id")
        build_version = project.findProperty("build_version")
        maxUploadAttempts = project.findProperty("maxUploadAttempts")
        waitTimeBetweenAttempts = project.findProperty("waitTimeBetweenAttempts")
        delete = project.findProperty("delete")
        println "[debug] app_id: ${app_id}"
        println "[debug] build_version: ${build_version}"

        // Apply default properties to tasks
        buildInfoTask.app_id = app_id
        buildInfoTask.veracodeAPI = veracodeAPI

        createBuildTask.app_id = app_id
        createBuildTask.build_version = build_version
        createBuildTask.veracodeAPI = veracodeAPI

        uploadFileTask.app_id = app_id
        uploadFileTask.maxUploadAttempts = maxUploadAttempts
        uploadFileTask.waitTimeBetweenAttempts = waitTimeBetweenAttempts
        uploadFileTask.delete = delete
        uploadFileTask.veracodeAPI = veracodeAPI

        beginPreScanTask.app_id = app_id
        beginPreScanTask.veracodeAPI = veracodeAPI

        beginScanTask.app_id = app_id
        beginScanTask.veracodeAPI = veracodeAPI

        // Get build info
        printf "[debug] getBuildInfo\n"
        buildInfoTask.run()

        // Save to variable to do the API call only once
        String buildStatus = buildInfoTask.getBuildStatus()
        printf "[debug] buildStatus: %s\n", buildStatus

        // Done previous work
        if (buildStatus == "Results Ready") {
            printf "[debug] createBuild\n"
            createBuildTask.run()
            buildStatus = buildInfoTask.getBuildStatus()
            printf "[debug] buildStatus: %s\n", buildStatus
        }

        // Clean build or with some uploaded files
        if (buildStatus == "Incomplete") {
            printf "[debug] uploadFile\n"
            uploadFileTask.run()
            printf "[debug] beginPreScan\n"
            beginPreScanTask.run()
            buildStatus = buildInfoTask.getBuildStatus()
            printf "[debug] buildStatus: %s\n", buildStatus
        }

        // Pre-Scan completed
        if (buildStatus == "Pre-Scan Success") {
            printf "[debug] begin scan\n"
            beginScanTask.run()
        }
    }
}
