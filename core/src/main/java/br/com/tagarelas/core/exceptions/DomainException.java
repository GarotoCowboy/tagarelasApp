package br.com.tagarelas.core.exceptions;

public abstract class DomainException extends RuntimeException {

    private final ErrorCode errorCode;

    // Construtor legado / genérico aceitando apenas mensagem
    protected DomainException(String message) {
        super(message);
        this.errorCode = null;
    }

    // Construtor legado aceitando mensagem e causa
    protected DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }

    // Construtor usando a mensagem padrão do próprio ErrorCode
    protected DomainException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getErrorDescription() : null);
        this.errorCode = errorCode;
    }

    // Construtor principal recebendo ErrorCode e mensagem customizada
    protected DomainException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    // Construtor com causa (para exceções encadeadas)
    protected DomainException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

