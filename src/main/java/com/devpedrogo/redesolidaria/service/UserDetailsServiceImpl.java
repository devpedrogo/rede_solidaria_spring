package com.devpedrogo.redesolidaria.service;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.devpedrogo.redesolidaria.repository.IUsuarioRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{
  
  private final IUsuarioRepository usuarioRepository;
  //IMPLEMENTAR ALUNOS ENTITYYYY
  /**
   * Localiza uma conta pelo e-mail para que o Spring Security carregue seus dados
   * durante a autenticação.
   *
   * @param username e-mail usado como nome de usuário
   * @return dados de segurança da conta encontrada
   * @throws UsernameNotFoundException se não houver usuário associado ao e-mail
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return usuarioRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
  }

}

