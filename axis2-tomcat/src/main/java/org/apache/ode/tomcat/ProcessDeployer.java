package org.apache.ode.tomcat;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ProcessDeployer {

	private final Path odeProcessesPath;

	/**
	 * Copies process folders to the given path (e.g. processes in WEB-INF)
	 * 
	 * @param odeProcessesPath
	 *            where to copy the to deploy processes
	 */
	public ProcessDeployer(Path odeProcessesPath) {
		this.odeProcessesPath = odeProcessesPath;
	}

	/**
	 * Copies the given process directories to the ODE processes directory
	 * 
	 * @param toDeployProcessPaths
	 *            that should be copied
	 * @throws IOException
	 */
	public void deployProcesses(List<String> toDeployProcessPaths)
			throws IOException {
		for (String singleProcessPath : toDeployProcessPaths) {
			copyProcess(Paths.get(singleProcessPath));
		}
	}

	private void copyProcess(Path processPath) throws IOException {
		if (!isValidBpelProcessDirectory(processPath)) {
			return; // path ignored
		}

		System.out.println("Copy Process " + processPath);
		Path singleProcessTargetFolder = odeProcessesPath.resolve(processPath.getFileName());

		Files.createDirectories(singleProcessTargetFolder);

		DirectoryStream<Path> dirStream = null;
		try {
			dirStream = Files.newDirectoryStream(processPath);
			for (Path sourceFile : dirStream) {
				Path target = singleProcessTargetFolder.resolve(sourceFile
						.getFileName());
				Files.copy(sourceFile, target,
						StandardCopyOption.REPLACE_EXISTING);
				System.out.println(sourceFile.toString() + " copied to "
						+ target);
			}
		} catch (IOException e) {
			System.err.println("Cannot copy process: " + e.getMessage());
		} finally {
			if (dirStream != null) {
				try {
					dirStream.close();
				} catch (IOException e2) {
					// ignore
				}
			}
		}
	}

	private boolean isValidBpelProcessDirectory(Path processPath) {
		if (Files.isDirectory(processPath)) {
			// Future validation possible
			return true;
		}
		System.out.println(processPath + " is not a directory");
		return false;
	}

}
