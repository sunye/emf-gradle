/**
 * <copyright>
 *
 * Copyright (c) 2002-2005 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *
 * Contributors:
 *   IBM - Initial API and implementation
 *
 * </copyright>
 *
 * $Id: EMFTestPerformancePlugin.java,v 1.21 2005/02/21 04:28:09 nickb Exp $
 */
package org.eclipse.emf.test.performance;

import java.io.PrintWriter;
import java.lang.reflect.Method;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;

import com.ibm.xslt4j.bcel.generic.GETSTATIC;


public class EMFTestPerformancePlugin extends Plugin
{
  private static class DerbyHelper
  {
    private String home;
    private String user;
    private String pass;
    private String performanceConfiguration;
    
    private Class networkServerControlClass;
    private Object networkServerControl;

    public boolean isAvailable()
    {
      if (networkServerControl == null)
      {
        try
        {
          networkServerControlClass = Class.forName("org.apache.derby.drda.NetworkServerControl");
        }
        catch (Exception e)
        {
          return false;
        }
      }

      return true;
    }

    public boolean isRunning()
    {
      if (networkServerControl == null)
      {
        if (isAvailable())
        {
          try
          {
            networkServerControl = networkServerControlClass.newInstance();
          }
          catch (Exception e)
          {
          }
        }
      }

      if (networkServerControl != null)
      {
        try
        {
          Method sysInfoMethod = networkServerControlClass.getDeclaredMethod("getSysinfo", new Class [0]);
          return sysInfoMethod.invoke(networkServerControl, null) != null;
        }
        catch (Exception e)
        {
        }
      }

      return false;
    }

    public void setHome(String home)
    {
      this.home = home;
    }

    public void setUser(String user)
    {
      this.user = user;
    }

    public void setPass(String pass)
    {
      this.pass = pass;
    }

    public void setPerformanceConfiguration(String performanceConfiguration)
    {
      this.performanceConfiguration = performanceConfiguration;
    }
    
    //Returns true if the server is running
    public void startIfDown()
    {
      if (!isRunning() && networkServerControl != null)
      {
        Thread thread = new Thread()
        {
          public void run()
          {
            try
            {
              System.out.println("*** Starting Derby...");
              if (home != null) System.setProperty("derby.system.home", home);
              Method startMethod = networkServerControlClass.getDeclaredMethod("start", new Class []{ PrintWriter.class });
              startMethod.invoke(networkServerControl, new Object [1]);
            }
            catch (Exception e)
            {
              System.err.println("*** Unable to start Derby");
              e.printStackTrace();
            }
          }
        };
        thread.start();
      }
    }

    public void writeSystemProperties()
    {
      System.setProperty("test.target", "performance");
      if (home != null) System.setProperty("derby.system.home", home);
      if (performanceConfiguration != null) System.setProperty("eclipse.perf.config", performanceConfiguration);

      String userAtt = user != null ? "" : (";dbuser=" + user);
      String passAtt = pass != null ? "" : (";dbpasswd=" + pass);
      
      Class driverClass = null;
      try
      {
        driverClass = Class.forName("com.ibm.db2.jcc.DB2Driver");
      }
      catch (Throwable t)
      {
      }

      if (driverClass != null)
      {
        System.setProperty("eclipse.perf.dbloc", "net://localhost" + userAtt + passAtt);
      }
      else
      {
        System.setProperty("eclipse.perf.dbloc", home + userAtt + passAtt);
      }
    }

    public void printSystemProperties()
    {
      System.out.println("*** Derby properties");
      System.out.println("derby.system.home: " + System.getProperty("derby.system.home"));
      System.out.println("test.target: " + System.getProperty("test.target"));
      System.out.println("eclipse.perf.config: " + System.getProperty("eclipse.perf.config"));
      System.out.println("eclipse.perf.dbloc: " + System.getProperty("eclipse.perf.dbloc"));
      System.out.println("*** Java properties");
      try
      {
        System.out.println(
          "getLocation(): " + 
           getClass().getSuperclass().getProtectionDomain().getCodeSource().getLocation().toString()
        );
        
      }
      catch (Exception e)
      {
         e.getStackTrace();
      }
      
    }
  }

  private static EMFTestPerformancePlugin instance;

  public EMFTestPerformancePlugin()
  {
    super();
    instance = this;

    DerbyHelper derbyHelper = new DerbyHelper();
    if (derbyHelper.isAvailable())
    {
      setDerbyAttributes(derbyHelper);
      
      derbyHelper.startIfDown();
      derbyHelper.writeSystemProperties();
      derbyHelper.printSystemProperties();
    }
  }
  
  private static void setDerbyAttributes(DerbyHelper derbyHelper)
  {
    if (Platform.isRunning())
    {
      String[] args = Platform.getApplicationArgs();
      for (int i = 0, maxi = args.length; i < maxi; i++)
      {
        String arg = args[i];
        int index = arg.indexOf("emf.test.performance");
        if (index >= 0)
        {
          index = arg.indexOf(".dbuser=");
          if (index >= 0)
          {
            derbyHelper.setUser(arg.substring(index + ".dbuser=".length()));
            continue;
          }
          index = arg.indexOf(".dbpass=");
          if (index >= 0)
          {
            derbyHelper.setPass(arg.substring(index + ".dbpass=".length()));
            continue;
          }
          index = arg.indexOf(".dbhome=");
          if (index >= 0)
          {
            derbyHelper.setHome(arg.substring(index + ".dbhome=".length()));
            continue;
          }
          index = arg.indexOf(".dbconf=");
          if (index >= 0)
          {
            derbyHelper.setPerformanceConfiguration(arg.substring(index + ".dbconf=".length()));
            continue;
          }
        }
      }
    }
  }

  public static EMFTestPerformancePlugin getPlugin()
  {
    return instance;
  }
}
