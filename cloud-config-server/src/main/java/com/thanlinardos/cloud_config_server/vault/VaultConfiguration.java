package com.thanlinardos.cloud_config_server.vault;

import com.thanlinardos.spring_enterprise_library.https.SecureHttpRequestFactory;
import com.thanlinardos.spring_enterprise_library.https.SslContextUtil;
import com.thanlinardos.spring_enterprise_library.model.properties.KeyAndTrustStoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

@Configuration
@Slf4j
public class VaultConfiguration {

    @Value("${spring.cloud.config.server.vault.token}")
    private String token;
    @Value("${spring.cloud.config.server.vault.host}")
    private String host;
    @Value("${spring.cloud.config.server.vault.port}")
    private String port;
    @Value("${spring.cloud.config.server.vault.scheme}")
    private String scheme;
    @Value("${spring.cloud.config.server.vault.ssl.key-store}")
    private Resource keyStore;
    @Value("${spring.cloud.config.server.vault.ssl.key-store-password}")
    private String keyStorePassword;
    @Value("${spring.cloud.config.server.vault.ssl.trust-store}")
    private Resource trustStore;
    @Value("${spring.cloud.config.server.vault.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${kv-data-path}")
    private String kvDataPath;
    @Value("${spring.application.name}")
    private String configServerName;

    @Bean
    public VaultSyncService vaultSyncService(ClientHttpRequestFactory vaultClientRequestFactory) {
        String vaultUrl = String.format("%s://%s:%s/v1/", scheme, host, port);
        RestTemplate vaultClientRestTemplate = new RestTemplate(vaultClientRequestFactory);
        return new VaultSyncService(vaultUrl, token, vaultClientRestTemplate, kvDataPath, configServerName);
    }

    @Bean
    public ClientHttpRequestFactory vaultClientRequestFactory() throws UnrecoverableKeyException, CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {
        KeyAndTrustStoreProperties keystore = new KeyAndTrustStoreProperties(keyStore, keyStorePassword);
        KeyAndTrustStoreProperties truststore = new KeyAndTrustStoreProperties(trustStore, trustStorePassword);
        return new SecureHttpRequestFactory(SslContextUtil.buildSSLContext(keystore, truststore));
    }
}
