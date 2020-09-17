package com.tfc.utils;

import entries.FlameAPI.Main;

public class Dependencies {
	
	/**
	 * downloads a maven artifact from the internet
	 *
	 * @param repo    the repo to download from
	 * @param path    the path
	 * @param name    artifact name
	 * @param version version
	 */
	public static void addDep(String repo, String path, String name, String version) {
		Main.addDep(repo, path, name, version);
	}
}
