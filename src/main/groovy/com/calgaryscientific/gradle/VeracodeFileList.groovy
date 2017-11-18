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
class VeracodeFileList {
    static File getFile(String dir, String app_id, String build_id) {
        if (build_id) {
            return new File(dir, "filelist-${app_id}-${build_id}.xml")
        }
        return new File(dir, "filelist-${app_id}-latest.xml")
    }

    static File getSandboxFile(String dir, String app_id, String sandbox_id, build_id) {
        if (build_id) {
            return new File(dir, "filelist-${app_id}-${sandbox_id}-${build_id}.xml")
        }
        return new File(dir, "filelist-${app_id}-${sandbox_id}-latest.xml")
    }

    static void printFileList(Node xml) {
        XMLIO.getNodeList(xml, 'file').each { file ->
            printf "%s=%s\n", XMLIO.getNodeAttributes(file, 'file_name', 'file_status')
        }
    }
}
