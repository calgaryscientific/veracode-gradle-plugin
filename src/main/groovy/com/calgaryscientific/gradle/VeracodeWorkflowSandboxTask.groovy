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
    String ignoreFailure

    VeracodeWorkflowSandboxTask() {
        group = 'Veracode Sandbox'
        description = "Run through the Veracode Workflow for the given 'app_id' and 'sandbox_id' using 'build_version' as the build identifier"
        requiredArguments << 'app_id' << 'sandbox_id' << 'build_version'
        optionalArguments << 'maxUploadAttempts' << 'waitTimeBetweenAttempts' << 'delete' << 'ignoreFailure'
        app_id = project.findProperty("app_id")
        sandbox_id = project.findProperty("sandbox_id")
        build_version = project.findProperty("build_version")
        maxUploadAttempts = project.findProperty("maxUploadAttempts")
        waitTimeBetweenAttempts = project.findProperty("waitTimeBetweenAttempts")
        delete = project.findProperty("delete")
        ignoreFailure = project.findProperty("ignoreFailure")
    }

    Set<File> getFileSet() {
        veracodeSetup = project.findProperty("veracodeSetup") as VeracodeSetup
        return veracodeSetup.sandboxFilesToUpload
    }

    Set<String> getModuleWhitelist() {
        veracodeSetup = project.findProperty("veracodeSetup") as VeracodeSetup
        return veracodeSetup.moduleWhitelist
    }

    void run() {
        Integer maxTries = Integer.parseInt((maxUploadAttempts != null) ? maxUploadAttempts : '10')
        Integer waitTime = Integer.parseInt((waitTimeBetweenAttempts != null) ? waitTimeBetweenAttempts : '5000')
        Boolean deleteBool = false
        if (delete == "true") {
            deleteBool = true
        }
        Boolean ignoreFailureBool = false
        if (ignoreFailure == "true") {
            ignoreFailureBool = true
        }

        try {
            VeracodeWorkflow.sandboxWorkflow(veracodeAPI,
                    "${project.buildDir}/veracode",
                    app_id,
                    sandbox_id,
                    build_version,
                    getFileSet(),
                    getModuleWhitelist(),
                    maxTries,
                    waitTime,
                    deleteBool)
        } catch (Exception e) {
            if (ignoreFailureBool) {
                println e.getMessage()
            } else {
                throw e
            }
        }
    }
}
