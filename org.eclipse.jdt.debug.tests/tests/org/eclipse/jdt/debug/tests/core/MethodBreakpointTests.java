package org.eclipse.jdt.debug.tests.core;

/*
 * (c) Copyright IBM Corp. 2000, 2001.
 * All Rights Reserved.
 */

/**
 * Tests method breakpoints.
 */
import java.util.ArrayList;
import java.util.List;

import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.jdt.debug.core.IJavaDebugTarget;
import org.eclipse.jdt.debug.core.IJavaMethodBreakpoint;
import org.eclipse.jdt.debug.core.IJavaPrimitiveValue;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;
import org.eclipse.jdt.debug.tests.AbstractDebugTest;

public class MethodBreakpointTests extends AbstractDebugTest {
	
	public MethodBreakpointTests(String name) {
		super(name);
	}

	public void testEntryAndExitBreakpoints() throws Exception {
		String typeName = "DropTests";
		List bps = new ArrayList();
		// method 4 - entry
		bps.add(createMethodBreakpoint(typeName, "method4", "()V", true, false));
		// method 1 - exit
		bps.add(createMethodBreakpoint(typeName, "method1", "()V", false, true));
		
		
		IJavaThread thread= null;
		try {
			thread= launch(typeName);
			assertNotNull("Breakpoint not hit within timeout period", thread);
			
			IBreakpoint hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);

			// onto the next breakpoint			
			thread = resume(thread);
			
			hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit exit breakpoint second", bps.get(1), hit);			

		} finally {
			terminateAndRemove(thread);
			removeAllBreakpoints();
		}		
	}
	
	public void testDisabledEntryAndExitBreakpoints() throws Exception {
		String typeName = "DropTests";
		// method 4 - entry
		IBreakpoint bp1 = createMethodBreakpoint(typeName, "method4", "()V", true, false);
		bp1.setEnabled(false);
		// method 1 - exit
		IBreakpoint bp2 = createMethodBreakpoint(typeName, "method1", "()V", false, true);
		bp2.setEnabled(false);		
		
		IJavaDebugTarget debugTarget= null;
		try {
			debugTarget= launchAndTerminate(typeName, 6000);
		} finally {
			terminateAndRemove(debugTarget);
			removeAllBreakpoints();
		}		
	}
	
	public void testInnerClassNotHit() throws Exception {
		String typeNamePattern = "A";
		List bps = new ArrayList();
		// method b - entry
		bps.add(createMethodBreakpoint(typeNamePattern, "b", "()V", true, false));
		
		
		IJavaThread thread= null;
		try {
			thread= launch(typeNamePattern);
			assertNotNull("Breakpoint not hit within timeout period", thread);
			
			IBreakpoint hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);

		
			resumeAndExit(thread);

		} finally {
			terminateAndRemove(thread);
			removeAllBreakpoints();
		}		
	}
	
	public void testInnerClassesHit() throws Exception {
		String typeNamePattern = "A*";
		List bps = new ArrayList();
		// method b - entry
		bps.add(createMethodBreakpoint(typeNamePattern, "b", "()V", true, false));
		
		
		IJavaThread thread= null;
		try {
			thread= launch("A");
			assertNotNull("Breakpoint not hit within timeout period", thread);
			
			IBreakpoint hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);

			thread= resume(thread);
			hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);

			thread= resume(thread);
			hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);
			
			thread= resume(thread);
			hit = getBreakpoint(thread);
			assertNotNull("suspended, but not by breakpoint", hit);
			assertEquals("should hit entry breakpoint first", bps.get(0),hit);
			
			resumeAndExit(thread);
		} finally {
			terminateAndRemove(thread);
			removeAllBreakpoints();
		}		
	}

	public void testHitCountEntryBreakpoint() throws Exception {
		String typeName = "MethodLoop";
		List bps = new ArrayList();
		IJavaMethodBreakpoint bp = createMethodBreakpoint(typeName, "calculateSum", "()V", true, false);
		bp.setHitCount(3);
		
		IJavaThread thread= null;
		try {
			thread= launch(typeName);
			assertNotNull("Method entry breakpoint not hit within timeout period", thread);
			
			IJavaStackFrame frame = (IJavaStackFrame)thread.getTopStackFrame();
			IVariable var = frame.findVariable("sum");
			assertNotNull("Could not find variable 'sum'", var);
			
			IJavaPrimitiveValue value = (IJavaPrimitiveValue)var.getValue();
			assertNotNull("variable 'sum' has no value", value);
			int iValue = value.getIntValue();
			assertTrue("value of 'sum' should be '3', but was " + iValue, iValue == 3);			
			
			bp.delete();
		} finally {
			terminateAndRemove(thread);
			removeAllBreakpoints();
		}		
	}

	public void testHitCountExitBreakpoint() throws Exception {
		String typeName = "MethodLoop";
		List bps = new ArrayList();
		IJavaMethodBreakpoint bp = createMethodBreakpoint(typeName, "calculateSum", "()V", false, true);
		bp.setHitCount(3);
		
		IJavaThread thread= null;
		try {
			thread= launch(typeName);
			assertNotNull("Method exit breakpoint not hit within timeout period", thread);
			
			IJavaStackFrame frame = (IJavaStackFrame)thread.getTopStackFrame();
			IVariable var = frame.findVariable("sum");
			assertNotNull("Could not find variable 'sum'", var);
			
			IJavaPrimitiveValue value = (IJavaPrimitiveValue)var.getValue();
			assertNotNull("variable 'sum' has no value", value);
			int iValue = value.getIntValue();
			assertTrue("value of 'sum' should be '6', but was " + iValue, iValue == 6);			
			
			bp.delete();
		} finally {
			terminateAndRemove(thread);
			removeAllBreakpoints();
		}		
	}
}
