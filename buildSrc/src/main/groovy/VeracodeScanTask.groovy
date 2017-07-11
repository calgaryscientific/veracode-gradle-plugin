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

class VeracodeScanTask extends VeracodeTask {
    static final String NAME = 'veracodeScan'

    VeracodeScanTask() {
        description = 'Starts a Veracode scan for the application id passed in'
        requiredArguments << 'app_id'
    }

    void run() {
        def moduleIds = []
        def whiteList = readListFromFile(new File("src/apps/${project.app_id}/modules-whitelist.txt"))
        readXml('build/pre-scan-results.xml').each() { module ->
            if (whiteList.contains(module.@name)) {
                moduleIds << module.@id
            }
        }
        println "Modules in whitelist: ${whiteList.size()}"
        println "Modules selected: ${moduleIds.size()}"
        if (whiteList.size() != moduleIds.size()) {
            println 'WARNING: Not all the files in whitelist are being scanned. Some modules no longer exist? Manual whitelist maintenance should be performed.'
        }

        writeXml('build/scan.xml', loginUpdate().beginScan(project.app_id, moduleIds.join(","), 'false'))
    }
}
