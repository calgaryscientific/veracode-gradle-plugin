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

abstract class VeracodeUploadFile extends VeracodeTask {

    void upload() {
        String xmlResponse = ''
        UploadAPIWrapper update = uploadAPI()
        def error
        Integer tries = 1;
        Integer maxTries = Integer.parseInt((hasProperty('maxUploadAttempts') ? maxUploadAttempts : '10'))

        println ''
        if (tries > 1) {
            println "Attempt ${tries}"
        }
        println "Maximum upload attempts = ${maxTries} (0 means keep trying)"
        println ''
        def fileList = []
        inputDir.eachFileRecurse(FileType.FILES) { file ->
            fileList << file
        }
        // upload each file in inputDir
        for (File file : fileList) {
            boolean success = false
            while (!success && (tries <= maxTries || maxTries == 0)) {
                try {
                    println ''
                    println "Processing ${file.name}"
                    String response = uploadFile(update, file.absolutePath)
                    Node filelist = writeXml(outputFile, response)
                    filelist.each() { fileEntry ->
                        println "${fileEntry.@file_name}=${fileEntry.@file_status}"
                    }
//                    project.delete file.absolutePath
                    success = true
                } catch (Exception e) {
                    println ''
                    println e
                    println ''
                    if (tries > 1) {
                        println "Upload failing after ${tries} total attempts"
                    }
                    error = e
                    sleep(5000)
                    tries++
                    break
                }
            }
        }

        if (tries > maxTries) {
            throw error
        }
    }
}

