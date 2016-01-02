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
import java.io.*;

public class AntColony4TSP extends AntColony
{
    protected static final double A = 0.1;

    public AntColony4TSP(AntGraph graph, int ants, int iterations)
    {
        super(graph, ants, iterations);
    }

    protected Ant[] createAnts(AntGraph graph, int nAnts)
    {
        Random ran = new Random(System.currentTimeMillis());
        Ant4TSP.reset();
        Ant4TSP.setAntColony(this);
        Ant4TSP ant[] = new Ant4TSP[nAnts];
        for(int i = 0; i < nAnts; i++)
        {
            ant[i] = new Ant4TSP((int)(graph.nodes() * ran.nextDouble()), this);
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
                    // get the value for deltatau
                    double deltaTau = //Ant4TSP.s_dBestPathValue * (double)Ant4TSP.s_bestPath[r][s];
                        ((double)1 / Ant4TSP.s_dBestPathValue) * (double)Ant4TSP.s_bestPath[r][s];

                    // get the value for phermone evaporation as defined in eq. d)
                    dEvaporation = ((double)1 - A) * m_graph.tau(r,s);
                    // get the value for phermone deposition as defined in eq. d)
                    dDeposition  = A * deltaTau;

                    // update tau
                    m_graph.updateTau(r, s, dEvaporation + dDeposition);
                }
            }
        }
    }
}
