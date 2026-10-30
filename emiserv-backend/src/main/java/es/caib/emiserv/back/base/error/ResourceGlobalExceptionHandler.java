package es.caib.emiserv.back.base.error;

import es.caib.emiserv.back.base.controller.BaseController;
import es.caib.emiserv.back.base.controller.RestApiController;
import es.caib.emiserv.logic.intf.base.config.BaseBootConfig;
import es.caib.emiserv.logic.intf.base.exception.*;
import es.caib.emiserv.logic.intf.exception.ExportacioMassaGranException;
import es.caib.emiserv.logic.intf.exception.OrdenacioNoValidaException;
import es.caib.emiserv.logic.intf.exception.PermissionDeniedException;
import es.caib.emiserv.logic.intf.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.ejb.EJBException;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * Tractament global de les excepcions en els controladors de l'API REST.
 * <p/>
 * Adaptació d'EMISERV: a diferència de base-boot, s'aplica només als controladors de
 * l'API ({@link BaseController} i {@link RestApiController}) perquè no canviï el
 * tractament d'errors dels controladors de la interfície JSP, que comparteixen el
 * mateix context web.
 * 
 * @author Límit Tecnologies
 */
@Slf4j
@ControllerAdvice(assignableTypes = { BaseController.class, RestApiController.class })
public class ResourceGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String REQUEST_PARAM_TRACE = "trace";

	@Autowired
	private MessageSource messageSource;

	/**
	 * Adaptació d'EMISERV: les traces d'error (?trace=true) no es tornen si no s'activa
	 * expressament, perquè revelen detalls interns de l'aplicació.
	 */
	@Value("${reflectoring.trace:false}")
	private boolean printStackTrace;

	/** Missatge per als errors no previstos: el detall només es registra al log del servidor. */
	static final String MISSATGE_ERROR_INTERN = HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase();

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleNotFoundException(
			NotFoundException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.NOT_FOUND,
				request);
	}

	@ExceptionHandler(ResourceAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleResourceAlreadyExistsException(
			ResourceAlreadyExistsException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.CONFLICT,
				request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Object> handleAccessDeniedException(
			AccessDeniedException ex,
			WebRequest request) {
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.FORBIDDEN,
				request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleDataIntegrityViolationException(
			DataIntegrityViolationException ex,
			WebRequest request) {
		if (ex.getCause() != null && ex.getCause().getClass().getSimpleName().equals("ConstraintViolationException")) {
			String constraintName = null;
			try {
				Field constraintNameField = ex.getCause().getClass().getDeclaredField("constraintName");
				constraintNameField.setAccessible(true);
				constraintName = (String)constraintNameField.get(ex.getCause());
			} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException ignored) {
			}
			String errorMessage;
			if (constraintName != null) {
				if (constraintName.contains(".")) {
					constraintName = constraintName.split("\\.")[1];
				}
				String constraintNameUpper = constraintName.toUpperCase();
				try {
					String errorKey = BaseBootConfig.BASE_PACKAGE + ".error.handling.DataIntegrityViolationException." + constraintNameUpper + ".message";
					errorMessage = messageSource.getMessage(
							errorKey,
							null,
							request.getLocale());
				} catch (NoSuchMessageException nmex) {
					String errorKey = BaseBootConfig.BASE_PACKAGE + ".error.handling.ConstraintViolationException.message";
					if (constraintNameUpper.endsWith("_FK")) {
						errorKey = BaseBootConfig.BASE_PACKAGE + ".error.handling.ForeigKeyViolationException.message";
					} else if (constraintNameUpper.endsWith("_UK")) {
						errorKey = BaseBootConfig.BASE_PACKAGE + ".error.handling.UniqueKeyViolationException.message";
					} else if (constraintNameUpper.endsWith("_PK")) {
						errorKey = BaseBootConfig.BASE_PACKAGE + ".error.handling.PrimaryKeyViolationException.message";
					}
					errorMessage = messageSource.getMessage(
							errorKey,
							new String[] { constraintName },
							request.getLocale());
				}
			} else {
				log.error("Data integrity violation", ex);
				errorMessage = MISSATGE_ERROR_INTERN;
			}
			return buildConstraintValidationErrorResponse(
					ex,
					errorMessage,
					HttpStatus.INTERNAL_SERVER_ERROR,
					request,
					constraintName);
		} else {
			log.error("Data integrity violation", ex);
			return buildErrorResponse(
					ex,
					MISSATGE_ERROR_INTERN,
					HttpStatus.INTERNAL_SERVER_ERROR,
					request);
		}
	}

	@ExceptionHandler(AnswerRequiredException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ResponseEntity<Object> handleAnswerRequiredException(
			AnswerRequiredException ex,
			WebRequest request) {
		return buildAnswerRequiredErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.UNPROCESSABLE_ENTITY,
				request,
				ex.toAnswerRequiredError());
	}

	@ExceptionHandler(ResourceNotCreatedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotCreatedException(
			ResourceNotCreatedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(ResourceNotUpdatedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotUpdatedException(
			ResourceNotUpdatedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(ResourceNotDeletedException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleResourceNotDeletedException(
			ResourceNotDeletedException ex,
			WebRequest request) {
		return buildModificationCanceledErrorResponse(
				ex,
				ex.getReason(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request,
				null);
	}

	@ExceptionHandler(ReportGenerationException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleReportGenerationException(
			ReportGenerationException ex,
			WebRequest request) {
		return buildErrorResponse(
				ex,
				ex.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	@ExceptionHandler(ActionExecutionException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleActionExecutionException(
			ActionExecutionException ex,
			WebRequest request) {
		return buildErrorResponse(
				ex,
				ex.getErrorMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	/**
	 * Adaptació d'EMISERV: exportació que supera el màxim de files (es.caib.emiserv.exportacio.max).
	 * Es torna 422 amb un missatge traduït que inclou el total i el màxim.
	 */
	@ExceptionHandler(ExportacioMassaGranException.class)
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	public ResponseEntity<Object> handleExportacioMassaGranException(
			ExportacioMassaGranException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		String message = messageSource.getMessage(
				"error.exportacio.massa.gran",
				new Object[] { ex.getTotal(), ex.getMaxim() },
				ex.getMessage(),
				request.getLocale());
		return buildErrorResponse(
				ex,
				message,
				HttpStatus.UNPROCESSABLE_ENTITY,
				request);
	}

	/** Adaptació d'EMISERV: ordenació per un camp que no es pot ordenar. */
	@ExceptionHandler(OrdenacioNoValidaException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> handleOrdenacioNoValidaException(
			OrdenacioNoValidaException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.BAD_REQUEST,
				request);
	}

	/**
	 * Adaptació d'EMISERV: errors de validació de negoci dels serveis existents (per exemple, un
	 * servei amb peticions que no es pot esborrar). Es torna 409 amb el missatge del servei.
	 */
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ResponseEntity<Object> handleValidationException(
			ValidationException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.CONFLICT,
				request);
	}

	/** Adaptació d'EMISERV: permís denegat pels serveis existents. */
	@ExceptionHandler(PermissionDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ResponseEntity<Object> handlePermissionDeniedException(
			PermissionDeniedException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.FORBIDDEN,
				request);
	}

	/** Adaptació d'EMISERV: objecte no trobat pels serveis existents. */
	@ExceptionHandler(es.caib.emiserv.logic.intf.exception.NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleEmiservNotFoundException(
			es.caib.emiserv.logic.intf.exception.NotFoundException ex,
			WebRequest request) {
		log.warn(ex.getMessage());
		return buildErrorResponse(
				ex,
				ex.getMessage(),
				HttpStatus.NOT_FOUND,
				request);
	}

	@ExceptionHandler(EJBException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleEJBException(
			EJBException ex,
			WebRequest request) {
		Exception causedBy = ex.getCausedByException();
		if (causedBy instanceof NotFoundException) {
			return handleNotFoundException((NotFoundException)causedBy, request);
		} else if (causedBy instanceof ResourceAlreadyExistsException) {
			return handleResourceAlreadyExistsException((ResourceAlreadyExistsException)causedBy, request);
		} else if (causedBy instanceof AccessDeniedException) {
			return handleAccessDeniedException((AccessDeniedException)causedBy, request);
		} else if (causedBy instanceof DataIntegrityViolationException) {
			return handleDataIntegrityViolationException((DataIntegrityViolationException)causedBy, request);
		} else if (causedBy instanceof AnswerRequiredException) {
			return handleAnswerRequiredException((AnswerRequiredException)causedBy, request);
		} else if (causedBy instanceof ResourceNotCreatedException) {
			return handleResourceNotCreatedException((ResourceNotCreatedException)causedBy, request);
		} else if (causedBy instanceof ResourceNotUpdatedException) {
			return handleResourceNotUpdatedException((ResourceNotUpdatedException)causedBy, request);
		} else if (causedBy instanceof ResourceNotDeletedException) {
			return handleResourceNotDeletedException((ResourceNotDeletedException)causedBy, request);
		} else if (causedBy instanceof ReportGenerationException) {
			return handleReportGenerationException((ReportGenerationException)causedBy, request);
		} else if (causedBy instanceof ActionExecutionException) {
			return handleActionExecutionException((ActionExecutionException)causedBy, request);
		} else if (causedBy instanceof ExportacioMassaGranException) {
			return handleExportacioMassaGranException((ExportacioMassaGranException)causedBy, request);
		} else if (causedBy instanceof OrdenacioNoValidaException) {
			return handleOrdenacioNoValidaException((OrdenacioNoValidaException)causedBy, request);
		} else if (causedBy instanceof ValidationException) {
			return handleValidationException((ValidationException)causedBy, request);
		} else if (causedBy instanceof PermissionDeniedException) {
			return handlePermissionDeniedException((PermissionDeniedException)causedBy, request);
		} else if (causedBy instanceof es.caib.emiserv.logic.intf.exception.NotFoundException) {
			return handleEmiservNotFoundException((es.caib.emiserv.logic.intf.exception.NotFoundException)causedBy, request);
		} else {
			return handleAllUncaughtException(causedBy, request);
		}
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<Object> handleAllUncaughtException(
			Exception ex,
			WebRequest request) {
		log.error("Uncaught exception", ex);
		// Adaptació d'EMISERV: el missatge pot contenir detalls interns (p. ex. SQL).
		return buildErrorResponse(
				ex,
				MISSATGE_ERROR_INTERN,
				HttpStatus.INTERNAL_SERVER_ERROR,
				request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException ex,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		HttpStatus responseStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		FieldValidationErrorResponse fieldValidationErrorResponse = new FieldValidationErrorResponse(
				responseStatus.value(),
				messageSource.getMessage(
						BaseBootConfig.BASE_PACKAGE + ".error.handling.MethodArgumentNotValidException.message",
						new String[] { "validationErrors" },
						request.getLocale())); // "Validation error. Check 'validationErrors' field for details."
		for (ObjectError objectError: ex.getBindingResult().getGlobalErrors()) {
			fieldValidationErrorResponse.addValidationError(
					null,
					null,
					objectError.getCode(),
					objectError.getCodes(),
					objectError.getArguments(),
					objectError.getDefaultMessage());
		}
		for (FieldError fieldError: ex.getBindingResult().getFieldErrors()) {
			fieldValidationErrorResponse.addValidationError(
					fieldError.getField(),
					fieldError.getRejectedValue(),
					fieldError.getCode(),
					fieldError.getCodes(),
					fieldError.getArguments(),
					fieldError.getDefaultMessage());
		}
		return toErrorResponseEntity(responseStatus, fieldValidationErrorResponse);
	}

	@Override
	public ResponseEntity<Object> handleExceptionInternal(
			Exception ex,
			Object body,
			HttpHeaders headers,
			HttpStatus status,
			WebRequest request) {
		log.error("Internal exception", ex);
		if (status.is5xxServerError()) {
			return buildErrorResponse(ex, MISSATGE_ERROR_INTERN, status, request);
		}
		return buildErrorResponse(ex, status, request);
	}

	private ResponseEntity<Object> buildErrorResponse(
			Exception ex,
			HttpStatus httpStatus,
			WebRequest request) {
		return buildErrorResponse(ex, ex.getMessage(), httpStatus, request);
	}

	private ResponseEntity<Object> buildErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), message);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildAnswerRequiredErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request,
			AnswerRequiredException.AnswerRequiredError answerRequiredError) {
		AnswerRequiredErrorResponse errorResponse = new AnswerRequiredErrorResponse(
				httpStatus.value(),
				message,
				answerRequiredError);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildModificationCanceledErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request,
			String action) {
		ErrorResponse errorResponse = new ModificationCanceledErrorResponse(
				httpStatus.value(),
				message,
				action);
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private ResponseEntity<Object> buildConstraintValidationErrorResponse(
			Exception ex,
			String message,
			HttpStatus httpStatus,
			WebRequest request,
			String constraintName) {
		ErrorResponse errorResponse;
		if (constraintName != null) {
			errorResponse = new ConstraintValidationErrorResponse(
					httpStatus.value(),
					message,
					constraintName);
		} else {
			errorResponse = new ErrorResponse(httpStatus.value(), message);
		}
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(ex));
		}
		return toErrorResponseEntity(httpStatus, errorResponse);
	}

	private boolean isTraceOn(WebRequest request) {
		String[] value = request.getParameterValues(REQUEST_PARAM_TRACE);
		return Objects.nonNull(value) && value.length > 0 && (value[0].isEmpty() || value[0].contentEquals("true"));
	}

	private ResponseEntity<Object> toErrorResponseEntity(
			HttpStatus httpStatus,
			ErrorResponse errorResponse) {
		return ResponseEntity.
				status(httpStatus).
				header("Content-Type", "application/problem+json; charset=utf-8").
				body(errorResponse);
	}

}
