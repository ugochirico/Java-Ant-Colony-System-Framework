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

package com.ugos.acs.sp;
import com.ugos.acs.*;
import com.ugos.acs.tsp.*;

import java.util.*;
import java.io.*;

public class AntColony4SP extends AntColony4TSP
{
    private int[] m_nodesInZ = null;

    private static final double L = 0.5;

    public AntColony4SP(AntGraph graph, int[] nodesInZ, int nAnts, int nIterations)
    {
        super(graph, nAnts, nIterations);

        m_nodesInZ = nodesInZ;

        // add pheromones to arcs that go from nodes in Z
        for (int i = 0; i < m_nodesInZ.length; i++)
            for(int j = i + 1; j < m_nodesInZ.length; j++)
            {
                int r = m_nodesInZ[i];
                int s = m_nodesInZ[j];
                m_graph.updateTau(r,s, (1 + L) * m_graph.tau(r,s));
            }
    }

    protected Ant[] createAnts(AntGraph graph, int nAnts)
    {
        Random ran = new Random(System.currentTimeMillis());
        Ant4SP.reset();
        Ant4SP.setAntColony(this);
        Ant4SP ant[] = new Ant4SP[nAnts];
        int nCount = 0;
        for(int i = 0; i < nAnts; i++)
        {
            int nIndex = nCount;
            nCount++;
            if(nCount == m_nodesInZ.length)
                nCount = 0;

            ant[i] = new Ant4SP(m_nodesInZ[nIndex], m_nodesInZ, this);
        }

        return ant;
    }

    protected void globalUpdatingRule()
    {
        double dEvaporation = 0;
        double dDeposition  = 0;

        for(int r = 0; r < m_graph.nodes(); r++)
        {
            for(int s = 0; s < m_graph.nodes(); s++)
            {
                if(r != s)
                {
                    dEvaporation = ((double)1 - A) * m_graph.tau(r,s);

                    double deltaTau = //Ant4TSP.s_dBestPathValue * (double)Ant4TSP.s_bestPath[r][s];
                        ((double)1 / Ant4TSP.s_dBestPathValue) * (double)Ant4TSP.s_bestPath[r][s];

                    dDeposition  = A * deltaTau;

                    int i = 0;
                    for (i = 0; i < m_nodesInZ.length && s != m_nodesInZ[i]; i++);

                    if(i == m_nodesInZ.length)
                        dEvaporation -= A * deltaTau;

                    m_graph.updateTau(r, s, dEvaporation + dDeposition);
                }
            }
        }
     }
}
