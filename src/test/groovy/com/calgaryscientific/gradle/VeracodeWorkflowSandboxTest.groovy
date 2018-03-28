/*******************************************************************************
 * MIT License
 *
 * Copyright (c) 2017-2018 Calgary Scientific Incorporated
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

class VeracodeWorkflowSandboxTest extends TestCommonSetup {
    // Status on an empty sandbox
    File buildInfoNoBuildError = getResource('buildinfo-sandbox-no-build-error.xml')
    // Status after just creating a new build
    File buildInfoFileIncomplete = getResource('buildinfo-1.4-incomplete.xml')
    // Status after Scan submitted
    File buildInfoFile = getResource('buildinfo-1.4.xml')
    // Status after pre scan is completed
    File buildInfoFilePreScanSuccess = getResource('buildinfo-1.4-preScanSuccess.xml')
    // Status after scan completed
    File buildInfoFileResultsReady = getResource('buildinfo-1.4-complete.xml')

    File buildlistFile = getResource('buildlist-1.3.xml')
    File filelistFile = getResource('filelist-1.1.xml')
    File preScanResultsFile = getResource('prescanresults-1.4.xml')
    File detailedReportFile = getResource('detailedreport-1.5.xml')

    String errorXMLResponse = '''<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<error>Veracode API Error</error>
'''

    def 'Test veracodeSandboxWorkflow Task'() {
        given:
        def task = taskSetup('veracodeSandboxWorkflow')

        task.veracodeSetup.app_id = "123"
        task.veracodeSetup.sandbox_id = "456"
        task.veracodeSetup.build_version = "new-build"
        task.veracodeSetup.maxUploadAttempts = 1
        task.veracodeSetup.waitTimeBetweenAttempts = 0
        task.veracodeSetup.deleteUploadedArtifacts = false
        task.veracodeSetup.ignoreFailure = false
        task.veracodeSetup.sandboxFilesToUpload = task.project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        task.veracodeSetup.moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfoSandbox(_) >> {
            return new String(buildInfoFileResultsReady.readBytes())
        }

        then:
        1 * task.veracodeAPI.getBuildListSandbox() >> {
            return new String(buildlistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.detailedReport(_) >> {
            return new String(detailedReportFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.createBuildSandbox('new-build') >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * task.veracodeAPI.uploadFileSandbox(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginPreScanSandbox(false) >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * task.veracodeAPI.getPreScanResultsSandbox(_) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginScanSandbox(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test veracodeSandboxWorkflow Task on empty sandbox'() {
        given:
        def task = taskSetup('veracodeSandboxWorkflow')

        task.veracodeSetup.app_id = "123"
        task.veracodeSetup.sandbox_id = "456"
        task.veracodeSetup.build_version = "new-build"
        task.veracodeSetup.maxUploadAttempts = 1
        task.veracodeSetup.waitTimeBetweenAttempts = 0
        task.veracodeSetup.deleteUploadedArtifacts = false
        task.veracodeSetup.ignoreFailure = false
        task.veracodeSetup.sandboxFilesToUpload = task.project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        task.veracodeSetup.moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfoSandbox(_) >> {
            return new String(buildInfoNoBuildError.readBytes())
        }

        then:
        1 * task.veracodeAPI.createBuildSandbox('new-build') >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * task.veracodeAPI.uploadFileSandbox(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginPreScanSandbox(false) >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * task.veracodeAPI.getPreScanResultsSandbox(_) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginScanSandbox(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test veracodeSandboxWorkflow Task failure'() {
        given:
        def task = taskSetup('veracodeSandboxWorkflow')
        task.veracodeSetup.app_id = '123'
        task.veracodeSetup.sandbox_id = '456'
        task.veracodeSetup.build_version = 'new-build'
        task.veracodeSetup.ignoreFailure = false

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfoSandbox(_) >> {
            return errorXMLResponse
        }
        def e = thrown(GradleException)
        e.toString().contains("ERROR: Veracode API Error")
    }

    def 'Test veracodeSandboxWorkflow ignore Task failure'() {
        given:
        def os = mockSystemOut()
        def task = taskSetup('veracodeSandboxWorkflow')
        task.veracodeSetup.app_id = '123'
        task.veracodeSetup.sandbox_id = '456'
        task.veracodeSetup.build_version = 'new-build'
        task.veracodeSetup.ignoreFailure = true

        when:
        task.run()
        def is = getSystemOut(os)
        restoreStdout()

        then:
        1 * task.veracodeAPI.getBuildInfoSandbox(_) >> {
            return errorXMLResponse
        }
        assert is.readLine() =~ "ERROR: Veracode API Error"
    }

    def 'Test veracodeSandboxWorkflow autoscan'() {
        given:
        def task = taskSetup('veracodeSandboxWorkflow')

        task.veracodeSetup.app_id = "123"
        task.veracodeSetup.sandbox_id = "456"
        task.veracodeSetup.build_version = "new-build"
        task.veracodeSetup.maxUploadAttempts = 1
        task.veracodeSetup.waitTimeBetweenAttempts = 0
        task.veracodeSetup.deleteUploadedArtifacts = false
        task.veracodeSetup.ignoreFailure = false
        task.veracodeSetup.sandboxFilesToUpload = task.project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        task.veracodeSetup.moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']
        task.veracodeSetup.autoscan = true

        when:
        task.run()

        then:
        1 * task.veracodeAPI.getBuildInfoSandbox(_) >> {
            return new String(buildInfoFileResultsReady.readBytes())
        }

        then:
        1 * task.veracodeAPI.getBuildListSandbox() >> {
            return new String(buildlistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.detailedReport(_) >> {
            return new String(detailedReportFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.createBuildSandbox('new-build') >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * task.veracodeAPI.uploadFileSandbox(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginPreScanSandbox(true) >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * task.veracodeAPI.getPreScanResultsSandbox(_) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginScanSandbox(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }
}
