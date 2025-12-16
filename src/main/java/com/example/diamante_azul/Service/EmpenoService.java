package com.example.diamante_azul.Service;

import com.example.diamante_azul.Models.Empeno;
import com.example.diamante_azul.Models.Producto;
import com.example.diamante_azul.Repository.EmpenoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class EmpenoService {

    private final EmpenoRepository empenoRepository;
    private final ProductoService productoService;

    public EmpenoService(EmpenoRepository empenoRepository, ProductoService productoService) {
        this.empenoRepository = empenoRepository;
        this.productoService = productoService;
    }

    public  List<Empeno> findAll(){
        return empenoRepository.findAll();
    }
    public List<Empeno> listarEmpenosActivos() {
        return empenoRepository.findByEstadoEmpeno("ACTIVO");
    }
    public List<Empeno> listarTodosEmpenos(){return empenoRepository.findAll();}

    public Optional<Empeno> buscarPorId(Integer id) {
        return empenoRepository.findById(id);
    }

    // AGREGA ESTE MÉTODO QUE FALTABA
    public List<Empeno> findByClienteId(Integer clienteId) {
        return empenoRepository.findEmpenosDelCliente(clienteId);
    }

    @Transactional
    public Empeno guardarEmpeno(Empeno empeno) {
        return empenoRepository.save(empeno);
    }

    @Transactional
    public Empeno crearEmpeno(Empeno empeno) {
        Producto productoEmpenado = productoService.findById(empeno.getProducto().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoService.actualizarEstadoProducto(productoEmpenado.getId(), "EN EMPENO");
        empeno.setProducto(productoEmpenado);

        if (empeno.getFechaEmpeno() == null) {
            empeno.setFechaEmpeno(LocalDate.now());
        }
        if (empeno.getFechaVencimiento() == null) {
            empeno.setFechaVencimiento(empeno.getFechaEmpeno().plusDays(30));
        }
        if (empeno.getEstadoEmpeno() == null || empeno.getEstadoEmpeno().isEmpty()){
            empeno.setEstadoEmpeno("ACTIVO");
        }
        return empenoRepository.save(empeno);
    }

    public BigDecimal calcularTotalAPagar(Empeno empeno, int diasTranscurridos) {
        BigDecimal montoBase = empeno.getMontoPrestado();
        BigDecimal tasaInteres = empeno.getTasaInteres();
        final BigDecimal CIEN = new BigDecimal("100");
        final BigDecimal DIAS_POR_MES = new BigDecimal("30");

        BigDecimal tasaDecimal = tasaInteres.divide(CIEN, 4, RoundingMode.HALF_UP);
        BigDecimal factorTiempo = new BigDecimal(diasTranscurridos).divide(DIAS_POR_MES, 4, RoundingMode.HALF_UP);
        BigDecimal interesAcumulado = montoBase
                .multiply(tasaDecimal)
                .multiply(factorTiempo)
                .setScale(2, RoundingMode.HALF_UP);

        empeno.setInteres(interesAcumulado);
        return montoBase.add(interesAcumulado);
    }

    @Transactional
    public void finalizarEmpeno(Integer empenoId, String nuevoEstado) {
        Empeno empeno = empenoRepository.findById(empenoId)
                .orElseThrow(() -> new RuntimeException("Empeno no encontrado"));
        
        String nuevoEstadoProducto;

        if ("PAGADO".equalsIgnoreCase(nuevoEstado)) {
            nuevoEstadoProducto = "ACTIVO";
        } else if ("PERDIDO".equalsIgnoreCase(nuevoEstado)) {
            nuevoEstadoProducto = "EN VENTA";
        } else {
            empeno.setEstadoEmpeno(nuevoEstado);
            empenoRepository.save(empeno);
            return;
        }

        empeno.setEstadoEmpeno(nuevoEstado);
        empenoRepository.save(empeno);
        productoService.actualizarEstadoProducto(empeno.getProducto().getId(), nuevoEstadoProducto);
    }
}