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

class VeracodeSandboxGetBuildInfoTask extends VeracodeTask {
    static final String NAME = 'veracodeSandboxGetBuildInfo'

    VeracodeSandboxGetBuildInfoTask() {
        group = 'Veracode Sandbox'
        description = "Lists build information for the given applicaiton ID and sandbox ID. If no build ID is provided, the latest will be used"
        requiredArguments << 'app_id' << 'sandbox_id'
        optionalArguments << 'build_id'
    }

    void run() {
        String response
        String file
        if (project.hasProperty('build_id')) {
            response = uploadAPI().getBuildInfo(project.app_id, project.build_id, project.sandbox_id)
            file = "sandbox-build-info-${project.build_id}.xml"
        } else {
            response = uploadAPI().getBuildInfo(project.app_id, "", project.sandbox_id)
            file = 'sandbox-build-info-latest.xml'
        }
        Node buildInfo = writeXml(file, response)
        printf "app_id=%s\n", buildInfo.@app_id
        printf "sandbox_id=%s\n", buildInfo.@sandbox_id
        buildInfo.each() { build ->
            println "[build]"
            build.attributes().each() { k, v ->
                println "$k=$v"
            }
            build.children().each { child ->
                println "\t[analysis_unit]"
                child.attributes().each() { k, v ->
                    println "\t$k=$v"
                }
            }
        }
    }
}
