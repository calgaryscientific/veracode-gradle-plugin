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

import groovy.io.FileType
import com.veracode.apiwrapper.wrappers.UploadAPIWrapper

class VeracodeUploadFileTask extends VeracodeTask {
    static final String NAME = 'veracodeUploadFile'

    VeracodeUploadFileTask() {
        description = "Uploads all files from 'build/to-upload' folder to Veracode based on the application id provided"
        requiredArguments << 'appId'
        optionalArguments << 'maxUploadAttempts'
    }

    void run() {
        String xmlResponse = ''
        UploadAPIWrapper update = uploadAPI()
        File uploadFolder = new File('build/to-upload')
        def error
        def tries = 1;
        def maxTries = Integer.parseInt((hasProperty('maxUploadAttempts') ? maxUploadAttempts : '10'))

        while (uploadFolder.list().length > 0 && (tries <= maxTries || maxTries == 0)) {
            println '\\/----------\\/----------\\/----------\\/----------\\/'
            println "Take ${tries}"
            println "Maximum upload attempts = ${maxTries} (0 means until the end of the world as we know it)"
            println ''

            def fileList = []
            uploadFolder.eachFileRecurse(FileType.FILES) { file ->
                fileList << file
            }

            // upload each file in build/to-upload
            for (File file : fileList) {
                try {
                    xmlResponse = update.uploadFile(project.appId, file.absolutePath)
                    project.delete file.absolutePath
                    println "Processed $file.name"
                } catch (Exception e) {
                    println ''
                    println e
                    println ''
                    println "Upload failing at take ${tries}"
                    println '/\\----------/\\----------/\\----------/\\----------/\\'
                    println ''

                    // write output of last upload
                    writeXml("build/upload-file.xml", xmlResponse)
                    error = e

                    sleep(5000)
                    ++tries

                    break
                }
            }
        }

        if (tries > maxTries) {
            println "Exceeded maximum upload attempt : ${maxTries}"
            throw error
        }

        println 'Check build/upload-file.xml for status of uploaded files.'
    }
}