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

class VeracodeBeginScanSandboxTest extends TestCommonSetup {
    File buildInfoFile = getResource('buildinfo-1.4.xml')
    File preScanResultsFile = getResource('prescanresults-1.4.xml')

    def 'Test veracodeSandboxBeginScan Task'() {
        given:
        def os = mockSystemOut()
        def task = taskSetup('veracodeSandboxBeginScan')
        task.project.ext.app_id = "123"
        task.project.ext.sandbox_id = "456"
        task.project.veracodeSetup.moduleWhitelist = ['class1.jar', 'class2.jar', 'class3.jar']

        when:
        task.run()
        def is = getSystemOut(os)
        restoreStdout()

        then:
        1 * task.veracodeAPI.getPreScanResultsSandbox(_) >> {
            return new String(preScanResultsFile.readBytes())
        }

        then:
        1 * task.veracodeAPI.beginScanSandbox(_) >> {
            return new String(buildInfoFile.readBytes())
        }

        // Get Pre-Scan results output
        assert is.readLine() == 'id=4 name="goodLib.jar" status="Supporting Files Compiled without Debug Symbols - X Files, PDB Files Missing - X Files"'
        assert is.readLine() == 'id=5 name="class1.jar" status="OK"'
        assert is.readLine() == 'id=6 name="badLib.dll" status="(Fatal)PDB Files Missing - 1 File"'
        assert is.readLine() == 'id=7 name="class2.jar" status="OK"'

        // Begin Scan output
        assert is.readLine() == 'Selecting module: 5: class1.jar - OK'
        assert is.readLine() == 'Selecting module: 7: class2.jar - OK'
        assert is.readLine() == 'WARNING: Missing whitelist modules: [class3.jar]'
        assert is.readLine() == 'Module IDs: 5,7'
        assert is.readLine() == '[build]'
        assert is.readLine() == 'build_id=2'
    }

}
