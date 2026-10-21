package com.gustavo.dio_desafio.rede_social.exception;

import com.gustavo.dio_desafio.rede_social.dto.response.ErroResponse;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErroResponse tratarValidacao(MethodArgumentNotValidException exception) {
		String mensagem = exception.getBindingResult().getFieldErrors().stream()
				.map(erro -> erro.getDefaultMessage())
				.sorted()
				.collect(Collectors.joining("; "));
		return new ErroResponse(mensagem);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErroResponse tratarCorpoInvalido() {
		return new ErroResponse("Corpo da requisição ausente ou inválido");
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErroResponse tratarParametroInvalido(MethodArgumentTypeMismatchException exception) {
		return new ErroResponse("Valor inválido para o parâmetro '" + exception.getName() + "'");
	}

	@ExceptionHandler(OrdemFeedInvalidaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErroResponse tratarOrdemInvalida(OrdemFeedInvalidaException exception) {
		return new ErroResponse(exception.getMessage());
	}

	@ExceptionHandler({UsuarioNaoEncontradoException.class, PostNaoEncontradoException.class})
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErroResponse tratarNaoEncontrado(RuntimeException exception) {
		return new ErroResponse(exception.getMessage());
	}

	@ExceptionHandler({EmailDuplicadoException.class, CurtidaDuplicadaException.class})
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErroResponse tratarConflito(RuntimeException exception) {
		return new ErroResponse(exception.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ErroResponse tratarConflitoDeIntegridade() {
		return new ErroResponse("Registro já existe (e-mail já cadastrado ou post já curtido por este usuário)");
	}
}
