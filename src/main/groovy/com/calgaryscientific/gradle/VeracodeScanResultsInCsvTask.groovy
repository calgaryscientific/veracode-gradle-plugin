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

class VeracodeScanResultsInCsvTask extends VeracodeTask {
    static final String NAME = 'veracodeScanResultsInCsv'

    VeracodeScanResultsInCsvTask() {
        description = 'Gets the Veracode scan results based on the build id passed in and convert it to csv format'
    }

    void run() {
        File csvFile = new File('build/scan-results.csv')
        csvFile.newWriter()
        csvFile << ["Issue Id",
                    "Severity",
                    "Exploit Level",
                    "CWE Id",
                    "CWE Name",
                    "Module",
                    "Source",
                    "Source File Path",
                    "Line",
                    "Remediation Status",
                    "Mitigation Status",
                    "Mitigation Action",
                    "Mitigation Description",
                    "Mitigation Date"].join(",") + "\n"

        readXml('build/scan-results.xml').severity.each() { severity ->
            severity.category.each() { category ->
                category.cwe.each() { cwe ->
                    cwe.staticflaws.flaw.each() { flaw ->
                        def row = [flaw.@issueid,
                                   flaw.@severity,
                                   flaw.@exploitLevel,
                                   cwe.@cweid,
                                   cwe.@cwename,
                                   flaw.@module,
                                   flaw.@sourcefile,
                                   flaw.@sourcefilepath,
                                   flaw.@line,
                                   flaw.@remediation_status,
                                   flaw.@mitigation_status_desc,
                                   flaw.mitigations?.mitigation[0]?.@action,
                                   flaw.mitigations?.mitigation[0]?.@description,
                                   flaw.mitigations?.mitigation[0]?.@date]
                                .collect { '"' + (it == null ? "" : it.replace('"', '""')) + '"' }
                        def rowString = row.join(',')
                        csvFile << rowString + "\n"
                    }
                }
            }
        }
    }
}