package com.devpedrogo.redesolidaria.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ProblemaResponseDto(
    Integer status,
    String mensagem,
    LocalDateTime timestamp,
    List<CampoErro> erros
) {
    // Record auxiliar para erros de validação de formulários (@Valid)
    public record CampoErro(String campo, String mensagem) {}

    // Construtor utilitário para erros simples (sem lista de campos)
    public ProblemaResponseDto(Integer status, String mensagem) {
        this(status, mensagem, LocalDateTime.now(), null);
    }

    // Construtor utilitário para erros de validação com campos específicos
    public ProblemaResponseDto(Integer status, String mensagem, List<CampoErro> erros) {
        this(status, mensagem, LocalDateTime.now(), erros);
    }
}
