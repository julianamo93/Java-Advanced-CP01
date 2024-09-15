package com.fiap.biblioteca.Controller;

import com.fiap.biblioteca.Model.dto.LivroDTO;
import com.fiap.biblioteca.Model.dto.ReservaDTO;
import com.fiap.biblioteca.Model.Livro;
import com.fiap.biblioteca.Service.GerenciadorBiblioteca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Queue;

@RestController
@RequestMapping("/livros")
public class LivroController {

    @Autowired
    private GerenciadorBiblioteca gerenciadorBiblioteca;

    @PostMapping
    public ResponseEntity<Livro> criarLivro(@RequestBody LivroDTO livroDTO) {
        Livro livro = new Livro();
        livro.setIsbn(livroDTO.getIsbn());
        livro.setTitulo(livroDTO.getTitulo());
        livro.setAutor(livroDTO.getAutor());
        livro.setCategoria(livroDTO.getCategoria());
        return ResponseEntity.ok(gerenciadorBiblioteca.criarLivro(livro));
    }

    @GetMapping
    public ResponseEntity<List<Livro>> listarLivros(
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String categoria) {
        List<Livro> livros = gerenciadorBiblioteca.listarLivros(ordenarPor, categoria);
        if (livros.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(livros);
    }

    @DeleteMapping("/{isbn}")
    public ResponseEntity<Void> excluirLivro(@PathVariable String isbn) {
        gerenciadorBiblioteca.excluirLivro(isbn);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reservas/{isbn}")
    public ResponseEntity<Void> reservarLivro(@PathVariable String isbn, @RequestBody ReservaDTO reservaDTO) {
        gerenciadorBiblioteca.reservarLivro(isbn, reservaDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservas/{isbn}")
    public ResponseEntity<Queue<String>> listarFilaEspera(@PathVariable String isbn) {
        Queue<String> fila = gerenciadorBiblioteca.listarFilaEspera(isbn);
        if (fila.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fila);
    }

    @DeleteMapping("/reservas/{isbn}/{userId}")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String isbn, @PathVariable String userId) {
        gerenciadorBiblioteca.cancelarReserva(isbn, userId);
        return ResponseEntity.noContent().build();
    }
}

