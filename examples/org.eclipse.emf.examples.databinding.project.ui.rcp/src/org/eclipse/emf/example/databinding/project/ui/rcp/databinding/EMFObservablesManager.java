/**
 * Copyright (c) 2009 BestSolution.at and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 *   Tom Schindl<tom.schindl@bestsolution.at> - Initial API and implementation
 */
package org.eclipse.emf.example.databinding.project.ui.rcp.databinding;

import org.eclipse.core.databinding.ObservablesManager;
import org.eclipse.core.databinding.observable.IObservable;

import org.eclipse.emf.databinding.IEMFObservable;


/**
 * Because of a too late discovered bug in {@link ObservablesManager} it's not behaving
 * appropriately in some situations.
 * 
 * This class is modified so that it only collects observable of type {@link IEMFObservable}
 */
public class EMFObservablesManager extends ObservablesManager
{
  @Override
  public void addObservable(IObservable observable)
  {
    if (observable instanceof IEMFObservable)
    {
      super.addObservable(observable);
    }
  }
}