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
import com.ugos.acs.tsp.*;
import com.ugos.acs.*;

public class TSPTest
{
    private static Random s_ran = new Random(System.currentTimeMillis());

    public static void main(String[] args)
    {
        // Print application prompt to console.
        System.out.println("AntColonySystem for TSP");

        if(args.length < 8)
        {
            System.out.println("Wrong number of parameters");
            return;
        }

        int nAnts = 0;
        int nNodes = 0;
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


        double d[][] = new double[nNodes][nNodes];
//        double t[][] = new double[nNodes][nNodes];

        for(int i = 0; i < nNodes; i++)
            for(int j = i + 1; j < nNodes; j++)
            {
                d[i][j] = s_ran.nextDouble();
                d[j][i] = d[i][j];
//                t[i][j] = 1; //(double)1 / (double)(nNodes * 10);
//                t[j][i] = t[i][j];
            }

         AntGraph graph = new AntGraph(nNodes, d);

        try
        {
          ObjectOutputStream outs = new ObjectOutputStream(new FileOutputStream("" + nNodes + "_antgraph.bin"));
          outs.writeObject(graph);
          outs.close();

//            ObjectInputStream ins = new ObjectInputStream(new FileInputStream("c:\\temp\\" + nNodes + "_antgraph.bin"));
//            graph = (AntGraph)ins.readObject();
//            ins.close();

            FileOutputStream outs1 = new FileOutputStream("" + nNodes + "_antgraph.txt");

            for(int i = 0; i < nNodes; i++)
            {
                for(int j = 0; j < nNodes; j++)
                {
                    outs1.write((graph.delta(i,j) + ",").getBytes());
                }
                outs1.write('\n');
            }

            outs1.close();

            PrintStream outs2 = new PrintStream(new FileOutputStream("" + nNodes + "x" + nAnts + "x" + nIterations + "_results.txt"));

            for(int i = 0; i < nRepetitions; i++)
            {
                graph.resetTau();
                AntColony4TSP antColony = new AntColony4TSP(graph, nAnts, nIterations);
                antColony.start();
                outs2.println(i + "," + antColony.getBestPathValue() + "," + antColony.getLastBestPathIteration());
            }
            outs2.close();
        }
        catch(Exception ex)
        {}
    }
}

