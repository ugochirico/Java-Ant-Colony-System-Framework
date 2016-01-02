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

public class Ant4SP extends Ant4TSP
{
    public Hashtable m_nodesInZTbl;
    private int[] m_nodesInZ;

    public Ant4SP(int nStartNode, int[] nodesInZ, Observer observer)
    {
        super(nStartNode, observer);

        m_nodesInZ = nodesInZ;
    }

    public void init()
    {
        super.init();

        // inizializza l'array di città da visitare
        m_nodesInZTbl = new Hashtable(m_nodesInZ.length);
        for(int i = 0; i < m_nodesInZ.length; i++)
            m_nodesInZTbl.put(new Integer(m_nodesInZ[i]), new Integer(m_nodesInZ[i]));

        // Rimuove la città corrente
        m_nodesInZTbl.remove(new Integer(m_nStartNode));
    }
    public int stateTransitionRule(int curNode)
    {
        int nNextNodeToVisit = super.stateTransitionRule(curNode);

        // Elimina la città selezionata dalla lista
        m_nodesInZTbl.remove(new Integer(nNextNodeToVisit));

        return nNextNodeToVisit;
    }

    public boolean end()
    {
        return m_nodesInZTbl.isEmpty();
    }
}

