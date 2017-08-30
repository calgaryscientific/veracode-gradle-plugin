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

import groovy.transform.CompileStatic

@CompileStatic
class VeracodeBeginPreScanSandboxTask extends VeracodeTask {
	static final String NAME = 'veracodeSandboxBeginPreScan'
	private String app_id
	private String sandbox_id

	VeracodeBeginPreScanSandboxTask() {
		group = 'Veracode Sandbox'
		description = 'Begin Veracode pre-scan for the given application ID and sanbox ID'
		requiredArguments << 'app_id' << 'sandbox_id'
		if (project.hasProperty("app_id")) {
			app_id = project.findProperty("app_id")
			sandbox_id = project.findProperty("sandbox_id")
			defaultOutputFile = new File("${project.buildDir}/veracode", "sandbox-begin-pre-scan.xml")
		}
	}

	void run() {
		String file = 'sandbox-begin-pre-scan.xml'
		Node xml = writeXml(
			file,
			uploadAPI().beginPreScan(app_id, sandbox_id)
		)
		printf "app_id=%-10s sandbox_id=%-10s build_id=%-10s version=\"%s\" status=\"%s\"\n",
				xml.attribute('app_id'),
				xml.attribute('sandbox_id'),
				xml.attribute('build_id'),
				getNode(xml, 'build').attribute('version'),
				getNode(xml, 'build', 'analysis_unit').attribute('status')
	}
}
