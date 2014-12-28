/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.macgyver.plugin.elb.a10;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.squareup.okhttp.ConnectionSpec;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.TlsVersion;
import com.squareup.okhttp.ConnectionSpec.Builder;

import io.macgyver.core.jaxrs.SslTrust;
import io.macgyver.plugin.elb.a10.A10ClientImpl;
import io.macgyver.plugin.elb.a10.A10RemoteException;
import io.macgyver.test.MacGyverIntegrationTest;

public class A10ClientTest {

	@Test
	public void testInvalidSession() throws IOException {
		String invalidMethod = "{\n" + 
				"  \"response\" : {\n" + 
				"    \"status\" : \"fail\",\n" + 
				"    \"err\" : {\n" + 
				"      \"code\" : 1009,\n" + 
				"      \"msg\" : \"Invalid session ID\"\n" + 
				"    }\n" + 
				"  }\n" + 
				"}";

		ObjectNode x = (ObjectNode) new ObjectMapper().readTree(invalidMethod);
		
		try {
			A10ClientImpl client = new A10ClientImpl("http://localhost", "xx", "");
			client.throwExceptionIfNecessary(x);
			Assert.fail("exception not thrown");
		} catch (A10RemoteException e) {
			
			
			Assert.assertEquals("1009", e.getErrorCode());
			Assert.assertEquals("Invalid session ID",
					e.getErrorMessage());
		}
	}

	@Test
	public void testRemoteException() throws IOException {
		String json = "{\n" + "  \"response\" : {\n"
				+ "    \"status\" : \"fail\",\n" + "    \"err\" : {\n"
				+ "      \"code\" : 1008,\n"
				+ "      \"msg\" : \"Invalid web service method name\"\n"
				+ "    }\n" + "  }\n" + "}";

		ObjectNode x = (ObjectNode) new ObjectMapper().readTree(json);

		try {
			A10ClientImpl client = new A10ClientImpl("http://localhost", "xx", "");
			client.throwExceptionIfNecessary(x);
			Assert.fail("exception not thrown");
		} catch (A10RemoteException e) {
			Assert.assertEquals("1008", e.getErrorCode());
			Assert.assertEquals("Invalid web service method name",
					e.getErrorMessage());
		}
	}

	@Test
	public void testToMap() {
		A10ClientImpl client = new A10ClientImpl("http://localhost", "xx", "");
		Assert.assertTrue(client.toMap(null).isEmpty());
		Assert.assertEquals("2", client.toMap("a", "1", "b", "2").get("b"));

	}

}