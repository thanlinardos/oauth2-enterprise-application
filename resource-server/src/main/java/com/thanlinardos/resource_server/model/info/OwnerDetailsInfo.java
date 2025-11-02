package com.thanlinardos.resource_server.model.info;

import com.thanlinardos.resource_server.model.mapped.RoleModel;
import com.thanlinardos.spring_enterprise_library.parse.utils.ParserUtil;
import com.thanlinardos.spring_enterprise_library.spring_cloud_security.model.base.PrivilegedResource;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class OwnerDetailsInfo implements Serializable, PrivilegedResource {

    private UUID uuid;
    @NotBlank
    private String name;
    private List<RoleModel> roles;
    private LocalDate createDt;

    @Override
    public int getPrivilegeLevel() {
        return PrivilegedResource.calcPrivilegeLvlFromRoles(getRoles());
    }

    @Override
    public String getPrincipalName() {
        return ParserUtil.safeParseString(uuid);
    }
}
