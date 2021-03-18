/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.xjc.reproducable_builds;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class ReproducableBuildTest extends Assert
{
	@Test
	public void testGeneratedAnnotationsDoNotHaveADate() throws Exception
	{
		// Because the @Generated annotation is not available anymore during runtime, we check the generated source code.

		final Pattern pattern = Pattern.compile("@Generated\\(.* date ?=.*\\)");

		final Path fooJavaFile = Paths.get("target/generated/src/test/java/org/apache/cxf/configuration/foo/Foo.java");
		final List<String> fooJavaLines = Files.readAllLines(fooJavaFile);
		for (String line : fooJavaLines)
		{
			assertFalse(pattern.matcher(line).matches());
		}
	}
}