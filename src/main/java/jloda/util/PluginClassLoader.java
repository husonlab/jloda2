/*
 * PluginClassLoader.java Copyright (C) 2023 Daniel H. Huson
 *
 * (Some files contain contributions from other authors, who are then mentioned separately.)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jloda.util;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Finds all classes in the named package, of the given type
 * <p>
 * Daniel Huson and others, 2003
 */
public class PluginClassLoader {
    /**
     * get an instance of each class of the given type in the given packages
     *
     * @return instances
     */
    public static <C> List<C> getInstances(Class<C> clazz, String... packageNames) {
        return getInstances(clazz, null, packageNames);
    }

    /**
     * get an instance of each class of the given types in the given packages
     *
     * @param clazz1       this must be assignable from the returned class
     * @param clazz2       if non-null, must also be assignable from the returned class
     * @return instances
     */
    public static <C, D> List<C> getInstances(Class<C> clazz1, Class<D> clazz2, String... packageNames) {
        return getInstances(null, clazz1, clazz2, packageNames);
    }

    /**
	 * get an instance of each class of the given types in the given packages
	 *
	 * @param className    the name of the class (ignoring case)
	 * @param clazz1       this must be assignable from the returned class
	 * @param clazz2       if non-null, must also be assignable from the returned class
	 * @param packageNames list of package names to search
	 * @return instances
	 */
    public static <C, D> List<C> getInstances(String className, Class<C> clazz1, Class<D> clazz2, String... packageNames) {
    	if(className!=null)
    		className=className.replaceAll(" ","").trim();

        final var plugins = new LinkedList<C>();
        final var packageNameQueue = new LinkedList<>(Arrays.asList(packageNames));
		while (!packageNameQueue.isEmpty()) {
            try {
                final var packageName = packageNameQueue.removeFirst();
                final var resources = ResourceUtils.fetchResources(clazz1, packageName);

                for (var i = 0; i != resources.length; ++i) {
                    // System.err.println("Resource: " + resources[i]);
                    if (resources[i].endsWith(".class")) {
                        try {
                            resources[i] = resources[i].substring(0, resources[i].length() - 6);
                            final Class<C> c = classForName(clazz1, packageName.concat(".").concat(resources[i]));
							if (!c.isInterface() && !Modifier.isAbstract(c.getModifiers()) && clazz1.isAssignableFrom(c) && (clazz2 == null || clazz2.isAssignableFrom(c)) ) {
								if(className == null || c.getSimpleName().equalsIgnoreCase(className)){
									try {
										plugins.add(c.getConstructor().newInstance());
									} catch (InstantiationException ex) {
										Basic.caught(ex);
									}
								}
							}
                        } catch (Exception ex) {
                            // Basic.caught(ex);
                        }
                    } else {
                        try {
                            final var newPackageName = resources[i];
                            if (!newPackageName.equals(packageName)) {
                                packageNameQueue.addLast(newPackageName);
                                // System.err.println("Adding package name: " + newPackageName);
                            }
                        } catch (Exception ex) {
                            // Basic.caught(ex);
                        }
                    }
                }
            } catch (IOException ex) {
                Basic.caught(ex);
            }
        }
        return plugins;
    }

    /**
     * Get a class instance for the given fully qualified classname.
     *
	 */
    public static <T> Class classForName(Class<T> clazz, String name) throws ClassNotFoundException {
        return clazz.getClassLoader().loadClass(name);
    }
}

