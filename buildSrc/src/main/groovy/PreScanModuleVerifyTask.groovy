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

class PreScanModuleVerifyTask extends VeracodeTask {
    static final String NAME = 'preScanModuleVerify'

    PreScanModuleVerifyTask() {
        description = 'Verifies that all jars uploaded to Veracode are categorized in the blacklists/whitelist'
        requiredArguments << 'app_id'
    }

    void run() {
        println "Verifying against modules list modules-*.txt..."
        println ""

        def blackListErr = readListFromFile(new File("src/apps/${project.app_id}/modules-blacklist-error.txt"))
        def blackListExt = readListFromFile(new File("src/apps/${project.app_id}/modules-blacklist-external.txt"))
        def blackListInt = readListFromFile(new File("src/apps/${project.app_id}/modules-blacklist-internal.txt"))
        def whiteList = readListFromFile(new File("src/apps/${project.app_id}/modules-whitelist.txt"))

        def allList = blackListErr + blackListExt + blackListInt + whiteList
        allList = allList.sort()

        println "modules-blacklist-error = ${blackListErr.size()}"
        println "modules-blacklist-external = ${blackListExt.size()}"
        println "modules-blacklist-internal = ${blackListInt.size()}"
        println "modules-whitelist = ${whiteList.size()}"
        println "all lists total = ${allList.size()}"
        println ""

        def notFoundList = new ArrayList<String>()
        def fatalErrorList = new ArrayList<String>()
        readXml('build/pre-scan-results.xml').each() { module ->
            if (!allList.contains(module.@name)) {
                notFoundList << module.@name
            }

            if (module.@has_fatal_errors.equals("true") && whiteList.contains(module.@name)) {
                fatalErrorList << module.@name
            }
        }

        if (notFoundList.isEmpty() && fatalErrorList.isEmpty()) {
            println "Prescan module verification success."
        } else {
            println "Prescan module verification failed. DO NOT PROCEED WITH VERACODE SCAN!"
            if (!notFoundList.isEmpty()) {
                println "Module not found in all list:"
                notFoundList.each { notFoundModule ->
                    println "${notFoundModule}"
                }
            }

            if (!fatalErrorList.isEmpty()) {
                println "Module in fatal error but in whitelist:"
                fatalErrorList.each { fatalErrorModule ->
                    println "${fatalErrorModule}"
                }
            }
        }
    }
}
