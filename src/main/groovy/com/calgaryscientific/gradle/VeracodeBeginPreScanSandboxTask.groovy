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
class VeracodeBeginPreScanSandboxTask extends VeracodeTask {
    static final String NAME = 'veracodeSandboxBeginPreScan'

    VeracodeBeginPreScanSandboxTask() {
        group = 'Veracode Sandbox'
        description = "Begin a Veracode Pre-Scan for the given 'app_id' and 'sandbox_id'"
        requiredArguments << 'app_id' << 'sandbox_id'
        app_id = project.findProperty("app_id")
        sandbox_id = project.findProperty("sandbox_id")
    }

    File getOutputFile() {
        VeracodeBuildInfo.getSandboxFile("${project.buildDir}/veracode", app_id, sandbox_id, null)
    }

    void run() {
        Node xml = XMLIO.writeXml(getOutputFile(), veracodeAPI.beginPreScanSandbox())
        VeracodeBuildInfo.printBuildInfo(xml)
        printf "report file: %s\n", getOutputFile()
    }
}
