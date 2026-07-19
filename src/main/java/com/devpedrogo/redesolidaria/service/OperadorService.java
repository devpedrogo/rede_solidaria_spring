package com.devpedrogo.redesolidaria.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.dto.OperadorDto;
import com.devpedrogo.redesolidaria.model.OperadorEntity;
import com.devpedrogo.redesolidaria.repository.IOperadorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OperadorService {
    private final IOperadorRepository operadorRepository;
    
    public void cadastrarOperador(OperadorDto operadorDto) throws Exception {
        OperadorEntity operador = operadorRepository.findByEmail(operadorDto.getEmail()).orElse(null);

        if (operador != null) {
            throw new Exception("Email já cadastrado");
        }

        String anoAtual = String.valueOf(LocalDate.now().getYear());
        String numeroAleatorio = String.format("%04d", new Random().nextInt(10000));
        String matriculaGerada = "OP-" + anoAtual + "-" + numeroAleatorio; // Ex: OP-2026-8741

        operador = OperadorEntity.builder()
                .nome(operadorDto.getNome())
                .telefone(operadorDto.getTelefone())
                .email(operadorDto.getEmail())
                .endereco(operadorDto.getEndereco())
                .senha(operadorDto.getSenha())
                .matricula(matriculaGerada)
                .build();

        operadorRepository.save(operador);
    }

    public List<OperadorEntity> listarOperadores() {
        return operadorRepository.findAll();
    }
}
