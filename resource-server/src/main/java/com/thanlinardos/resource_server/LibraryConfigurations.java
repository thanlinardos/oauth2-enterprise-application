package com.thanlinardos.resource_server;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.SpringEnterpriseLibraryEnvironmentPackage;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.environment.datasource.properties.CustomDataSourceProperties;
import com.thanlinardos.spring_enterprise_library.time.SpringEnterpriseLibraryTimePackage;
import com.thanlinardos.spring_enterprise_library.time.properties.TimeProviderProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@ComponentScan(basePackageClasses = {SpringEnterpriseLibraryTimePackage.class, SpringEnterpriseLibraryEnvironmentPackage.class})
@EnableConfigurationProperties({TimeProviderProperties.class, CustomDataSourceProperties.class})
@DependsOn("timeFactory")
public class LibraryConfigurations {
}
