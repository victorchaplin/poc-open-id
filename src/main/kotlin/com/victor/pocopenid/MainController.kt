package com.victor.pocopenid

import org.springframework.http.HttpHeaders
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Controller
class MainController(
    private val authorizedClientService: OAuth2AuthorizedClientService
) {
    @RequestMapping("/")
    fun index(model: Model, authentication: OAuth2AuthenticationToken): String {
        val authorizedClient = getAuthorizedClient(authentication)
        model.apply {
            addAttribute("userName", authentication.name)
            addAttribute("clientName", authorizedClient?.clientRegistration?.clientName)
        }

        return "index"
    }

    @RequestMapping("/userinfo")
    fun userinfo(model: Model, authentication: OAuth2AuthenticationToken): String {
        val authorizedClient = getAuthorizedClient(authentication)
        val userInfoEndpointUri = authorizedClient?.clientRegistration?.providerDetails?.userInfoEndpoint?.uri
        var userAttributes = HashMap<String, Any>()

        // userInfoEndpointUri is optional for OIDC Clients
        if (!userInfoEndpointUri.isNullOrEmpty()) {
            userAttributes = WebClient.builder()
                .filter(oauth2Credentials(authorizedClient)).build()
                .get().uri(userInfoEndpointUri)
                .retrieve()
                .bodyToMono(HashMap::class.java)
                .block() as HashMap<String, Any>
        }

        model.addAttribute("userAttributes", userAttributes)

        return "userinfo"
    }

    private fun getAuthorizedClient(authentication: OAuth2AuthenticationToken): OAuth2AuthorizedClient? {
        return authorizedClientService.loadAuthorizedClient(
            authentication.authorizedClientRegistrationId,
            authentication.name
        )
    }

    private fun oauth2Credentials(authorizedClient: OAuth2AuthorizedClient): ExchangeFilterFunction {
        return ExchangeFilterFunction.ofRequestProcessor {
            val authorizedRequest = ClientRequest.from(it)
                .header(HttpHeaders.AUTHORIZATION, "Bearer ${authorizedClient.accessToken.tokenValue}")
                .build()

            Mono.just(authorizedRequest)
        }
    }
}
