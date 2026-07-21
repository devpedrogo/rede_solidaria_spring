package com.devpedrogo.redesolidaria.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.devpedrogo.redesolidaria.dto.ProblemaResponseDto;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Trata erros de validação de DTO (@Valid / @NotNull / @NotBlank / @Min)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemaResponseDto> tratarValidacao(MethodArgumentNotValidException ex) {
        List<ProblemaResponseDto.CampoErro> erros = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ProblemaResponseDto.CampoErro(erro.getField(), erro.getDefaultMessage()))
                .toList();

        ProblemaResponseDto problema = new ProblemaResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente.",
                erros
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    // 2. Trata Entidade Não Encontrada (404 Not Found)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemaResponseDto> tratarEntidadeNaoEncontrada(EntityNotFoundException ex) {
        ProblemaResponseDto problema = new ProblemaResponseDto(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problema);
    }

    // 3. Trata Regras de Negócio Invalidadas (400 Bad Request ou 422 Unprocessable Entity)
    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ProblemaResponseDto> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        ProblemaResponseDto problema = new ProblemaResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problema);
    }

    // 4. Captura qualquer outro erro inesperado do sistema (500 Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemaResponseDto> tratarErroInesperado(Exception ex) {
        // Imprime a stacktrace no console do servidor para você conseguir debugar
        ex.printStackTrace();

        ProblemaResponseDto problema = new ProblemaResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro interno inesperado no sistema. Tente novamente mais tarde."
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problema);
    }
}
