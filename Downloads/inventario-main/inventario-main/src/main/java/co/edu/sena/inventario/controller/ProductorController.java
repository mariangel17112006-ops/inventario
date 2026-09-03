package co.edu.sena.inventario.controller;

import co.edu.sena.inventario.model.Productor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/productores")
public class ProductorController {

    private final List<Productor> productores = List.of(
        new Productor(1L, "Asociación Agro Sabana", "Mosquera"),
        new Productor(2L, "Cultivos El Prado", "Facatativá"),
        new Productor(3L, "Finca La Esperanza", "Madrid")
    );

    @GetMapping
    public List<Productor> listarProductores() {
        return productores;
    }

    @GetMapping("/{id}")
    public Productor buscarProductor(@PathVariable Long id) {
        for (Productor productor : productores) {
            if (productor.getId().equals(id)) {
                return productor;
            }
        }
        return null;
    }
}