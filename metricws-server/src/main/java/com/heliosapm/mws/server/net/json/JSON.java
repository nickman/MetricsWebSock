/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.mws.server.net.json;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.util.JSONPObject;


/**
 * <p>Title: JSON</p>
 * <p>Description: Static jackson Json methods</p> 
 * <p>Company: Helios Development Group LLC</p>
 * <p>Mostly copied from <a href="http://opentsdb.net">OpenTSDB</a>.
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.mws.server.net.json.JSON</code></p>
 */

public class JSON {
	  private static final ObjectMapper jsonMapper = new ObjectMapper();
	  static {
	    jsonMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
	    jsonMapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	  }
	  
	  /**
	   * Deserializes a JSON formatted string to a specific class type
	   * <b>Note:</b> If you get mapping exceptions you may need to provide a 
	   * TypeReference
	   * @param json The string to deserialize
	   * @param pojo The class type of the object used for deserialization
	   * @return An object of the {@code pojo} type
	   * @param <T> The expected type of the return object
	   * @throws IllegalArgumentException if the data or class was null or parsing 
	   * failed
	   * @throws JSONException if the data could not be parsed
	   */
	  public static final <T> T parseToObject(final String json,
	      final Class<T> pojo) {
	    if (json == null || json.isEmpty())
	      throw new IllegalArgumentException("Incoming data was null or empty");
	    if (pojo == null)
	      throw new IllegalArgumentException("Missing class type");
	    
	    try {
	      return jsonMapper.readValue(json, pojo);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (JsonMappingException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Deserializes a JSON formatted byte array to a specific class type
	   * <b>Note:</b> If you get mapping exceptions you may need to provide a 
	   * TypeReference
	   * @param json The byte array to deserialize
	   * @param pojo The class type of the object used for deserialization
	   * @return An object of the {@code pojo} type
	   * @param <T> The expected type of the return object
	   * @throws IllegalArgumentException if the data or class was null or parsing 
	   * failed
	   * @throws JSONException if the data could not be parsed
	   */
	  public static final <T> T parseToObject(final byte[] json,
	      final Class<T> pojo) {
	    if (json == null)
	      throw new IllegalArgumentException("Incoming data was null");
	    if (pojo == null)
	      throw new IllegalArgumentException("Missing class type");
	    try {
	      return jsonMapper.readValue(json, pojo);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (JsonMappingException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Deserializes a JSON formatted string to a specific class type
	   * @param json The string to deserialize
	   * @param type A type definition for a complex object
	   * @return An object of the {@code pojo} type
	   * @param <T> The expected type of the return object
	   * @throws IllegalArgumentException if the data or type was null or parsing
	   * failed
	   * @throws JSONException if the data could not be parsed
	   */
	  @SuppressWarnings("unchecked")
	  public static final <T> T parseToObject(final String json,
	      final TypeReference<T> type) {
	    if (json == null || json.isEmpty())
	      throw new IllegalArgumentException("Incoming data was null or empty");
	    if (type == null)
	      throw new IllegalArgumentException("Missing type reference");
	    try {
	      return (T)jsonMapper.readValue(json, type);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (JsonMappingException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Deserializes a JSON formatted byte array to a specific class type 
	   * @param json The byte array to deserialize
	   * @param type A type definition for a complex object
	   * @return An object of the {@code pojo} type
	   * @param <T> The expected type of the return object
	   * @throws IllegalArgumentException if the data or type was null or parsing
	   * failed
	   * @throws JSONException if the data could not be parsed
	   */
	  @SuppressWarnings("unchecked")
	  public static final <T> T parseToObject(final byte[] json,
	      final TypeReference<T> type) {
	    if (json == null)
	      throw new IllegalArgumentException("Incoming data was null");
	    if (type == null)
	      throw new IllegalArgumentException("Missing type reference");
	    try {
	      return (T)jsonMapper.readValue(json, type);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (JsonMappingException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Parses a JSON formatted string into raw tokens for streaming or tree
	   * iteration
	   * <b>Warning:</b> This method can parse an invalid JSON object without
	   * throwing an error until you start processing the data
	   * @param json The string to parse
	   * @return A JsonParser object to be used for iteration
	   * @throws IllegalArgumentException if the data was null or parsing failed
	   * @throws JSONException if the data could not be parsed
	   */
	  public static final JsonParser parseToStream(final String json) {
	    if (json == null || json.isEmpty())
	      throw new IllegalArgumentException("Incoming data was null or empty");
	    try {
	      return jsonMapper.getFactory().createParser(json);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Parses a JSON formatted byte array into raw tokens for streaming or tree
	   * iteration
	   * <b>Warning:</b> This method can parse an invalid JSON object without
	   * throwing an error until you start processing the data
	   * @param json The byte array to parse
	   * @return A JsonParser object to be used for iteration
	   * @throws IllegalArgumentException if the data was null or parsing failed
	   * @throws JSONException if the data could not be parsed
	   */
	  public static final JsonParser parseToStream(final byte[] json) {
	    if (json == null)
	      throw new IllegalArgumentException("Incoming data was null");
	    try {
	      return jsonMapper.getFactory().createParser(json);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Parses a JSON formatted inputs stream into raw tokens for streaming or tree
	   * iteration
	   * <b>Warning:</b> This method can parse an invalid JSON object without
	   * throwing an error until you start processing the data
	   * @param json The input stream to parse
	   * @return A JsonParser object to be used for iteration
	   * @throws IllegalArgumentException if the data was null or parsing failed
	   * @throws JSONException if the data could not be parsed
	   */
	  public static final JsonParser parseToStream(final InputStream json) {
	    if (json == null)
	      throw new IllegalArgumentException("Incoming data was null");
	    try {
	      return jsonMapper.getFactory().createParser(json);
	    } catch (JsonParseException e) {
	      throw new IllegalArgumentException(e);
	    } catch (IOException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Serializes the given object to a JSON string
	   * @param object The object to serialize
	   * @return A JSON formatted string
	   * @throws IllegalArgumentException if the object was null
	   * @throws JSONException if the object could not be serialized
	   * @throws IOException Thrown when there was an issue reading the object
	   */
	  @SuppressWarnings("javadoc")
	public static final String serializeToString(final Object object) {
	    if (object == null)
	      throw new IllegalArgumentException("Object was null");
	    try {
	      return jsonMapper.writeValueAsString(object);
	    } catch (JsonProcessingException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Serializes the given object to a JSON byte array
	   * @param object The object to serialize
	   * @return A JSON formatted byte array
	   * @throws IllegalArgumentException if the object was null
	   * @throws JSONException if the object could not be serialized
	   * @throws IOException Thrown when there was an issue reading the object
	   */
	  @SuppressWarnings("javadoc")
	  public static final byte[] serializeToBytes(final Object object) {
	    if (object == null)
	      throw new IllegalArgumentException("Object was null");
	    try {
	      return jsonMapper.writeValueAsBytes(object);
	    } catch (JsonProcessingException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Serializes the given object and wraps it in a callback function
	   * i.e. &lt;callback&gt;(&lt;json&gt)
	   * Note: This will not append a trailing semicolon
	   * @param callback The name of the Javascript callback to prepend
	   * @param object The object to serialize
	   * @return A JSONP formatted string
	   * @throws IllegalArgumentException if the callback method name was missing 
	   * or object was null
	   * @throws JSONException if the object could not be serialized
	   * @throws IOException Thrown when there was an issue reading the object
	   */
	  @SuppressWarnings("javadoc")
	  public static final String serializeToJSONPString(final String callback,
	      final Object object) {
	    if (callback == null || callback.isEmpty())
	      throw new IllegalArgumentException("Missing callback name");
	    if (object == null)
	      throw new IllegalArgumentException("Object was null");
	    try {
	      return jsonMapper.writeValueAsString(new JSONPObject(callback, object));
	    } catch (JsonProcessingException e) {
	      throw new JSONException(e);
	    }
	  }
	  
	  /**
	   * Serializes the given object and wraps it in a callback function
	   * i.e. &lt;callback&gt;(&lt;json&gt)
	   * Note: This will not append a trailing semicolon
	   * @param callback The name of the Javascript callback to prepend
	   * @param object The object to serialize
	   * @return A JSONP formatted byte array
	   * @throws IllegalArgumentException if the callback method name was missing 
	   * or object was null
	   * @throws JSONException if the object could not be serialized
	   * @throws IOException Thrown when there was an issue reading the object
	   */
	  @SuppressWarnings("javadoc")
	  public static final byte[] serializeToJSONPBytes(final String callback,
	      final Object object) {
	    if (callback == null || callback.isEmpty())
	      throw new IllegalArgumentException("Missing callback name");
	    if (object == null)
	      throw new IllegalArgumentException("Object was null");
	    try {
	      return jsonMapper.writeValueAsBytes(new JSONPObject(callback, object));
	    } catch (JsonProcessingException e) {
	      throw new JSONException(e);
	    }
	  }

	  /**
	   * Returns a reference to the static ObjectMapper
	   * @return The ObjectMapper
	   */
	  public final static ObjectMapper getMapper() {
	    return jsonMapper;
	  }

	  /**
	   * Returns a reference to the JsonFactory for streaming creation
	   * @return The JsonFactory object
	   */
	  public final static JsonFactory getFactory() {		  
	    return jsonMapper.getFactory();
	  }
	  
	/**
	 * @return
	 */
	public final static JsonNodeFactory getNodeFactory() {
		  return jsonMapper.getNodeFactory();
	  }
	  

	private JSON() {}

}
