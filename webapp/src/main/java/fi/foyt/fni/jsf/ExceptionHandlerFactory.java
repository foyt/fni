package fi.foyt.fni.jsf;

import javax.faces.context.ExceptionHandler;

public class ExceptionHandlerFactory extends javax.faces.context.ExceptionHandlerFactory {
	private final javax.faces.context.ExceptionHandlerFactory parent;

	public ExceptionHandlerFactory(final javax.faces.context.ExceptionHandlerFactory parent) {
		this.parent = parent;
	}

	@Override
	public ExceptionHandler getExceptionHandler() {
		return new fi.foyt.fni.jsf.ExceptionHandler(this.parent.getExceptionHandler());
	}

}
