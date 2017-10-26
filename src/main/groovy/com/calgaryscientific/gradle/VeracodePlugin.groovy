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

import org.gradle.api.Plugin
import org.gradle.api.Project

@groovy.transform.CompileStatic
class VeracodePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('veracodeSetup', VeracodeSetup)

        // Main tasks
        project.task(VeracodeWorkflowTask.NAME, type: VeracodeWorkflowTask)
        project.task(VeracodeWorkflowSandboxTask.NAME, type: VeracodeWorkflowSandboxTask)

        // App tasks
        project.task(VeracodeBeginPreScanTask.NAME, type: VeracodeBeginPreScanTask)
        project.task(VeracodeBeginScanTask.NAME, type: VeracodeBeginScanTask)
        project.task(VeracodeCreateBuildTask.NAME, type: VeracodeCreateBuildTask)
        project.task(VeracodeDeleteBuildTask.NAME, type: VeracodeDeleteBuildTask)
        project.task(VeracodeGetAppInfoTask.NAME, type: VeracodeGetAppInfoTask)
        project.task(VeracodeGetAppListTask.NAME, type: VeracodeGetAppListTask)
        project.task(VeracodeGetBuildInfoTask.NAME, type: VeracodeGetBuildInfoTask)
        project.task(VeracodeGetBuildListTask.NAME, type: VeracodeGetBuildListTask)
        project.task(VeracodeGetCallStacksTask.NAME, type: VeracodeGetCallStacksTask)
        project.task(VeracodeGetFileListTask.NAME, type: VeracodeGetFileListTask)
        project.task(VeracodeGetPreScanResultsTask.NAME, type: VeracodeGetPreScanResultsTask)
        project.task(VeracodeRemoveFileTask.NAME, type: VeracodeRemoveFileTask)
        project.task(VeracodeUploadFileTask.NAME, type: VeracodeUploadFileTask)

        // Sandbox tasks
        project.task(VeracodeBeginPreScanSandboxTask.NAME, type: VeracodeBeginPreScanSandboxTask)
        project.task(VeracodeBeginScanSandboxTask.NAME, type: VeracodeBeginScanSandboxTask)
        project.task(VeracodeCreateBuildSandboxTask.NAME, type: VeracodeCreateBuildSandboxTask)
        project.task(VeracodeCreateSandboxTask.NAME, type: VeracodeCreateSandboxTask)
        project.task(VeracodeDeleteBuildSandboxTask.NAME, type: VeracodeDeleteBuildSandboxTask)
        project.task(VeracodeGetBuildInfoSandboxTask.NAME, type: VeracodeGetBuildInfoSandboxTask)
        project.task(VeracodeGetBuildListSandboxTask.NAME, type: VeracodeGetBuildListSandboxTask)
        project.task(VeracodeGetFileListSandboxTask.NAME, type: VeracodeGetFileListSandboxTask)
        project.task(VeracodeGetPreScanResultsSandboxTask.NAME, type: VeracodeGetPreScanResultsSandboxTask)
        project.task(VeracodeGetSandboxListTask.NAME, type: VeracodeGetSandboxListTask)
        project.task(VeracodeRemoveFileSandboxTask.NAME, type: VeracodeRemoveFileSandboxTask)
        project.task(VeracodeUploadFileSandboxTask.NAME, type: VeracodeUploadFileSandboxTask)

        // Common tasks
        project.task(VeracodeDetailedReportTask.NAME, type: VeracodeDetailedReportTask)
        project.task(VeracodeDetailedReportCSVTask.NAME, type: VeracodeDetailedReportCSVTask)
        project.task(VeracodeDetailedReportPDFTask.NAME, type: VeracodeDetailedReportPDFTask)
        project.task(VeracodeGetMitigationInfoTask.NAME, type: VeracodeGetMitigationInfoTask)
        project.task(VeracodeUpdateMitigationInfoTask.NAME, type: VeracodeUpdateMitigationInfoTask)
    }
}
