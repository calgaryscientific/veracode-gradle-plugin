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

import com.veracode.apiwrapper.wrappers.UploadAPIWrapper
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile

class VeracodeUploadFileSandboxTask extends VeracodeUploadFile {
    static final String NAME = 'veracodeSandboxUploadFile'

    VeracodeUploadFileSandboxTask() {
        group = 'Veracode Sandbox'
        description = "Uploads all files from 'to-upload' folder to Veracode based on the given app_id and sandbox_id"
        requiredArguments << 'app_id' << 'sandbox_id'
        optionalArguments << 'maxUploadAttempts'
    }

    @OutputFile
    File outputFile = new File("${project.buildDir}/veracode", 'sandbox-upload-file-latest.xml')

    @InputDirectory
    File inputDir = new File("${project.buildDir}/veracode/to-upload")

    String uploadFile(UploadAPIWrapper api, String filepath) {
        return api.uploadFile(project.app_id, filepath, project.sandbox_id)
    }

    void run() {
        upload()
    }
}
