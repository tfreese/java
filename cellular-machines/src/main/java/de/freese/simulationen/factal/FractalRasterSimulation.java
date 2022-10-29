// Created: 27.12.2021
package de.freese.simulationen.factal;

import java.awt.Color;

import de.freese.simulationen.model.AbstractRasterSimulation;

/**
 * <a href="https://www.hameister.org/projects_fractal.html">projects_fractal</a><br>
 * <a href="https://www.gm.fh-koeln.de/~konen/Mathe2-SS2015/Apfelmaennchen/Apfelmaennchen_2.html">Apfelmännchen</a><br>
 *
 * @author Thomas Freese
 */
public class FractalRasterSimulation extends AbstractRasterSimulation
{
    /**
     * Erstellt ein neues {@link FractalRasterSimulation} Object.
     *
     * @param width int
     * @param height int
     */
    public FractalRasterSimulation(final int width, final int height)
    {
        super(width, height);

        fillRaster(() -> new FractalCell(this));
    }

    /**
     * @see de.freese.simulationen.model.AbstractRasterSimulation#getCell(int, int)
     */
    @Override
    protected FractalCell getCell(final int x, final int y)
    {
        return (FractalCell) super.getCell(x, y);
    }

    /**
     * @see de.freese.simulationen.model.Simulation#nextGeneration()
     */
    @Override
    public void nextGeneration()
    {
        // getCellStream().forEach(Cell::nextGeneration);
        //
        // fireCompleted();

        double reC, zelle = 0.00625D; // Ein Pixel = 0.00625

        Color colAppleman = new Color(0, 0, 180); // Farbe Apfelmännchen

        double imC = -1.35D; // oberer Rand

        for (int y = 0; y < getHeight(); y++)
        {
            reC = -2; // linker Rand

            for (int x = 0; x < getWidth(); x++)
            {
                FractalCell cell = getCell(x, y);

                int iterationenC = cell.checkFast(imC, reC);
                // int iterationenC = cell.checkConvergence(imC, reC);

                if (iterationenC == FractalCell.ITERATIONEN)
                {
                    cell.setColor(colAppleman);
                }
                else
                {
                    int red = Math.abs(255 - ((iterationenC % 2) * 125));
                    int green = Math.abs(255 - ((iterationenC % 7) * 55));
                    int blue = Math.abs(255 - ((iterationenC % 3) * 85));

                    // System.out.printf("r-g-b: %d-%d-%d%n", red, green, blue);

                    Color colPeriphery = new Color(red, green, blue);
                    cell.setColor(colPeriphery); // Farbe Umgebung

                    // if ((iterationenC <= 30) && (iterationenC > 25))
                    // {
                    // cell.setColor(Color.RED);
                    // }
                    // else if ((iterationenC <= 25) && (iterationenC > 20))
                    // {
                    // cell.setColor(Color.BLUE);
                    // }
                    // else if ((iterationenC <= 20) && (iterationenC > 15))
                    // {
                    // cell.setColor(Color.GREEN);
                    // }
                    // else if ((iterationenC <= 15) && (iterationenC > 10))
                    // {
                    // cell.setColor(Color.YELLOW);
                    // }
                    // else if ((iterationenC <= 10) && (iterationenC > 5))
                    // {
                    // cell.setColor(Color.PINK);
                    // }
                    // else
                    // {
                    // cell.setColor(Color.WHITE);
                    // }
                }

                reC = reC + zelle; // nächste Spalte
            }

            imC = imC + zelle; // nächste Zeile
        }

        fireCompleted();
    }
}
