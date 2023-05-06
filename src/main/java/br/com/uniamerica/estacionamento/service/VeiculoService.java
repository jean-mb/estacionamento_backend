package br.com.uniamerica.estacionamento.service;

import br.com.uniamerica.estacionamento.entity.Modelo;
import br.com.uniamerica.estacionamento.entity.Veiculo;
import br.com.uniamerica.estacionamento.repository.ModeloRepository;
import br.com.uniamerica.estacionamento.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

@Service
public class VeiculoService {
    @Autowired
    private VeiculoRepository veiculoRepository;
    @Autowired
    private ModeloRepository modeloRepository;

    @Transactional
    public Veiculo cadastrar(Veiculo veiculo){
        Assert.notNull(veiculo.getPlaca(), "Placa não foi informada!");
        Assert.hasText(veiculo.getPlaca(), "Placa informada em branco!");
        Assert.notNull(veiculo.getCor(), "Cor não informada!");
        Assert.notNull(veiculo.getTipo(), "Tipo do veículo não foi informado!");

        final List<Veiculo> veiculosByPlaca = this.veiculoRepository.findByPlaca(veiculo.getPlaca());
        Assert.isTrue(veiculosByPlaca.isEmpty(),  String.format("Veiculo com placa [ %s ] já existe!", veiculo.getPlaca()));

        Assert.notNull(veiculo.getModelo(), "Modelo não informado!");

        final Modelo modelo = this.modeloRepository.findById(veiculo.getModelo().getId()).orElse(null);
        Assert.notNull(modelo, "Modelo informado não existe!");

        return this.veiculoRepository.save(veiculo);
    }
}
