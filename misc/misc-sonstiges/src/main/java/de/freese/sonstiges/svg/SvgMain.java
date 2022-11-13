// Created: 29.11.2018
package de.freese.sonstiges.svg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serial;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.gvt.GVTTreeRendererEvent;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderEvent;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderEvent;
import org.apache.batik.transcoder.SVGAbstractTranscoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.w3c.dom.svg.SVGDocument;

/**
 * @author Thomas Freese
 * @see SVGGraphics2D
 */
public final class SvgMain extends JFrame
{
    @Serial
    private static final long serialVersionUID = 8384522285700890883L;

    public static void main(final String[] args)
    {
        SvgMain application = new SvgMain();
        application.initAndShowGUI();

        application.addWindowListener(new WindowAdapter()
        {
            /**
             * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
             */
            @Override
            public void windowClosing(final WindowEvent event)
            {
                application.dispose();
                System.exit(0);
            }
        });
    }

    // private static void saveImageAsPng(final InputStream inputStream, final OutputStream outputStream, final float width, final float height)
    private static void saveImageAsPng(final SVGDocument svgDocument, final OutputStream outputStream, final float width, final float height)
    {
        // JPEGTranscoder transcoder = new JPEGTranscoder();
        // transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.8F);

        PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);
        // transcoder.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);

        // TranscoderInput input = new TranscoderInput(inputStream);
        TranscoderInput input = new TranscoderInput(svgDocument);

        try
        {
            TranscoderOutput output = new TranscoderOutput(outputStream);

            transcoder.transcode(input, output);

            outputStream.flush();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private SvgMain()
    {
        super();

        setTitle("Batik");
    }

    public void initAndShowGUI()
    {
        final JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton buttonLoad = new JButton("Load...");
        JButton buttonSave = new JButton("Save...");
        JLabel label = new JLabel();

        p.add(buttonLoad);
        p.add(buttonSave);
        p.add(label);

        // SVGGraphics2D
        JSVGCanvas svgCanvas = new JSVGCanvas();

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        buttonLoad.addActionListener(event ->
        {
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Vector / SVG Images", "svg");

            JFileChooser fc = new JFileChooser(".");
            fc.setFileFilter(filter);

            int choice = fc.showOpenDialog(panel);

            if (choice == JFileChooser.APPROVE_OPTION)
            {
                File svgFile = fc.getSelectedFile();

                svgCanvas.setURI(svgFile.toURI().toString());
            }
        });

        buttonSave.addActionListener(event ->
        {
            Path path = Paths.get(System.getProperty("java.io.tmpdir"), "svg-demo.png");
            // URL url = ClassLoader.getSystemResource("image.svg");
            // InputStream inputStream = new FileInputStream(url.getPath());

            try
            {
                // Dimension2D dimension = svgCanvas.getSVGDocumentSize();

                try (OutputStream outputStream = new FileOutputStream(path.toFile()))
                {
                    // saveImageAsPng(svgCanvas.getSVGDocument(), outputStream, (float) dimension.getWidth(), (float) dimension.getHeight());
                    saveImageAsPng(svgCanvas.getSVGDocument(), outputStream, 600F, 600F);
                    // saveImageAsPng(inputStream, outputStream, 600F, 600F);

                    outputStream.flush();
                }

                System.out.println("PNG written to: " + path);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });

        SwingUtilities.invokeLater(() ->
        {
            try
            {
                URL url = ClassLoader.getSystemResource("image.svg");
                svgCanvas.setURI(url.toURI().toString());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });

        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter()
        {
            /**
             * @see org.apache.batik.swing.svg.SVGDocumentLoaderAdapter#documentLoadingCompleted(org.apache.batik.swing.svg.SVGDocumentLoaderEvent)
             */
            @Override
            public void documentLoadingCompleted(final SVGDocumentLoaderEvent e)
            {
                System.out.println("Document Loaded.");
                label.setText("Document Loaded.");
            }

            /**
             * @see org.apache.batik.swing.svg.SVGDocumentLoaderAdapter#documentLoadingStarted(org.apache.batik.swing.svg.SVGDocumentLoaderEvent)
             */
            @Override
            public void documentLoadingStarted(final SVGDocumentLoaderEvent e)
            {
                System.out.println("Document Loading...");
                label.setText("Document Loading...");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter()
        {
            /**
             * @see org.apache.batik.swing.svg.GVTTreeBuilderAdapter#gvtBuildCompleted(org.apache.batik.swing.svg.GVTTreeBuilderEvent)
             */
            @Override
            public void gvtBuildCompleted(final GVTTreeBuilderEvent e)
            {
                System.out.println("Build Done.");
                label.setText("Build Done.");
                pack();
            }

            /**
             * @see org.apache.batik.swing.svg.GVTTreeBuilderAdapter#gvtBuildStarted(org.apache.batik.swing.svg.GVTTreeBuilderEvent)
             */
            @Override
            public void gvtBuildStarted(final GVTTreeBuilderEvent e)
            {
                System.out.println("Build Started...");
                label.setText("Build Started...");
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter()
        {
            /**
             * @see org.apache.batik.swing.gvt.GVTTreeRendererAdapter#gvtRenderingCompleted(org.apache.batik.swing.gvt.GVTTreeRendererEvent)
             */
            @Override
            public void gvtRenderingCompleted(final GVTTreeRendererEvent e)
            {
                label.setText("");
            }

            /**
             * @see org.apache.batik.swing.gvt.GVTTreeRendererAdapter#gvtRenderingPrepare(org.apache.batik.swing.gvt.GVTTreeRendererEvent)
             */
            @Override
            public void gvtRenderingPrepare(final GVTTreeRendererEvent e)
            {
                System.out.println("Rendering Started...");
                label.setText("Rendering Started...");
            }
        });

        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
