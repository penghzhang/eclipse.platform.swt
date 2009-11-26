/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.accessibility;


import java.util.*;
import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;

/**
 * Instances of this class provide a bridge between application
 * code and assistive technology clients. Many platforms provide
 * default accessible behavior for most widgets, and this class
 * allows that default behavior to be overridden. Applications
 * can get the default Accessible object for a control by sending
 * it <code>getAccessible</code>, and then add an accessible listener
 * to override simple items like the name and help string, or they
 * can add an accessible control listener to override complex items.
 * As a rule of thumb, an application would only want to use the
 * accessible control listener to implement accessibility for a
 * custom control.
 * 
 * @see Control#getAccessible
 * @see AccessibleListener
 * @see AccessibleEvent
 * @see AccessibleControlListener
 * @see AccessibleControlEvent
 * @see <a href="http://www.eclipse.org/swt/snippets/#accessibility">Accessibility snippets</a>
 * @see <a href="http://www.eclipse.org/swt/">Sample code and further information</a>
 * 
 * @since 2.0
 */
public class Accessible {
	Vector accessibleListeners = new Vector ();
	Vector accessibleControlListeners = new Vector ();
	Vector accessibleTextListeners = new Vector ();
	Vector accessibleActionListeners = new Vector();
	Vector accessibleHyperlinkListeners = new Vector();
	Vector accessibleTableListeners = new Vector();
	Vector accessibleTableCellListeners = new Vector();
	Vector accessibleTextExtendedListeners = new Vector();
	Vector accessibleValueListeners = new Vector();
	Vector accessibleScrollListeners = new Vector();
	Vector accessibleAttributeListeners = new Vector();
	Accessible parent;
	AccessibleObject accessibleObject;
	Control control;
	Vector relations;
	
	static class Relation {
		int type;
		Accessible target;
		
		public Relation(int type, Accessible target) {
			this.type = type;
			this.target = target;
		}
	}
	
	/**
	 * Constructs a new instance of this class given its parent.
	 * 
	 * @param parent the Accessible parent
	 * 
	 * @see Control#getAccessible
	 * 
	 * @since 3.6
	 */
	public Accessible(Accessible parent) {
		this.parent = parent;
		//TODO: (platform-specific?) code to add this accessible to the parent's children
	}

	/**
	 * @since 3.5
	 */
	protected Accessible() {
	}

	Accessible (Control control) {
		super ();
		this.control = control;
		AccessibleFactory.registerAccessible (this);
		control.addDisposeListener (new DisposeListener () {
			public void widgetDisposed (DisposeEvent e) {
				release ();
			}
		});
	}	
	
	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an accessible client asks for certain strings,
	 * such as name, description, help, or keyboard shortcut. The
	 * listener is notified by sending it one of the messages defined
	 * in the <code>AccessibleListener</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for a name, description, help, or keyboard shortcut string
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleListener
	 * @see #removeAccessibleListener
	 */
	public void addAccessibleListener (AccessibleListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleListeners.addElement (listener);	
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an accessible client asks for custom control
	 * specific information. The listener is notified by sending it
	 * one of the messages defined in the <code>AccessibleControlListener</code>
	 * interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for custom control specific information
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleControlListener
	 * @see #removeAccessibleControlListener
	 */
	public void addAccessibleControlListener (AccessibleControlListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleControlListeners.addElement (listener);		
	}

	/**
	 * Adds the listener to the collection of listeners who will
	 * be notified when an accessible client asks for custom text control
	 * specific information. The listener is notified by sending it
	 * one of the messages defined in the <code>AccessibleTextListener</code>
	 * interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for custom text control specific information
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTextListener
	 * @see #removeAccessibleTextListener
	 * 
	 * @since 3.0
	 */
	public void addAccessibleTextListener (AccessibleTextListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleTextListeners.addElement (listener);		
	}
	
	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleAction</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleAction</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleActionListener
	 * @see #removeAccessibleActionListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleActionListener(AccessibleActionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleActionListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleHyperlink</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleHyperlink</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleHyperlinkListener
	 * @see #removeAccessibleHyperlinkListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleHyperlinkListener(AccessibleHyperlinkListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleHyperlinkListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTable</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleTable</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTableListener
	 * @see #removeAccessibleTableListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleTableListener(AccessibleTableListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTableListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTableCell</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleTableCell</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTableCellListener
	 * @see #removeAccessibleTableCellListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleTableCellListener(AccessibleTableCellListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTableCellListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTextExtended</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleTextExtended</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTextExtendedListener
	 * @see #removeAccessibleTextExtendedListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleTextExtendedListener(AccessibleTextExtendedListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTextExtendedListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleValue</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleValue</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleValueListener
	 * @see #removeAccessibleValueListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleValueListener(AccessibleValueListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleValueListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleScroll</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleScroll</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleScrollListener
	 * @see #removeAccessibleScrollListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleScrollListener(AccessibleScrollListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleScrollListeners.addElement(listener);
	}

	/**
	 * Adds the listener to the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleAttribute</code> interface.
	 *
	 * @param listener the listener that should be notified when the receiver
	 * is asked for <code>AccessibleAttribute</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleAttributeListener
	 * @see #removeAccessibleAttributeListener
	 * 
	 * @since 3.6
	 */
	public void addAccessibleAttributeListener(AccessibleAttributeListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleAttributeListeners.addElement(listener);
	}

	/**
	 * Adds a relation with the specified type and target
	 * to the receiver's set of relations.
	 * 
	 * @param type a constant beginning with RELATION_* indicating the type of relation
	 * @param target the accessible that is the target for this relation
	 * 
	 * @since 3.6
	 */
	public void addRelation(int type, Accessible target) {
		checkWidget();
		if (relations == null) relations = new Vector();
		relations.add(new Relation(type, target));
		if (accessibleObject != null) accessibleObject.addRelation(type, target);
	}
	
	void addRelations () {
		if (relations == null) return;
		if (accessibleObject == null) return;
		for (int i = 0; i < relations.size(); i++) {
			Relation relation = (Relation)relations.elementAt(i);
			accessibleObject.addRelation(relation.type, relation.target);
		}
	}
	
	/**
	 * Disposes of the operating system resources associated with
	 * the receiver.
	 * <p>
	 * This method should be called when an accessible that was created
	 * with the public constructor is no longer needed.
	 * It is not necessary to call this for instances of Accessible that
	 * were retrieved with Control#getAccessible.
	 * </p>
	 */
	public void dispose () {
		if (control != null) return;
		// TODO: platform-specific code to dispose a lightweight accessible
	}

	/**
	 * Returns the control for this Accessible object. 
	 *
	 * @return the receiver's control
	 * @since 3.0
	 */
	public Control getControl() {
		return control;
	}

	/* checkWidget was copied from Widget, and rewritten to work in this package */
	void checkWidget () {
		if (!isValidThread ()) SWT.error (SWT.ERROR_THREAD_INVALID_ACCESS);
		if (control.isDisposed ()) SWT.error (SWT.ERROR_WIDGET_DISPOSED);
	}

	AccessibleListener[] getAccessibleListeners () {
		if (accessibleListeners == null) return null;
		AccessibleListener[] result = new AccessibleListener [accessibleListeners.size ()];
		accessibleListeners.copyInto (result);
		return result;
	}

	int /*long*/ getControlHandle () {
		return control.handle;
	}

	AccessibleControlListener[] getControlListeners () {
		if (accessibleControlListeners == null) return null;
		AccessibleControlListener[] result = new AccessibleControlListener [accessibleControlListeners.size ()];
		accessibleControlListeners.copyInto (result);
		return result;
	}

	/**
	 * Invokes platform specific functionality to allocate a new accessible object.
	 * <p>
	 * <b>IMPORTANT:</b> This method is <em>not</em> part of the public
	 * API for <code>Accessible</code>. It is marked public only so that it
	 * can be shared within the packages provided by SWT. It is not
	 * available on all platforms, and should never be called from
	 * application code.
	 * </p>
	 *
	 * @param control the control to get the accessible object for
	 * @return the platform specific accessible object
	 */
	public static Accessible internal_new_Accessible (Control control) {
		return new Accessible (control);
	}

	/* isValidThread was copied from Widget, and rewritten to work in this package */
	boolean isValidThread () {
		return control.getDisplay ().getThread () == Thread.currentThread ();
	}
	
	void release () {
		AccessibleFactory.unregisterAccessible (this);
		if (accessibleObject != null) {
			accessibleObject.release ();
			accessibleObject = null;
		}
	}
	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when an accessible client asks for custom control
	 * specific information.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for custom control specific information
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleControlListener
	 * @see #addAccessibleControlListener
	 */
	public void removeAccessibleControlListener (AccessibleControlListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleControlListeners.removeElement (listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when an accessible client asks for certain strings,
	 * such as name, description, help, or keyboard shortcut.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for a name, description, help, or keyboard shortcut string
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleListener
	 * @see #addAccessibleListener
	 */
	public void removeAccessibleListener (AccessibleListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleListeners.removeElement (listener);
	}

	/**
	 * Removes the listener from the collection of listeners who will
	 * be notified when an accessible client asks for custom text control
	 * specific information.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for custom text control specific information
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTextListener
	 * @see #addAccessibleTextListener
	 * 
	 * @since 3.0
	 */
	public void removeAccessibleTextListener (AccessibleTextListener listener) {
		checkWidget ();
		if (listener == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
		accessibleTextListeners.removeElement (listener);
	}
	
	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleAction</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleAction</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleActionListener
	 * @see #addAccessibleActionListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleActionListener(AccessibleActionListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleActionListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleHyperlink</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleHyperlink</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleHyperlinkListener
	 * @see #addAccessibleHyperlinkListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleHyperlinkListener(AccessibleHyperlinkListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleHyperlinkListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTable</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleTable</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTableListener
	 * @see #addAccessibleTableListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleTableListener(AccessibleTableListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTableListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTableCell</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleTableCell</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTableCellListener
	 * @see #addAccessibleTableCellListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleTableCellListener(AccessibleTableCellListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTableCellListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleTextExtended</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleTextExtended</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleTextExtendedListener
	 * @see #addAccessibleTextExtendedListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleTextExtendedListener(AccessibleTextExtendedListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleTextExtendedListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleValue</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleValue</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleValueListener
	 * @see #addAccessibleValueListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleValueListener(AccessibleValueListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleValueListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleScroll</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleScroll</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleScrollListener
	 * @see #addAccessibleScrollListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleScrollListener(AccessibleScrollListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleScrollListeners.removeElement(listener);
	}

	/**
	 * Removes the listener from the collection of listeners that will be
	 * notified when an accessible client asks for any of the properties
	 * defined in the <code>AccessibleAttribute</code> interface.
	 *
	 * @param listener the listener that should no longer be notified when the receiver
	 * is asked for <code>AccessibleAttribute</code> interface properties
	 *
	 * @exception IllegalArgumentException <ul>
	 *    <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
	 * </ul>
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @see AccessibleAttributeListener
	 * @see #addAccessibleAttributeListener
	 * 
	 * @since 3.6
	 */
	public void removeAccessibleAttributeListener(AccessibleAttributeListener listener) {
		checkWidget();
		if (listener == null) SWT.error(SWT.ERROR_NULL_ARGUMENT);
		accessibleAttributeListeners.removeElement(listener);
	}

	/**
	 * Removes the relation with the specified type and target
	 * from the receiver's set of relations.
	 * 
	 * @param type a constant beginning with RELATION_* indicating the type of relation
	 * @param target the accessible that is the target for this relation
	 * 
	 * @since 3.6
	 */
	public void removeRelation(int type, Accessible target) {
		checkWidget();
		if (relations == null) return;
		for (int i = relations.size() - 1; i >= 0; i--) {
			Relation relation = (Relation)relations.elementAt(i);
			if (relation.type == type && relation.target == target) {
				relations.remove(i);
				if (accessibleObject != null) {
					accessibleObject.removeRelation(relation.type, relation.target);
				}
				break;
			}
		}
	}
	
	/**
	 * Sends a message to accessible clients indicating that something
	 * has changed within a custom control.
	 *
	 * @param event a constant beginning with EVENT_* indicating the message to send
	 * @param childID an identifier specifying a child of the control or the control itself
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 * 
	 * @since 3.6
	 */
	public void sendEvent(int event, int childID) {
		checkWidget();
		//TODO: send platform-specific event (i.e. WinEvent with EVENT_OBJECT_* or IA2_EVENT_* on Win, Signal on ATK, Notification on Mac)
	}

	/**
	 * Sends a message to accessible clients that the child selection
	 * within a custom container control has changed.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 * 
	 * @since 3.0
	 */
	public void selectionChanged () {
		checkWidget ();
		if (accessibleObject != null) {
			accessibleObject.selectionChanged ();
		}
	}

	/**
	 * Sends a message to accessible clients indicating that the focus
	 * has changed within a custom control.
	 *
	 * @param childID an identifier specifying a child of the control
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 */
	public void setFocus (int childID) {
		checkWidget ();
		if (accessibleObject != null) {
			accessibleObject.setFocus (childID);
		}
	}
	
	/**
	 * Sends a message to accessible clients that the text
	 * caret has moved within a custom control.
	 *
	 * @param index the new caret index within the control
	 * 
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @since 3.0
	 */
	public void textCaretMoved (int index) {
		checkWidget ();
		if (accessibleObject != null) {
			accessibleObject.textCaretMoved (index);
		}
	}

	/**
	 * Sends a message to accessible clients that the text
	 * within a custom control has changed.
	 *
	 * @param type the type of change, one of <code>ACC.NOTIFY_TEXT_INSERT</code>
	 * or <code>ACC.NOTIFY_TEXT_DELETE</code>
	 * @param startIndex the text index within the control where the insertion or deletion begins
	 * @param length the non-negative length in characters of the insertion or deletion
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 * 
	 * @see ACC#TEXT_INSERT
	 * @see ACC#TEXT_DELETE
	 * 
	 * @since 3.0
	 */
	public void textChanged (int type, int startIndex, int length) {
		checkWidget ();
		if (accessibleObject != null) {
			accessibleObject.textChanged (type, startIndex, length);
		}
	}

	/**
	 * Sends a message to accessible clients that the text
	 * selection has changed within a custom control.
	 *
	 * @exception SWTException <ul>
	 *    <li>ERROR_WIDGET_DISPOSED - if the receiver's control has been disposed</li>
	 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver's control</li>
	 * </ul>
	 *
	 * @since 3.0
	 */
	public void textSelectionChanged () {
		checkWidget ();
		if (accessibleObject != null) {
			accessibleObject.textSelectionChanged ();
		}
	}
}
