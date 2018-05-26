package com.enrico.microservices.got.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LogAspect {
	
	@Around("@annotation(logExecution)")
	public Object LogExecution(ProceedingJoinPoint joinPoint, LogExecution logExecution) throws Throwable {

		// Intercepts called class and method name
		final String className = joinPoint.getSignature().getDeclaringTypeName();
		final String methodName = joinPoint.getSignature().getName();
		
		Object result = null;
		LoggerFactory.getLogger(className).info("Will execute method {}.", methodName);
		final StopWatch stopWatch = new StopWatch();
	
		try {
			// Starts timer
			stopWatch.start();
			
			// Deixa o método original executar e armazena o resultado (se houver)
			result = joinPoint.proceed();
			
			// Stops timer
			stopWatch.stop();
			
			LoggerFactory.getLogger(className).info("Method {} executed within {} miliseconds.",
					methodName, stopWatch.getTotalTimeMillis());
		} catch (Exception ex) {
			if (logExecution.logException()) {
				LoggerFactory.getLogger(className).error("Exception was raised while trying to execute method " + methodName, ex);
			}
			// Throws original exception regardless anything
			throw ex;
		}

		return result;
	}

	@Around("@annotation(logRequest)")
	public Object LogRequest(ProceedingJoinPoint joinPoint, LogRequest logRequest) throws Throwable {

		// Intercepts called class and method name
		final String className = joinPoint.getSignature().getDeclaringTypeName();
		final Logger logger = LoggerFactory.getLogger(className);
		
		// Intercepts HTTP/HTTPS request
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		// Logs info
		logger.info("[{}][{}][{}][{}][{}]",
				request.getHeader("X-Request-ID"), 	request.getRemoteHost(), request.getHeader("X-Forwarded-For"),
				request.getHeader("X-Forwarded-Host"), request.getHeader("X-Forwarded-Proto"));
		
		// Allows called method to execute and return it's result, if any
		return joinPoint.proceed();
	}
	
}