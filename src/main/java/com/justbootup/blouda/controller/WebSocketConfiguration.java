package com.justbootup.blouda.controller;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
	
	 @Override
	    public void registerStompEndpoints(final StompEndpointRegistry registry) {
	        registry.addEndpoint("/notify").withSockJS();
	    }

	    @Override
	    public void configureClientInboundChannel(
	        final ChannelRegistration registration) {
	    }

	    @Override
	    public void configureClientOutboundChannel(
	        final ChannelRegistration registration) {
	    }

	    @Override
	    public void configureMessageBroker(final MessageBrokerRegistry config) {
	    	 config.setApplicationDestinationPrefixes("/app");
	         config.enableSimpleBroker("/queue", "/topic");
	    }

		@Override
		public boolean configureMessageConverters(List<MessageConverter> arg0) {
			// TODO Auto-generated method stub			
			return true;
		}

		
}
