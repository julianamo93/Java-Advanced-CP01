package com.fiap.biblioteca.Exception;

public class LivroJaReservadoException extends RuntimeException {
    public LivroJaReservadoException(String message) {
        super(message);
    }
}
