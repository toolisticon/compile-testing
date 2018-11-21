package io.toolisticon.compiletesting;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Utility class to create JavaFileObjects and therefore also FileObjects.
 * These files can be used for comparision or as source files during compilation.
 *
 */
public class JavaFileObjectUtils {


    /**
     * A file object used to represent source coming from a string.
     */
    public static class JavaSourceFromString extends SimpleJavaFileObject {
        /**
         * The source content of this "file".
         */
        private final String content;

        /**
         * Constructs a new JavaSourceFromString.
         *
         * @param name    the name of the compilation unit represented by this file object
         * @param content the source content for the compilation unit represented by this file object
         */
        private JavaSourceFromString(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.content = content;
        }

        @Override
        public InputStream openInputStream() throws IOException {
            return new StringBufferInputStream(this.content);
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }


    }

    /**
     * Resource based java source.
     */
    public static class JavaSourceFromResource extends SimpleJavaFileObject {

        private final Class<?> relativeLocationRoot;
        private final String location;

        private JavaSourceFromResource(String location) {

            this(location, null);

        }

        private JavaSourceFromResource(String location, Class<?> relativeLocationRoot) {

            super(URI.create("resource:///" + location), Kind.SOURCE);
            this.relativeLocationRoot = relativeLocationRoot;
            this.location = location;

        }

        @Override
        public InputStream openInputStream() throws IOException {

            Class<?> relativeRoot = relativeLocationRoot != null ? relativeLocationRoot : JavaFileObjectUtils.class;
            InputStream inputStream = relativeRoot.getResourceAsStream(location);

            if (inputStream == null) {
                throw new IllegalStateException("Cannot open InputStream for resource with uri '" + uri.toString() + "' ! ");
            }

            return inputStream;
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return readFromInputStream(openInputStream());
        }
    }


    /**
     * Url based java source.
     */
    public static class JavaSourceFromUrl extends SimpleJavaFileObject {

        private final URL url;

        private JavaSourceFromUrl(URL url) throws URISyntaxException {
            super(url.toURI(), Kind.SOURCE);
            this.url = url;
        }


        @Override
        public InputStream openInputStream() throws IOException {
            return url.openStream();
        }

        @Override
        public Reader openReader(boolean ignoreEncodingErrors) throws IOException {
            return new InputStreamReader(openInputStream());
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
            return readFromInputStream(openInputStream());
        }
    }


    /**
     * Read a java source file from resurces.
     *
     * @param location             the location
     * @param relativeLocationRoot relative location root class
     * @return
     */
    public static SimpleJavaFileObject readFromResource(String location, Class<?> relativeLocationRoot) {

        if (location == null) {
            throw new IllegalArgumentException("Passed location must not be null");
        }

        return new JavaSourceFromResource(location, relativeLocationRoot);
    }

    /**
     * Read a java source file from resources.
     * Passed location will be handled as absolute path
     *
     * @param location the location
     * @return
     */
    public static SimpleJavaFileObject readFromResource(String location) {

        if (location == null) {
            throw new IllegalArgumentException("Passed location must not be null");
        }

        return new JavaSourceFromResource((!location.startsWith("/") ? "/" : "") + location, null);
    }


    /**
     * Read a java source file from resurces.
     *
     * @param location the location
     * @param content  content of the file
     * @return
     */
    public static SimpleJavaFileObject readFromString(String location, String content) {

        if (location == null) {
            throw new IllegalArgumentException("Passed location must not be null");
        }

        if (content == null) {
            throw new IllegalArgumentException("Passed content must not be null");
        }

        return new JavaSourceFromString(location, content);
    }

    /**
     * Read a java source file from resurces.
     *
     * @param url the location
     * @return
     */
    public static SimpleJavaFileObject readFromUrl(URL url) throws URISyntaxException {

        if (url == null) {
            throw new IllegalArgumentException("Passed url must not be null");
        }

        return new JavaSourceFromUrl(url);
    }

    /**
     * Reads a String from an InputStream.
     * CLoses the stream
     *
     * @param stream the inputStream to use
     * @return The String read from the inputStreams
     * @throws IOException
     */
    private static String readFromInputStream(InputStream stream) throws IOException {

        byte[] buffer = new byte[10000];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        // read bytes from stream, and store them in buffer
        while ((line = stream.read(buffer)) != -1) {
            // Writes bytes from byte array (buffer) into output stream.
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();

        return new String(os.toByteArray());
    }


}
