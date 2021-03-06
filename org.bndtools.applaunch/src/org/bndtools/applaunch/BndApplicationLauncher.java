/*******************************************************************************
 * Copyright (c) 2010 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
package org.bndtools.applaunch;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.osgi.service.runnable.ApplicationLauncher;
import org.eclipse.osgi.service.runnable.ParameterizedRunnable;
import org.osgi.framework.BundleContext;

class BndApplicationLauncher implements ApplicationLauncher {

	private final Logger		log	= Logger.getLogger(BndApplicationLauncher.class.getPackage()
		.getName());
	private final BundleContext	bc;

	public BndApplicationLauncher(BundleContext context) {
		this.bc = context;
	}

	public void launch(final ParameterizedRunnable runnable, final Object context) {
		log.log(Level.FINE,
			"Received launch request from Eclipse application service, registering java.lang.Runnable{main.thread=true}");
		Runnable service = () -> {
			try {
				log.log(Level.FINE, "Executing appplication on thread {0} ({1}).", new Object[] {
					Thread.currentThread()
						.getName(),
					Thread.currentThread()
						.getId()
				});
				runnable.run(context);
			} catch (Exception e) {
				log.log(Level.SEVERE, "Error executing application", e);
			}
		};

		Dictionary<String, String> svcProps = new Hashtable<>();
		svcProps.put("main.thread", "true");
		bc.registerService(Runnable.class, service, svcProps);
	}

	public void shutdown() {
		log.warning("Ignoring shutdown call");
	}
}
