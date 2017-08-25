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

class GenerateToUploadTask extends VeracodeTask {
    static final String NAME = 'generateToUpload'

    GenerateToUploadTask() {
        description = "Grabs all jar files from the dir provided and filter it into a the 'build/to-upload' folder"
        requiredArguments << 'dir'
        optionalArguments << 'force'
    }

    void run() {
        List<File> files = []

        // prevent uploading if "to-upload" is not empty unless user force
        if (!project.hasProperty('force')) {
            File toUploadDir = new File('build/to-upload')
            if (toUploadDir.isDirectory() && toUploadDir.list().size() > 0) {
                // to-upload is not empty
                println 'Directory "build/to-upload" is not empty. Cannot proceed.'
                return
            }
        }

        // get files to process
        new File((String) project.dir).eachFileRecurse() { file ->
            if (file.isFile() && file.name.toLowerCase().endsWith(".jar")) {
                files.add file
            }
        }

        // copy to build directory
        for (File file : files) {
            project.copy {
                from file
                into 'build/to-upload'

                // strip timestamp
                rename { String fileName ->
                    fileName.replaceAll(/(.+)-\d{8}.\d{6}-\d+/, '$1')
                }
            }
        }

        println "${files.size()} file(s) copied to build/to-upload"
    }
}