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

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

class VeracodeWorkflowDOTest extends TestCommonSetup {
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

    def 'Test appWorkflow when previous build has results ready'() {
        given:
        Project project = new ProjectBuilder().build()
        String app_id = "123"
        String build_version = "new-build"
        Integer maxUploadAttempts = 1
        Integer waitTimeBetweenAttempts = 0
        Set<File> fileSet = project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        Set<String> moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']
        Boolean delete = false
        VeracodeAPI veracodeAPIMock = Mock(VeracodeAPI, constructorArgs: [null, null, null])

        when:
        VeracodeWorkflow.appWorkflow(veracodeAPIMock,
                testProjectDir.root.toString(),
                app_id,
                build_version,
                fileSet,
                moduleWhitelist,
                maxUploadAttempts,
                waitTimeBetweenAttempts,
                delete)

        then:
        1 * veracodeAPIMock.getBuildInfo(null) >> {
            return new String(buildInfoFileResultsReady.readBytes())
        }

        then:
        1 * veracodeAPIMock.createBuild(build_version) >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * veracodeAPIMock.uploadFile(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * veracodeAPIMock.beginPreScan() >> {
            // status=Submitted to Engine
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test appWorkflow when previous build has pre-scan results ready'() {
        given:
        Project project = new ProjectBuilder().build()
        String app_id = "123"
        String build_version = "new-build"
        Integer maxUploadAttempts = 1
        Integer waitTimeBetweenAttempts = 0
        Set<File> fileSet = project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        Set<String> moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']
        Boolean delete = false
        VeracodeAPI veracodeAPIMock = Mock(VeracodeAPI, constructorArgs: [null, null, null])

        when:
        VeracodeWorkflow.appWorkflow(veracodeAPIMock,
                testProjectDir.root.toString(),
                app_id,
                build_version,
                fileSet,
                moduleWhitelist,
                maxUploadAttempts,
                waitTimeBetweenAttempts,
                delete)

        then:
        1 * veracodeAPIMock.getBuildInfo(null) >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * veracodeAPIMock.getPreScanResults(null) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * veracodeAPIMock.beginScan(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test sandboxWorkflow when previous build has results ready'() {
        given:
        Project project = new ProjectBuilder().build()
        String app_id = "123"
        String sandbox_id = "123"
        String build_version = "new-build"
        Integer maxUploadAttempts = 1
        Integer waitTimeBetweenAttempts = 0
        Set<File> fileSet = project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        Set<String> moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']
        Boolean delete = false
        VeracodeAPI veracodeAPIMock = Mock(VeracodeAPI, constructorArgs: [null, null, null])

        when:
        VeracodeWorkflow.sandboxWorkflow(veracodeAPIMock,
                testProjectDir.root.toString(),
                app_id,
                sandbox_id,
                build_version,
                fileSet,
                moduleWhitelist,
                maxUploadAttempts,
                waitTimeBetweenAttempts,
                delete)

        then:
        1 * veracodeAPIMock.getBuildInfoSandbox(null) >> {
            return new String(buildInfoFileResultsReady.readBytes())
        }

        then:
        1 * veracodeAPIMock.createBuildSandbox(build_version) >> {
            return new String(buildInfoFileIncomplete.readBytes())
        }

        then:
        1 * veracodeAPIMock.uploadFileSandbox(_) >> {
            return new String(filelistFile.readBytes())
        }

        then:
        1 * veracodeAPIMock.beginPreScanSandbox() >> {
            // status=Submitted to Engine
            return new String(buildInfoFile.readBytes())
        }
    }

    def 'Test sandboxWorkflow when previous build has pre-scan results ready'() {
        given:
        Project project = new ProjectBuilder().build()
        String app_id = "123"
        String sandbox_id = "123"
        String build_version = "new-build"
        Integer maxUploadAttempts = 1
        Integer waitTimeBetweenAttempts = 0
        Set<File> fileSet = project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()
        Set<String> moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']
        Boolean delete = false
        VeracodeAPI veracodeAPIMock = Mock(VeracodeAPI, constructorArgs: [null, null, null])

        when:
        VeracodeWorkflow.sandboxWorkflow(veracodeAPIMock,
                testProjectDir.root.toString(),
                app_id,
                sandbox_id,
                build_version,
                fileSet,
                moduleWhitelist,
                maxUploadAttempts,
                waitTimeBetweenAttempts,
                delete)

        then:
        1 * veracodeAPIMock.getBuildInfoSandbox(null) >> {
            return new String(buildInfoFilePreScanSuccess.readBytes())
        }

        then:
        1 * veracodeAPIMock.getPreScanResultsSandbox(null) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * veracodeAPIMock.beginScanSandbox(_) >> {
            return new String(buildInfoFile.readBytes())
        }
    }
}
