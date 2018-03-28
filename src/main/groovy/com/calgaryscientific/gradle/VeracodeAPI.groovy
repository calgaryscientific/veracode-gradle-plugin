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

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
class VeracodeAPI {
    private String app_id
    private String sandbox_id
    private VeracodeAPIWrapperFactory veracodeAPIFactory
    private static Logger log = LoggerFactory.getLogger(VeracodeAPI.class);

    VeracodeAPI(VeracodeAPIWrapperFactory veracodeAPIFactory, String app_id, String sandbox_id) {
        this.app_id = app_id
        this.sandbox_id = sandbox_id
        this.veracodeAPIFactory = veracodeAPIFactory
    }

    // upload API methods

    String beginPreScan() {
        return veracodeAPIFactory.uploadAPI().beginPreScan(app_id)
    }

    String beginPreScan(Boolean autoscan) {
        return veracodeAPIFactory.uploadAPI().beginPreScan(app_id, null, autoscan.toString())
    }

    String beginPreScanSandbox() {
        return veracodeAPIFactory.uploadAPI().beginPreScan(app_id, sandbox_id)
    }

    String beginPreScanSandbox(Boolean autoscan) {
        return veracodeAPIFactory.uploadAPI().beginPreScan(app_id, sandbox_id, autoscan.toString())
    }

    String beginScan(Set<String> moduleIds) {
        return veracodeAPIFactory.uploadAPI().beginScan(
                app_id,
                moduleIds.join(","),
                "", // scan_all_top_level_modules
                "scan_selected_modules",
                "") // scan_previously_selected_modules
    }

    String beginScanSandbox(Set<String> moduleIds) {
        return veracodeAPIFactory.uploadAPI().beginScan(
                app_id,
                moduleIds.join(","),
                "", // scan_all_top_level_modules
                "scan_selected_modules",
                "", // scan_previously_selected_modules
                sandbox_id)
    }

    String createBuild(String build_version) {
        return veracodeAPIFactory.uploadAPI().createBuild(app_id, build_version)
    }

    String createBuildSandbox(String build_version) {
        return veracodeAPIFactory.uploadAPI().createBuild(app_id, build_version,
                "", // platform
                "", // platform_id
                "", // lifecycle_stage
                "", // lifecycle_stage_id
                "", // launch_date
                sandbox_id)
    }

    String deleteBuild() {
        return veracodeAPIFactory.uploadAPI().deleteBuild(app_id)
    }

    String deleteBuildSandbox() {
        return veracodeAPIFactory.uploadAPI().deleteBuild(app_id, sandbox_id)
    }

    String getAppInfo() {
        return veracodeAPIFactory.uploadAPI().getAppInfo(app_id)
    }

    String getAppList() {
        return veracodeAPIFactory.uploadAPI().getAppList()
    }

    String getBuildList() {
        return veracodeAPIFactory.uploadAPI().getBuildList(app_id)
    }

    String getBuildListSandbox() {
        return veracodeAPIFactory.uploadAPI().getBuildList(app_id, sandbox_id)
    }

    String getBuildInfo(String build_id) {
        return veracodeAPIFactory.uploadAPI().getBuildInfo(app_id, build_id)
    }

    String getBuildInfoSandbox(String build_id) {
        return veracodeAPIFactory.uploadAPI().getBuildInfo(app_id, build_id, sandbox_id)
    }

    String getFileList(String build_id) {
        return veracodeAPIFactory.uploadAPI().getFileList(app_id, build_id)
    }

    String getFileListSandbox(String build_id) {
        return veracodeAPIFactory.uploadAPI().getFileList(app_id, build_id, sandbox_id)
    }

    String getPreScanResults(String build_id) {
        return veracodeAPIFactory.uploadAPI().getPreScanResults(app_id, build_id)
    }

    String getPreScanResultsSandbox(String build_id) {
        return veracodeAPIFactory.uploadAPI().getPreScanResults(app_id, build_id, sandbox_id)
    }

    String removeFile(String file_id) {
        return veracodeAPIFactory.uploadAPI().removeFile(app_id, file_id)
    }

    String removeFileSandbox(String file_id) {
        return veracodeAPIFactory.uploadAPI().removeFile(app_id, file_id, sandbox_id)
    }

    String uploadFile(String filePath) {
        return veracodeAPIFactory.uploadAPI().uploadFile(app_id, filePath)
    }

    String uploadFileSandbox(String filePath) {
        return veracodeAPIFactory.uploadAPI().uploadFile(app_id, filePath, sandbox_id)
    }

    // results API methods

    String detailedReport(String build_id) {
        return veracodeAPIFactory.resultsAPI().detailedReport(build_id)
    }

    byte[] detailedReportPdf(String build_id) {
        return veracodeAPIFactory.resultsAPI().detailedReportPdf(build_id)
    }

    String getCallStacks(String build_id, String flaw_id) {
        return veracodeAPIFactory.resultsAPI().getCallStacks(build_id, flaw_id)
    }

    // sandbox API methods

    String createSandbox(String sandbox_name) {
        return veracodeAPIFactory.sandboxAPI().createSandbox(app_id, sandbox_name)
    }

    String getSandboxList() {
        return veracodeAPIFactory.sandboxAPI().getSandboxList(app_id)
    }

    // mitigation API methods

    String getMitigationInfo(String build_id, String flaw_id_list) {
        return veracodeAPIFactory.mitigationAPI().getMitigationInfo(build_id, flaw_id_list)
    }

    String updateMitigationInfo(String build_id, String action, String comment, String flaw_id_list) {
        return veracodeAPIFactory.mitigationAPI().updateMitigationInfo(build_id, action, comment, flaw_id_list)
    }
}
