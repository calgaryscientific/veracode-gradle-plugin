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

class VeracodeSetup {
    // Authentication
    String username
    String password
    String id
    String key

    // Output location
    String outputDir

    // App and Sandbox
    String app_id
    String sandbox_id
    String sandbox_name

    // Build Info
    String build_id
    String build_version

    // Flaw Info
    String flaw_id
    String flaw_id_list
    String action
    String comment
    String cweid

    // File Info
    String file_id

    // Upload Config
    Boolean deleteUploadedArtifacts = false
    Integer maxUploadAttempts = 10
    Integer waitTimeBetweenAttempts = 5000
    Set<File> filesToUpload
    Set<File> sandboxFilesToUpload
    Boolean autoscan = false

    // Scan Config
    Set<String> moduleWhitelist

    // Error Management
    Boolean ignoreFailure = false

    // Workflow reports
    Boolean failWorkflowTasksOnNewFlaws = false
}
