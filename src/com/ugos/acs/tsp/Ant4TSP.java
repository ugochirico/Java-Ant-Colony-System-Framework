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

package com.ugos.acs.tsp;
import com.ugos.acs.*;

import java.util.*;


public class Ant4TSP extends Ant
{
    private static final double B    = 2;
    private static final double Q0   = 0.8;
    private static final double R    = 0.1;

    private static final Random s_randGen = new Random(System.currentTimeMillis());

    protected Hashtable m_nodesToVisitTbl;

    public Ant4TSP(int startNode, Observer observer)
    {
        super(startNode, observer);
    }

    public void init()
    {
        super.init();

        final AntGraph graph = s_antColony.getGraph();

        // inizializza l'array di città da visitare
        m_nodesToVisitTbl = new Hashtable(graph.nodes());
        for(int i = 0; i < graph.nodes(); i++)
            m_nodesToVisitTbl.put(new Integer(i), new Integer(i));

        // Rimuove la città corrente
        m_nodesToVisitTbl.remove(new Integer(m_nStartNode));

//      nExplore = 0;
    }

    public int stateTransitionRule(int nCurNode)
    {
        final AntGraph graph = s_antColony.getGraph();

        // generate a random number
        double q    = s_randGen.nextDouble();
        int nMaxNode = -1;

        if(q <= Q0)  // Exploitation
        {
//            System.out.print("Exploitation: ");
            double dMaxVal = -1;
            double dVal;
            int nNode;

            // search the max of the value as defined in Eq. a)
            Enumeration en = m_nodesToVisitTbl.elements();
            while(en.hasMoreElements())
            {
                // select a node
                nNode = ((Integer)en.nextElement()).intValue();

                // check on tau
                if(graph.tau(nCurNode, nNode) == 0)
                    throw new RuntimeException("tau = 0");

                // get the value
                dVal = graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B);

                // check if it is the max
                if(dVal > dMaxVal)
                {
                    dMaxVal  = dVal;
                    nMaxNode = nNode;
                }
            }
        }
        else  // Exploration
        {
//              System.out.println("Exploration");
            double dSum = 0;
            int nNode = -1;

            // get the sum at denominator
            Enumeration en = m_nodesToVisitTbl.elements();
            while(en.hasMoreElements())
            {
                nNode = ((Integer)en.nextElement()).intValue();
                if(graph.tau(nCurNode, nNode) == 0)
                    throw new RuntimeException("tau = 0");

                // Update the sum
                dSum += graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B);
            }

            if(dSum == 0)
                throw new RuntimeException("SUM = 0");

            // get the everage value
            double dAverage = dSum / (double)m_nodesToVisitTbl.size();

            // search the node in agreement with eq. b)
            en = m_nodesToVisitTbl.elements();
            while(en.hasMoreElements() && nMaxNode < 0)
            {
                nNode = ((Integer)en.nextElement()).intValue();

                // get the value of p as difined in eq. b)
                double p =
                    (graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B)) / dSum;

                // if the value of p is greater the the average value the node is good
                if((graph.tau(nCurNode, nNode) * Math.pow(graph.etha(nCurNode, nNode), B)) > dAverage)
                {
                    //System.out.println("Found");
                    nMaxNode = nNode;
                }
            }

            if(nMaxNode == -1)
                nMaxNode = nNode;
       }

        if(nMaxNode < 0)
            throw new RuntimeException("maxNode = -1");

        // delete the selected node from the list of node to visit
        m_nodesToVisitTbl.remove(new Integer(nMaxNode));

        return nMaxNode;
    }

    public void localUpdatingRule(int nCurNode, int nNextNode)
    {
        final AntGraph graph = s_antColony.getGraph();

        // get the value of the Eq. c)
        double val =
            ((double)1 - R) * graph.tau(nCurNode, nNextNode) +
            (R * (graph.tau0()));

        // update tau
        graph.updateTau(nCurNode, nNextNode, val);
    }

    public boolean better(double dPathValue1, double dPathValue2)
    {
        return dPathValue1 < dPathValue2;
    }

    public boolean end()
    {
        return m_nodesToVisitTbl.isEmpty();
    }
}

