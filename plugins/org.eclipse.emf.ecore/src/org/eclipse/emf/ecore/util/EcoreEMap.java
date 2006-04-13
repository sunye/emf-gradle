/**
 * <copyright>
 *
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: EcoreEMap.java,v 1.7 2006/04/13 11:43:13 emerks Exp $
 */
package  org.eclipse.emf.ecore.util;


import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;


public class EcoreEMap extends BasicEMap implements InternalEList.Unsettable, EStructuralFeature.Setting
{
  public static class Unsettable extends EcoreEMap
  {
    public Unsettable(EClass entryEClass, Class entryClass, InternalEObject owner, int featureID)
    {
      super(entryEClass, entryClass, null);
      delegateEList = new UnsettableDelegateEObjectContainmentEList(entryClass, owner, featureID);
    }
    
    protected class UnsettableDelegateEObjectContainmentEList extends DelegateEObjectContainmentEList
    {
      protected boolean isSet;

      public UnsettableDelegateEObjectContainmentEList(Class dataClass, InternalEObject owner, int featureID)
      {
        super(dataClass, owner, featureID);
      }

      protected void didChange()
      {
        isSet = true;
      }

      public boolean isSet()
      {
        return isSet;
      }

      public void unset()
      {
        super.unset();
        if (isNotificationRequired())
        {
          boolean oldIsSet = isSet;
          isSet = false;
          owner.eNotify(createNotification(Notification.UNSET, oldIsSet, false));
        }
        else
        {
          isSet = false;
        }
      }
    }
  }

  protected EClass entryEClass;
  protected Class entryClass;

  public EcoreEMap(EClass entryEClass, Class entryClass, InternalEObject owner, int featureID)
  {
    this.entryClass = entryClass;
    this.entryEClass = entryEClass;
    delegateEList = new DelegateEObjectContainmentEList(entryClass, owner, featureID);
  }
  
  public EcoreEMap(EClass entryEClass, Class entryClass, EList delegateEList)
  {
    this.entryClass = entryClass;
    this.entryEClass = entryEClass;
    this.delegateEList = delegateEList;
  }

  protected void initializeDelegateEList()
  {
  }

  protected class DelegateEObjectContainmentEList extends EObjectContainmentEList
  {
    public DelegateEObjectContainmentEList(Class entryClass, InternalEObject owner, int featureID)
    {
      super(entryClass, owner, featureID);
    }

    protected void didAdd(int index, Object newObject)
    {
      EcoreEMap.this.doPut((Entry)newObject);
    }

    protected void didSet(int index, Object newObject, Object oldObject)
    {
      didRemove(index, oldObject);
      didAdd(index, newObject);
    }

    protected void didRemove(int index, Object oldObject)
    {
      EcoreEMap.this.doRemove((Entry)oldObject);
    }

    protected void didClear(int size, Object [] oldObjects)
    {
      EcoreEMap.this.doClear();
    }

    protected void didMove(int index, Object movedObject, int oldIndex)
    {
      EcoreEMap.this.doMove((Entry)movedObject);
    }
  }

  protected BasicEList newList()
  {
    return
      new BasicEList()
      {
        public Object [] newData(int listCapacity)
        {
          return (Object [])Array.newInstance(entryClass, listCapacity);
        }
      };
  }

  protected Entry newEntry(int hash, Object key, Object value)
  {
    Entry entry = (Entry)entryEClass.getEPackage().getEFactoryInstance().create(entryEClass);
    entry.setHash(hash);
    entry.setKey(key);
    entry.setValue(value);
    return entry;
  }

  public Object basicGet(int index)
  {
    return ((InternalEList)delegateEList).basicGet(index);
  }

  public List basicList()
  {
    return ((InternalEList)delegateEList).basicList();
  }

  /**
   * Returns an iterator that yields unresolved values.
   */
  public Iterator basicIterator()
  {
    return ((InternalEList)delegateEList).basicIterator();
  }

  /**
   * Returns a list iterator that yields unresolved values.
   */
  public ListIterator basicListIterator()
  {
    return ((InternalEList)delegateEList).basicListIterator();
  }

  /**
   * Returns a list iterator that yields unresolved values.
   */
  public ListIterator basicListIterator(int index)
  {
    return ((InternalEList)delegateEList).basicListIterator(index);
  }

  /**
   * Remove the object with without updating the inverse.
   */
  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    return ((InternalEList)delegateEList).basicRemove(object, notifications);
  }

  /**
   * Add the object without updating the inverse.
   */
  public NotificationChain basicAdd(Object object, NotificationChain notifications)
  {
    return ((InternalEList)delegateEList).basicAdd(object, notifications);
  }

  /**
   * Add the object without verifying uniqueness.
   */
  public void addUnique(Object object)
  {
    ((InternalEList)delegateEList).addUnique(object);
  }

  /**
   * Add the object without verifying uniqueness.
   */
  public void addUnique(int index, Object object)
  {
    ((InternalEList)delegateEList).addUnique(index, object);
  }

  /**
   * Set the object without verifying uniqueness.
   */
  public Object setUnique(int index, Object object)
  {
    return ((InternalEList)delegateEList).setUnique(index, object);
  }

  public EObject getEObject()
  {
    return ((EStructuralFeature.Setting)delegateEList).getEObject();
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return ((EStructuralFeature.Setting)delegateEList).getEStructuralFeature();
  }

  public Object get(boolean resolve)
  {
    return ((EStructuralFeature.Setting)delegateEList).get(resolve);
  }

  public void set(Object value)
  {
    if (value instanceof Map)
    {
      ((EStructuralFeature.Setting)delegateEList).unset();
      putAll((Map)value);
    }
    else
    {
      ((EStructuralFeature.Setting)delegateEList).set(value);
    }
  }

  public boolean isSet()
  {
    return ((EStructuralFeature.Setting)delegateEList).isSet();
  }

  public void unset()
  {
    ((EStructuralFeature.Setting)delegateEList).unset();
  }
}
