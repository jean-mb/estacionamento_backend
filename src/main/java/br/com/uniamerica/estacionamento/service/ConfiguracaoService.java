package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Configuracao;
import br.com.uniamerica.estacionamento.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class ConfiguracaoService {
    @Autowired
    private ConfiguracaoRepository configuracaoRepository;
    @Transactional
    public Configuracao cadastrar(final Configuracao configuracao){
        Assert.notNull(configuracao.getValorHora(), "Valor hora não informada!");
        Assert.notNull(configuracao.getHoraAbertura(), "Hora de abertura não informada!");
        Assert.notNull(configuracao.getHoraFechamento(), "Hora de fechamento não informada!");

        return this.configuracaoRepository.save(configuracao);
    }
    @Transactional
    public Configuracao editar(Long id, Configuracao configuracao){
        /*
         * Verifica se a configuracao existe
         */
        final Configuracao configuracaoBanco = this.configuracaoRepository.findById(id).orElse(null);
        Assert.notNull(configuracaoBanco, "Configuração não existe!");

        /*
         * Verifica as configurações coincidem
         */
        Assert.isTrue(configuracao.getId().equals(configuracaoBanco.getId()), "Configuração informada não é o mesma que a configuração a ser atualizada");


        Assert.notNull(configuracao.getValorHora(), "Valor hora não informada!");
        Assert.notNull(configuracao.getHoraAbertura(), "Hora de abertura não informada!");
        Assert.notNull(configuracao.getHoraFechamento(), "Hora de fechamento não informada!");

        return this.configuracaoRepository.save(configuracao);
    }
}
