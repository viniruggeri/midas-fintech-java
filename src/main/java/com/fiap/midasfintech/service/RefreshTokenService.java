package com.fiap.midasfintech.service;

import com.fiap.midasfintech.entity.AppUser;
import com.fiap.midasfintech.entity.RefreshToken;

public interface RefreshTokenService {

    String criarToken(AppUser appUser);

    RefreshToken validarTokenAtivo(String token);

    String rotacionarToken(RefreshToken tokenAtual);

    void revogarToken(String token);
}
