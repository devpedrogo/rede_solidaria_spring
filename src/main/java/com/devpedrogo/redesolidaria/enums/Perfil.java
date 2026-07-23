package com.devpedrogo.redesolidaria.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Perfil implements GrantedAuthority{
    ROLE_ADMIN,
    ROLE_OPERADOR,
    ROLE_DOADOR,
    ROLE_BENEFICIARIO,;

    @Override
    public String getAuthority() {
        return name(); // Retorna "ROLE_ADMIN", etc.
    }
}
