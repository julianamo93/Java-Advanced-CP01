package com.fiap.biblioteca.Service;

import com.fiap.biblioteca.Model.Livro;
import com.fiap.biblioteca.Model.dto.ReservaDTO;
import com.fiap.biblioteca.Exception.LivroNaoEncontradoException;
import com.fiap.biblioteca.Exception.LivroJaReservadoException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GerenciadorBiblioteca {
    private final Map<String, Livro> livros = new HashMap<>();
    private final Map<String, Queue<String>> filaEspera = new HashMap<>();

    public Livro criarLivro(Livro livro) {
        if (livros.containsKey(livro.getIsbn())) {
            throw new IllegalArgumentException("ISBN existente.");
        }
        livro.setReservado(false);
        livros.put(livro.getIsbn(), livro);
        return livro;
    }

    public List<Livro> listarLivros(String ordenarPor, String categoria) {
        List<Livro> livrosList = new ArrayList<>(livros.values());
        if (categoria != null) {
            livrosList.removeIf(livro -> !livro.getCategoria().equalsIgnoreCase(categoria));
        }
        if ("titulo".equalsIgnoreCase(ordenarPor)) {
            livrosList.sort(Comparator.comparing(Livro::getTitulo));
        } else if ("autor".equalsIgnoreCase(ordenarPor)) {
            livrosList.sort(Comparator.comparing(Livro::getAutor));
        }
        return livrosList;
    }

    public void excluirLivro(String isbn) {
        Livro livro = livros.get(isbn);
        if (livro == null) {
            throw new LivroNaoEncontradoException(isbn);
        }
        if (livro.isReservado() || filaEspera.containsKey(isbn) && !filaEspera.get(isbn).isEmpty()) {
            throw new IllegalArgumentException("O livro possui reservas ou fila de espera, não pode ser excluído.");
        }
        livros.remove(isbn);
        filaEspera.remove(isbn);
    }

    public void reservarLivro(String isbn, ReservaDTO reservaDTO) {
        Livro livro = livros.get(isbn);
        if (livro == null) {
            throw new LivroNaoEncontradoException(isbn);
        }
        if (livro.isReservado()) {
            filaEspera.computeIfAbsent(isbn, k -> new LinkedList<>()).add(reservaDTO.getUserId());
            throw new LivroJaReservadoException(isbn);
        }
        livro.setReservado(true);
    }

    public Queue<String> listarFilaEspera(String isbn) {
        return filaEspera.getOrDefault(isbn, new LinkedList<>());
    }

    public void cancelarReserva(String isbn, String userId) {
        Livro livro = livros.get(isbn);
        if (livro == null) {
            throw new LivroNaoEncontradoException(isbn);
        }
        Queue<String> fila = filaEspera.get(isbn);
        if (livro.isReservado() && fila != null && fila.contains(userId)) {
            fila.remove(userId);
        }
        if (fila != null && fila.isEmpty()) {
            livro.setReservado(false);
        }
    }
}


