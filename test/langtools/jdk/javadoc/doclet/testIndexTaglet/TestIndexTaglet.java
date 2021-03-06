/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8202462
 * @summary {@index} may cause duplicate labels
 * @library /tools/lib ../lib
 * @modules jdk.javadoc/jdk.javadoc.internal.tool
 * @build JavadocTester toolbox.ToolBox builder.ClassBuilder
 * @run main TestIndexTaglet
 */


import java.nio.file.Path;
import java.nio.file.Paths;

import builder.ClassBuilder;
import builder.ClassBuilder.MethodBuilder;
import toolbox.ToolBox;

public class TestIndexTaglet extends JavadocTester {

    final ToolBox tb;

    public static void main(String... args) throws Exception {
        TestIndexTaglet tester = new TestIndexTaglet();
        tester.runTests(m -> new Object[]{Paths.get(m.getName())});
    }

    TestIndexTaglet() {
        tb = new ToolBox();
    }

    @Test
    void test(Path base) throws Exception {
        Path srcDir = base.resolve("src");
        createTestClass(srcDir);

        Path outDir = base.resolve("out");
        javadoc("-d", outDir.toString(),
                "-sourcepath", srcDir.toString(),
                "pkg");

        checkExit(Exit.OK);

        checkOrder("pkg/A.html",
                "<h3>Method Detail</h3>\n",
                "<div class=\"block\">test description with <a id=\"search_phrase_a\" "
                 +    "class=\"searchTagResult\">search_phrase_a</a></div>");

        checkOrder("pkg/A.html",
                "<h3>Method Summary</h3>\n",
                "<div class=\"block\">test description with search_phrase_a</div>");
    }

    void createTestClass(Path srcDir) throws Exception {
        MethodBuilder method = MethodBuilder
                .parse("public void func(A a) {}")
                .setComments("test description with {@index search_phrase_a class a}");

        new ClassBuilder(tb, "pkg.A")
                .setModifiers("public", "class")
                .addMembers(method)
                .write(srcDir);

    }
}
