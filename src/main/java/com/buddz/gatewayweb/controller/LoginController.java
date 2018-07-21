package com.buddz.gatewayweb.controller;

import java.net.URLEncoder;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import feign.Headers;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import feign.jackson.JacksonEncoder;




@RestController
public class LoginController {
	
	@Autowired
	private OAuthClient oauthClient;
	
	@RequestMapping(method=RequestMethod.POST, value="/login") 
	String login(@RequestParam String username, @RequestParam String password) {
		
		MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<String, String>();
		headerMap.put("Content-Type", Collections.singletonList("application/x-www-form-urlencoded"));
		headerMap.put("Authorization", Collections.singletonList("Basic YnVkZHotd2ViOmJ1ZGR6LXdlYi1zZWNyZXQ="));
		//return oauthClient.getLoginInfo( new Credentials(username, password));
		
		MultiValueMap<String, String> credentials = new LinkedMultiValueMap<String, String>();
		credentials.put("username", Collections.singletonList(username));
		credentials.put("password", Collections.singletonList(password));
		credentials.put("grant_type", Collections.singletonList("password"));
//		return oauthClient.getLoginInfo("Basic YnVkZHotd2ViOmJ1ZGR6LXdlYi1zZWNyZXQ=",
//				//"application/x-www-form-urlencoded",
//				credentials);

		return oauthClient.getLoginInfo("Basic YnVkZHotd2ViOmJ1ZGR6LXdlYi1zZWNyZXQ=",
				//"application/x-www-form-urlencoded",
				new Credentials(username, password).toString());
	}
}

@FeignClient(url="http://localhost:9191", name="auth-client", configuration=FeignClientConfig.class)
interface OAuthClient {

	@RequestMapping(method = RequestMethod.POST, value="/oauth/token", consumes="application/x-www-form-urlencoded")
	@Headers("Content-Type: application/x-www-form-urlencoded")
//	public String getLoginInfo(@RequestHeader("Authorization") String token,
//			//				   @RequestHeader("Content-Type") String content_type,
//							   MultiValueMap<String, String> credentials);
//
	public String getLoginInfo(@RequestHeader("Authorization") String token,
			//				   @RequestHeader("Content-Type") String content_type,
							   String credentials);

	//public String getLoginInfo( @RequestBody Credentials credentials);

}

class Credentials {
	final private String username;
	final private String password;
	final private String grant_type="password";
	
	Credentials(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getGrant_type() {
		return grant_type;
	}
	
	@Override
	public String toString() {
		return "username=" + URLEncoder.encode(username) +
				"&password=" + URLEncoder.encode(password) +
				"&grant_type=password";
	}
}

class FeignClientConfig {

    @Bean
    public Encoder encoder(){
        return new FormEncoder(new JacksonEncoder());
    }
} 