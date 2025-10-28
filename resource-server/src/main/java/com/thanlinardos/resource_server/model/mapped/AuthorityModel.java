package com.thanlinardos.resource_server.model.mapped;

import com.thanlinardos.resource_server.model.entity.AuthorityJpa;
import com.thanlinardos.spring_enterprise_library.model.mapped.base.BasicIdModel;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.Authority;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.types.AccessType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@AllArgsConstructor
@SuperBuilder
@ToString
@EqualsAndHashCode(callSuper = true)
public class AuthorityModel extends BasicIdModel implements Serializable, Authority {

    private String name;
    private AccessType accessType;
    private String uri;
    @Nullable
    private String expression;

    public AuthorityModel(AuthorityJpa entity) {
        super(entity);
        this.name = entity.getName();
        this.accessType = entity.getAccessType();
        this.uri = entity.getUri();
        this.expression = entity.getExpression();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AccessType getAccess() {
        return accessType;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    @Nullable
    public String getExpression() {
        return expression;
    }
}
