/*
 * Copyright (C) 2007-2016 Ugo Chirico
 *
 * This is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Affero GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.ugos.acs;

import java.util.*;
import java.io.*;

public abstract class Ant extends Observable implements Runnable
{
    private int m_nAntID;

    protected int[][]  m_path;
    protected int      m_nCurNode;
    protected int      m_nStartNode;
    protected double   m_dPathValue;
    protected Observer m_observer;
    protected Vector   m_pathVect;

    private static int s_nAntIDCounter = 0;
    private static PrintStream s_outs;

    protected static AntColony s_antColony;

    public static double    s_dBestPathValue = Double.MAX_VALUE;
    public static Vector    s_bestPathVect  = null;
    public static int[][]   s_bestPath      = null;
    public static int       s_nLastBestPathIteration = 0;

    public static void setAntColony(AntColony antColony)
    {
        s_antColony = antColony;
    }

    public static void reset()
    {
        s_dBestPathValue = Double.MAX_VALUE;
        s_bestPathVect = null;
        s_bestPath = null;
        s_nLastBestPathIteration = 0;
        s_outs = null;
    }

    public Ant(int nStartNode, Observer observer)
    {
        s_nAntIDCounter++;
        m_nAntID    = s_nAntIDCounter;
        m_nStartNode = nStartNode;
        m_observer  = observer;
    }

    public void init()
    {
        if(s_outs == null)
        {
            try
            {
                s_outs = new PrintStream(new FileOutputStream("" + s_antColony.getID()+ "_" + s_antColony.getGraph().nodes() + "x" + s_antColony.getAnts() + "x" + s_antColony.getIterations() + "_ants.txt"));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }

        final AntGraph graph = s_antColony.getGraph();
        m_nCurNode   = m_nStartNode;

        m_path      = new int[graph.nodes()][graph.nodes()];
        m_pathVect  = new Vector(graph.nodes());

        m_pathVect.addElement(new Integer(m_nStartNode));
        m_dPathValue = 0;
    }

    public void start()
    {
        init();
        Thread thread = new Thread(this);
        thread.setName("Ant " + m_nAntID);
        thread.start();
    }

    public void run()
    {
        final AntGraph graph = s_antColony.getGraph();

        // repeat while End of Activity Rule returns false
        while(!end())
        {
            int nNewNode;

            // synchronize the access to the graph
            synchronized(graph)
            {
                // apply the State Transition Rule
                nNewNode = stateTransitionRule(m_nCurNode);

                // update the length of the path
                m_dPathValue += graph.delta(m_nCurNode, nNewNode);
            }

            // add the current node the list of visited nodes
            m_pathVect.addElement(new Integer(nNewNode));
            m_path[m_nCurNode][nNewNode] = 1;

            synchronized(graph)
            {
                // apply the Local Updating Rule
                localUpdatingRule(m_nCurNode, nNewNode);
            }

            // update the current node
            m_nCurNode = nNewNode;
        }

        synchronized(graph)
        {
            // update the best tour value
            if(better(m_dPathValue, s_dBestPathValue))
            {
                s_dBestPathValue        = m_dPathValue;
                s_bestPath              = m_path;
                s_bestPathVect          = m_pathVect;
                s_nLastBestPathIteration = s_antColony.getIterationCounter();

                s_outs.println("Ant + " + m_nAntID + "," + s_dBestPathValue + "," + s_nLastBestPathIteration + "," + s_bestPathVect.size() + "," + s_bestPathVect);
            }
        }

        // update the observer
        m_observer.update(this, null);

        if(s_antColony.done())
            s_outs.close();
    }

    protected abstract boolean better(double dPathValue, double dBestPathValue);

    public abstract int stateTransitionRule(int r);

    public abstract void localUpdatingRule(int r, int s);

    public abstract boolean end();

    public static int[] getBestPath()
    {
        int nBestPathArray[] = new int[s_bestPathVect.size()];
        for(int i = 0; i < s_bestPathVect.size(); i++)
        {
            nBestPathArray[i] = ((Integer)s_bestPathVect.elementAt(i)).intValue();
        }

        return nBestPathArray;
    }

    public String toString()
    {
        return "Ant " + m_nAntID + ":" + m_nCurNode;
    }
}

