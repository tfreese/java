// Created: 29.11.2018
package de.freese.sonstiges.svg;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.svg.SVGDocument;

/**
 * @author Thomas Freese
 * @see SVGGraphics2D
 */
public final class SvgDemo extends JFrame {
    private static final Logger LOGGER = LoggerFactory.getLogger(SvgDemo.class);

    @Serial
    private static final long serialVersionUID = 8384522285700890883L;

    static BufferedImage loadSvgImage(final InputStream inputStream, final float width, final float height) throws Exception {
        final BufferedImageTranscoder transcoder = new BufferedImageTranscoder();
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);

        final TranscoderInput transcoderInput = new TranscoderInput(inputStream);
        transcoder.transcode(transcoderInput, null);

        return transcoder.getBufferedImage();
    }

    static void main() {
        final SvgDemo application = new SvgDemo();
        application.initAndShowGUI();

        application.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                application.dispose();
                System.exit(0);
            }
        });
    }

    // private static void saveImageAsPng(final InputStream inputStream, final OutputStream outputStream, final float width, final float height)
    private static void saveImageAsPng(final SVGDocument svgDocument, final OutputStream outputStream, final float width, final float height) {
        // final JPEGTranscoder transcoder = new JPEGTranscoder();
        // transcoder.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, 0.8F);

        final PNGTranscoder transcoder = new PNGTranscoder();
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_WIDTH, width);
        transcoder.addTranscodingHint(SVGAbstractTranscoder.KEY_HEIGHT, height);
        // transcoder.addTranscodingHint(ImageTranscoder.KEY_FORCE_TRANSPARENT_WHITE, true);

        // final TranscoderInput transcoderInput = new TranscoderInput(inputStream);
        final TranscoderInput transcoderInput = new TranscoderInput(svgDocument);

        try {
            final TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            transcoder.transcode(transcoderInput, transcoderOutput);

            outputStream.flush();
        }
        catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    private SvgDemo() {
        super();

        setTitle("Batik");
    }

    public void initAndShowGUI() {
        final JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        final JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));

        final JButton buttonLoad = new JButton("Load SVG");
        final JButton buttonSave = new JButton("Save as PNG");
        final JLabel label = new JLabel();

        p.add(buttonLoad);
        p.add(buttonSave);
        p.add(label);

        // SVGGraphics2D
        final JSVGCanvas svgCanvas = new JSVGCanvas();

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        buttonLoad.addActionListener(event -> {
            final FileNameExtensionFilter filter = new FileNameExtensionFilter("Vector / SVG Images", "svg");

            final JFileChooser fc = new JFileChooser(".");
            fc.setFileFilter(filter);

            final int choice = fc.showOpenDialog(panel);

            if (choice == JFileChooser.APPROVE_OPTION) {
                final File svgFile = fc.getSelectedFile();

                svgCanvas.setURI(svgFile.toURI().toString());
            }
        });

        buttonSave.addActionListener(event -> {
            final Path path = Paths.get(System.getProperty("java.io.tmpdir"), "svg-demo.png");
            // final URL url = ClassLoader.getSystemResource("images/image.svg");
            // final InputStream inputStream = new FileInputStream(url.getPath());

            try {
                // final Dimension2D dimension = svgCanvas.getSVGDocumentSize();

                try (OutputStream outputStream = new FileOutputStream(path.toFile())) {
                    // saveImageAsPng(svgCanvas.getSVGDocument(), outputStream, (float) dimension.getWidth(), (float) dimension.getHeight());
                    saveImageAsPng(svgCanvas.getSVGDocument(), outputStream, 600F, 600F);
                    // saveImageAsPng(inputStream, outputStream, 600F, 600F);

                    outputStream.flush();
                }

                LOGGER.info("PNG written to: {}", path);
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });

        SwingUtilities.invokeLater(() -> {
            try {
                final URL url = ClassLoader.getSystemResource("images/image.svg");
                svgCanvas.setURI(url.toURI().toString());
            }
            catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        });

        svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {
            @Override
            public void documentLoadingCompleted(final SVGDocumentLoaderEvent e) {
                LOGGER.info("Document Loaded.");
                label.setText("Document Loaded.");
            }

            @Override
            public void documentLoadingStarted(final SVGDocumentLoaderEvent e) {
                LOGGER.info("Document Loading...");
                label.setText("Document Loading...");
            }
        });

        svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {
            @Override
            public void gvtBuildCompleted(final GVTTreeBuilderEvent e) {
                LOGGER.info("Build Done.");
                label.setText("Build Done.");
                pack();
            }

            @Override
            public void gvtBuildStarted(final GVTTreeBuilderEvent e) {
                LOGGER.info("Build Started...");
                label.setText("Build Started...");
            }
        });

        svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {
            @Override
            public void gvtRenderingCompleted(final GVTTreeRendererEvent e) {
                label.setText("");
            }

            @Override
            public void gvtRenderingPrepare(final GVTTreeRendererEvent e) {
                LOGGER.info("Rendering Started...");
                label.setText("Rendering Started...");
            }
        });

        setSize(400, 400);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
