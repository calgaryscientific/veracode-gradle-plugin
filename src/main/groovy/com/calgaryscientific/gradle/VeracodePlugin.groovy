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
import org.gradle.api.Task

class VeracodePlugin implements Plugin<Project> {
    void apply(Project project) {
        project.extensions.create('veracodeCredentials', VeracodeCredentials)

        // App tasks
        project.task(VeracodeGetAppListTask.NAME, type: VeracodeGetAppListTask)
        project.task(VeracodeGetAppInfoTask.NAME, type: VeracodeGetAppInfoTask)
        project.task(VeracodeGetBuildListTask.NAME, type: VeracodeGetBuildListTask)
        project.task(VeracodeGetBuildInfoTask.NAME, type: VeracodeGetBuildInfoTask)
        project.task(VeracodeGetFileListTask.NAME, type: VeracodeGetFileListTask)
        project.task(VeracodeCreateBuildTask.NAME, type: VeracodeCreateBuildTask)
        project.task(VeracodeDeleteBuildTask.NAME, type: VeracodeDeleteBuildTask)
        project.task(VeracodeUploadFileTask.NAME, type: VeracodeUploadFileTask)
        project.task(VeracodeBeginPreScanTask.NAME, type: VeracodeBeginPreScanTask)
        project.task(VeracodeGetPreScanResultsTask.NAME, type: VeracodeGetPreScanResultsTask)
        project.task(VeracodeBeginScanTask.NAME, type: VeracodeBeginScanTask)
        project.task(VeracodeDetailedReportPDFTask.NAME, type: VeracodeDetailedReportPDFTask)
        Task veracodeScanResultsTask = project.task(VeracodeDetailedReportTask.NAME, type: VeracodeDetailedReportTask)
        project.task(VeracodeScanResultsInCsvTask.NAME, type: VeracodeScanResultsInCsvTask, dependsOn: veracodeScanResultsTask)
        project.task(VeracodeRemoveFileTask.NAME, type: VeracodeRemoveFileTask)
        project.task(ReportFlawsByTeamTask.NAME, type: ReportFlawsByTeamTask)
        project.task(ReportFlawsDiffTask.NAME, type: ReportFlawsDiffTask)

        // Sandbox tasks
        project.task(VeracodeSandboxGetFileListTask.NAME, type: VeracodeSandboxGetFileListTask)
        project.task(VeracodeSandboxGetBuildListTask.NAME, type: VeracodeSandboxGetBuildListTask)
        project.task(VeracodeSandboxCreateBuildTask.NAME, type: VeracodeSandboxCreateBuildTask)
        project.task(VeracodeSandboxDeleteBuildTask.NAME, type: VeracodeSandboxDeleteBuildTask)
        project.task(VeracodeSandboxBeginPreScanTask.NAME, type: VeracodeSandboxBeginPreScanTask)
        project.task(VeracodeSandboxBeginScanTask.NAME, type: VeracodeSandboxBeginScanTask)
        project.task(VeracodeSandboxGetPreScanResultsTask.NAME, type: VeracodeSandboxGetPreScanResultsTask)
        project.task(VeracodeSandboxGetBuildInfoTask.NAME, type: VeracodeSandboxGetBuildInfoTask)
        project.task(VeracodeSandboxUploadFileTask.NAME, type: VeracodeSandboxUploadFileTask)
    }
}
