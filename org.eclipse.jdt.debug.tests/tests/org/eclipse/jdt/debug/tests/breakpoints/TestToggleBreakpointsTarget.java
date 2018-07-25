/*******************************************************************************
 * Copyright (c) 2010, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.debug.tests.breakpoints;

import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jdt.debug.core.IJavaLineBreakpoint;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.IJavaWatchpoint;
import org.eclipse.jdt.debug.tests.TestUtil;

/**
 * Tests the Java debugger's 'toggle breakpoints target'.
 */
public class TestToggleBreakpointsTarget extends AbstractToggleBreakpointsTarget {

	public TestToggleBreakpointsTarget(String name) {
		super(name);
	}

	/**
	 * Tests that qualified names get created for line breakpoints in external
	 * files.
	 *
	 * @throws Exception
	 */
	public void testExternalLineBreakpoint() throws Exception {
		Listener listener = new Listener();
		IBreakpointManager manager = getBreakpointManager();
		manager.addBreakpointListener(listener);
		try {
			Path path = new Path("testfiles/source/SomeClass.java");
			toggleBreakpoint(path, 22); // 0 based offset in document line numbers
			IBreakpoint added = listener.getAdded();
			assertTrue("Should be a line breakpoint", added instanceof IJavaLineBreakpoint);
			IJavaLineBreakpoint breakpoint = (IJavaLineBreakpoint) added;
			assertEquals("Wrong line number", 23, breakpoint.getLineNumber());
			assertEquals("Wrong type name", "a.b.c.SomeClass", breakpoint.getTypeName());
			TestUtil.waitForJobs(getName(), 100, DEFAULT_TIMEOUT);
		} finally {
			manager.removeBreakpointListener(listener);
			removeAllBreakpoints();
		}
	}

	/**
	 * Tests that qualified names get created for watchpoints in external
	 * files.
	 *
	 * @throws Exception
	 */
	public void testExternalWatchpoint() throws Exception {
		Listener listener = new Listener();
		IBreakpointManager manager = getBreakpointManager();
		manager.addBreakpointListener(listener);
		try {
			Path path = new Path("testfiles/source/SomeClass.java");
			toggleBreakpoint(path, 19); // 0 based offset in document line numbers
			IBreakpoint added = listener.getAdded();
			assertTrue("Should be a watchpoint", added instanceof IJavaWatchpoint);
			IJavaWatchpoint breakpoint = (IJavaWatchpoint) added;
			assertEquals("Wrong type name", "a.b.c.SomeClass", breakpoint.getTypeName());
			assertEquals("Wrong field name", "someField", breakpoint.getFieldName());
			TestUtil.waitForJobs(getName(), 100, DEFAULT_TIMEOUT);
		} finally {
			manager.removeBreakpointListener(listener);
			removeAllBreakpoints();
		}
	}

	/**
	 * Tests that qualified names get created for method breakpoints in external
	 * files.
	 *
	 * @throws Exception
	 */
	public void testExternalMethodBreakpoint() throws Exception {
		Listener listener = new Listener();
		IBreakpointManager manager = getBreakpointManager();
		manager.addBreakpointListener(listener);
		try {
			Path path = new Path("testfiles/source/SomeClass.java");
			toggleBreakpoint(path, 21); // 0 based offset in document line numbers
			IBreakpoint added = listener.getAdded();
			assertTrue("Should be a method breakpoint", added instanceof IJavaMethodBreakpoint);
			IJavaMethodBreakpoint breakpoint = (IJavaMethodBreakpoint) added;
			assertEquals("Wrong type name", "a.b.c.SomeClass", breakpoint.getTypeName());
			assertEquals("Wrong method name", "someMethod", breakpoint.getMethodName());
			// this will actually fail to suspend since 'SomeClass' is not qualified, but we can't resolve the type
			// without a build path, etc. (not a regression)
			assertEquals("Wrong signature", "(Ljava/lang/String;LSomeClass;)V", breakpoint.getMethodSignature());
			TestUtil.waitForJobs(getName(), 100, DEFAULT_TIMEOUT);
		} finally {
			manager.removeBreakpointListener(listener);
			removeAllBreakpoints();
		}
	}

}
