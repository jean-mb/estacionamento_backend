package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class ConfiguracaoService {
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Transactional
    public Configuracao cadastrar(final Configuracao configuracao){

        /*
        * Verifica se já não existe uma configuração vigente
        * */
        final Configuracao isPrimeiraConfiguracao = this.configuracaoRepository.getConfiguracao();
        Assert.isNull(isPrimeiraConfiguracao, "Já existe uma configuração vigente, se quiser fazer alterações, edite-a!");
        Assert.isTrue(configuracao.getHoraAbertura().isBefore(configuracao.getHoraFechamento()), "O horário de abertura deve ser anterior ao horário de fechamento.");
        return this.configuracaoRepository.save(configuracao);
    }
    @Transactional
    public Configuracao editar(Configuracao novaConfiguracao){
        /*
         * Verifica se a configuracao existe
         */
        final Configuracao configuracaoVigente = this.configuracaoRepository.getConfiguracao();
        Assert.notNull(configuracaoVigente, "Não existe uma configuração vigente, crie uma configuração com o método POST");
        Assert.isTrue(novaConfiguracao.getHoraAbertura().isBefore(novaConfiguracao.getHoraFechamento()), "O horário de abertura deve ser anterior ao horário de fechamento.");

        return this.configuracaoRepository.save(novaConfiguracao);
    }
}
