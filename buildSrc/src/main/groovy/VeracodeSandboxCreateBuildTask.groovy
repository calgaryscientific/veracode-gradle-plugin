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

class VeracodeSandboxCreateBuildTask extends VeracodeTask {
    static final String NAME = 'veracodeSandboxCreateBuild'

    VeracodeSandboxCreateBuildTask() {
        group = 'Veracode Sandbox'
        description = 'Creates a new build for the given application ID and sandbox ID, using build_version as the identifier'
        requiredArguments << 'app_id' << 'sandbox_id' << 'build_version'
    }

    void run() {
        String file = 'build/sandbox-create-build-list.xml'
        Node buildInfo = writeXml(
                file,
                loginUpdate().createBuild(
                        project.app_id,
                        project.build_version,
                        "", // platform
                        "", // platform_id
                        "", // lifecycle_stage
                        "", // lifecycle_stage_id
                        "", // launch_date
                        project.sandbox_id
                )
        )
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
