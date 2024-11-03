package net.cavitos.workshop.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public final class ImageLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImageLoader.class);

    private ImageLoader() {
    }

    public static byte[] loadImageFromUrl(String imageUrl) throws IOException {

        try {
            final var imageURI = new URI(imageUrl);

            final var url = imageURI.toURL();

            try (InputStream inputStream = url.openStream(); var byteArrayOutputStream = new ByteArrayOutputStream()) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                return byteArrayOutputStream.toByteArray();
            }

        } catch (Exception exception) {

            LOGGER.error("Error loading image from url", exception);
            throw new IOException("Error loading image from url", exception);
        }

    }
}