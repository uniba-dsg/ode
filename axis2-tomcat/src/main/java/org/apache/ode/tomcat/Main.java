package org.apache.ode.tomcat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import org.apache.catalina.LifecycleException;

import edu.emory.mathcs.backport.java.util.Arrays;

public class Main {

	/**
	 * Execution / JAR directory
	 */
	private Path tomcatBase;

	/**
	 * Starts the Tomcat ODE server after extracting WEB-INF and copying given
	 * process folders to deployment directory.
	 * 
	 * @param toDeployProcessPaths
	 *            List of paths to directories with BPEL process files in it.
	 *            (Every process in it's single directory / path)
	 * @throws LifecycleException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public void startServer(List<String> toDeployProcessPaths)
			throws LifecycleException, URISyntaxException, IOException {

		int tomcatPort = 8080;
		tomcatBase = getActualWorkingDirTomcatBase();
		new WebInfExtractor(tomcatBase)
				.checkForExistingWebInfAndExtractIfNecessary();

		Path webInfPath = tomcatBase.toAbsolutePath().resolve("WEB-INF");
		// System.out.println(webInfPath);

		new ProcessDeployer(webInfPath.resolve("processes"))
				.deployProcesses(toDeployProcessPaths);

		System.out.println("########### Starting Tomcat and ODE ###########");
		new TomcatAxisServletStarter(tomcatBase).startTomcat(tomcatPort);

	}

	private Path getActualWorkingDirTomcatBase() throws URISyntaxException {
		Path base = Paths.get(Main.class.getProtectionDomain().getCodeSource()
				.getLocation().toURI().getPath());

		if (base.toString().endsWith("jar")) {
			int start = 0;
			int end = base.toString().lastIndexOf("/");
			base = Paths.get(base.toString().substring(start, end));
		}
		System.out.println("Tomcat base: " + base.toAbsolutePath());
		return base;
	}

	/**
	 * @param args
	 *            first arg determines whether to "start" or to "extract" the
	 *            WEB-INF directory. Further args are paths to single BPEL
	 *            folders e.g. ./HelloWorld2/
	 * @throws LifecycleException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public static void main(String[] args) throws LifecycleException,
			URISyntaxException, IOException {

		if (args.length > 0 && args[0] != null) {
			if (args[0].equals("start")) {
				List<String> arguments = new LinkedList<String>(
						Arrays.asList(args));
				arguments.remove(0);
				new Main().startServer(arguments);
			} else if (args[0].equals("extract")) {
				Path tomcatDir = new Main().getActualWorkingDirTomcatBase();
				new WebInfExtractor(tomcatDir)
						.checkForExistingWebInfAndExtractIfNecessary();
			} else {
				printUsage();
			}
		} else {
			printUsage();
		}
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("ode-tomcat.jar start|extract [BPEL-Processes...]");
		System.out
				.println("For only extracting WEB-INF Dir: ode-tomcat.jar extract");
		System.out
				.println("For starting: ode-tomcat.jar start [BPEL-Processes...]");
	}

}

