/*******************************************************************************
 * Copyright (c) 2010, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.internal.webkit;


import org.eclipse.swt.internal.ole.win32.*;
import org.eclipse.swt.internal.win32.*;

public class IWebURLProtectionSpace extends IUnknown {

public IWebURLProtectionSpace(long /*int*/ address) {
	super(address);
}

public int host (long /*int*/[] result) {
	return OS.VtblCall (4, getAddress (), result);
}

public int port (int[] result) {
	return OS.VtblCall (8, getAddress (), result);
}

public int realm (long /*int*/[] result) {
	return OS.VtblCall (11, getAddress (), result);
}

}
