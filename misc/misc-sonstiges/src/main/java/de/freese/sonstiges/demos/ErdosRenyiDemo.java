// Created: 25 Juli 2024
package de.freese.sonstiges.demos;

import java.security.SecureRandom;
import java.util.Random;

/**
 * @author Thomas Freese
 */
public final class ErdosRenyiDemo {
    private static final Random RANDOM = new SecureRandom();

    public static void main(final String[] args) {
        // int n = Integer.parseInt(args[1]);
        final int n = 10;

        // double probability = 1.0D; // Fortlaufendes Muster
        final double probability = 0.5D;

        erdosRenyi(n, probability);
    }

    private static void erdosRenyi(final int n, final double probability) {
        if (probability <= 0D || probability > 1D) {
            System.err.println("Edge probability = " + probability + ": must be in (0, 1]");
            return;
        }

        if (n <= 0) {
            System.err.println("Number of nodes: " + n + ": must be positive");
            return;
        }

        final int[][] graph = new int[n][n];

        // Generate an undirected random graph with N nodes
        // and probability p of any two nodes to be connected.
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                final double rand = RANDOM.nextDouble(1D);

                if (rand <= probability) {
                    graph[i][j] = 1;
                    graph[j][i] = 1;
                }
            }
        }

        // Print out the resulting graph as NxN matrix of 0's and 1's
        // Use nested loops and a combination of System.print and System.println.
        // For example, a possible output for a matrix may be looks as follows:
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                System.out.print(graph[x][y]);
            }

            System.out.println();
        }
    }

    private ErdosRenyiDemo() {
        super();
    }
}
