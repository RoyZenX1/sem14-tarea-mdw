package pe.utp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.utp.model.Celular;
import pe.utp.repository.CelularRepository;

@Service
public class CelularService {

    @Autowired
    private CelularRepository celularRepository;

    public List<Celular> listarTodos() {
        return celularRepository.findAll();
    }

    public Optional<Celular> buscarPorNumeroSerie(String numeroSerie) {
        return celularRepository.findByNumeroSerie(numeroSerie);
    }

    public void guardar(Celular celular) {
        celularRepository.save(celular);
    }

    public void actualizar(Celular celular) {
        celularRepository.save(celular);
    }

    public void eliminar(String numeroSerie) {
        celularRepository.deleteByNumeroSerie(numeroSerie);
    }

    public boolean existe(String numeroSerie) {
        return celularRepository.existsByNumeroSerie(numeroSerie);
    }
}