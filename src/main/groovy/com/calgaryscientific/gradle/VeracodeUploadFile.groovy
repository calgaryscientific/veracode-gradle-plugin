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
class VeracodeUploadFile {
    static void uploadAppFiles(VeracodeAPI veracodeAPI, File outputFile, Set<File> fileSet, Integer maxTries, Integer waitTime, Boolean delete) {
        fileSet.each { file ->
            uploadFile(file, maxTries, waitTime, veracodeAPI, outputFile, false)
            if (delete) {
                println("Deleting ${file}")
                file.delete()
            }
        }
    }

    static void uploadSandboxFiles(VeracodeAPI veracodeAPI, File outputFile, Set<File> fileSet, Integer maxTries, Integer waitTime, Boolean delete) {
        fileSet.each { file ->
            uploadFile(file, maxTries, waitTime, veracodeAPI, outputFile, true)
            if (delete) {
                println("Deleting ${file}")
                file.delete()
            }
        }
    }

    static void uploadFile(File file, Integer maxTries, Integer waitTime, VeracodeAPI veracodeAPI, File outputFile, boolean sandbox) {
        Exception error
        Integer tries = 1;
        println "Processing ${file.name}"

        boolean success = false
        while (!success && (tries <= maxTries || maxTries == 0)) {
            if (tries > 1) {
                println "Attempt ${tries}"
                println "Maximum upload attempts = ${maxTries} (0 means keep trying)"
            }
            try {
                String response
                if (sandbox) {
                    response = veracodeAPI.uploadFileSandbox(file.absolutePath)
                } else {
                    response = veracodeAPI.uploadFile(file.absolutePath)
                }
                Node xml = XMLIO.writeXml(outputFile, response)
                VeracodeFileList.printFileList(xml)
                success = true
            } catch (Exception e) {
                println ''
                println e
                println ''
                error = e
                sleep(waitTime)
                tries++
            }
        }
        if (tries > maxTries) {
            throw error
        }
    }
}
