/**
* Helios Development Group LLC, 2013. 
 *
 */
package com.heliosapm.jmx.util.helpers;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import com.heliosapm.Configuration;


/**
 * <p>Title: ConfigurationHelper</p>
 * <p>Description: Configuration helper utilities</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.jmx.util.helpers.ConfigurationHelper</code></p>
 */
public class ConfigurationHelper {
	/** Empty String aray const */
	public static final String[] EMPTY_STR_ARR = {};
	/** Empty int aray const */
	public static final int[] EMPTY_INT_ARR = {};
	
	/** Comma splitter regex const */
	public static final Pattern COMMA_SPLITTER = Pattern.compile(",");
	
	/** If property names start with this, system properties and environment variables should be ignored. */
	public static final String NOSYSENV = "tsd.";

	/**
	 * Merges the passed properties
	 * @param properties The properties to merge
	 * @return the merged properties
	 */
	public static Properties mergeProperties(Properties...properties) {
		Properties allProps = new Properties(System.getProperties());
		for(int i = properties.length-1; i>=0; i--) {
			if(properties[i] != null && properties[i].size() >0) {
				allProps.putAll(properties[i]);
			}
		}
		return allProps;
	}
	
	
	/**
	 * Looks up a property, first in the environment, then the system properties. 
	 * If not found in either, returns the supplied default.
	 * @param name The name of the key to look up.
	 * @param defaultValue The default to return if the name is not found.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located value or the default if it was not found.
	 */
	public static String getEnvThenSystemProperty(String name, String defaultValue, Properties...properties) {
		
		String value = System.getenv(name.replace('.', '_'));
		if(value==null) {			
			value = mergeProperties(properties).getProperty(name);
		}
		if(value==null) {
			value=defaultValue;
		}
		return value;
	}
	
	/**
	 * Looks up a property, first in the system properties, then the environment. 
	 * If not found in either, returns the supplied default.
	 * @param name The name of the key to look up.
	 * @param defaultValue The default to return if the name is not found.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located value or the default if it was not found.
	 */
	public static String getSystemThenEnvProperty(String name, String defaultValue, Properties...properties) {
		if(name==null || name.trim().isEmpty()) throw new IllegalArgumentException("The passed property name was null or empty");
		if(name.trim().toLowerCase().startsWith(NOSYSENV)) {
			if(properties==null || properties.length==0 || properties[0]==null) return defaultValue;
			return properties[0].getProperty(name.trim(), defaultValue);
		}
		String value = mergeProperties(properties).getProperty(name);
		if(value==null) {
			value = System.getenv(name.replace('.', '_'));
		}
		if(value==null) {
			value=defaultValue;
		}
		return value;
	}
	
	/** The default value passed for an empty array */
	public static final String EMPTY_ARRAY_TOKEN = "_org_helios_empty_array_";
	
	/**
	 * Looks up a property and converts to a string array, first in the system properties, then the environment. 
	 * If not found in either, returns the supplied default.
	 * @param name The name of the key to look up.
	 * @param defaultValue The default to return if the name is not found. Expected as a comma separated list of strings
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located value or the default if it was not found.
	 */
	public static String[] getSystemThenEnvPropertyArray(String name, String defaultValue, Properties...properties) {
		if(defaultValue.isEmpty()) defaultValue = EMPTY_ARRAY_TOKEN;
		String raw = getSystemThenEnvProperty(name, defaultValue, properties);
		if(EMPTY_ARRAY_TOKEN.equals(raw)) return EMPTY_STR_ARR; 
		List<String> values = new ArrayList<String>();
		for(String s: COMMA_SPLITTER.split(raw.trim())) {
			if(s.trim().isEmpty()) continue;
			values.add(s.trim());
		}
		return values.toArray(new String[0]);
	}

	/**
	 * Looks up a property and converts to an int array, first in the system properties, then the environment. 
	 * If not found in either, returns the supplied default.
	 * @param name The name of the key to look up.
	 * @param defaultValue The default to return if the name is not found. Expected as a comma separated list of strings
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located value or the default if it was not found.
	 */
	public static int[] getIntSystemThenEnvPropertyArray(String name, String defaultValue, Properties...properties) {
		String raw = getSystemThenEnvProperty(name, defaultValue, properties);
		if(raw==null || raw.trim().isEmpty()) return EMPTY_INT_ARR;
		List<Integer> values = new ArrayList<Integer>();
		for(String s: COMMA_SPLITTER.split(raw.trim())) {
			if(s.trim().isEmpty()) continue;
			try { values.add(new Integer(s.trim())); } catch (Exception ex) {}
		}		
		if(values.isEmpty()) return EMPTY_INT_ARR;
		int[] ints = new int[values.size()];
		for(int i = 0; i < values.size(); i++) {
			ints[i] = values.get(i);
		}
		return ints;
	}
	
	
	/**
	 * Determines if a name has been defined in the environment or system properties.
	 * @param name the name of the property to check for.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return true if the name is defined in the environment or system properties.
	 */
	public static boolean isDefined(String name, Properties...properties) {
		if(System.getenv(name) != null) return true;
		if(mergeProperties(properties).getProperty(name) != null) return true;
		return false;		
	}
	
	/**
	 * Determines if a name has been defined as a valid int in the environment or system properties.
	 * @param name the name of the property to check for.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return true if the name is defined as a valid int in the environment or system properties.
	 */
	public static boolean isIntDefined(String name, Properties...properties) {
		String tmp = getEnvThenSystemProperty(name, null, properties);
		if(tmp==null) return false;
		try {
			Integer.parseInt(tmp);
			return true;
		} catch (Exception e) {
			return false;
		}				
	}
	
	/**
	 * Determines if a name has been defined as a valid boolean in the environment or system properties.
	 * @param name the name of the property to check for.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return true if the name is defined as a valid boolean in the environment or system properties.
	 */
	public static boolean isBooleanDefined(String name, Properties...properties) {
		String tmp = getEnvThenSystemProperty(name, null, properties);
		if(tmp==null) return false;
		try {
			tmp = tmp.toUpperCase();
			if(
					tmp.equalsIgnoreCase("TRUE") || tmp.equalsIgnoreCase("Y") || tmp.equalsIgnoreCase("YES") ||
					tmp.equalsIgnoreCase("FALSE") || tmp.equalsIgnoreCase("N") || tmp.equalsIgnoreCase("NO")
			) return true;
			return false;
		} catch (Exception e) {
			return false;
		}				
	}	
	
	/**
	 * Determines if a name has been defined as a valid long in the environment or system properties.
	 * @param name the name of the property to check for.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return true if the name is defined as a valid long in the environment or system properties.
	 */
	public static boolean isLongDefined(String name, Properties...properties) {
		String tmp = getEnvThenSystemProperty(name, null, properties);
		if(tmp==null) return false;
		try {
			Long.parseLong(tmp);
			return true;
		} catch (Exception e) {
			return false;
		}				
	}
	
	/**
	 * Returns the value defined as an Integer looked up from the Environment, then System properties.
	 * @param name The name of the key to lookup.
	 * @param defaultValue The default value to return if the name is not defined or the value is not a valid int.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located integer or the passed default value.
	 */
	public static Integer getIntSystemThenEnvProperty(String name, Integer defaultValue, Properties...properties) {
		String tmp = getSystemThenEnvProperty(name, null, properties);
		try {
			return Integer.parseInt(tmp);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	/**
	 * Returns the value defined as an Float looked up from the Environment, then System properties.
	 * @param name The name of the key to lookup.
	 * @param defaultValue The default value to return if the name is not defined or the value is not a valid int.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located float or the passed default value.
	 */
	public static Float getFloatSystemThenEnvProperty(String name, Float defaultValue, Properties...properties) {
		String tmp = getSystemThenEnvProperty(name, null, properties);
		try {
			return Float.parseFloat(tmp);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	
	/**
	 * Returns the value defined as a Long looked up from the Environment, then System properties.
	 * @param name The name of the key to lookup.
	 * @param defaultValue The default value to return if the name is not defined or the value is not a valid long.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located long or the passed default value.
	 */
	public static Long getLongSystemThenEnvProperty(String name, Long defaultValue, Properties...properties) {
		String tmp = getSystemThenEnvProperty(name, null, properties);
		try {
			return Long.parseLong(tmp);
		} catch (Exception e) {
			return defaultValue;
		}
	}	
	
	/**
	 * Returns the value defined as a Boolean looked up from the Environment, then System properties.
	 * @param name The name of the key to lookup.
	 * @param defaultValue The default value to return if the name is not defined or the value is not a valid boolean.
	 * @param properties An array of properties to search in. If empty or null, will search system properties. The first located match will be returned.
	 * @return The located boolean or the passed default value.
	 */
	public static Boolean getBooleanSystemThenEnvProperty(String name, Boolean defaultValue, Properties...properties) {
		String tmp = getSystemThenEnvProperty(name, null, properties);
		if(tmp==null) return defaultValue;
		tmp = tmp.toUpperCase();
		if(tmp.equalsIgnoreCase("TRUE") || tmp.equalsIgnoreCase("Y") || tmp.equalsIgnoreCase("YES")) return true;
		if(tmp.equalsIgnoreCase("FALSE") || tmp.equalsIgnoreCase("N") || tmp.equalsIgnoreCase("NO")) return false;
		return defaultValue;
	}
	
	/**
	 * Attempts to create an instance of the passed class using one of:<ol>
	 * 	<li>Attempts to find a Constructor with the passed signature</li>
	 * 	<li>Attempts to find a static factory method called <b><code>getInstance</code></b> with the passed signature</li>
	 * 	<li>Attempts to find a static factory method called <b><code>newInstance</code></b> with the passed signature</li>
	 * </ol>
	 * @param clazz The class to create an instance of
	 * @param sig The signature of the constructor or static factory method
	 * @param args The arguments to the constructor or static factory method
	 * @return The created instance
	 * @throws Exception thrown on any error
	 */
	public static <T> T inst(Class<T> clazz, Class<?>[] sig, Object...args) throws Exception {
		Constructor<T> ctor = null;
		try {
			ctor = clazz.getDeclaredConstructor(sig);
			return ctor.newInstance(args);
		} catch (Exception e) {
			Method method = null;
			try { method = clazz.getDeclaredMethod("getInstance", sig); 
				if(!Modifier.isStatic(method.getModifiers())) throw new Exception();
			} catch (Exception ex) {}
			if(method==null) {
				try { method = clazz.getDeclaredMethod("newInstance", sig); } catch (Exception ex) {}
			}
			if(method==null) throw new Exception("Failed to find Constructor or Static Factory Method for [" + clazz.getName() + "]");
			if(!Modifier.isStatic(method.getModifiers())) throw new Exception("Factory Method [" + method.toGenericString() + "] is not static");
			return (T)method.invoke(null, args);
		}
	}
	
	/** Empty class signature const */
	public static final Class<?>[] EMPTY_SIG = {};
	/** Empty arg const */
	public static final Object[] EMPTY_ARGS = {};
	
	/**
	 * Attempts to create an instance of the passed class using one of:<ol>
	 * 	<li>Attempts to find a Constructor</li>
	 * 	<li>Attempts to find a static factory method called <b><code>getInstance</code></b></li>
	 * 	<li>Attempts to find a static factory method called <b><code>newInstance</code></b></li>
	 * </ol>
	 * @param clazz The class to create an instance of
	 * @return The created instance
	 * @throws Exception thrown on any error
	 */
	public static <T> T inst(Class<T> clazz) throws Exception {
		return inst(clazz, EMPTY_SIG, EMPTY_ARGS);
	}
	
	/**
	 * Creates a new config from the passed CL args
	 * @param args The command line args
	 * @return the Config instance
	 */
	public static Config newInstance(final String...args) {
		return new Config(args);
	}
	
	
	/**
	 * <p>Title: Config</p>
	 * <p>Description: Wraps the parsed command line configuration</p> 
	 * <p>Company: Helios Development Group LLC</p>
	 * @author Whitehead (nwhitehead AT heliosdev DOT org)
	 * <p><code>com.heliosapm.jmx.util.helpers.ConfigurationHelper.Config</code></p>
	 */
	@SuppressWarnings("unchecked")
	public static class Config {
		private final Map<String, Object> config = new HashMap<String, Object>();
		/** The config key prefix */
		public static final String CONFIG_PREFIX = "--";
		/** The config key suffix for a property key */
		public static final String KEY_SUFFIX = "_PROP";
		/** The config key suffix for a property default */
		public static final String DEFAULT_SUFFIX = "_DEFAULT";
		
		
		private static final Map<String, Class<?>> configTypes = new HashMap<String, Class<?>>(); 
		private static final Map<String, Object> propToDef = new HashMap<String, Object>();
		private static final Map<Class<?>, PropertyEditor> propEditors = new HashMap<Class<?>, PropertyEditor>(); 
		@SuppressWarnings("unchecked")
		private static final Set<Class<?>> BOOLEANS = Collections.unmodifiableSet(new HashSet<Class<?>>(Arrays.asList(Boolean.class, boolean.class)));
		
		static {
			
			final Map<String, String> defFieldToProp = new HashMap<String, String>();
			
			Field[] fields = Configuration.class.getDeclaredFields();
			Set<Field> defaultFields = new HashSet<Field>();
			for(Field f: fields) {
				final int mod = f.getModifiers();
				if(!Modifier.isStatic(mod) || !Modifier.isPublic(mod)) continue;
				final String name = f.getName();
				try {
					if(name.endsWith(KEY_SUFFIX)) {
						//  HTTP_PORT_DEFAULT -->  http.port
						defFieldToProp.put(name.replace(KEY_SUFFIX, DEFAULT_SUFFIX).toUpperCase(), f.get(null).toString().toUpperCase());
					} else if(name.endsWith(DEFAULT_SUFFIX)) {
						defaultFields.add(f);
					}
				} catch (Exception x) {/* No Op */}
			}
			for(Field f: defaultFields) {
				try {
					final String name = f.getName();
					final String propName = defFieldToProp.get(name);				
					propToDef.put(propName.toUpperCase(), f.get(null));
					configTypes.put(propName.toUpperCase(), f.getType());
					propEditors.put(f.getType(), PropertyEditorManager.findEditor(f.getType()));
				} catch (Exception x) {/* No Op */}
			}
		}
		
		
		private void setFlag(final String key) {
			Class<?> clazz = configTypes.get(key);
			if(clazz==null || BOOLEANS.contains(clazz)) {
				config.put(key, true);
			}
		}
		
		private void setPair(final String key, final String value) {
			Class<?> clazz = configTypes.get(key);
			if(clazz==null) {
				config.put(key, value);
			} else {
				PropertyEditor pe = propEditors.get(clazz);
				pe.setAsText(value);
				config.put(key, pe.getValue());
			}			
		}
		
		private Config(final String...args) {
			final int cnt = args.length;
			final int last = args.length-1;
			for(int i = 0; i < cnt; i++) {
				if(args[i].startsWith(CONFIG_PREFIX)) {
					if(i==last) {
						setFlag(args[i].replace(CONFIG_PREFIX, "").toUpperCase());
						break;
					}
					if(!args[i+1].startsWith(CONFIG_PREFIX)) {
						setFlag(args[i].replace(CONFIG_PREFIX, "").toUpperCase());
						i++;
					} else {
						setPair(args[i].replace(CONFIG_PREFIX, "").toUpperCase(), args[i+1]);
						i++;
					}
				}
			}
		}		 
		
		
		/**
		 * Returns the configuration value for the passed key
		 * @param name the key name
		 * @param type The expected type
		 * @return the value or null if not found
		 */		
		public <T> T get(final String name, final Class<T> type) {
			T t = (T)config.get(name.trim().toUpperCase());
			if(t==null) {
				t = (T)propToDef.get(name.trim().toUpperCase());
			}
			return t;
		}
		
		/**
		 * Returns the configuration value for the passed key
		 * @param name the key name
		 * @return the value or null if not found
		 */
		public Object get(final String name) {
			return propToDef.get(name.trim().toUpperCase());
		}
	}

}
