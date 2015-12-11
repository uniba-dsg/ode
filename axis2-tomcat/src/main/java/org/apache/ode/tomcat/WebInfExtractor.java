package org.apache.ode.tomcat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class WebInfExtractor {

	private final Path tomcatBase;

	/**
	 * Extractor for WEB-INF
	 * 
	 * @param tomcatBase
	 *            location of folder where to extract WEB-INF into. Normally
	 *            location of the jar / execution directory.
	 */
	public WebInfExtractor(Path tomcatBase) {
		this.tomcatBase = tomcatBase;
	}

	/**
	 * Extracts the WEB-INF folder into WorkingDir / JARs directory tomcat base
	 * 
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void checkForExistingWebInfAndExtractIfNecessary()
			throws IOException, URISyntaxException {
		Path odewebinf = tomcatBase.toAbsolutePath().resolve("WEB-INF");

		if (Files.exists(odewebinf)) {
			// leave as ist is
			System.out.println("Using existing WEB-INF in "
					+ odewebinf.toAbsolutePath());
		} else {
			System.out.println("Extracting WEB-INF into "
					+ odewebinf.toAbsolutePath());
			extractWebInf();
		}
	}

	private void extractWebInf() throws IOException, URISyntaxException {
		final String pathToExtract = "WEB-INF";
		final Path jarFile = Paths.get(getClass().getProtectionDomain()
				.getCodeSource().getLocation().getPath());

		if (Files.isRegularFile(jarFile)) { // Run with JAR file
			extractFromJar(pathToExtract, jarFile);
		} else { // Run with IDE
			System.out
					.println("Run with IDE not possible - please start the standalone JAR built with buildr");
		}
	}

	private void extractFromJar(final String path, final Path jarFile)
			throws IOException, URISyntaxException {
		final JarFile jar = new JarFile(jarFile.toAbsolutePath().toString());
		final Enumeration<JarEntry> entries = jar.entries(); // gives
																// ALL
																// entries
																// in
																// jar
		while (entries.hasMoreElements()) {
			final String name = entries.nextElement().getName();
			if (name.startsWith(path + "/")) { // filter according to
												// the path
				// System.out.println(name);
				Path exportPath = tomcatBase.resolve(name);
				InputStream internalPathStream = getClass().getClassLoader()
						.getResourceAsStream(name);
				URL internalPath = getClass().getClassLoader()
						.getResource(name);

				if (isDirectory(internalPath)) {
					Files.createDirectories(exportPath);
				} else {
					Files.copy(internalPathStream, exportPath,
							StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		jar.close();
	}

	private boolean isDirectory(URL internalPath) {
		return internalPath.toString().endsWith("/");
	}
}
