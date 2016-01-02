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

package com.ugos.acs.test;


import java.util.*;
import java.io.*;
import com.ugos.acs.sp.*;
import com.ugos.acs.*;

public class SPTest
{
    /*
    private static final int nNodes = 50;
    private static final int nAnts = 10;
    private static final int nIterations = 2002;
     */
    private static Random s_ran = new Random(System.currentTimeMillis());

    public static void main(String[] args)
    {
        Properties C;

        // Print application prompt to console.
        System.out.println("AntColonySystem for SP");

        if(args.length < 10)
        {
            System.out.println("Wrong number of parameters");
            return;
        }

        int nAnts = 0;
        int nNodes = 0;
        int nNodesInZ = 0;
        int nIterations = 0;
        int nRepetitions = 0;

        for (int i = 0; i < args.length; i+=2)
        {
            if(args[i].equals("-a"))
            {
                nAnts = Integer.parseInt(args[i + 1]);
                System.out.println("Ants: " + nAnts);
            }
            else if(args[i].equals("-n"))
            {
                nNodes = Integer.parseInt(args[i + 1]);
                System.out.println("Nodes: " + nNodes);
            }
            else if(args[i].equals("-nz"))
            {
                nNodesInZ = Integer.parseInt(args[i + 1]);
                System.out.println("Nodes In Z: " + nNodesInZ);
            }
            else if(args[i].equals("-i"))
            {
                nIterations = Integer.parseInt(args[i + 1]);
                System.out.println("Iterations: " + nIterations );
            }
            else if(args[i].equals("-r"))
            {
                nRepetitions = Integer.parseInt(args[i + 1]);
                System.out.println("Repetitions: " + nRepetitions);
            }
        }

        if(nAnts == 0 || nNodes == 0 || nIterations == 0 || nRepetitions == 0)
        {
            System.out.println("One of the parameters is wrong");
            return;
        }

        // generate the graph
        double d[][] = new double[nNodes][nNodes];
        double t[][] = new double[nNodes][nNodes];

        for(int i = 0; i < nNodes; i++)
            for(int j = i + 1; j < nNodes; j++)
            {
                d[i][j] = s_ran.nextDouble();
                d[j][i] = d[i][j];
//                t[i][j] = 1;
//                t[j][i] = 1;
            }

         AntGraph graph = new AntGraph(nNodes, d);

        try
        {
        	ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream("" + nNodes + "_antgraph.bin"));
        	outs.writeObject(graph);
        	outs.close();

//            ObjectInputStream ins = new ObjectInputStream(new FileInputStream("" + nNodes + "_antgraph.bin"));
//            graph = (AntGraph)ins.readObject();
//            ins.close();

            // extract Z randomly
            int nNodesInZArray[] = new int[nNodesInZ];
            Hashtable auxTbl = new Hashtable();
            for(int i = 0; i < nNodesInZ; i++)
            {
                Integer nNode = new Integer((int)(graph.nodes() * s_ran.nextDouble()));
                while(auxTbl.containsKey(nNode))
                    nNode = new Integer((int)(graph.nodes() * s_ran.nextDouble()));

                nNodesInZArray[i] = nNode.intValue();
                auxTbl.put(nNode, nNode);
            }

            System.out.println(auxTbl);

            FileOutputStream outs1 = new FileOutputStream("SP_" + nNodes + "_antgraph.txt");

            for(int i = 0; i < nNodes; i++)
            {
                for(int j = 0; j < nNodes; j++)
                {
                    outs1.write((graph.delta(i,j) + ",").getBytes());
                }
                outs1.write('\n');
            }

            outs1.close();

            PrintStream outs2 = new PrintStream(new FileOutputStream("SP_" + nNodes + "x" + nAnts + "x" + nIterations + "_results.txt"));

            for(int i = 0; i < nRepetitions; i++)
            {
                AntColony4SP antColony = new AntColony4SP(graph, nNodesInZArray, nAnts, nIterations);
                antColony.start();
                outs2.println(i + "," + antColony.getBestPathValue() + "," + antColony.getLastBestPathIteration() + "," + antColony.getBestPath().length);
                graph.resetTau();
            }
            outs2.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}

