package com.thanlinardos.resource_server.model.info;

import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import jakarta.annotation.Nullable;

import java.util.List;

public record AuthorityInfo(String name, AccessType access, String uri, @Nullable String expression, List<String> roles) {
}
