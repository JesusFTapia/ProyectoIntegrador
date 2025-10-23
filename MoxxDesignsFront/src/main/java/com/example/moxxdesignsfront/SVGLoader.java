package com.example.moxxdesignsfront;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;

public class SVGLoader {

    public static Image loadSVG(String resourcePath, double width, double height) {
        try {
            InputStream input = SVGLoader.class.getResourceAsStream(resourcePath);
            if (input == null) {
                System.err.println("No se pudo encontrar el archivo: " + resourcePath);
                return null;
            }
            PNGTranscoder transcoder = new PNGTranscoder();
            transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
            transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            TranscoderInput transcoderInput = new TranscoderInput(input);
            TranscoderOutput transcoderOutput = new TranscoderOutput(outputStream);

            transcoder.transcode(transcoderInput, transcoderOutput);
            outputStream.flush();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            BufferedImage bufferedImage = ImageIO.read(inputStream);

            return SwingFXUtils.toFXImage(bufferedImage, null);

        } catch (TranscoderException e) {
            System.err.println("Error al transcodificar SVG: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}