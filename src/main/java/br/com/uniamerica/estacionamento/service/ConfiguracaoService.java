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
        final List<Configuracao> isPrimeiraConfiguracao = this.configuracaoRepository.findAll();
        Assert.isTrue(isPrimeiraConfiguracao.isEmpty(), "Já existe uma configuração vigente, se quiser fazer alterações, edite-a com o método PUT");

        Assert.isTrue(configuracao.getHoraAbertura().isBefore(configuracao.getHoraFechamento()), "O horário de abertura deve ser anterior ao horário de fechamento.");
        return this.configuracaoRepository.save(configuracao);
    }
    @Transactional
    public Configuracao editar(Configuracao novaConfiguracao){
        /*
         * Verifica se a configuracao existe
         */
        final List<Configuracao> configuracaoExiste = this.configuracaoRepository.findAll();
        Assert.isTrue(!configuracaoExiste.isEmpty(), "Nenhuma configuração foi feita! Configure com o método POST");

        final Configuracao configuracaoVigente = this.configuracaoRepository.findById(novaConfiguracao.getId()).orElse(null);
        Assert.notNull(configuracaoVigente, String.format("Não foi possível localizar a configuração com ID [ %s ]", novaConfiguracao.getId()));

        Assert.isTrue(novaConfiguracao.getHoraAbertura().isBefore(novaConfiguracao.getHoraFechamento()), "O horário de abertura deve ser anterior ao horário de fechamento.");

        return this.configuracaoRepository.save(novaConfiguracao);
    }
}
