/**
 * <copyright> 
 *
 * Copyright (c) 2002-2006 IBM Corporation and others.
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
 * $Id: AdapterFactoryContentProvider.java,v 1.9 2006/07/13 16:22:56 davidms Exp $
 */
package org.eclipse.emf.edit.ui.provider;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.IPropertySourceProvider;
import org.eclipse.swt.widgets.Display;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.IViewerNotification;
import org.eclipse.emf.edit.provider.ViewerNotification;


/**
 * This content provider wraps an AdapterFactory 
 * and it delegates its JFace provider interfaces to corresponding adapter-implemented item provider interfaces.
 * All method calls to the various structured content provider interfaces 
 * are delegated to interfaces implemented by the adapters generated by the AdapterFactory.
 * {@link org.eclipse.jface.viewers.IStructuredContentProvider} is delegated to 
 * {@link IStructuredItemContentProvider}; {@link ITreeContentProvider} is delegated 
 * to {@link ITreeItemContentProvider};
 * and {@link IPropertySourceProvider} to {@link IItemPropertySource}.
 */
public class AdapterFactoryContentProvider 
  implements 
    ITreeContentProvider, 
    IPropertySourceProvider,
    INotifyChangedListener
{
  /**
   * This keeps track of the one factory we are using.  
   * Use a {@link org.eclipse.emf.edit.provider.ComposedAdapterFactory} if adapters 
   * from more the one factory are involved in the model.
   */
  protected AdapterFactory adapterFactory;

  /**
   * This keeps track of the one viewer using this content provider.
   */
  protected Viewer viewer;

  /**
   * This is used to queue viewer notifications and refresh viewers based on them. 
   * @since 2.2.0
   */
  protected ViewerRefresh viewerRefresh;

  private static final Class IStructuredItemContentProviderClass = IStructuredItemContentProvider.class;
  private static final Class ITreeItemContentProviderClass = ITreeItemContentProvider.class;
  private static final Class IItemPropertySourceClass = IItemPropertySource.class;

  /**
   * This constructs an instance that wraps this factory.
   * The factory should yield adapters that implement the various IItemContentProvider interfaces.
   */
  public AdapterFactoryContentProvider(AdapterFactory adapterFactory)
  {
    this.adapterFactory = adapterFactory;

    if (adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)adapterFactory).addListener(this);
    }
  }

  /**
   * This sets the wrapped factory.
   */
  public void setAdapterFactory(AdapterFactory adapterFactory)
  {
    if (this.adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)this.adapterFactory).removeListener(this);
    }

    if (adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)adapterFactory).addListener(this);
    }

    this.adapterFactory = adapterFactory;
  }

  /**
   * This returns the wrapped factory.
   */
  public AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  /**
   * The given Viewer will start (oldInput == null) or stop (newInput == null) listening for domain events.
   */
  public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
  {
    // If there was no old input, then we must be providing content for this part for the first time...
    //
    this.viewer = viewer;
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.IStructuredContentProvider}.getElements to 
   * forward the call to an object that implements 
   * (@link org.eclipse.emf.edit.provider.IStructuredItemContentProvider#getElements IStructuredItemContentProvider.getElements}.
   */
  public Object [] getElements(Object object)
  {
    // Get the adapter from the factory.
    //
    IStructuredItemContentProvider structuredItemContentProvider = 
      (IStructuredItemContentProvider)adapterFactory.adapt(object, IStructuredItemContentProviderClass);

    // Either delegate the call or return nothing.
    //
    return 
      (structuredItemContentProvider != null ? 
        structuredItemContentProvider.getElements(object) : 
        Collections.EMPTY_LIST).toArray();
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ITreeContentProvider}.getChildren to forward the call to an object that implements 
   * {@link org.eclipse.emf.edit.provider.ITreeItemContentProvider#getChildren ITreeItemContentProvider.getChildren}.
   */
  public Object [] getChildren(Object object)
  {
    // Get the adapter from the factory.
    //
    ITreeItemContentProvider treeItemContentProvider = 
      (ITreeItemContentProvider)adapterFactory.adapt(object, ITreeItemContentProviderClass);

    // Either delegate the call or return nothing.
    //
    return
      (treeItemContentProvider != null ?
        treeItemContentProvider.getChildren(object) :
        Collections.EMPTY_LIST).toArray();
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ITreeContentProvider}.hasChildren to forward the call to an object that implements 
   * {@link org.eclipse.emf.edit.provider.ITreeItemContentProvider#hasChildren ITreeItemContentProvider.hasChildren}.
   */
  public boolean hasChildren(Object object)
  {
    // Get the adapter from the factory.
    //
    ITreeItemContentProvider treeItemContentProvider = 
      (ITreeItemContentProvider)adapterFactory.adapt(object, ITreeItemContentProviderClass);

    // Either delegate the call or return nothing.
    //
    return 
      treeItemContentProvider != null  && 
        treeItemContentProvider.hasChildren(object);
  }

  /**
   * This implements {@link org.eclipse.jface.viewers.ITreeContentProvider}.getParent to forward the call to an object that implements 
   * {@link org.eclipse.emf.edit.provider.ITreeItemContentProvider#getParent ITreeItemContentProvider.getParent}.
   */
  public Object getParent(Object object)
  {
    // Get the adapter from the factory.
    //
    ITreeItemContentProvider treeItemContentProvider = 
      (ITreeItemContentProvider)adapterFactory.adapt(object, ITreeItemContentProviderClass);

    // Either delegate the call or return nothing.
    //
    return 
      treeItemContentProvider != null ?
        treeItemContentProvider.getParent(object) :
        null;
  }

  /**
   * This discards the content provider and removes this as a listener to the {@link #adapterFactory}.
   */
  public void dispose()
  {
    if (adapterFactory instanceof IChangeNotifier)
    {
      ((IChangeNotifier)adapterFactory).removeListener(this);
    }
    viewer = null;
  }

  /**
   * This implements {@link org.eclipse.ui.views.properties.IPropertySourceProvider}.getPropertySource to forward the call to an object that implements
   * {@link org.eclipse.emf.edit.provider.IItemPropertySource}.
   */
  public IPropertySource getPropertySource(Object object)
  {
    if (object instanceof IPropertySource)
    {
      return (IPropertySource)object;
    }
    else
    {
      IItemPropertySource itemPropertySource =
        (IItemPropertySource)
          (object instanceof EObject && ((EObject)object).eClass() == null ?
            null :
            adapterFactory.adapt(object, IItemPropertySourceClass));
  
      return 
        itemPropertySource != null ?  createPropertySource(object, itemPropertySource) : null;
    }
  }

  protected IPropertySource createPropertySource(Object object, IItemPropertySource itemPropertySource)
  {
    return new PropertySource(object, itemPropertySource);
  }

  public void notifyChanged(Notification notification)
  {
    if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed())
    {
      // If the notification is an IViewerNotification, it specifies how ViewerRefresh should behave.  Otherwise fall
      // back to NotifyChangedToViewerRefresh, which determines how to refresh the viewer directly from the model
      // notification.
      //
      if (notification instanceof IViewerNotification)
      {
        if (viewerRefresh == null)
        {
          viewerRefresh = new ViewerRefresh(viewer);
        }

        if (viewerRefresh.addNotification((IViewerNotification)notification))
        {
          viewer.getControl().getDisplay().asyncExec(viewerRefresh);
        }
      }
      else
      {
        NotifyChangedToViewerRefresh.handleNotifyChanged(
          viewer,
          notification.getNotifier(),
          notification.getEventType(),
          notification.getFeature(),
          notification.getOldValue(),
          notification.getNewValue(),
          notification.getPosition());
      }
    }
  }

  /**
   * A runnable class that efficiently updates a {@link org.eclipse.jface.viewers.Viewer} via standard APIs, based on
   * queued {@link org.eclipse.emf.edit.provider.IViewerNotification}s from the model's item providers.
   */
  public static class ViewerRefresh implements Runnable
  {
    Viewer viewer;
    List notifications;
    boolean compatibility;

    /**
     * @since 2.2.0
     */
    public ViewerRefresh(Viewer viewer)
    {
      this.viewer = viewer;
    }

    /**
     * @deprecated in 2.2.0
     */
    public ViewerRefresh(Viewer viewer, IViewerNotification notification)
    {
      this.viewer = viewer;
      addNotification(notification);
      compatibility = true;
    }

    /**
     * Adds a viewer notification to the queue that will be processed by this <code>ViewerRefresh</code>.
     * Duplicative notifications will not be queued.
     * @param notification the notification to add to the queue 
     * @return whether the queue has been made non-empty, which would indicate that the <code>ViewerRefresh</code>
     *   needs to be {@link Display#asyncExec scheduled} on the event queue 
     * @since 2.2.0
     */
    public synchronized boolean addNotification(IViewerNotification notification)
    {
      if (notifications == null)
      {
        notifications = new ArrayList();
      }

      if (notifications.isEmpty())
      {
        notifications.add(notification);
        return true;
      }

      if (viewer instanceof StructuredViewer)
      {
        for (Iterator i = notifications.iterator(); i.hasNext() && notification != null; )
        {
          IViewerNotification old = (IViewerNotification)i.next();
          IViewerNotification merged = merge(old, notification);
          if (merged == old)
          {
            notification = null;
          }
          else if (merged != null)
          {
            notification = merged;
            i.remove();
          }
        }
        if (notification != null)
        {
          notifications.add(notification);
        }
      }
      return false;
    }

    /**
     * Compares two notifications and, if duplicative, returns a single notification that does the work of
     * both.  Note: this gives priority to a content refresh on the whole viewer over a content refresh or
     * label update on a specific element; however, it doesn't use parent-child relationships to determine
     * if refreshes on non-equal elements are duplicative.
     * @return a single notification that is equivalent to the two parameters, or null if they are non-duplicative
     * @since 2.2.0
     */
    protected IViewerNotification merge(IViewerNotification n1, IViewerNotification n2)
    {
      // This implements the following order of preference:
      //   1. full refresh and update
      //   2. full refresh (add update if necessary)
      //   3. refresh element with update
      //   4. refresh element (if necessary)
      //   5. update element
      // 
      if (n1.getElement() == null && n1.isLabelUpdate())
      {
        return n1;
      }
      else if (n2.getElement() == null && n2.isLabelUpdate())
      {
        return n2;
      }
      else if (n1.getElement() == null)
      {
        if (n2.isLabelUpdate())
        {
          n1 = new ViewerNotification(n1);
        }
        return n1;
      }
      else if (n2.getElement() == null)
      {
        if (n1.isLabelUpdate())
        {
          n2 = new ViewerNotification(n2);
        }
        return n2;
      }
      else if (n1.getElement() == n2.getElement())
      {
        if (n1.isContentRefresh() && n1.isLabelUpdate())
        {
          return n1;
        }
        else if (n2.isContentRefresh() && n2.isLabelUpdate())
        {
          return n2;
        }
        else if (n1.isContentRefresh())
        {
          if (n2.isLabelUpdate())
          {
            n1 = new ViewerNotification(n1, n1.getElement(), true, true);
          }
          return n1;
        }
        else if (n2.isContentRefresh())
        {
          if (n1.isLabelUpdate())
          {
            n2 = new ViewerNotification(n2, n2.getElement(), true, true);
          }
          return n2;
        }
        else if (n1.isLabelUpdate())
        {
          return n1;
        }
        else // n2.isLabelUpdate()
        {
          return n2;
        }
      }
      return null;
    }

    public void run()
    {
      if (viewer != null && viewer.getControl() != null && !viewer.getControl().isDisposed())
      {
        List current;
  
        synchronized (this)
        {
          current = notifications;
          notifications = null;
        }
  
        if (current != null)
        {
          for (Iterator i = current.iterator(); i.hasNext(); )
          {
            refresh((IViewerNotification)i.next());
          }
        }
      }
    }

    /**
     * @since 2.2.0
     */
    protected void refresh(IViewerNotification notification)
    {
      // Previously, we never updated the viewer on a resolve.  Now we post and merge it as appropriate.
      //
      if (compatibility && notification.getEventType() == Notification.RESOLVE) return;

      Object element = notification.getElement();

      if (viewer instanceof StructuredViewer)
      {
        StructuredViewer structuredViewer = (StructuredViewer)viewer;

        if (element != null)
        {
          if (notification.isContentRefresh())
          {
            structuredViewer.refresh(element, notification.isLabelUpdate());
          }
          else if (notification.isLabelUpdate())
          {
            structuredViewer.update(element, null);
          }
        }
        else
        {
          structuredViewer.refresh(notification.isLabelUpdate());
        }
      }
      else
      {
        viewer.refresh();
      }
    }
  }
}
