package de.uni.ba.main;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatForWar {
	private Tomcat mTomcat;
	private int port;
	private String mWorkingDir = "./";

	public TomcatForWar(int port) {
		this.port = port;
	}

	/**
	 * Starts an embedded Tomcat and adds for /ode as webapp the directory given
	 * in odeDir
	 * 
	 * @param odeDir
	 *            where the extracted ODE.war is on filesystem
	 * @throws IOException
	 * @throws LifecycleException
	 * @throws ServletException
	 */
	public void setup(String odeDir) throws IOException, LifecycleException,
			ServletException {
		mTomcat = new Tomcat();
		mTomcat.setPort(port);
		mTomcat.setBaseDir(mWorkingDir);
		mTomcat.getHost().setAppBase(mWorkingDir);
		mTomcat.getHost().setAutoDeploy(true);
		mTomcat.getHost().setDeployOnStartup(true);

		File baseDir = new File(mWorkingDir);
		mTomcat.addContext("", baseDir.getAbsolutePath());

		mTomcat.addWebapp("/ode", odeDir);

		mTomcat.start();
		mTomcat.getServer().await();
	}

}
