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

import groovy.json.*
import java.rmi.UnexpectedException

class ReportFlawsByTeamTask extends VeracodeTask {
    static final String NAME = 'reportFlawsByTeam'

    ReportFlawsByTeamTask() {
        description = 'Creates a report for flaws categorized to teams'
        requiredArguments << 'app_id' << 'mode'
    }

    void run() {
        def teams = new JsonSlurper().parse(new FileReader("src/apps/${project.app_id}/teams.json")).teams
        int count = 0

        readXml('build/scan-results.xml').severity.each() { severity ->
            severity.category.each() { category ->
                category.cwe.each() { cwe ->
                    cwe.staticflaws.flaw.each() { flaw ->

                        if (project.mode == 'verbose') {
                            // verbose report
                            def row = [findTeam(teams, flaw),
                                       flaw.@issueid,
                                       flaw.@severity,
                                       flaw.@exploitLevel,
                                       cwe.@cweid,
                                       cwe.@cwename,
                                       flaw.@module,
                                       flaw.@sourcefile,
                                       flaw.@sourcefilepath,
                                       flaw.@line,
                                       flaw.@remediation_status,
                                       flaw.@mitigation_status_desc,
                                       flaw.mitigations?.mitigation[0]?.@action,
                                       flaw.mitigations?.mitigation[0]?.@description,
                                       flaw.mitigations?.mitigation[0]?.@date]
                                    .collect { '"' + (it == null ? "" : it.replace('"', '""')) + '"' }
                            println row.join(',')
                            count++
                        } else if (project.mode == 'actionSummary') {
                            // only flaws that require action
                            if (!['Mitigation Proposed'].contains(flaw.@mitigation_status_desc) &&
                                    !['Fixed'].contains(flaw.@remediation_status)
                            ) {
                                def row = [findTeam(teams, flaw),
                                           flaw.@issueid,
                                           cwe.@cwename,
                                           flaw.@module,
                                           flaw.@remediation_status,
                                           flaw.@mitigation_status_desc]
                                        .collect { '"' + (it == null ? "" : it.replace('"', '""')) + '"' }
                                println row.join(',')
                                count++


                            }
                        } else if (project.mode == 'action') {
                            // only flaws that require action
                            if (!['Mitigation Proposed'].contains(flaw.@mitigation_status_desc) &&
                                    !['Fixed'].contains(flaw.@remediation_status)
                            ) {
                                def row = [findTeam(teams, flaw),
                                           flaw.@issueid,
                                           cwe.@cwename,
                                           flaw.@module,
                                           flaw.@sourcefile,
                                           flaw.@sourcefilepath,
                                           flaw.@remediation_status,
                                           flaw.@mitigation_status_desc]
                                        .collect { '"' + (it == null ? "" : it.replace('"', '""')) + '"' }
                                println row.join(',')
                                count++
                            }
                        } else {
                            println 'Unsupported mode.'
                        }

                    }
                }
            }
        }
        println "Total flaw count: $count."

    }

    String findTeam(teams, flaw) {
        // TODO: support source path exclusions? should not be part of team?

        for (def team : teams) {
            // see if flaw's module is excluded from this team
            boolean excludeFromTeam = false;

            for (def moduleExclusion : team.moduleExclusions) {
                if (flaw.@module.contains(moduleExclusion)) {
                    excludeFromTeam = true;
                    break;
                }
            }

            // this flaw's module is excluded, check next team
            if (excludeFromTeam) {
                continue;
            }

            // see if this flaw's module is assign to this team
            for (def module : team.modules) {
                if (flaw.@module.contains(module)) {
                    return team.name
                }
            }
        }

        // no match
        throw new UnexpectedException("Unable to resolve team")
    }
}
