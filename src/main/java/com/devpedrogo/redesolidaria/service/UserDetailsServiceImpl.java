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
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      return usuarioRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado!"));
  }

}

