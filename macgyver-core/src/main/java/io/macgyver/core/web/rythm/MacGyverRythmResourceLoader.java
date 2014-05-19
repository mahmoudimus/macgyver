package io.macgyver.core.web.rythm;

import io.macgyver.core.Kernel;
import io.macgyver.core.VfsManager;

import java.io.File;

import org.apache.commons.vfs2.FileObject;
import org.rythmengine.RythmEngine;
import org.rythmengine.resource.ITemplateResource;
import org.rythmengine.resource.ResourceLoaderBase;
import org.rythmengine.resource.TemplateResourceBase;
import org.rythmengine.utils.IO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MacGyverRythmResourceLoader extends ResourceLoaderBase {

	Logger logger = LoggerFactory.getLogger(MacGyverRythmResourceLoader.class);

	public MacGyverRythmResourceLoader() {
		super();

	}

	static RythmEngine macgyverRythmEngine;

	public static void setRhythmEngine(RythmEngine engine) {
		// this is kind of a hack
		macgyverRythmEngine = engine;
	}

	@Override
	public ITemplateResource load(String templateName) {
		templateName = MacGyverRythmResourceLoader.stripLeadingSlash(templateName);
	
		logger.trace("resolving template resource for: "+templateName);
		
		VfsManager vfsManager = Kernel.getInstance().getApplicationContext().getBean(VfsManager.class);
		
		FileObject templateVirtualDir = vfsManager.getWebLocation();
		
		File templateDir = VfsManager.asLocalFile(templateVirtualDir);

		

		final File finalTemplate = new File(convertDotsToSlash(new File(templateDir,templateName).getAbsolutePath()));
		logger.trace("looking for template file: {}", finalTemplate);
		if (finalTemplate.exists()) {
			return new TemplateResourceBase() {

			
				private static final long serialVersionUID = -8139914562941211801L;

				public boolean isValid() {
					return null != finalTemplate
							&& !finalTemplate.isDirectory()
							&& finalTemplate.canRead();
				}

				@Override
				public Object getKey() {
					return finalTemplate.getAbsolutePath();
				}

				@Override
				protected String reload() {
					return IO.readContentAsString(finalTemplate);
				}

				@Override
				protected long lastModified() {

					return finalTemplate.lastModified();
				}

				@Override
				protected long defCheckInterval() {
					// TODO Auto-generated method stub
					return 5 * 1000;
				}
			};

		}
		

		String classpathResource = "web/"+stripLeadingSlash(templateName);
		
		logger.trace("searching classpath for: {}" , classpathResource);
		CustomClasspathTemplateResource ctr = new CustomClasspathTemplateResource(
				classpathResource, this,stripLeadingSlash(templateName));
		if (ctr.exists()) {
			logger.trace("not found");
			return ctr;
		} else {
			logger.trace("did not locate template for: {}",templateName);
		}

		return null;
	}

	@Override
	public String getResourceLoaderRoot() {
		return "/";
	}

	@Override
	public RythmEngine getEngine() {
		RythmEngine parentEngine = super.getEngine();
		if (parentEngine != null) {
			return parentEngine;
		}
		return macgyverRythmEngine;
	}

	public static String convertDotsToSlash(String input) {
		int idx = input.lastIndexOf(".");
		if (idx>0) {
			return input.substring(0,idx).replace(".", "/")+input.substring(idx);
		}
		else {
			return input;
		}
	}
	public static String stripLeadingSlash(String input) {
		while (input.startsWith("/")) {
			input = input.substring(1);
		}
		return input;
	}
}
