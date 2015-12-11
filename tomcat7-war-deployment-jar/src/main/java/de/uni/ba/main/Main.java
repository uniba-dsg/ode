package de.uni.ba.main;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

public class Main {

	public static void main(String[] args) throws IOException,
			LifecycleException, ServletException {
		TomcatForWar tc = new TomcatForWar(8080);
		tc.setup("./webapps/ode");
	}
}
