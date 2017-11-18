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

import org.gradle.api.GradleException

class VeracodeWorkflowTest extends TestCommonSetup {
    // Status after just creating a new build
    File buildInfoFileIncomplete = getResource('buildinfo-1.4-incomplete.xml')
    // Status after Scan submitted
    File buildInfoFile = getResource('buildinfo-1.4.xml')
    // Status after pre scan is completed
    File buildInfoFilePreScanSuccess = getResource('buildinfo-1.4-preScanSuccess.xml')
    // Status after scan completed
    File buildInfoFileResultsReady = getResource('buildinfo-1.4-complete.xml')

    File filelistFile = getResource('filelist-1.1.xml')
    File preScanResultsFile = getResource('prescanresults-1.4.xml')

    String errorXMLResponse = '''<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<error>Veracode API Error</error>
'''

    def 'Test veracodeWorkflow Task'() {
        given:
        def task = taskSetup('veracodeWorkflow')

        task.app_id = "123"
        task.build_version = "new-build"
        task.maxUploadAttempts = "1"
        task.waitTimeBetweenAttempts = "0"
        task.delete = "false"
        task.ignoreFailure = "false"
        task.project.veracodeSetup.filesToUpload = task.project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        task.project.veracodeSetup.moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfo(_) >> {
            return new String(buildInfoFileResultsReady.readBytes())
        }

        then:
        1 * task.veracodeAPI.createBuild('new-build') >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * task.veracodeAPI.uploadFile(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginPreScan() >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * task.veracodeAPI.getPreScanResults(_) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginScan(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test veracodeWorkflow Task failure'() {
        given:
        def task = taskSetup('veracodeWorkflow')
        task.ignoreFailure = "false"

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfo(_) >> {
            return errorXMLResponse
        }
        def e = thrown(GradleException)
        e.toString().contains("ERROR: Veracode API Error")
    }

    def 'Test veracodeWorkflow ignore Task failure'() {
        given:
        def os = mockSystemOut()
        def task = taskSetup('veracodeWorkflow')
        task.ignoreFailure = "true"

        when:
        task.run()
        def is = getSystemOut(os)
        restoreStdout()

        then:
        1 * task.veracodeAPI.getBuildInfo(_) >> {
            return errorXMLResponse
        }
        assert is.readLine() =~ "ERROR: Veracode API Error"
    }
}
