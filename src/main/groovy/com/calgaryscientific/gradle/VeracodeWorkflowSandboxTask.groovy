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
class VeracodeWorkflowSandboxTask extends VeracodeTask {
    static final String NAME = 'veracodeSandboxWorkflow'
    String build_version
    String maxUploadAttempts
    String waitTimeBetweenAttempts
    String delete

    VeracodeWorkflowSandboxTask() {
        group = 'Veracode Sandbox'
        description = "Run through the Veracode Workflow for the given 'app_id' and 'sandbox_id' using 'build_version' as the build identifier"
        requiredArguments << 'app_id' << 'sandbox_id' << 'build_version'
        optionalArguments << 'maxUploadAttempts' << 'waitTimeBetweenAttempts' << 'delete'
        app_id = project.findProperty("app_id")
        sandbox_id = project.findProperty("sandbox_id")
        build_version = project.findProperty("build_version")
    }

    VeracodeGetBuildInfoSandboxTask buildInfoSandboxTask = new VeracodeGetBuildInfoSandboxTask()
    VeracodeCreateBuildSandboxTask createBuildSandboxTask = new VeracodeCreateBuildSandboxTask()
    VeracodeUploadFileSandboxTask uploadFileSandboxTask = new VeracodeUploadFileSandboxTask()
    VeracodeBeginPreScanSandboxTask beginPreScanSandboxTask = new VeracodeBeginPreScanSandboxTask()
    VeracodeBeginScanSandboxTask beginScanSandboxTask = new VeracodeBeginScanSandboxTask()

    void run() {
        // Retrieve the properties in the run method to allow for testing
        // TODO: Look for a better way
        app_id = project.findProperty("app_id")
        sandbox_id = project.findProperty("sandbox_id")
        build_version = project.findProperty("build_version")
        maxUploadAttempts = project.findProperty("maxUploadAttempts")
        waitTimeBetweenAttempts = project.findProperty("waitTimeBetweenAttempts")
        delete = project.findProperty("delete")
        println "[debug] app_id: ${app_id}"
        println "[debug] sandbox_id: ${sandbox_id}"
        println "[debug] build_version: ${build_version}"

        // Apply default properties to tasks
        buildInfoSandboxTask.app_id = app_id
        buildInfoSandboxTask.sandbox_id = sandbox_id
        buildInfoSandboxTask.veracodeAPI = veracodeAPI

        createBuildSandboxTask.app_id = app_id
        createBuildSandboxTask.sandbox_id = sandbox_id
        createBuildSandboxTask.build_version = build_version
        createBuildSandboxTask.veracodeAPI = veracodeAPI

        uploadFileSandboxTask.app_id = app_id
        uploadFileSandboxTask.sandbox_id = sandbox_id
        uploadFileSandboxTask.maxUploadAttempts = maxUploadAttempts
        uploadFileSandboxTask.waitTimeBetweenAttempts = waitTimeBetweenAttempts
        uploadFileSandboxTask.delete = delete
        uploadFileSandboxTask.veracodeAPI = veracodeAPI

        beginPreScanSandboxTask.app_id = app_id
        beginPreScanSandboxTask.sandbox_id = sandbox_id
        beginPreScanSandboxTask.veracodeAPI = veracodeAPI

        beginScanSandboxTask.app_id = app_id
        beginScanSandboxTask.sandbox_id = sandbox_id
        beginScanSandboxTask.veracodeAPI = veracodeAPI

        // Get build info
        printf "[debug] getBuildInfo\n"
        buildInfoSandboxTask.run()

        // Save to variable to do the API call only once
        String buildStatus = buildInfoSandboxTask.getBuildStatus()
        printf "[debug] buildStatus: %s\n", buildStatus

        // Done previous work
        if (buildStatus == "Results Ready") {
            printf "[debug] createBuild\n"
            createBuildSandboxTask.run()
            buildStatus = buildInfoSandboxTask.getBuildStatus()
            printf "[debug] buildStatus: %s\n", buildStatus
        }

        // Clean build or with some uploaded files
        if (buildStatus == "Incomplete") {
            printf "[debug] uploadFile\n"
            uploadFileSandboxTask.run()
            printf "[debug] beginPreScan\n"
            beginPreScanSandboxTask.run()
            buildStatus = buildInfoSandboxTask.getBuildStatus()
            printf "[debug] buildStatus: %s\n", buildStatus
        }

        // Pre-Scan completed
        if (buildStatus == "Pre-Scan Success") {
            printf "[debug] begin scan\n"
            beginScanSandboxTask.run()
        }
    }
}
