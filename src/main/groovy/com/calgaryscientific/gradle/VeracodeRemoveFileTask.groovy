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
class VeracodeRemoveFileTask extends VeracodeTask {
    static final String NAME = 'veracodeRemoveFile'
    private String file_id

    VeracodeRemoveFileTask() {
        description = "Remove an uploaded file based on the given 'app_id' and 'file_id' combination"
        requiredArguments << 'app_id' << 'file_id'
        app_id = project.findProperty("app_id")
        file_id = project.findProperty("file_id")
    }

    File getOutputFile() {
        VeracodeFileList.getFile("${project.buildDir}/veracode", app_id, null)
    }

    void run() {
        Node xml = XMLIO.writeXml(getOutputFile(), veracodeAPI.removeFile(file_id))
        VeracodeFileList.printFileList(xml)
        printf "report file: %s\n", getOutputFile()
    }
}
