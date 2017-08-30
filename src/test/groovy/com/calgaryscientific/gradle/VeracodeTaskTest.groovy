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

import org.gradle.api.file.FileTree
import org.gradle.testkit.runner.UnexpectedBuildFailure
import spock.lang.Specification
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import com.calgaryscientific.gradle.VeracodeTask
import org.gradle.testfixtures.ProjectBuilder

class VeracodeTaskTest extends Specification {
    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile
    Boolean debug = true

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def GradleBuild(String... tasks) {
        GradleRunner.create()
                .withDebug(debug)
                .withProjectDir(testProjectDir.root)
                .withPluginClasspath()
                .withArguments(tasks)
                .build()
    }

    def 'Test Task Existence'() {
        when:
        def project = new ProjectBuilder().build()
        project.plugins.apply('com.calgaryscientific.gradle.veracode')

        then:
        project.tasks.getByName("veracodeGetAppList") != null
        project.tasks.getByName("veracodeGetBuildList") != null
        project.tasks.getByName("veracodeDetailedReport") != null
    }

    def 'Test build failure when arguments are missing'() {
        given:
        buildFile << """
            plugins {
                id 'com.calgaryscientific.gradle.veracode'
            }
            task getBuildList (type: com.calgaryscientific.gradle.VeracodeGetBuildListTask) {
            }
        """

        when:
        def result = GradleBuild('getBuildList')

        then:
        def e = thrown(UnexpectedBuildFailure)
        e.toString().contains("Missing required arguments")
    }

    def 'Test error message on missing parameters'() {
        when:
        List<String> requiredArgs = ["app_id"]
        List<String> optionalArgs = ["build_id"]
        String correctUsage = VeracodeTask.correctUsage('VeracodeGetBuildList', requiredArgs, optionalArgs)

        then:
        correctUsage == "Missing required arguments: gradle VeracodeGetBuildList -Papp_id=123 [-Pbuild_id=123]"
    }

    def 'Test veracodeSetup usage'() {
        given:
        buildFile << """
            plugins {
                id 'com.calgaryscientific.gradle.veracode'
            }

            veracodeSetup {
                username = 'user'
                password = 'pass'
                id = 'id'
                key = 'key'
                filesToUpload = fileTree(dir: ".", include: "*").getFiles()
            }
            task verify {
                doLast {
                    assert project.veracodeSetup.username == 'user'
                    assert project.veracodeSetup.password == 'pass'
                    assert project.veracodeSetup.id == 'id'
                    assert project.veracodeSetup.key == 'key'
                    def vc = project.findProperty('veracodeSetup')
                    assert vc.key == 'key'
                    assert vc.filesToUpload == [buildFile] as Set
                    vc.filesToUpload.add(buildFile)
                    assert vc.filesToUpload  == [buildFile, buildFile] as Set
                    vc.filesToUpload.addAll(fileTree(dir: ".", include: "*").getFiles())
                    assert vc.filesToUpload  == [buildFile, buildFile, buildFile] as Set
                }
            }
        """

        when:
        def result = GradleBuild('verify')

        then:
        result.task(":verify").outcome == SUCCESS
    }

    def 'Test getNode method'() {
        given:
        String xmlStr = '''
<root xmlns="something" xmlns:xsi="something" rootAttr1="123" rootAttr2="456">
    <level1 level1Attr="123">
        <level2 level2Attr="456">
        </level2>
    </level1>
</root>
'''
        when:
        XmlParser xmlParser = new XmlParser()
        Node xml = xmlParser.parseText(xmlStr)

        then:
        assert '123' == VeracodeTask.getNode(xml, 'level1').attribute('level1Attr')
        assert '456' == VeracodeTask.getNode(xml, 'level1', 'level2').attribute('level2Attr')
        assert null == VeracodeTask.getNode(xml, 'level3')
    }

    def 'Test extractModuleIds function'() {
        given:
        String xmlStr = '''
<prescanresults xmlns="something" xmlns:xsi="something" app_id="123" build_id="456" prescanresults_version="1.4">
   <module has_fatal_errors="false" id="789" name="goodlib.dll" status="OK">
      <file_issue details="Found (Optional)" filename="path.pdb"/>
   </module>
   <module has_fatal_errors="true" id="012" name="badlib.dll" status="(Fatal)PDB Files Missing - 1 File">
      <file_issue details="Not Found (Required)" filename="path"/>
   </module>
</prescanresults>
'''

        when:
        XmlParser xmlParser = new XmlParser()
        Node xml = xmlParser.parseText(xmlStr)
        List<String> moduleIds = VeracodeBeginScan.extractModuleIds(xml)

        then:
        assert moduleIds[0] == "789"
    }

    def 'Test veracodeSetup filesToUpload are properly set'() {
        given:
        def project = new ProjectBuilder().build()
        VeracodeSetup vs = new VeracodeSetup()
        vs.filesToUpload = project.fileTree(dir: testProjectDir.root, include: '**/*').getFiles()

        when:
        project.plugins.apply('com.calgaryscientific.gradle.veracode')
        project.ext.veracodeSetup = vs

        then:
        VeracodeSetup vsRead = project.findProperty("veracodeSetup") as VeracodeSetup
        Set<File> expected = [buildFile] as Set
        assert vsRead.filesToUpload == expected
        def task = project.tasks.getByName("veracodeUploadFile")
        assert task.getFileSet() == expected
        def _ = vsRead.filesToUpload.add(buildFile)
        assert vsRead.filesToUpload == [buildFile, buildFile] as Set
    }

}
